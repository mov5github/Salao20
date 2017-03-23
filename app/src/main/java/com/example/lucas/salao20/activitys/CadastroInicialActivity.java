package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.model.CadastroInicial;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCabeleireiro;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCliente;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentCabeleireiros;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentServicos;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentTipoCadastro;
import com.example.lucas.salao20.intentServices.AtualizarCadastroInicialIntentService;
import com.example.lucas.salao20.slidingTabLayout.ConfiguracaoInicialAdapter;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class CadastroInicialActivity extends AppCompatActivity {
    static String INTENT_SERVICE_ATUALIZAR_CADASTRO_INICIAL = "com.example.lucas.salao20.intentservice.atualizarcadastroinicial";
    static String BRODCAST_RECEIVER_ATUALIZAR_CADASTRO_INICIAL = "com.example.lucas.salao20.brodcastreceiver.cadastroinicialatualizado";

    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;

    //ALERT DIALOG
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    //PROGRESDIALOG
    private ProgressDialog progressDialog;

    //BRODCASTRECEIVER
    private BroadcastReceiver broadcastReceiverCadastroInicialAtualizado;

    //CONTROLES
    static boolean cadastroInicialActivityAtiva;
    static boolean sincronizacaoConfiguracaoInicialRequerida;
    private boolean processandoClique;

    //CADASTROS INICIAL CRONTROLE
    static CadastroInicial cadastroInicialBD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","CadastroInicialActivity() onCreate()");

        setContentView(R.layout.activity_cadastro_inicial);

        mAuth = FirebaseAuth.getInstance();
        verifyLogged();

        initControles();

        initView();

        initBrodcastReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
            mSlidingTabLayout.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","CadastroInicialActivity() onStart()");
        cadastroInicialActivityAtiva = true;

        if (!sincronizacaoConfiguracaoInicialRequerida){
            if (cadastroInicialBD.getNivelUsuario() != 1.0){
                if (cadastroInicialBD.getTipoUsuario().equals("salão")){
                    sincronizacaoConfiguracaoInicialRequerida = true;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","CadastroInicialActivity() onDestroy()");
        cadastroInicialActivityAtiva = false;

        //CENCELA ATUALIZARBANCOSINTENTSERVICE
        Intent intent = new Intent(getApplicationContext(), AtualizarCadastroInicialIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("desligar",1);
        intent.putExtras(bundle);
        startService(intent);

        unregisterReceiver(this.broadcastReceiverCadastroInicialAtualizado);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cadastro_inicial,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_ajuda:
                Toast.makeText(this,String.valueOf(id), Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        //BUNDLE
        Bundle bundle = getIntent().getExtras();
        if (cadastroInicialBD == null){
            cadastroInicialBD = new CadastroInicial();
        }
        if (bundle.containsKey(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)){
            cadastroInicialBD.setNivelUsuario(bundle.getDouble(DatabaseHelper.CadastroInicial.NIVEL_USUARIO));
        }
        if (bundle.containsKey(DatabaseHelper.CadastroInicial.TIPO_USUARIO)){
            cadastroInicialBD.setTipoUsuario(bundle.getString(DatabaseHelper.CadastroInicial.TIPO_USUARIO));
        }
        if (bundle.containsKey(DatabaseHelper.CadastroInicial.CODIGO_UNICO)){
            cadastroInicialBD.setCodigoUnico(bundle.getInt(DatabaseHelper.CadastroInicial.CODIGO_UNICO));
        }

        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
        if (cadastroInicialBD.getNivelUsuario() == 1.0){
            mToolbar.setTitle("CONFIGURAÇÃO INICIAL");
            mToolbar.setSubtitle("Tipo de  usuário");
            mToolbar.setLogo(R.mipmap.ic_launcher);
        }else {
            mToolbar.setTitle("CONFIGURAÇÃO INICIAL");
            if (cadastroInicialBD.getTipoUsuario().equals("salão")){
                mToolbar.setSubtitle("Configurações do salão");
                mToolbar.setLogo(R.mipmap.ic_launcher);
            }else if (cadastroInicialBD.getTipoUsuario().equals("cabeleireiro")){
                mToolbar.setSubtitle("Configurações do cabeleireiro");
                mToolbar.setLogo(R.mipmap.ic_launcher);
            }else if (cadastroInicialBD.getTipoUsuario().equals("cliente")){
                mToolbar.setSubtitle("Configurações do cliente");
                mToolbar.setLogo(R.mipmap.ic_launcher);
            }
        }
        setSupportActionBar(mToolbar);

        //TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);
        if (cadastroInicialBD.getNivelUsuario() == 1.0){
            String[] titles = {FragmentTipoCadastro.getTitulo()};
            mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles,null));
        }else {
            if (cadastroInicialBD.getTipoUsuario().equals("salão")){
                String[] titles = {FragmentFuncionamento.getTitulo(), FragmentServicos.getTitulo(),FragmentCabeleireiros.getTitulo()};
                mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles,cadastroInicialBD.getTipoUsuario()));
            }else if (cadastroInicialBD.getTipoUsuario().equals("cabeleireiro")){
                String[] titles = {FragmentBasicoCliente.getTitulo()};
                mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles,cadastroInicialBD.getTipoUsuario()));
            }else if (cadastroInicialBD.getTipoUsuario().equals("cliente")){
                String[] titles = {FragmentBasicoCabeleireiro.getTitulo()};
                mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles,cadastroInicialBD.getTipoUsuario()));
            }
        }

        //PROGREAS DIALOG
        if (cadastroInicialBD.getNivelUsuario() != 1.0){
            if (cadastroInicialBD.getTipoUsuario().equals("salão")){
                if (this.progressDialog == null){
                    this.progressDialog = new ProgressDialog(this);
                }
                this.progressDialog.setMessage("Sincronizando dados na nuvem ...");
                this.progressDialog.show();
            }
        }


        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
    }

    private void initControles(){
        this.processandoClique = false;
        sincronizacaoConfiguracaoInicialRequerida = false;
    }

    private void initBrodcastReceiver(){
        this.broadcastReceiverCadastroInicialAtualizado = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                processandoClique = false;
                progressDialog.dismiss();
                recriarCadastroInicialActivity();
            }
        };
        IntentFilter intentFilter = new IntentFilter(BRODCAST_RECEIVER_ATUALIZAR_CADASTRO_INICIAL);
        registerReceiver(this.broadcastReceiverCadastroInicialAtualizado, intentFilter);
    }

    private void verifyLogged(){
        Log.i("script","verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            Log.i("script","verifyLogged() mAuth.getCurrentUser() != null");
            if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                Log.i("script","verifyLogged() set UID");
                //segue no onCreate
            }else {
                Log.i("script","verifyLogged()  UID = null");
                mAuth.signOut();
                callErroActivity("SPLASHSCREENuserFirebase.getUid()==null");
            }
        }
        else{
            Log.i("script","verifyLogged() mAuth.getCurrentUser() == null");
            callLoginActivity();
        }
    }

    //CALL
    private void callErroActivity(String erro){
        Intent intent = new Intent(this, ErroActivity.class);
        if (erro != null && !erro.isEmpty()){
            intent.putExtra("erro",erro);
        }
        startActivity(intent);
    }

    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void recriarCadastroInicialActivity(){
        Intent intent = new Intent(this,CadastroInicialActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble(DatabaseHelper.CadastroInicial.NIVEL_USUARIO, cadastroInicialBD.getNivelUsuario());
        bundle.putString(DatabaseHelper.CadastroInicial.TIPO_USUARIO, cadastroInicialBD.getTipoUsuario());
        bundle.putInt(DatabaseHelper.CadastroInicial.CODIGO_UNICO, cadastroInicialBD.getCodigoUnico());
        intent.putExtras(bundle);
        finish();
        startActivity(intent);
    }

    //BUTTONS
    public void confirmarTipoCadastro(View view) {
        if (!this.processandoClique){
            this.processandoClique = true;
            if (cadastroInicialBD == null){
                cadastroInicialBD = new CadastroInicial();
            }
            switch (view.getId()){
                case R.id.btn_cadastro_cliente:
                    cadastroInicialBD.setTipoUsuario("cliente");
                    break;
                case R.id.btn_cadastro_salao:
                    cadastroInicialBD.setTipoUsuario("salão");
                    break;
                case R.id.btn_cadastro_cabeleireiro:
                    cadastroInicialBD.setTipoUsuario("cabeleireiro");
                    break;
                default:
                    break;
            }

            if (this.progressDialog == null){
                this.progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Salvando na nuvem ...");
                progressDialog.setCancelable(false);
            }
            if (this.builder == null){
                this.builder = new AlertDialog.Builder(this);
                //define um botão como positivo
                builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        progressDialog.show();
                        Intent intent = new Intent(getApplicationContext(), AtualizarCadastroInicialIntentService.class);
                        Bundle bundle = new Bundle();
                        if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                            bundle.putString(DatabaseHelper.CadastroInicial.UID, mAuth.getCurrentUser().getUid());
                        }
                        bundle.putString(DatabaseHelper.CadastroInicial.TIPO_USUARIO, cadastroInicialBD.getTipoUsuario());
                        bundle.putDouble(DatabaseHelper.CadastroInicial.NIVEL_USUARIO, 2.0);

                        intent.putExtras(bundle);
                        startService(intent);
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        processandoClique = false;
                    }
                });
            }
            switch (cadastroInicialBD.getTipoUsuario()){
                case "cliente":
                    builder.setTitle("Salvar cadastro como Cliente ?");
                    builder.setMessage("Ao criar uma conta como Cliente você podera se vincular a um ou mais salões online, para ter acesso a promoções, agendar horários com seus cabeleireiros e muito mais !");
                    break;
                case "salão":
                    builder.setTitle("Salvar cadastro como Salão ?");
                    builder.setMessage("Ao criar uma conta como Salão você estara abrindo um salão online; podendo definir os serviços prestados, abrir uma agenda para que seus clientes possam agendar horários, adicionar os cabeleireiros que realizaram os serviços no seu salão, gerar promoções e muito mais !");
                    break;
                case "cabeleireiro":
                    builder.setTitle("Salvar cadastro como Cabeleireiro ?");
                    builder.setMessage("Ao criar uma conta como Cabeleireiro você podera se vincular a um ou mais salões online ja existentes, os clientes destes salões poderam agendar horarios com você, voce podera gerenciar seus serviços prestados no decorrer do mês e muito mais !");
                    break;
                default:
                    break;
            }

            this.alertDialog = builder.create();
            this.alertDialog.show();
        }


    }

    public void proximaEtapa(View view) {
    }

    //GETTERS SETTERS
    public static boolean isCadastroInicialActivityAtiva() {
        return cadastroInicialActivityAtiva;
    }

    public static String getIntentServiceAtualizarCadastroInicial() {
        return INTENT_SERVICE_ATUALIZAR_CADASTRO_INICIAL;
    }

    public static String getBrodcastReceiverAtualizarCadastroInicial() {
        return BRODCAST_RECEIVER_ATUALIZAR_CADASTRO_INICIAL;
    }

    public BroadcastReceiver getBroadcastReceiverCadastroInicialAtualizado() {
        return broadcastReceiverCadastroInicialAtualizado;
    }

    public static void setCadastroInicialBD(CadastroInicial cadastroInicialBD) {
        CadastroInicialActivity.cadastroInicialBD = cadastroInicialBD;
    }



}

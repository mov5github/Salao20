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
import com.example.lucas.salao20.dao.model.CadastroBasico;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCabeleireiro;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCliente;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentCabeleireiros;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentServicos;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentTipoCadastro;
import com.example.lucas.salao20.intentServices.AtualizarCadastroInicialIntentService;
import com.example.lucas.salao20.intentServices.BackgroundIntentService;
import com.example.lucas.salao20.slidingTabLayout.ConfiguracaoInicialAdapter;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class CadastroInicialActivity extends AppCompatActivity {
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

    //CONTROLES
    static boolean cadastroInicialActivityAtiva;
    private boolean processandoClique;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","CadastroInicialActivity() onCreate()");

        setContentView(R.layout.activity_cadastro_inicial);

        mAuth = FirebaseAuth.getInstance();

        initControles();
        initView();
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","CadastroInicialActivity() onDestroy()");
        cadastroInicialActivityAtiva = false;
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
            case R.id.action_logout:
                mAuth.signOut();
                callLoginActivity();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
        mToolbar.setTitle("CONFIGURAÇÃO INICIAL");
        if (BackgroundIntentService.getCadastroBasico().getNivelUsuario() == 1.0){
            mToolbar.setSubtitle("Tipo de  usuário");
            mToolbar.setLogo(R.mipmap.ic_launcher);
        }else {
            switch (BackgroundIntentService.getCadastroBasico().getTipoUsuario()){
                case TipoUsuarioENUM.SALAO:
                    mToolbar.setSubtitle("Configurações do salão");
                    mToolbar.setLogo(R.mipmap.ic_launcher);
                    break;
                case TipoUsuarioENUM.CABELEIREIRO:
                    mToolbar.setSubtitle("Configurações do cabeleireiro");
                    mToolbar.setLogo(R.mipmap.ic_launcher);
                    break;
                case TipoUsuarioENUM.CLIENTE:
                    mToolbar.setSubtitle("Configurações do cabeleireiro");
                    mToolbar.setLogo(R.mipmap.ic_launcher);
                    break;
                default:
                    mAuth.signOut();
                    break;
            }
        }
        setSupportActionBar(mToolbar);

        //TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);
        if (BackgroundIntentService.getCadastroBasico().getNivelUsuario() == 1.0){
            String[] titles = {FragmentTipoCadastro.getTitulo()};
            mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles,null));
        }else {
            switch (BackgroundIntentService.getCadastroBasico().getTipoUsuario()){
                case TipoUsuarioENUM.SALAO:
                    String[] titles = {FragmentFuncionamento.getTitulo(), FragmentServicos.getTitulo(),FragmentCabeleireiros.getTitulo()};
                    mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles, BackgroundIntentService.getCadastroBasico().getTipoUsuario()));
                    break;
                case TipoUsuarioENUM.CABELEIREIRO:
                    String[] titles2 = {FragmentBasicoCliente.getTitulo()};
                    mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles2, BackgroundIntentService.getCadastroBasico().getTipoUsuario()));
                    break;
                case TipoUsuarioENUM.CLIENTE:
                    String[] titles3 = {FragmentBasicoCabeleireiro.getTitulo()};
                    mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles3, BackgroundIntentService.getCadastroBasico().getTipoUsuario()));
                    break;
                default:
                    mAuth.signOut();
                    break;
            }
        }

        //PROGREAS DIALOG
        if (this.progressDialog == null){
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setMessage("Sincronizando dados na nuvem ...");
        }


        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
    }

    private void initControles(){
        this.processandoClique = false;
    }

    //CALL
    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void recriarCadastroInicialActivity(){
        Intent intent = new Intent(this,CadastroInicialActivity.class);
        finish();
        startActivity(intent);
    }

    //BUTTONS
    public void confirmarTipoCadastro(View view) {
        if (!this.processandoClique){
            this.processandoClique = true;
            String tipoUsuario = "";
            switch (view.getId()){
                case R.id.btn_cadastro_cliente:
                    tipoUsuario = TipoUsuarioENUM.CLIENTE;
                    break;
                case R.id.btn_cadastro_salao:
                    tipoUsuario = TipoUsuarioENUM.SALAO;
                    break;
                case R.id.btn_cadastro_cabeleireiro:
                    tipoUsuario = TipoUsuarioENUM.CABELEIREIRO;
                    break;
                default:
                    break;
            }

            if (this.builder == null){
                this.builder = new AlertDialog.Builder(this);
                //define um botão como positivo
                final String finalTipoUsuario = tipoUsuario;
                builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        BackgroundIntentService.salvarTipoUsuario(finalTipoUsuario);
                        recriarCadastroInicialActivity();
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        processandoClique = false;
                    }
                });
            }

            switch (tipoUsuario){
                case TipoUsuarioENUM.CLIENTE:
                    builder.setTitle("Salvar cadastro como Cliente ?");
                    builder.setMessage("Ao criar uma conta como Cliente você podera se vincular a um ou mais salões online, para ter acesso a promoções, agendar horários com seus cabeleireiros e muito mais !");
                    break;
                case TipoUsuarioENUM.SALAO:
                    builder.setTitle("Salvar cadastro como Salão ?");
                    builder.setMessage("Ao criar uma conta como Salão você estara abrindo um salão online; podendo definir os serviços prestados, abrir uma agenda para que seus clientes possam agendar horários, adicionar os cabeleireiros que realizaram os serviços no seu salão, gerar promoções e muito mais !");
                    break;
                case TipoUsuarioENUM.CABELEIREIRO:
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

    //GETTERS SETTERS
}

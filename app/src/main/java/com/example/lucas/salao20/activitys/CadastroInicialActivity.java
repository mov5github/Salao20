package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import com.example.lucas.salao20.R;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCabeleireiro;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCliente;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentProfissionais;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentServicos;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentTipoCadastro;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.adapters.ConfiguracaoInicialAdapter;
import com.example.lucas.salao20.geral.FuncionamentoSalao;
import com.example.lucas.salao20.geral.ProfissionaisSalao;
import com.example.lucas.salao20.geral.ServicosSalao;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CadastroInicialActivity extends AppCompatActivity{
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //ENUMS
    private static final String SALVAR_NIVEL_TIPO = "CadastroInicialActivity.SalvarNivelTipoUsuario";

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE REF
    private DatabaseReference refFuncionamento;
    private DatabaseReference refServicos;
    private DatabaseReference refProfissionais;
    private DatabaseReference refUsers;

    //FIREBASE VEL
    private ValueEventListener valueEventListenerFuncionamento;
    private ValueEventListener valueEventListenerServicos;
    private ValueEventListener valueEventListenerProfissionais;

    //ALERT DIALOG
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    //PROGRESDIALOG
    private ProgressDialog progressDialog;

    //CONTROLES
    private static boolean cadastroInicialActivityAtiva;
    private boolean processandoClique;
    private boolean salvarNivelTipo;
    private static boolean etapaFuncionamentoPreenchida;
    private static boolean etapaServicosPreenchida;
    private static boolean etapaProfissionaisPreenchida;
    private static boolean etapaFuncionamentoSalvo;
    private static boolean etapaServicosSalvo;
    private static boolean etapaProfissionaisSalvo;

    //OBJETOS
    private static CadastroBasico cadastroBasico;
    private static FuncionamentoSalao funcionamentoSalao;
    private static ServicosSalao servicosSalao;
    private static ProfissionaisSalao profissionaisSalao;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","CadastroInicialActivity() onCreate()");

        setContentView(R.layout.activity_cadastro_inicial);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();

        initControles();
        receberBundle();

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
        mAuth.addAuthStateListener(mAuthListener);
        if (this.salvarNivelTipo){
            this.salvarNivelTipo = false;
            if (cadastroBasico == null || cadastroBasico.getTipoUsuario() == null || cadastroBasico.getTipoUsuario() == null || cadastroBasico.getTipoUsuario().isEmpty()){
                this.mAuth.signOut();
            }else {
                if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
                    if (refUsers == null){
                        refUsers = LibraryClass.getFirebase().child(GeralENUM.USERS).child( mAuth.getCurrentUser().getUid());
                    }
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(CadastroBasico.getCADASTRO_BASICO(), cadastroBasico.toMap());
                    refUsers.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Log.i("script","onComplete() erro != null");
                                mAuth.signOut();
                            }
                        }
                    });
                }else {
                    this.mAuth.signOut();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","CadastroInicialActivity() onStop()");
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","CadastroInicialActivity() onDestroy()");
        cadastroInicialActivityAtiva = false;
        removerFirebaseEvents();
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
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
        mToolbar.setTitle("CONFIGURAÇÃO INICIAL");
        //TABS
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);

        if (cadastroBasico == null || cadastroBasico.getNivelUsuario() == null){
            this.mAuth.signOut();
        }else {
            if (cadastroBasico.getNivelUsuario() == 1.0){
                mToolbar.setSubtitle("Tipo de  usuário");
                mToolbar.setLogo(R.mipmap.ic_launcher);
                String[] titles = {FragmentTipoCadastro.getTitulo()};
                mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles,null));
            }else {
                if (cadastroBasico.getTipoUsuario() == null || cadastroBasico.getTipoUsuario().isEmpty()){
                    this.mAuth.signOut();
                }else{
                    switch (cadastroBasico.getTipoUsuario()){
                        case TipoUsuarioENUM.SALAO:
                            mToolbar.setSubtitle("Configurações do salão");
                            mToolbar.setLogo(R.mipmap.ic_launcher);
                            String[] titles = {FragmentFuncionamento.getTitulo(), FragmentServicos.getTitulo(), FragmentProfissionais.getTitulo()};
                            mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles, cadastroBasico.getTipoUsuario()));
                            break;
                        case TipoUsuarioENUM.CABELEIREIRO:
                            mToolbar.setSubtitle("Configurações do cabeleireiro");
                            mToolbar.setLogo(R.mipmap.ic_launcher);
                            String[] titles2 = {FragmentBasicoCliente.getTitulo()};
                            mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles2, cadastroBasico.getTipoUsuario()));
                            break;
                        case TipoUsuarioENUM.CLIENTE:
                            mToolbar.setSubtitle("Configurações do cliente");
                            mToolbar.setLogo(R.mipmap.ic_launcher);
                            String[] titles3 = {FragmentBasicoCliente.getTitulo()};
                            mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles3, cadastroBasico.getTipoUsuario()));
                            break;
                        default:
                            mAuth.signOut();
                            break;
                    }
                }
            }
        }
        setSupportActionBar(mToolbar);

        //PROGREAS DIALOG
        if (this.progressDialog == null){
            this.progressDialog = new ProgressDialog(this);
            this.progressDialog.setMessage("Sincronizando dados na nuvem ...");
        }

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
        mSlidingTabLayout.setDistributeEvenly(true);
        if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
            mSlidingTabLayout.setViewPager(mViewPager);
        }
        mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));
    }

    private void initControles(){
        this.processandoClique = false;
        this.salvarNivelTipo = false;
        etapaFuncionamentoPreenchida = false;
        etapaServicosPreenchida = false;
        etapaProfissionaisPreenchida = false;
        etapaFuncionamentoSalvo = false;
        etapaServicosSalvo = false;
        etapaProfissionaisSalvo = false;
    }

    private void initDados(){
        if (cadastroBasico != null && cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
            switch (cadastroBasico.getTipoUsuario()){
                case TipoUsuarioENUM.SALAO:
                    if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
                        if (this.refFuncionamento == null){
                            this.refFuncionamento = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.FUNCIONAMENTO);
                        }
                        if (this.refServicos == null){
                            this.refServicos = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.SERVICOS);
                        }
                        if (this.refProfissionais == null){
                            this.refProfissionais = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.PROFISSIONAIS);
                        }

                        if (this.valueEventListenerFuncionamento == null){
                            this.valueEventListenerFuncionamento = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.i("script","valueEventListenerFuncionamento onDataChange CadastroInicial ");
                                    if (dataSnapshot.exists() && dataSnapshot.getValue(FuncionamentoSalao.class) != null){
                                        Log.i("script","valueEventListenerFuncionamento onDataChange if CadastroInicial ");
                                        funcionamentoSalao = dataSnapshot.getValue(FuncionamentoSalao.class);
                                        liberarFragmentFuncionamento();
                                    }else {
                                        Log.i("script","valueEventListenerFuncionamento onDataChange else CadastroInicial ");
                                        funcionamentoSalao = new FuncionamentoSalao();
                                        liberarFragmentFuncionamento();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i("script","valueEventListenerFuncionamento onCancelled CadastroInicial ");
                                }
                            };
                        }
                        if (this.valueEventListenerServicos == null){
                            this.valueEventListenerServicos = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getValue(ServicosSalao.class) != null){
                                        servicosSalao = dataSnapshot.getValue(ServicosSalao.class);
                                        liberarFragmentServicos();
                                    }else {
                                        servicosSalao = new ServicosSalao();
                                        liberarFragmentServicos();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            };
                        }
                        if (this.valueEventListenerProfissionais == null){
                            this.valueEventListenerProfissionais = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getValue(ProfissionaisSalao.class) != null){
                                        profissionaisSalao = dataSnapshot.getValue(ProfissionaisSalao.class);
                                        liberarFragmentProfissionais();
                                    }else {
                                        profissionaisSalao = new ProfissionaisSalao();
                                        liberarFragmentProfissionais();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            };
                        }

                        this.refFuncionamento.addListenerForSingleValueEvent(valueEventListenerFuncionamento);
                        this.refFuncionamento.addListenerForSingleValueEvent(valueEventListenerServicos);
                        this.refFuncionamento.addListenerForSingleValueEvent(valueEventListenerProfissionais);
                    }else {
                        this.mAuth.signOut();
                    }
                    break;
                case TipoUsuarioENUM.CABELEIREIRO:
                    //TODO
                    break;
                case TipoUsuarioENUM.CLIENTE:
                    //TODO
                    break;
                default:
                    mAuth.signOut();
                    break;
            }
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() CadastroInicial ");

        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged CadastroInicial");
                if(mAuth.getCurrentUser() == null){
                    Log.i("script","getFirebaseAuthResultHandler() getCurrentUser() == null CadastroInicial");
                    callLoginActivity();
                }else if (mAuth.getCurrentUser().getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() uid == null CadastroInicial");
                    mAuth.signOut();
                }
            }
        };
        return( callback );
    }

    private void receberBundle(){
        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(CadastroBasico.getNIVEL_USUARIO())){
            if (cadastroBasico == null){
                cadastroBasico = new CadastroBasico();
            }
            cadastroBasico.setNivelUsuario(bundle.getDouble(CadastroBasico.getNIVEL_USUARIO()));
        }
        if (bundle != null && bundle.containsKey(CadastroBasico.getTIPO_USUARIO())){
            if (cadastroBasico == null){
                cadastroBasico = new CadastroBasico();
            }
            cadastroBasico.setTipoUsuario(bundle.getString(CadastroBasico.getTIPO_USUARIO()));
        }
        if (bundle != null && bundle.containsKey(CadastroBasico.getCODIGO_UNICO())){
            if (cadastroBasico == null){
                cadastroBasico = new CadastroBasico();
            }
            cadastroBasico.setCodigoUnico(bundle.getString(CadastroBasico.getCODIGO_UNICO()));
        }
        if (bundle != null && bundle.containsKey(CadastroInicialActivity.getSalvarNivelTipo())){
            this.salvarNivelTipo = true;
        }
    }

    private void removerFirebaseEvents(){
       //TODO
    }

    private void liberarFragmentFuncionamento(){
        Log.i("script","liberarFragmentFuncionamento CadastroInicial ");
        FragmentFuncionamento fragmentFuncionamento = (FragmentFuncionamento)((ConfiguracaoInicialAdapter) this.mViewPager.getAdapter()).getFragment(0);
       /* fragmentFuncionamento.iniciarFormulario();
        fragmentFuncionamento.liberarPreenchimento();*/
    }

    private void liberarFragmentServicos(){
        //TODO
        FragmentServicos fragmentServicos = (FragmentServicos) ((ConfiguracaoInicialAdapter) this.mViewPager.getAdapter()).getFragment(1);
    }

    private void liberarFragmentProfissionais(){
        //TODO
        FragmentProfissionais fragmentProfissionais = (FragmentProfissionais) ((ConfiguracaoInicialAdapter) this.mViewPager.getAdapter()).getFragment(2);
    }

    public void salvarFuncionamentoSalao(final ArrayList<Funcionamento> funcionamentos){
        if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
            //this.funcionamentoSalvo = false;
            if (funcionamentoSalao == null){
                funcionamentoSalao = new FuncionamentoSalao();
            }
            //funcionamentoSalao.setFuncionamentoDoSalao(funcionamentos);
            if (refFuncionamento == null){
                refFuncionamento = LibraryClass.getFirebase().child(GeralENUM.USERS).child( mAuth.getCurrentUser().getUid()).child(GeralENUM.FUNCIONAMENTO);
            }
            Map<String, Object> childUpdates = new HashMap<>();
            /*for (Funcionamento funcionamento : funcionamentoSalao.getFuncionamentoDoSalao()){
                childUpdates.put(funcionamento.getDia(), funcionamento.toMap());
            }*/
            refFuncionamento.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Log.i("script","onComplete() erro != null");
                        salvarFuncionamentoSalao(funcionamentos);
                    }else {
                        //funcionamentoSalvo = true;
                    }
                }
            });
        }else {
            this.mAuth.signOut();
        }
    }

    public void aguardarSalvar(){
        //TODO
        if (!etapaFuncionamentoSalvo || !etapaServicosSalvo || !etapaProfissionaisSalvo){

        }else {

        }
    }

    public static void notificarSalvamentoConcluido(){
        //TODO
        if (etapaFuncionamentoSalvo && etapaServicosSalvo && etapaFuncionamentoSalvo){

        }
    }

    //CALL
    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void recriarCadastroInicialActivity(){
        Intent intent = new Intent(this,CadastroInicialActivity.class);
        Bundle bundle = new Bundle();
        if (cadastroBasico.getNivelUsuario() != null){
            bundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
        }
        if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
            bundle.putString(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
        }
        bundle.putBoolean(SALVAR_NIVEL_TIPO,true);
        intent.putExtras(bundle);
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
                        if (cadastroBasico == null){
                            cadastroBasico = new CadastroBasico();
                        }
                        cadastroBasico.setTipoUsuario(finalTipoUsuario);
                        cadastroBasico.setNivelUsuario(2.0);
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

    public void chamaTimePicker(View view) {
        //TODO
    }

    public void selecionaDia(View view) {
        ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)this.mViewPager.getAdapter()).getFragment(0)).diaSelecionado((CheckBox) view);
    }

    //GETTERS SETTERS
    public static String getSalvarNivelTipo() {
        return SALVAR_NIVEL_TIPO;
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }

    public static FuncionamentoSalao getFuncionamentoSalao() {
        return funcionamentoSalao;
    }
    public static void setFuncionamentoSalao(FuncionamentoSalao funcionamentoSalao) {
        CadastroInicialActivity.funcionamentoSalao = funcionamentoSalao;
    }

    public static ServicosSalao getServicosSalao() {
        return servicosSalao;
    }

    public static boolean isEtapaFuncionamentoPreenchida() {
        return etapaFuncionamentoPreenchida;
    }
    public static void setEtapaFuncionamentoPreenchida(boolean etapaFuncionamentoPreenchida) {
        CadastroInicialActivity.etapaFuncionamentoPreenchida = etapaFuncionamentoPreenchida;
    }

    public static boolean isEtapaServicosPreenchida() {
        return etapaServicosPreenchida;
    }
    public static void setEtapaServicosPreenchida(boolean etapaServicosPreenchida) {
        CadastroInicialActivity.etapaServicosPreenchida = etapaServicosPreenchida;
    }

    public static boolean isEtapaProfissionaisPreenchida() {
        return etapaProfissionaisPreenchida;
    }
    public static void setEtapaProfissionaisPreenchida(boolean etapaProfissionaisPreenchida) {
        CadastroInicialActivity.etapaProfissionaisPreenchida = etapaProfissionaisPreenchida;
    }

    public static boolean isEtapaFuncionamentoSalvo() {
        return etapaFuncionamentoSalvo;
    }
    public static void setEtapaFuncionamentoSalvo(boolean etapaFuncionamentoSalvo) {
        CadastroInicialActivity.etapaFuncionamentoSalvo = etapaFuncionamentoSalvo;
    }

    public static boolean isEtapaServicosSalvo() {
        return etapaServicosSalvo;
    }
    public static void setEtapaServicosSalvo(boolean etapaServicosSalvo) {
        CadastroInicialActivity.etapaServicosSalvo = etapaServicosSalvo;
    }

    public static boolean isEtapaProfissionaisSalvo() {
        return etapaProfissionaisSalvo;
    }
    public static void setEtapaProfissionaisSalvo(boolean etapaProfissionaisSalvo) {
        CadastroInicialActivity.etapaProfissionaisSalvo = etapaProfissionaisSalvo;
    }
}

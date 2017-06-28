package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
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
import android.widget.TimePicker;
import android.widget.Toast;
import com.example.lucas.salao20.R;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.cliente.FragmentConfiguracaoInicialClienteBasico;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoProfissionais;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoServicos;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentConfiguracaoInicialTipoCadastro;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.adapters.ConfiguracaoInicialAdapter;
import com.example.lucas.salao20.geral.geral.CadastroComplementar;
import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;
import com.example.lucas.salao20.geral.profissional.ExpedienteProfissional;
import com.example.lucas.salao20.geral.profissional.ServicoProfissional;
import com.example.lucas.salao20.geral.profissional.ServicosProfissional;
import com.example.lucas.salao20.geral.salao.FuncionamentoSalao;
import com.example.lucas.salao20.geral.salao.ProfissionaisSalao;
import com.example.lucas.salao20.geral.salao.ServicosSalao;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ConfiguracaoInicialActivity extends AppCompatActivity{
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //HANDLER
    private Handler handler;


    //ENUMS
    private static final String SALVAR_NIVEL_TIPO = "ConfiguracaoInicialActivity.SalvarNivelTipoUsuario";

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE REF
    private DatabaseReference refUser;
    private DatabaseReference refCadastroBasico;
    private DatabaseReference refCadastroComplementar;
    private DatabaseReference refFuncionamentoSalao;
    private DatabaseReference refServicosSalao;
    private DatabaseReference refProfissionaisSalao;
    private DatabaseReference refRegrasDeNegocio;
    private DatabaseReference refServicosProfissionais;


    //FIREBASE VEL
    private ValueEventListener valueEventListenerCadastroBasico;
    private ValueEventListener valueEventListenerRegrasDeNegocio;
    private ValueEventListener valueEventListenerFuncionamentoSalao;
    private ChildEventListener childEventListenerFuncionamentoSalao;
    private ValueEventListener valueEventListenerServicosSalao;
    private ChildEventListener childEventListenerServicosSalao;
    private ValueEventListener valueEventListenerProfissionaisSalao;
    private ChildEventListener childEventListenerProfissionaisSalao;
    private ChildEventListener childEventListenerCadastroComplementar;
    private ValueEventListener valueEventListenerCadastroComplementar;
    private ValueEventListener valueEventListenerServicosProfissional;
    private ChildEventListener childEventListenerServicosProfissional;

    //ALERT DIALOG
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    //PROGRESDIALOG
    private ProgressDialog progressDialog;

    //CONTROLES
    private static boolean cadastroInicialActivityAtiva;
    private boolean processandoClique;
    private static boolean cadastroComplementarObtido;
    private static boolean funcionamentoSalaoObtido;
    private static boolean etapa1Preenchida;
    private static boolean etapa1Salva;
    private static boolean etapa2Preenchida;
    private static boolean etapa2Salva;
    private static boolean etapa3Preenchida;
    private static boolean etapa3Salva;




    //OBJETOS
    private static CadastroBasico cadastroBasico = null;
    private static FuncionamentoSalao funcionamentoSalao = null;
    private static ServicosSalao servicosSalao = null;
    private static ProfissionaisSalao profissionaisSalao = null;
    private static CadastroComplementar cadastroComplementar = null;
    private static HashMap<String,Integer> regrasDeNegocio = new HashMap<String,Integer>();

    //TIMEPICKER
    private TimePickerDialog timePickerDialogAbertura;
    private TimePickerDialog timePickerDialogFechamento;

    //AUXILIARES
    private static int auxViewIdTimePicker;
    private static int auxCodUnico;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","ConfiguracaoInicialActivity() onCreate()");

        setContentView(R.layout.activity_cadastro_inicial);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();

        this.handler  = new Handler();

        initControles();
        receberBundle();
        initView();

        initDados();
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
        Log.i("script","ConfiguracaoInicialActivity() onStart()");
        cadastroInicialActivityAtiva = true;
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","ConfiguracaoInicialActivity() onStop()");
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","ConfiguracaoInicialActivity() onDestroy()");
        cadastroInicialActivityAtiva = false;
        removerFirebaseEvents();
        this.handler.removeCallbacksAndMessages(null);
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
                String[] titles = {FragmentConfiguracaoInicialTipoCadastro.getTITULO()};
                mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles,null));
            }else {
                if (cadastroBasico.getTipoUsuario() == null || cadastroBasico.getTipoUsuario().isEmpty()){
                    this.mAuth.signOut();
                }else{
                    switch (cadastroBasico.getTipoUsuario()){
                        case TipoUsuarioENUM.SALAO:
                            mToolbar.setSubtitle("Configurações do salão");
                            mToolbar.setLogo(R.mipmap.ic_launcher);
                            String[] titles = {FragmentConfiguracaoInicialSalaoFuncionamento.getTITULO(), FragmentConfiguracaoInicialSalaoServicos.getTITULO(), FragmentConfiguracaoInicialSalaoProfissionais.getTITULO()};
                            mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles, cadastroBasico.getTipoUsuario()));
                            break;
                        case TipoUsuarioENUM.PROFISSIONAl:
                            mToolbar.setSubtitle("Configurações do cabeleireiro");
                            mToolbar.setLogo(R.mipmap.ic_launcher);
                            String[] titles2 = {FragmentConfiguracaoInicialClienteBasico.getTITULO()};
                            mViewPager.setAdapter(new ConfiguracaoInicialAdapter(getSupportFragmentManager(),this,titles2, cadastroBasico.getTipoUsuario()));
                            break;
                        case TipoUsuarioENUM.CLIENTE:
                            mToolbar.setSubtitle("Configurações do cliente");
                            mToolbar.setLogo(R.mipmap.ic_launcher);
                            String[] titles3 = {FragmentConfiguracaoInicialClienteBasico.getTITULO()};
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
            this.progressDialog.setCancelable(false);
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
        funcionamentoSalaoObtido = false;
        cadastroComplementarObtido = false;
        etapa1Preenchida = false;
        etapa1Salva = false;
        etapa2Preenchida = false;
        etapa2Salva = false;
        etapa3Preenchida = false;
        etapa3Salva = false;
    }

    private void initDados(){
        if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
            if (this.refCadastroComplementar == null){
                this.refCadastroComplementar = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroComplementar.getCADASTRO_COMPLEMENTAR());
                this.refCadastroComplementar.keepSynced(true);
            }
            if (this.refCadastroBasico == null){
                this.refCadastroBasico = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroBasico.getCADASTRO_BASICO());
                this.refCadastroBasico.keepSynced(true);
            }
            if (this.refUser == null){
                this.refUser = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid());
            }

            if (this.valueEventListenerCadastroBasico == null){
                this.valueEventListenerCadastroBasico = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (cadastroBasico == null){
                                cadastroBasico = new CadastroBasico();
                            }
                            if (dataSnapshot.hasChild(CadastroBasico.getTIPO_USUARIO())){
                                cadastroBasico.setTipoUsuario(dataSnapshot.child(CadastroBasico.getTIPO_USUARIO()).getValue(String.class));
                            }
                            if (dataSnapshot.hasChild(CadastroBasico.getNIVEL_USUARIO())){
                                cadastroBasico.setNivelUsuario(dataSnapshot.child(CadastroBasico.getNIVEL_USUARIO()).getValue(Double.class));
                            }
                            if (dataSnapshot.hasChild(CadastroBasico.getCODIGO_UNICO())){
                                cadastroBasico.setCodigoUnico(dataSnapshot.child(CadastroBasico.getCODIGO_UNICO()).getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            }
            this.refCadastroBasico.addValueEventListener(this.valueEventListenerCadastroBasico);

            if (this.valueEventListenerCadastroComplementar == null){
                this.valueEventListenerCadastroComplementar = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Log.i("testeteste","valueEventListenerCadastroComplementar onDataChange");
                            if (cadastroComplementar == null){
                                cadastroComplementar = new CadastroComplementar();
                            }
                            if (dataSnapshot.hasChild(CadastroComplementar.getNOME())){
                                cadastroComplementar.setNome(dataSnapshot.child(CadastroComplementar.getNOME()).getValue(String.class));
                                if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                    ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).nomeAtualizado();
                                }
                            }
                        }else{
                            if (cadastroComplementar == null){
                                cadastroComplementar = new CadastroComplementar();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            }
            this.refCadastroComplementar.addListenerForSingleValueEvent(this.valueEventListenerCadastroComplementar);

            if (cadastroBasico != null && cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
                switch (cadastroBasico.getTipoUsuario()){
                    case TipoUsuarioENUM.SALAO:
                        if (this.refFuncionamentoSalao == null){
                            this.refFuncionamentoSalao = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.FUNCIONAMENTO);
                            this.refFuncionamentoSalao.keepSynced(true);
                        }
                        if (this.refServicosSalao == null){
                            this.refServicosSalao = LibraryClass.getFirebase().child(GeralENUM.SERVICOS).child(mAuth.getCurrentUser().getUid()).child(TipoUsuarioENUM.SALAO);
                            this.refServicosSalao.keepSynced(true);
                        }
                        if (this.refProfissionaisSalao == null){
                            this.refProfissionaisSalao = LibraryClass.getFirebase().child(GeralENUM.PROFISSIONAIS);
                            this.refProfissionaisSalao.keepSynced(true);
                        }
                        /*if (this.refServicosProfissionais == null){
                            this.refServicosProfissionais = LibraryClass.getFirebase().child(GeralENUM.SERVICOS).child(TipoUsuarioENUM.PROFISSIONAl).child(mAuth.getCurrentUser().getUid());
                            this.refServicosProfissionais.keepSynced(true);
                        }*/


                        //FUNCIONAMNENTO SALAO
                        if (this.childEventListenerFuncionamentoSalao == null){
                            this.childEventListenerFuncionamentoSalao = new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()){
                                        Funcionamento funcionamento = new Funcionamento();
                                        funcionamento.setDia(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(DiasENUM.ABRE)){
                                            funcionamento.setAbre(dataSnapshot.child(DiasENUM.ABRE).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(DiasENUM.FECHA)){
                                            funcionamento.setFecha(dataSnapshot.child(DiasENUM.FECHA).getValue(String.class));
                                        }
                                        funcionamentoSalao.addFuncionamento(funcionamento);
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).funcionamentoAdicionado(funcionamento.getDia());
                                        }
                                    }

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()){
                                        Funcionamento funcionamento = new Funcionamento();
                                        funcionamento.setDia(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(DiasENUM.ABRE)){
                                            funcionamento.setAbre(dataSnapshot.child(DiasENUM.ABRE).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(DiasENUM.FECHA)){
                                            funcionamento.setFecha(dataSnapshot.child(DiasENUM.FECHA).getValue(String.class));
                                        }
                                        funcionamentoSalao.addFuncionamento(funcionamento);
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).funcionamentoAlterado(funcionamento.getDia());
                                        }
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        funcionamentoSalao.removerFuncionamento(dataSnapshot.getKey());
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).funcionamentoRemovido(dataSnapshot.getKey());
                                        }
                                    }
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                        }
                        if (this.valueEventListenerFuncionamentoSalao == null){
                            this.valueEventListenerFuncionamentoSalao = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.i("testeteste","valueEventListenerFuncionamentoSalao onDataChange");
                                    if (funcionamentoSalao == null){
                                        funcionamentoSalao = new FuncionamentoSalao();
                                        funcionamentoSalao.setFuncionamentoDoSalao(new HashMap<String, Funcionamento>());
                                    }
                                    if (dataSnapshot.exists()){
                                        if (dataSnapshot.getChildrenCount() == 0){
                                            if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                                ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).liberarFormulario();
                                            }
                                        }
                                    }else{
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).liberarFormulario();
                                        }
                                    }
                                    refFuncionamentoSalao.addChildEventListener(childEventListenerFuncionamentoSalao);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                        }
                        this.refFuncionamentoSalao.addListenerForSingleValueEvent(this.valueEventListenerFuncionamentoSalao);

                        //SERVICOS SALAO
                        if (this.childEventListenerServicosSalao == null){
                            this.childEventListenerServicosSalao = new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.i("fireServicos","onChildAdded");
                                    if (dataSnapshot.exists()){
                                        Servico servico = new Servico();
                                        servico.setIdServico(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(Servico.getNOME())){
                                            servico.setNome(dataSnapshot.child(Servico.getNOME()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getICONE())){
                                            servico.setIcone(dataSnapshot.child(Servico.getICONE()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDURACAO())){
                                            servico.setDuracao(dataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getPRECO())){
                                            servico.setPreco(dataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDESCRICAO())){
                                            servico.setDescricao(dataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDataDeInsercao())){
                                            servico.setDataInsercao(dataSnapshot.child(Servico.getDataDeInsercao()).getValue(Long.class));
                                        }
                                        servicosSalao.addServico(servico);
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoAdicionado(servico.getIdServico());
                                        }
                                    }

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    Log.i("fireServicos","onChildChanged");
                                    if (dataSnapshot.exists()){
                                        Servico servico = new Servico();
                                        servico.setIdServico(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(Servico.getNOME())){
                                            servico.setNome(dataSnapshot.child(Servico.getNOME()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getICONE())){
                                            servico.setIcone(dataSnapshot.child(Servico.getICONE()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDURACAO())){
                                            servico.setDuracao(dataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getPRECO())){
                                            servico.setPreco(dataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDESCRICAO())){
                                            servico.setDescricao(dataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDataDeInsercao())){
                                            servico.setDataInsercao(dataSnapshot.child(Servico.getDataDeInsercao()).getValue(Long.class));
                                        }
                                        servicosSalao.addServico(servico);
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoAlterado(servico.getIdServico());
                                        }
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    Log.i("fireServicos","onChildRemoved");
                                    if (dataSnapshot.exists()){
                                        servicosSalao.removerServico(dataSnapshot.getKey());
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoRemovido(dataSnapshot.getKey());
                                        }
                                    }

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i("fireServicos","onCancelled");
                                }
                            };
                        }
                        if (this.valueEventListenerServicosSalao == null){
                            this.valueEventListenerServicosSalao = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.i("testeteste","valueEventListenerServicosSalao onDataChange");
                                    if (servicosSalao == null){
                                        servicosSalao = new ServicosSalao();
                                        servicosSalao.setServicosSalao(new HashMap<String, Servico>());
                                    }
                                    if (dataSnapshot.exists()){
                                        if (dataSnapshot.getChildrenCount() == 0){
                                            if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                                ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).liberarFormulario();
                                            }
                                        }
                                    }else {
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).liberarFormulario();
                                        }
                                    }
                                    refServicosSalao.addChildEventListener(childEventListenerServicosSalao);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                        }
                        this.refServicosSalao.addListenerForSingleValueEvent(this.valueEventListenerServicosSalao);
                        //PROFISSIONAIS SALAO
                        if (this.childEventListenerProfissionaisSalao == null){
                            this.childEventListenerProfissionaisSalao = new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.i("fireServicos","onChildAdded");
                                    if (dataSnapshot.exists()){
                                        Log.i("testeteste",dataSnapshot.toString());
                                        /*Profissional profissional = new Profissional();
                                        profissional.setIdProfissional(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(Profissional.getDataDeInsercao())){
                                            profissional.setDataInsercao(dataSnapshot.child(Profissional.getDataDeInsercao()).getValue(Long.class));
                                        }
                                        if (dataSnapshot.hasChild(Profissional.getUidProfissional())){
                                            profissional.setuIDProfissional(dataSnapshot.child(Profissional.getUidProfissional()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Profissional.getEXPEDIENTE())){
                                            ExpedienteProfissional expedienteProfissional = new ExpedienteProfissional();
                                            for (DataSnapshot child : dataSnapshot.child(Profissional.getEXPEDIENTE()).getChildren() ){
                                                Funcionamento funcionamento = new Funcionamento();
                                                funcionamento.setDia(child.getKey());
                                                if (child.hasChild(DiasENUM.ABRE)){
                                                    funcionamento.setAbre(child.child(DiasENUM.ABRE).getValue(String.class));
                                                }
                                                if (child.hasChild(DiasENUM.FECHA)){
                                                    funcionamento.setFecha(child.child(DiasENUM.FECHA).getValue(String.class));
                                                }
                                                if (child.hasChild(DiasENUM.INICIO_ALMOCO)){
                                                    funcionamento.setInicioAlmoco(child.child(DiasENUM.INICIO_ALMOCO).getValue(String.class));
                                                }
                                                if (child.hasChild(DiasENUM.DURACAO_ALMOCO)){
                                                    funcionamento.setDuracaoAlmoco(child.child(DiasENUM.DURACAO_ALMOCO).getValue(Integer.class));
                                                }
                                                expedienteProfissional.addFuncionamento(funcionamento);
                                            }
                                            profissional.setExpedienteProfissional(expedienteProfissional);
                                        }
                                        if (dataSnapshot.hasChild(Profissional.getSERVICOS())){
                                            ServicosProfissional servicosProfissional = new ServicosProfissional();
                                            for (DataSnapshot child : dataSnapshot.child(Profissional.getSERVICOS()).getChildren() ){
                                                ServicoProfissional servicoProfissional = new ServicoProfissional();
                                                servicoProfissional.setIdServico(child.getKey());
                                                if (child.hasChild(ServicoProfissional.getDURACAO())){
                                                    servicoProfissional.setDuracao(child.child(ServicoProfissional.getDURACAO()).getValue(Integer.class));
                                                }
                                                if (child.hasChild(ServicoProfissional.getIdAuxServico())){
                                                    for (DataSnapshot childAux : child.child(ServicoProfissional.getIdAuxServico()).getChildren()){
                                                        Servico servico = new Servico();
                                                        servico.setIdServico(childAux.getKey());
                                                        if (childAux.hasChild(Servico.getNOME())){
                                                            servico.setNome(childAux.child(Servico.getNOME()).getValue(String.class));
                                                        }
                                                        if (childAux.hasChild(Servico.getICONE())){
                                                            servico.setIcone(childAux.child(Servico.getICONE()).getValue(Integer.class));
                                                        }
                                                        if (childAux.hasChild(Servico.getDURACAO())){
                                                            servico.setDuracao(childAux.child(Servico.getDURACAO()).getValue(Integer.class));
                                                        }
                                                        if (childAux.hasChild(Servico.getPRECO())){
                                                            servico.setPreco(childAux.child(Servico.getPRECO()).getValue(Double.class));
                                                        }
                                                        if (childAux.hasChild(Servico.getDESCRICAO())){
                                                            servico.setDescricao(childAux.child(Servico.getDESCRICAO()).getValue(String.class));
                                                        }
                                                        if (childAux.hasChild(Servico.getDataDeInsercao())){
                                                            servico.setDataInsercao(childAux.child(Servico.getDataDeInsercao()).getValue(Long.class));
                                                        }
                                                        servicoProfissional.setServico(servico);
                                                    }
                                                }
                                                servicosProfissional.addServicoProfissional(servicoProfissional);
                                            }
                                            profissional.setServicosProfissional(servicosProfissional);
                                        }

                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoProfissionais.isFragmentProfissionaisSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoProfissionais)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(2)).profissionalAdicionado(profissional.getIdProfissional());
                                        }*/
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    Log.i("fireServicos","onChildChanged");
                                    if (dataSnapshot.exists()){
                                        Servico servico = new Servico();
                                        servico.setIdServico(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(Servico.getNOME())){
                                            servico.setNome(dataSnapshot.child(Servico.getNOME()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getICONE())){
                                            servico.setIcone(dataSnapshot.child(Servico.getICONE()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDURACAO())){
                                            servico.setDuracao(dataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getPRECO())){
                                            servico.setPreco(dataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDESCRICAO())){
                                            servico.setDescricao(dataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDataDeInsercao())){
                                            servico.setDataInsercao(dataSnapshot.child(Servico.getDataDeInsercao()).getValue(Long.class));
                                        }
                                        servicosSalao.addServico(servico);
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoAlterado(servico.getIdServico());
                                        }
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    Log.i("fireServicos","onChildRemoved");
                                    if (dataSnapshot.exists()){
                                        servicosSalao.removerServico(dataSnapshot.getKey());
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoRemovido(dataSnapshot.getKey());
                                        }
                                    }

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i("fireServicos","onCancelled");
                                }
                            };
                        }
                        if (this.valueEventListenerProfissionaisSalao == null){
                            this.valueEventListenerProfissionaisSalao = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (profissionaisSalao == null){
                                        profissionaisSalao = new ProfissionaisSalao();
                                        profissionaisSalao.setProfissionais(new HashMap<String, Profissional>());
                                    }
                                    if (dataSnapshot.exists()){
                                        Log.i("snapshot",dataSnapshot.toString());
                                        if (dataSnapshot.getChildrenCount() == 0){
                                            /*if (mViewPager != null && FragmentConfiguracaoInicialSalaoProfissionais.isFragmentProfissionaisSalaoAtivo()){
                                                ((FragmentConfiguracaoInicialSalaoProfissionais)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(2)).liberarFormulario();
                                            }*/
                                        }
                                    }else {
                                       /* if (mViewPager != null && FragmentConfiguracaoInicialSalaoProfissionais.isFragmentProfissionaisSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoProfissionais)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(2)).liberarFormulario();
                                        }*/
                                    }
                                    refProfissionaisSalao.addChildEventListener(childEventListenerProfissionaisSalao);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                        }
                        this.refProfissionaisSalao.addListenerForSingleValueEvent(this.valueEventListenerProfissionaisSalao);
                        /*//SERVICOS PROFISSIONAL
                        if (this.childEventListenerServicosProfissional == null){
                            this.childEventListenerServicosProfissional = new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    Log.i("fireServicos","onChildAdded");
                                    if (dataSnapshot.exists()){
                                        Servico servico = new Servico();
                                        servico.setIdServico(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(Servico.getNOME())){
                                            servico.setNome(dataSnapshot.child(Servico.getNOME()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getICONE())){
                                            servico.setIcone(dataSnapshot.child(Servico.getICONE()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDURACAO())){
                                            servico.setDuracao(dataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getPRECO())){
                                            servico.setPreco(dataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDESCRICAO())){
                                            servico.setDescricao(dataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDataDeInsercao())){
                                            servico.setDataInsercao(dataSnapshot.child(Servico.getDataDeInsercao()).getValue(Long.class));
                                        }
                                        servicosSalao.addServico(servico);
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoAdicionado(servico.getIdServico());
                                        }
                                    }

                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    Log.i("fireServicos","onChildChanged");
                                    if (dataSnapshot.exists()){
                                        Servico servico = new Servico();
                                        servico.setIdServico(dataSnapshot.getKey());
                                        if (dataSnapshot.hasChild(Servico.getNOME())){
                                            servico.setNome(dataSnapshot.child(Servico.getNOME()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getICONE())){
                                            servico.setIcone(dataSnapshot.child(Servico.getICONE()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDURACAO())){
                                            servico.setDuracao(dataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getPRECO())){
                                            servico.setPreco(dataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDESCRICAO())){
                                            servico.setDescricao(dataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(Servico.getDataDeInsercao())){
                                            servico.setDataInsercao(dataSnapshot.child(Servico.getDataDeInsercao()).getValue(Long.class));
                                        }
                                        servicosSalao.addServico(servico);
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoAlterado(servico.getIdServico());
                                        }
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    Log.i("fireServicos","onChildRemoved");
                                    if (dataSnapshot.exists()){
                                        servicosSalao.removerServico(dataSnapshot.getKey());
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).servicoRemovido(dataSnapshot.getKey());
                                        }
                                    }

                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.i("fireServicos","onCancelled");
                                }
                            };
                        }
                        if (this.valueEventListenerServicosProfissional == null){
                            this.valueEventListenerServicosProfissional = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Log.i("testeteste","valueEventListenerServicosSalao onDataChange");
                                    if (servicosSalao == null){
                                        servicosSalao = new ServicosSalao();
                                        servicosSalao.setServicosSalao(new HashMap<String, Servico>());
                                    }
                                    if (dataSnapshot.exists()){
                                        if (dataSnapshot.getChildrenCount() == 0){
                                            if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                                ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).liberarFormulario();
                                            }
                                        }
                                    }else {
                                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoServicos.isFragmentServicosSalaoAtivo()){
                                            ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).liberarFormulario();
                                        }
                                    }
                                    refServicosSalao.addChildEventListener(childEventListenerServicosSalao);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                        }
                        this.refServicosProfissionais.addListenerForSingleValueEvent(this.valueEventListenerServicosProfissional);*/
                        if (cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() == 2.0){
                            gerarCodigoUnico(TipoUsuarioENUM.SALAO);
                        }
                        break;
                    case TipoUsuarioENUM.PROFISSIONAl:
                        //TODO
                        if (cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() == 2.0){
                            gerarCodigoUnico(TipoUsuarioENUM.PROFISSIONAl);
                        }
                        break;
                    case TipoUsuarioENUM.CLIENTE:
                        //TODO
                        break;
                    default:
                        mAuth.signOut();
                        break;
                }
            }
        }else {
            this.mAuth.signOut();
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
        if (getIntent().hasExtra(CadastroBasico.getCADASTRO_BASICO())){
            Bundle bundle = getIntent().getExtras().getBundle(CadastroBasico.getCADASTRO_BASICO());

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
        }
    }

    private void removerFirebaseEvents(){
       //TODO
        if (this.refFuncionamentoSalao != null){
            this.refFuncionamentoSalao.keepSynced(false);
        }
        if (this.refCadastroComplementar != null){
            this.refCadastroComplementar.keepSynced(false);
        }
        if (this.refServicosSalao != null){
            this.refServicosSalao.keepSynced(false);
        }
        if (this.refProfissionaisSalao != null){
            this.refProfissionaisSalao.keepSynced(false);
        }
        if (this.refCadastroBasico != null){
            this.refCadastroBasico.keepSynced(false);
        }

        if (this.childEventListenerFuncionamentoSalao != null){
            if (this.refFuncionamentoSalao != null) {
                this.refFuncionamentoSalao.removeEventListener(this.childEventListenerFuncionamentoSalao);
            }
        }
        if (this.valueEventListenerFuncionamentoSalao != null){
            if (this.refFuncionamentoSalao != null) {
                this.refFuncionamentoSalao.removeEventListener(this.valueEventListenerFuncionamentoSalao);
            }
        }
        if (this.childEventListenerCadastroComplementar != null){
            if (this.refCadastroComplementar != null) {
                this.refCadastroComplementar.removeEventListener(this.childEventListenerCadastroComplementar);
            }
        }
        if (this.valueEventListenerCadastroComplementar != null){
            if (this.refCadastroComplementar != null) {
                this.refCadastroComplementar.removeEventListener(this.valueEventListenerCadastroComplementar);
            }
        }
        if (this.childEventListenerServicosSalao != null){
            if (this.refServicosSalao != null) {
                this.refServicosSalao.removeEventListener(this.childEventListenerServicosSalao);
            }
        }
        if (this.valueEventListenerServicosSalao != null){
            if (this.refServicosSalao != null) {
                this.refServicosSalao.removeEventListener(this.valueEventListenerServicosSalao);
            }
        }
        if (this.valueEventListenerRegrasDeNegocio != null){
            if (this.refRegrasDeNegocio != null) {
                this.refRegrasDeNegocio.removeEventListener(this.valueEventListenerRegrasDeNegocio);
            }
        }
        if (this.valueEventListenerCadastroBasico != null){
            if (this.refCadastroBasico != null) {
                this.refCadastroBasico.removeEventListener(this.valueEventListenerCadastroBasico);
            }
        }
    }

    private void gerarCodigoUnico(String tipoUsuario){
        if (this.refRegrasDeNegocio == null){
            this.refRegrasDeNegocio = LibraryClass.getFirebase().child(GeralENUM.REGRAS_DE_NEGOCIO);
            if (this.valueEventListenerRegrasDeNegocio == null){
                this.valueEventListenerRegrasDeNegocio = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("testeteste","valueEventListenerRegrasDeNegocio onDataChange");
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.hasChild("ControladorCodigoProfissional")){
                                if (regrasDeNegocio.containsKey("ControladorCodigoProfissional")){
                                    regrasDeNegocio.remove("ControladorCodigoProfissional");
                                }
                                regrasDeNegocio.put("ControladorCodigoProfissional",dataSnapshot.child("ControladorCodigoProfissional").getValue(Integer.class));
                            }
                            if(dataSnapshot.hasChild("ControladorCodigoSalao")){
                                if (regrasDeNegocio.containsKey("ControladorCodigoSalao")){
                                    regrasDeNegocio.remove("ControladorCodigoSalao");
                                }
                                regrasDeNegocio.put("ControladorCodigoSalao",dataSnapshot.child("ControladorCodigoSalao").getValue(Integer.class));
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    atualizarControladorCodigoUnico(cadastroBasico.getTipoUsuario());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                };
            }
            this.refRegrasDeNegocio.addValueEventListener(this.valueEventListenerRegrasDeNegocio);
        }
    }

    private void atualizarControladorCodigoUnico(String tipoUsuario){
       /* switch (tipoUsuario){
            case TipoUsuarioENUM.SALAO:
                if (regrasDeNegocio.containsKey("ControladorCodigoSalao")){
                    auxCodUnico = regrasDeNegocio.get("ControladorCodigoSalao") + 1;
                    refRegrasDeNegocio.child("ControladorCodigoSalao").setValue(auxCodUnico, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        // gerarCodigoUnico(TipoUsuarioENUM.SALAO);
                                    }
                                },500);
                            }else {
                                Map<String,Object> childUpdates = new HashMap<String, Object>();
                                cadastroBasico.setCodigoUnico(LibraryClass.formatarCodUnico(auxCodUnico));
                                if (etapa1Salva && etapa2Salva && etapa3Salva){
                                    cadastroBasico.setNivelUsuario(3.0);
                                }else{
                                    cadastroBasico.setNivelUsuario(2.1);
                                }

                                childUpdates.put(CadastroBasico.getCADASTRO_BASICO(),cadastroBasico.toMap());
                                refUser.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null){
                                            cadastroBasico.setNivelUsuario(2.0);
                                            // gerarCodigoUnico(TipoUsuarioENUM.SALAO);
                                        }else{
                                            if (cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() == 2.1){
                                                if (etapa1Salva && etapa2Salva && etapa3Salva){
                                                    salvarCadastroBasico();
                                                }
                                            }else if (cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() == 3.0){
                                                callHomeActivity();
                                            }

                                        }
                                    }
                                });
                            }
                        }
                    });
                }else{
                    this.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //gerarCodigoUnico(TipoUsuarioENUM.SALAO);
                        }
                    },500);
                }
                break;
            case TipoUsuarioENUM.PROFISSIONAl:
                if (regrasDeNegocio.containsKey("ControladorCodigoProfissional")){
                    auxCodUnico = regrasDeNegocio.get("ControladorCodigoProfissional") + 1;
                    refRegrasDeNegocio.child("ControladorCodigoProfissional").setValue(auxCodUnico, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        //gerarCodigoUnico(TipoUsuarioENUM.PROFISSIONAl);
                                    }
                                },500);
                            }else {
                                Map<String,Object> childUpdates = new HashMap<String, Object>();
                                cadastroBasico.setCodigoUnico(LibraryClass.formatarCodUnico(auxCodUnico));
                                if (etapa1Salva && etapa2Salva && etapa3Salva){
                                    cadastroBasico.setNivelUsuario(3.0);
                                }else{
                                    cadastroBasico.setNivelUsuario(2.1);
                                }

                                childUpdates.put(CadastroBasico.getCADASTRO_BASICO(),cadastroBasico.toMap());
                                refUser.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null){
                                            cadastroBasico.setNivelUsuario(2.0);
                                            // gerarCodigoUnico(TipoUsuarioENUM.PROFISSIONAl);
                                        }else{
                                            if (cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() == 2.1){
                                                if (etapa1Salva && etapa2Salva && etapa3Salva){
                                                    salvarCadastroBasico();
                                                }
                                            }else if (cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() == 3.0){
                                                callHomeActivity();
                                            }

                                        }
                                    }
                                });
                            }
                        }
                    });
                }else{
                    this.handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //gerarCodigoUnico(TipoUsuarioENUM.PROFISSIONAl);
                        }
                    },500);
                }
                break;
        }*/
    }

    public void manterObjetosAtualizados(){
        this.refCadastroComplementar.addChildEventListener(this.childEventListenerCadastroComplementar);
        this.refFuncionamentoSalao.addChildEventListener(this.childEventListenerFuncionamentoSalao);
    }

    private void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }


    //ATUALIZADORES FIREBASE
    public void adicionaFuncionamentoFirebase(String dia){
        Map<String, Object> childUpdates = new HashMap<>();
        switch (dia){
            case DiasENUM.SEGUNDA:
                childUpdates.put(DiasENUM.SEGUNDA, ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).toMap());
                this.refFuncionamentoSalao.updateChildren(childUpdates);
                break;
            case DiasENUM.TERCA:
                childUpdates.put(DiasENUM.TERCA, ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).toMap());
                this.refFuncionamentoSalao.updateChildren(childUpdates);
                break;
            case DiasENUM.QUARTA:
                childUpdates.put(DiasENUM.QUARTA, ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).toMap());
                this.refFuncionamentoSalao.updateChildren(childUpdates);
                break;
            case DiasENUM.QUINTA:
                childUpdates.put(DiasENUM.QUINTA, ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).toMap());
                this.refFuncionamentoSalao.updateChildren(childUpdates);
                break;
            case DiasENUM.SEXTA:
                childUpdates.put(DiasENUM.SEXTA, ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).toMap());
                this.refFuncionamentoSalao.updateChildren(childUpdates);
                break;
            case DiasENUM.SABADO:
                childUpdates.put(DiasENUM.SABADO, ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).toMap());
                this.refFuncionamentoSalao.updateChildren(childUpdates);
                break;
            case DiasENUM.DOMINGO:
                childUpdates.put(DiasENUM.DOMINGO, ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).toMap());
                this.refFuncionamentoSalao.updateChildren(childUpdates);
                break;
            default:
                break;
        }
    }

    public void removeFuncionamentoFirebase(String dia){
        this.refFuncionamentoSalao.child(dia).removeValue();
    }

    /*public void saveEtapaFirebase(int etapa){
        if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
            switch (etapa){
                case 1:
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(CadastroComplementar.getCADASTRO_COMPLEMENTAR(), cadastroComplementar.toMap());
                    HashMap<String, Map<String,Object>> funcionamentos = new HashMap<>();
                    for (String key : funcionamentoSalao.getFuncionamentoDoSalao().keySet()){
                        funcionamentos.put(key,funcionamentoSalao.getFuncionamentoDoSalao().get(key).toMap());
                    }
                    childUpdates.put(GeralENUM.FUNCIONAMENTO, funcionamentos);
                    this.refUser.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                etapa1Preenchida = false;
                                etapa1Salva = false;
                                if (FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                    ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).liberarFab();
                                }
                                if ((!etapa2Preenchida || etapa2Salva) && (!etapa3Preenchida || etapa3Salva)){
                                    mViewPager.setCurrentItem(1);
                                    showToast("Salvar novamente!");
                                }
                                progressDialog.dismiss();
                            }else{
                                etapa1Salva = true;
                                if (etapa2Preenchida && etapa2Salva && etapa3Preenchida && etapa3Salva && codUnicoObtido){
                                    salvarCadastroBasico();
                                }else {
                                    if (FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                                        ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).liberarFab();
                                    }
                                }
                            }
                        }
                    });
                    break;
                case 2:
                    break;
                case 3:
                    break;
                default:
                    break;
            }

        }
    }*/

    private void salvarCadastroBasico(){
        cadastroBasico.setNivelUsuario(3.0);
        Map<String,Object> childUpdates = new HashMap<String, Object>();
        childUpdates.put(CadastroBasico.getCADASTRO_BASICO(),cadastroBasico.toMap());
        refUser.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    salvarCadastroBasico();
                }else{
                    if (cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() != 3.0){
                        salvarCadastroBasico();
                    }else{
                        callHomeActivity();
                    }
                }
            }
        });
    }

    public void adicionarServicoFirebase(String idServico){
        if (servicosSalao.getServicosSalao().containsKey(idServico)){
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(idServico, servicosSalao.getServicosSalao().get(idServico).toMap());
            this.refServicosSalao.updateChildren(childUpdates);
        }
    }

    //PROGRESS DIALOG
    public void showProgressDialog(boolean exibir){
        if (exibir){
            this.progressDialog.show();
        }else {
            this.progressDialog.dismiss();
        }
    }


    //CALL
    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void recriarCadastroInicialActivity(){
        /*Intent intent = new Intent(this,ConfiguracaoInicialActivity.class);
        Bundle bundle = new Bundle();
        if (cadastroBasico.getNivelUsuario() != null){
            bundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
        }
        if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
            bundle.putString(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
        }
        intent.putExtras(bundle);
        finish();
        startActivity(intent);*/
        Intent intent = new Intent(this,ConfiguracaoInicialActivity.class);
        Bundle auxBundle = new Bundle();
        Bundle bundle = new Bundle();
        auxBundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
        auxBundle.putString(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
        if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty()){
            auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
        }
        bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }

    public void callHomeActivity(){
        Intent intent = new Intent(this, HomeActivity.class);
        Bundle auxBundle = new Bundle();
        Bundle bundle = new Bundle();
        auxBundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
        auxBundle.putString(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
        if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty()){
            auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
        }
        bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
        intent.putExtras(bundle);
        startActivity(intent);
        finish();
    }


    //BUTTONS
    public void confirmarTipoCadastro(View view) {
        if (!this.processandoClique){
            this.processandoClique = true;
            switch (view.getId()){
                case R.id.btn_cadastro_cliente:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.CLIENTE);
                    refCadastroBasico.child(CadastroBasico.getTIPO_USUARIO()).setValue(TipoUsuarioENUM.CLIENTE);
                    break;
                case R.id.btn_cadastro_salao:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.SALAO);
                    refCadastroBasico.child(CadastroBasico.getTIPO_USUARIO()).setValue(TipoUsuarioENUM.SALAO);
                    break;
                case R.id.btn_cadastro_profissional:
                    cadastroBasico.setTipoUsuario(TipoUsuarioENUM.PROFISSIONAl);
                    refCadastroBasico.child(CadastroBasico.getTIPO_USUARIO()).setValue(TipoUsuarioENUM.PROFISSIONAl);
                    break;
                default:
                    break;
            }



            if (this.builder == null){
                this.builder = new AlertDialog.Builder(this);
                //define um botão como positivo
                builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        cadastroBasico.setNivelUsuario(2.0);
                        refCadastroBasico.child(CadastroBasico.getNIVEL_USUARIO()).setValue(cadastroBasico.getNivelUsuario());
                        recriarCadastroInicialActivity();
                    }
                });
                //define um botão como negativo.
                builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        processandoClique = false;
                        cadastroBasico.setTipoUsuario(null);
                        refCadastroBasico.child(CadastroBasico.getTIPO_USUARIO()).removeValue();
                    }
                });
            }

            switch (view.getId()){
                case R.id.btn_cadastro_cliente:
                    builder.setTitle("Salvar cadastro como Cliente ?");
                    builder.setMessage("Ao criar uma conta como Cliente você podera se vincular a um ou mais salões online, para ter acesso a promoções, agendar horários com seus cabeleireiros e muito mais !");
                    break;
                case R.id.btn_cadastro_salao:
                    builder.setTitle("Salvar cadastro como Salão ?");
                    builder.setMessage("Ao criar uma conta como Salão você estara abrindo um salão online; podendo definir os serviços prestados, abrir uma agenda para que seus clientes possam agendar horários, adicionar os cabeleireiros que realizaram os serviços no seu salão, gerar promoções e muito mais !");
                    break;
                case R.id.btn_cadastro_profissional:
                    builder.setTitle("Salvar cadastro como Profissional ?");
                    builder.setMessage("Ao criar uma conta como Profissional você podera se vincular a um ou mais salões online ja existentes, os clientes destes salões poderam agendar horarios com você, você podera gerenciar seus serviços prestados no decorrer do mês e muito mais !");
                    break;
                default:
                    break;
            }

            this.alertDialog = builder.create();
            this.alertDialog.show();
        }
    }

    public void definirHorarioAbertura(View view) {
        auxViewIdTimePicker = view.getId();
        if (this.timePickerDialogAbertura == null){
            this.timePickerDialogAbertura = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String horario = "";
                    if (hourOfDay <= 9 && minute <= 9){
                        horario = (new StringBuilder().append("0").append(hourOfDay).append(":").append("0").append(minute)).toString();
                    }else if(hourOfDay <= 9){
                        horario = (new StringBuilder().append("0").append(hourOfDay).append(":").append(minute)).toString();
                    }else if(minute <= 9){
                        horario = (new StringBuilder().append(hourOfDay).append(":").append("0").append(minute)).toString();
                    }else{
                        horario = (new StringBuilder().append(hourOfDay).append(":").append(minute)).toString();
                    }

                    switch (auxViewIdTimePicker){
                        case R.id.abre_segunda:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).setAbre(horario);
                            refFuncionamentoSalao.child(DiasENUM.SEGUNDA).child(DiasENUM.ABRE).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.abre_terca:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).setAbre(horario);
                            refFuncionamentoSalao.child(DiasENUM.TERCA).child(DiasENUM.ABRE).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.abre_quarta:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).setAbre(horario);
                            refFuncionamentoSalao.child(DiasENUM.QUARTA).child(DiasENUM.ABRE).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.abre_quinta:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).setAbre(horario);
                            refFuncionamentoSalao.child(DiasENUM.QUINTA).child(DiasENUM.ABRE).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.abre_sexta:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).setAbre(horario);
                            refFuncionamentoSalao.child(DiasENUM.SEXTA).child(DiasENUM.ABRE).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.abre_sabado:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).setAbre(horario);
                            refFuncionamentoSalao.child(DiasENUM.SABADO).child(DiasENUM.ABRE).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.abre_domingo:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).setAbre(horario);
                            refFuncionamentoSalao.child(DiasENUM.DOMINGO).child(DiasENUM.ABRE).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        default:
                            break;
                    }
                }
            },0,0,true);
        }
        this.timePickerDialogAbertura.show();
    }

    public void definirHorarioFechamento(View view) {
        auxViewIdTimePicker = view.getId();
        if (this.timePickerDialogFechamento == null){
            this.timePickerDialogFechamento = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String horario = (new StringBuilder().append(hourOfDay).append(":").append(minute)).toString();
                    switch (auxViewIdTimePicker){
                        case R.id.fecha_segunda:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).setFecha(horario);
                            refFuncionamentoSalao.child(DiasENUM.SEGUNDA).child(DiasENUM.FECHA).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.fecha_terca:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).setFecha(horario);
                            refFuncionamentoSalao.child(DiasENUM.TERCA).child(DiasENUM.FECHA).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.fecha_quarta:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).setFecha(horario);
                            refFuncionamentoSalao.child(DiasENUM.QUARTA).child(DiasENUM.FECHA).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.fecha_quinta:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).setFecha(horario);
                            refFuncionamentoSalao.child(DiasENUM.QUINTA).child(DiasENUM.FECHA).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.fecha_sexta:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).setFecha(horario);
                            refFuncionamentoSalao.child(DiasENUM.SEXTA).child(DiasENUM.FECHA).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.fecha_sabado:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).setFecha(horario);
                            refFuncionamentoSalao.child(DiasENUM.SABADO).child(DiasENUM.FECHA).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        case R.id.fecha_domingo:
                            funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).setFecha(horario);
                            refFuncionamentoSalao.child(DiasENUM.DOMINGO).child(DiasENUM.FECHA).setValue(horario);
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).setHorarioTextView(auxViewIdTimePicker,horario);
                            break;
                        default:
                            break;
                    }
                }
            },23,59,true);
        }
        this.timePickerDialogFechamento.show();
    }

    public void selecionaDia(View view) {
        ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialAdapter)this.mViewPager.getAdapter()).getFragment(0)).diaSelecionado((CheckBox) view);
    }

    public void addServico(View view) {
        ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).criarServico();
    }

    public void removerServico(View view) {
        ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(1)).removeList();

    }


    //GETTERS SETTERS
    public ViewPager getmViewPager() {
        return mViewPager;
    }

    public static FuncionamentoSalao getFuncionamentoSalao() {
        return funcionamentoSalao;
    }
    public static void setFuncionamentoSalao(FuncionamentoSalao funcionamentoSalao) {
        ConfiguracaoInicialActivity.funcionamentoSalao = funcionamentoSalao;
    }

    public static ServicosSalao getServicosSalao() {
        return servicosSalao;
    }

    public static CadastroComplementar getCadastroComplementar() {
        return cadastroComplementar;
    }

    public static boolean isCadastroComplementarObtido() {
        return cadastroComplementarObtido;
    }

    public static boolean isFuncionamentoSalaoObtido() {
        return funcionamentoSalaoObtido;
    }

    public static boolean isEtapa1Preenchida() {
        return etapa1Preenchida;
    }
    public static void setEtapa1Preenchida(boolean etapa1Preenchida) {
        ConfiguracaoInicialActivity.etapa1Preenchida = etapa1Preenchida;
    }

    public static boolean isEtapa1Salva() {
        return etapa1Salva;
    }
    public static void setEtapa1Salva(boolean etapa1Salva) {
        ConfiguracaoInicialActivity.etapa1Salva = etapa1Salva;
    }

    public static boolean isEtapa2Preenchida() {
        return etapa2Preenchida;
    }
    public static void setEtapa2Preenchida(boolean etapa2Preenchida) {
        ConfiguracaoInicialActivity.etapa2Preenchida = etapa2Preenchida;
    }

    public static boolean isEtapa2Salva() {
        return etapa2Salva;
    }
    public static void setEtapa2Salva(boolean etapa2Salva) {
        ConfiguracaoInicialActivity.etapa2Salva = etapa2Salva;
    }

    public static boolean isEtapa3Preenchida() {
        return etapa3Preenchida;
    }
    public static void setEtapa3Preenchida(boolean etapa3Preenchida) {
        ConfiguracaoInicialActivity.etapa3Preenchida = etapa3Preenchida;
    }

    public static boolean isEtapa3Salva() {
        return etapa3Salva;
    }
    public static void setEtapa3Salva(boolean etapa3Salva) {
        ConfiguracaoInicialActivity.etapa3Salva = etapa3Salva;
    }

    public DatabaseReference getRefServicosSalao() {
        return refServicosSalao;
    }

    public DatabaseReference getRefCadastroComplementar() {
        return refCadastroComplementar;
    }

    public static void setCadastroComplementar(CadastroComplementar cadastroComplementar) {
        ConfiguracaoInicialActivity.cadastroComplementar = cadastroComplementar;
    }

    //TESTES
    public void teste(){
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(DiasENUM.SEGUNDA, new Funcionamento(DiasENUM.SEGUNDA,"00:00","00:00").toMap());
        childUpdates.put(DiasENUM.TERCA, new Funcionamento(DiasENUM.TERCA,"00:00","00:00").toMap());
        refFuncionamentoSalao.updateChildren(childUpdates);
    }

    public void teste2(){
        /*if (this.refUser == null){
            this.refUser = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid());
        }
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> a  = new HashMap<>();
        a.put("nome","lucas");
        childUpdates.put("pasta1", a);
        /*HashMap<String,HashMap<String,Object>> funcionamentos = new HashMap<>();
        HashMap<String,Object> funcionamento = new HashMap<>();
        funcionamento.put("abre","00");
        funcionamentos.put("segunda",funcionamento);
        childUpdates.put("pasta2",funcionamentos);

        this.refUser.updateChildren(childUpdates);*/

        /*Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> pasta1 = new HashMap<>();
        pasta1.put(DiasENUM.SEGUNDA, new Funcionamento(DiasENUM.SEGUNDA,"00:00","00:00").toMap());
        pasta1.put(DiasENUM.TERCA, new Funcionamento(DiasENUM.TERCA,"00:00","00:00").toMap());
        childUpdates.put("pasta1",pasta1);
        refFuncionamentoSalao.updateChildren(childUpdates);*/

        /*if (this.refUser == null){
            this.refUser = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid());
        }
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(DiasENUM.SEGUNDA, new Funcionamento(DiasENUM.SEGUNDA,"00:00","00:00").toMap());
        childUpdates.put(DiasENUM.TERCA, new Funcionamento(DiasENUM.TERCA,"00:00","00:00").toMap());
        refUser.updateChildren(childUpdates);*/

        /*Log.i("fire","teste2");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(DiasENUM.SEGUNDA, new Funcionamento(DiasENUM.SEGUNDA,"00:00","00:00").toMap());
        childUpdates.put(DiasENUM.TERCA, new Funcionamento(DiasENUM.TERCA,"00:00","00:00").toMap());
        refFuncionamentoSalao.updateChildren(childUpdates);*/


        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> a  = new HashMap<>();
        a.put("nome","lucas");
        childUpdates.put("pasta1", a);
        HashMap<String,HashMap<String,Object>> funcionamentos = new HashMap<>();
        HashMap<String,Object> funcionamento = new HashMap<>();
        funcionamento.put("abre","00");
        funcionamentos.put("segunda",funcionamento);
        childUpdates.put("pasta2",funcionamentos);

        this.refFuncionamentoSalao.updateChildren(childUpdates);
    }


    public void addProf(View view) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("nome","lucas");
        childUpdates.put("aaaa",true);
        childUpdates.put("uidSalão","xooWyYJq5DVmpDU5CTBioozu3723");
        this.refProfissionaisSalao.child(this.refProfissionaisSalao.push().getKey()).updateChildren(childUpdates);
    }

    public void buscarProf(View view) {
        this.refProfissionaisSalao.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Log.i("dataSnapshot",dataSnapshot.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("dataSnapshot","onCancelled");
            }
        });
    }
}

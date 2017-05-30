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
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCliente;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentProfissionais;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentServicos;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentTipoCadastro;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.adapters.ConfiguracaoInicialAdapter;
import com.example.lucas.salao20.geral.CadastroComplementar;
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

public class CadastroInicialActivity extends AppCompatActivity implements DatabaseReference.CompletionListener{
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //ENUMS
    private static final String SALVAR_NIVEL_TIPO = "CadastroInicialActivity.SalvarNivelTipoUsuario";

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE REF
    private DatabaseReference refCadastroBasico;
    private DatabaseReference refCadastroComplementar;
    private DatabaseReference refFuncionamento;
    private DatabaseReference refServicos;
    private DatabaseReference refProfissionais;


    //FIREBASE VEL
    private ValueEventListener valueEventListenerFuncionamentoSalao;
    private ChildEventListener childEventListenerFuncionamentoSalao;
    private ChildEventListener childEventListenerServicosSalao;
    private ChildEventListener childEventListenerProfissionaisSalao;
    private ChildEventListener childEventListenerCadastroComplementar;
    private ValueEventListener valueEventListenerCadastroComplementar;

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


    //OBJETOS
    private static CadastroBasico cadastroBasico = null;
    private static FuncionamentoSalao funcionamentoSalao = null;
    private static ServicosSalao servicosSalao = null;
    private static ProfissionaisSalao profissionaisSalao = null;
    private static CadastroComplementar cadastroComplementar = null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","CadastroInicialActivity() onCreate()");

        setContentView(R.layout.activity_cadastro_inicial);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();

        initControles();
        receberBundle();
        initDados();

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

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        //TODO
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
                        case TipoUsuarioENUM.PROFISSIONAl:
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
    }

    private void initDados(){
        if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
            if (this.refCadastroComplementar == null){
                this.refCadastroComplementar = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroComplementar.getCADASTRO_COMPLEMENTAR());
                this.refCadastroComplementar.keepSynced(true);
            }
            if (this.refCadastroBasico == null){
                this.refCadastroBasico = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroBasico.getCADASTRO_BASICO());
            }
            if (this.childEventListenerCadastroComplementar == null){
                this.childEventListenerCadastroComplementar = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()){
                            if (cadastroComplementar == null){
                                cadastroComplementar = new CadastroComplementar();
                            }
                            if (dataSnapshot.hasChild(CadastroComplementar.getNOME())){
                               cadastroComplementar.setNome(dataSnapshot.child(CadastroComplementar.getNOME()).getValue(String.class));
                           }
                        }else{
                            if (cadastroComplementar == null){
                                cadastroComplementar = new CadastroComplementar();
                            }
                        }
                        if (mViewPager.getCurrentItem() == 0){
                            ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).aplicarDadosFormulario();
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.hasChild(CadastroComplementar.getNOME())){
                                cadastroComplementar.setNome(dataSnapshot.child(CadastroComplementar.getNOME()).getValue(String.class));
                            }
                            if (mViewPager.getCurrentItem() == 0){
                                ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).aplicarDadosFormulario();
                            }
                        }
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.hasChild(CadastroComplementar.getNOME())){
                                cadastroComplementar.setNome(null);
                            }
                            if (mViewPager.getCurrentItem() == 0){
                                ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).aplicarDadosFormulario();
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
            if (this.valueEventListenerCadastroComplementar == null){
                this.valueEventListenerCadastroComplementar = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            if (cadastroComplementar == null){
                                cadastroComplementar = new CadastroComplementar();
                            }
                            if (dataSnapshot.hasChild(CadastroComplementar.getNOME())){
                                cadastroComplementar.setNome(dataSnapshot.child(CadastroComplementar.getNOME()).getValue(String.class));
                            }
                        }else{
                            if (cadastroComplementar == null){
                                cadastroComplementar = new CadastroComplementar();
                            }
                        }
                        cadastroComplementarObtido = true;
                        liberarPreenchimentoFragmentFuncionamento();
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
                        if (this.refFuncionamento == null){
                            this.refFuncionamento = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.FUNCIONAMENTO);
                            this.refFuncionamento.keepSynced(true);
                        }
                        if (this.refServicos == null){
                            this.refServicos = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.SERVICOS);
                            this.refServicos.keepSynced(true);
                        }
                        if (this.refProfissionais == null){
                            this.refProfissionais = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.PROFISSIONAIS);
                            this.refProfissionais.keepSynced(true);
                        }
                        if (cadastroBasico.getCodigoUnico() == null || cadastroBasico.getCodigoUnico().isEmpty()){
                            gerarCodigoUnico(TipoUsuarioENUM.SALAO);
                        }

                        if (this.childEventListenerFuncionamentoSalao == null){
                            this.childEventListenerFuncionamentoSalao = new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()){
                                        if (funcionamentoSalao == null){
                                            funcionamentoSalao = new FuncionamentoSalao();
                                            funcionamentoSalao.setFuncionamentoDoSalao(new HashMap<String, Funcionamento>());
                                        }

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Funcionamento funcionamento = new Funcionamento();
                                            funcionamento.setDia(child.getKey());
                                            if (child.hasChild(DiasENUM.ABRE)){
                                                funcionamento.setAbre(child.child(DiasENUM.ABRE).getValue(String.class));
                                            }
                                            if (child.hasChild(DiasENUM.FECHA)){
                                                funcionamento.setFecha(child.child(DiasENUM.FECHA).getValue(String.class));
                                            }
                                            funcionamentoSalao.addFuncionamento(funcionamento);
                                        }
                                    }else{
                                        if (funcionamentoSalao == null){
                                            funcionamentoSalao = new FuncionamentoSalao();
                                            funcionamentoSalao.setFuncionamentoDoSalao(new HashMap<String, Funcionamento>());
                                        }
                                    }
                                    if (mViewPager.getCurrentItem() == 0){
                                        ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).aplicarDadosFormulario();
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            if (child.hasChild(DiasENUM.ABRE)){
                                                funcionamentoSalao.getFuncionamentoDoSalao().get(child.getKey()).setAbre(child.child(DiasENUM.ABRE).getValue(String.class));
                                            }
                                            if (child.hasChild(DiasENUM.FECHA)){
                                                funcionamentoSalao.getFuncionamentoDoSalao().get(child.getKey()).setFecha(child.child(DiasENUM.FECHA).getValue(String.class));
                                            }
                                        }
                                        if (mViewPager.getCurrentItem() == 0){
                                            ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).aplicarDadosFormulario();
                                        }
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()){
                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            if (child.hasChild(DiasENUM.ABRE) && child.hasChild(DiasENUM.FECHA)){
                                                funcionamentoSalao.removerFuncionamento(child.getKey());
                                            }else if (child.hasChild(DiasENUM.ABRE)){
                                                funcionamentoSalao.getFuncionamentoDoSalao().get(child.getKey()).setAbre(null);
                                            }else if (child.hasChild(DiasENUM.FECHA)){
                                                funcionamentoSalao.getFuncionamentoDoSalao().get(child.getKey()).setFecha(null);
                                            }
                                        }
                                        if (mViewPager.getCurrentItem() == 0){
                                            ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)mViewPager.getAdapter()).getFragment(0)).aplicarDadosFormulario();
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
                                    if (dataSnapshot.exists()){
                                        if (funcionamentoSalao == null){
                                            funcionamentoSalao = new FuncionamentoSalao();
                                            funcionamentoSalao.setFuncionamentoDoSalao(new HashMap<String, Funcionamento>());
                                        }

                                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                                            Funcionamento funcionamento = new Funcionamento();
                                            funcionamento.setDia(child.getKey());
                                            if (child.hasChild(DiasENUM.ABRE)){
                                                funcionamento.setAbre(child.child(DiasENUM.ABRE).getValue(String.class));
                                            }
                                            if (child.hasChild(DiasENUM.FECHA)){
                                                funcionamento.setFecha(child.child(DiasENUM.FECHA).getValue(String.class));
                                            }
                                            funcionamentoSalao.addFuncionamento(funcionamento);
                                        }
                                    }else{
                                        if (funcionamentoSalao == null){
                                            funcionamentoSalao = new FuncionamentoSalao();
                                            funcionamentoSalao.setFuncionamentoDoSalao(new HashMap<String, Funcionamento>());
                                        }
                                    }
                                    funcionamentoSalaoObtido = true;
                                    liberarPreenchimentoFragmentFuncionamento();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            };
                        }
                        this.refFuncionamento.addListenerForSingleValueEvent(this.valueEventListenerFuncionamentoSalao);
                        break;
                    case TipoUsuarioENUM.PROFISSIONAl:
                        //TODO
                        gerarCodigoUnico(TipoUsuarioENUM.PROFISSIONAl);
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
        if (this.refFuncionamento != null){
            this.refFuncionamento.keepSynced(false);
        }
        if (this.refCadastroComplementar != null){
            this.refCadastroComplementar.keepSynced(false);
        }

        if (this.childEventListenerFuncionamentoSalao != null){
            this.refFuncionamento.removeEventListener(this.childEventListenerFuncionamentoSalao);
        }
        if (this.childEventListenerCadastroComplementar != null){
            this.refCadastroComplementar.removeEventListener(this.childEventListenerCadastroComplementar);
        }
        if (this.valueEventListenerFuncionamentoSalao != null){
            this.refFuncionamento.removeEventListener(this.valueEventListenerFuncionamentoSalao);
        }
        if (this.valueEventListenerCadastroComplementar != null){
            this.refCadastroComplementar.removeEventListener(this.valueEventListenerCadastroComplementar);
        }
    }

    private void gerarCodigoUnico(String tipoUsuario){
        switch (tipoUsuario){
            case TipoUsuarioENUM.SALAO:
                //TODO
                break;
            case TipoUsuarioENUM.PROFISSIONAl:
                //TODO
                break;
            default:
                mAuth.signOut();
                break;
        }
    }

    public void manterObjetosAtualizados(){
        this.refCadastroComplementar.addChildEventListener(this.childEventListenerCadastroComplementar);
        this.refFuncionamento.addChildEventListener(this.childEventListenerFuncionamentoSalao);
    }


    //ATUALIZADORES FIREBASE
    public void adicionaFuncionamentoFirebase(String dia){
        Map<String, Object> childUpdates = new HashMap<>();
        switch (dia){
            case DiasENUM.SEGUNDA:
                childUpdates.put(DiasENUM.SEGUNDA, CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).toMap());
                this.refFuncionamento.updateChildren(childUpdates);
                break;
            case DiasENUM.TERCA:
                childUpdates.put(DiasENUM.TERCA, CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).toMap());
                this.refFuncionamento.updateChildren(childUpdates);
                break;
            case DiasENUM.QUARTA:
                childUpdates.put(DiasENUM.QUARTA, CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).toMap());
                this.refFuncionamento.updateChildren(childUpdates);
                break;
            case DiasENUM.QUINTA:
                childUpdates.put(DiasENUM.QUINTA, CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).toMap());
                this.refFuncionamento.updateChildren(childUpdates);
                break;
            case DiasENUM.SEXTA:
                childUpdates.put(DiasENUM.SEXTA, CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).toMap());
                this.refFuncionamento.updateChildren(childUpdates);
                break;
            case DiasENUM.SABADO:
                childUpdates.put(DiasENUM.SABADO, CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).toMap());
                this.refFuncionamento.updateChildren(childUpdates);
                break;
            case DiasENUM.DOMINGO:
                childUpdates.put(DiasENUM.DOMINGO, CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).toMap());
                this.refFuncionamento.updateChildren(childUpdates);
                break;
            default:
                break;
        }
    }

    public void removeFuncionamentoFirebase(String dia){
        this.refFuncionamento.child(dia).removeValue();
    }


    //LIBERARDOR DE FORMULARIOS
    private  void liberarPreenchimentoFragmentFuncionamento(){
        if (funcionamentoSalaoObtido && cadastroComplementarObtido){
            if (this.mViewPager.getCurrentItem() == 0){
                ((FragmentFuncionamento)((ConfiguracaoInicialAdapter)this.mViewPager.getAdapter()).getFragment(0)).aplicarDadosFormulario();
            }
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
        /*Intent intent = new Intent(this,CadastroInicialActivity.class);
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
        Intent intent = new Intent(this,CadastroInicialActivity.class);
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
                case R.id.btn_cadastro_cabeleireiro:
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
                case R.id.btn_cadastro_cabeleireiro:
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

    public static CadastroComplementar getCadastroComplementar() {
        return cadastroComplementar;
    }

    public static boolean isCadastroComplementarObtido() {
        return cadastroComplementarObtido;
    }

    public static boolean isFuncionamentoSalaoObtido() {
        return funcionamentoSalaoObtido;
    }
}

package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.lucas.salao20.R;
import com.example.lucas.salao20.adapters.ConfiguracaoInicialSalaoAdapter;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoProfissionais;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoServicos;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.geral.CadastroComplementar;
import com.example.lucas.salao20.slidingTabLayout.SlidingTabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracaoInicialSalaoActivity extends AppCompatActivity{
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //EDITTEXT
    private EditText nomeSalao;

    //HANDLER
    private Handler handler;


    //ENUMS


    //  FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE REF
    private DatabaseReference refRaiz;
    private DatabaseReference refUser;
    private DatabaseReference refCadastroComplementar;



    //FIREBASE VEL
    private ValueEventListener valueEventListenerNomeSalao;

    //ALERT DIALOG
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    //PROGRESDIALOG
    private ProgressDialog progressDialog;

    //CONTROLES
    private boolean funcionamentoSalaoOk;
    private boolean servicosSalaoOk;
    private boolean profissionaisSalaoOk;

    //OBJETOS
    private  CadastroBasico cadastroBasico = null;
    private  CadastroComplementar cadastroComplementar = null;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","ConfiguracaoInicialSalaoActivity() onCreate()");

        setContentView(R.layout.activity_configuracao_inicial_salao);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        mAuth.addAuthStateListener(mAuthListener);

        this.handler  = new Handler();



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
        Log.i("script","ConfiguracaoInicialSalaoActivity() onStart()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","ConfiguracaoInicialSalaoActivity() onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","ConfiguracaoInicialSalaoActivity() onDestroy()");
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

    /*@Override
    public Object evento(String evento) {
        if (evento == CadastroBasico.getCADASTRO_BASICO()){
            return cadastroBasico;
        }else{
            return null;
        }
    }*/

    private void initView() {
        if (cadastroBasico == null || cadastroBasico.getNivelUsuario() == null || cadastroBasico.getNivelUsuario() < 2.0 || cadastroBasico.getNivelUsuario() >= 3.0 || cadastroBasico.getTipoUsuario() == null || !cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.SALAO) || cadastroBasico.getCodigoUnico() == null || cadastroBasico.getCodigoUnico().isEmpty()){
            this.mAuth.signOut();
        }else {
            //EDITTEXT
            this.nomeSalao = new EditText(this);
            this.nomeSalao.setHint("Digite o nome do salão");
            mToolbar = (Toolbar) findViewById(R.id.toolbar_tabs);
            mViewPager = (ViewPager) findViewById(R.id.vp_tabs_tabs);
            //TOOLBAR
            mToolbar.setTitle("CONFIGURAÇÃO DO SALÃO");
            mToolbar.setLogo(R.mipmap.ic_launcher);
            mToolbar.setSubtitle("Código Do Salão #"+cadastroBasico.getCodigoUnico());
            setSupportActionBar(mToolbar);
            if (cadastroBasico.getNivelUsuario() > 2.0){
                if (this.refCadastroComplementar == null){
                    this.refCadastroComplementar = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(cadastroBasico.getUserMetadataUid()).child(CadastroComplementar.getCADASTRO_COMPLEMENTAR());
                }
                if (this.valueEventListenerNomeSalao == null){
                    this.valueEventListenerNomeSalao = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists() && dataSnapshot.hasChild(CadastroComplementar.getNOME_SALAO()) && dataSnapshot.child(CadastroComplementar.getNOME_SALAO()).getValue(String.class) != null && !dataSnapshot.child(CadastroComplementar.getNOME_SALAO()).getValue(String.class).isEmpty()){
                                if (cadastroComplementar == null){
                                    cadastroComplementar = new CadastroComplementar();
                                }
                                cadastroComplementar.setNomeSalao(dataSnapshot.child(CadastroComplementar.getNOME_SALAO()).getValue(String.class));
                                mToolbar.setTitle(cadastroComplementar.getNomeSalao());
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };
                }
                this.refCadastroComplementar.addListenerForSingleValueEvent(this.valueEventListenerNomeSalao);

            }
            //TABS
            String[] titles = {FragmentConfiguracaoInicialSalaoFuncionamento.getTITULO(), FragmentConfiguracaoInicialSalaoServicos.getTITULO(), FragmentConfiguracaoInicialSalaoProfissionais.getTITULO()};
            mViewPager.setAdapter(new ConfiguracaoInicialSalaoAdapter(getSupportFragmentManager(),this,titles));

            //PROGREAS DIALOG
            if (this.progressDialog == null){
                this.progressDialog = new ProgressDialog(this);
                this.progressDialog.setCancelable(false);
                this.progressDialog.setMessage("Sincronizando dados na nuvem ...");
            }

            mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs_tabs);
            mSlidingTabLayout.setDistributeEvenly(true);
            mSlidingTabLayout.setViewPager(mViewPager);
            mSlidingTabLayout.setBackgroundColor( getResources().getColor( R.color.primary));
            mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.accent));

            //ALERT DIALOG
            if (cadastroBasico.getNivelUsuario() < 2.1){
                this.builder = new AlertDialog.Builder(this);
                //define um botão como positivo
                this.builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        salvarNome();
                    }
                });
                this.builder.setTitle("NOME DO SALÃO");
                this.builder.setMessage("O nome do salão será exibido no perfil do salão juntamente com o código do salão (#"+cadastroBasico.getCodigoUnico()+"), para que seus clientes possam identifica-lo");
                this.builder.setView(nomeSalao);
                this.alertDialog = builder.create();
                this.alertDialog.setCancelable(false);
                this.alertDialog.show();
            }

        }
    }

    private void initControles(){
        this.servicosSalaoOk = false;
        this.profissionaisSalaoOk = false;
        this.funcionamentoSalaoOk = false;
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
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
        if (cadastroBasico == null) {
            Log.i("script", "cadastroBasico == null");
            cadastroBasico = new CadastroBasico();
        }
        if (mAuth.getCurrentUser() == null){
            Log.i("script","inicio user null");
        }else{
            Log.i("script","inicio user not null");
            if (getIntent().hasExtra(CadastroBasico.getCADASTRO_BASICO())){
                Bundle bundle = getIntent().getExtras().getBundle(CadastroBasico.getCADASTRO_BASICO());

                if (bundle != null && bundle.containsKey(CadastroBasico.getTIPO_USUARIO())){
                    cadastroBasico.setTipoUsuario(bundle.getString(CadastroBasico.getTIPO_USUARIO()));
                }else {
                    Log.i("script", "tipo usuario == null");
                    mAuth.signOut();
                }
                if (bundle != null && bundle.containsKey(CadastroBasico.getCODIGO_UNICO())){
                    cadastroBasico.setCodigoUnico(bundle.getString(CadastroBasico.getCODIGO_UNICO()));
                }else {
                    Log.i("script", "cod unico == null");
                    mAuth.signOut();
                }
                if (bundle != null && bundle.containsKey(CadastroBasico.getNIVEL_USUARIO())){
                    cadastroBasico.setNivelUsuario(bundle.getDouble(CadastroBasico.getNIVEL_USUARIO()));
                }else {
                    Log.i("script", "nivel usuario == null");
                    mAuth.signOut();
                }
                if (bundle != null && bundle.containsKey(CadastroBasico.getUSER_METADATA_UID())){
                    cadastroBasico.setUserMetadataUid(bundle.getString(CadastroBasico.getUSER_METADATA_UID()));
                }else {
                    Log.i("script", "user metadata uid == null");
                    mAuth.signOut();
                }
            }else {
                Log.i("script", "extra == null");
                mAuth.signOut();
            }
        }
        exibirCadastroBAsico();
    }

    private void salvarNome(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (nomeSalao.getText() == null || nomeSalao.getText().toString().isEmpty()){
                    showToast("Insira um nome para o salão");
                    alertDialog.show();
                }else {
                    showProgressDialog(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nomeSalao.getWindowToken(), 0);
                    if (refRaiz == null){
                        refRaiz = LibraryClass.getFirebase();
                    }
                    Map<String,Object> updates = new HashMap<String,Object>();
                    updates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+cadastroBasico.getUserMetadataUid()+"/"+CadastroComplementar.getCADASTRO_COMPLEMENTAR()+"/"+CadastroComplementar.getNOME_SALAO(),nomeSalao.getText().toString());
                    updates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getNIVEL_USUARIO(),2.1);
                    refRaiz.updateChildren(updates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null){
                                if (cadastroComplementar == null){
                                    cadastroComplementar = new CadastroComplementar();
                                }
                                cadastroComplementar.setNomeSalao(nomeSalao.getText().toString());
                                mToolbar.setTitle(cadastroComplementar.getNomeSalao());
                                showProgressDialog(false);
                            }else {
                                showProgressDialog(false);
                                alertDialog.show();
                                showToast("Erro ao salvar nome!");
                            }
                        }
                    });
                }

            }
        });
    }

    private void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    private void removerFirebaseEvents(){
        //TODO
        if (this.refCadastroComplementar != null){
            this.refCadastroComplementar.keepSynced(false);
            if (this.valueEventListenerNomeSalao != null){
                this.refCadastroComplementar.removeEventListener(this.valueEventListenerNomeSalao);
            }
        }
    }

    private Bundle gerarBundle(){
        if (cadastroBasico != null && cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() != 0.0
                && cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty() && cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty()) {
            Bundle bundle = new Bundle();
            Bundle auxBundle = new Bundle();
            auxBundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
            auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
            auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
            bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
            return bundle;
        }else{
            return null;
        }
    }



    //ATUALIZADORES FIREBASE


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

    public void callHome(){
        Intent intent = new Intent(this, HomeSalaoActivity.class);
        Bundle bundle = gerarBundle();
        if (bundle != null){
            intent.putExtras(bundle);
        }
        startActivity(intent);
        finish();
    }


    //BUTTONS
    public void selecionaDia(View view) {
        ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialSalaoAdapter)this.mViewPager.getAdapter()).getFragment(0)).diaSelecionado((CheckBox) view);
    }

    public void definirHorarioAbertura(View view) {
        ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialSalaoAdapter)this.mViewPager.getAdapter()).getFragment(0)).definirHorarioAbertura(view);
    }

    public void definirHorarioFechamento(View view) {
        ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialSalaoAdapter)this.mViewPager.getAdapter()).getFragment(0)).definirHorarioFechamento(view);
    }

    public void adicionarServico(View view) {
        ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialSalaoAdapter)mViewPager.getAdapter()).getFragment(1)).criarServico();
    }

    public void descartarServico(View view) {
        ((FragmentConfiguracaoInicialSalaoServicos)((ConfiguracaoInicialSalaoAdapter)mViewPager.getAdapter()).getFragment(1)).limparCampos();
    }


    //GETTERS SETTERS
    public CadastroBasico getCadastroBasico() {
        return cadastroBasico;
    }




    public void addProf(View view) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("nome","lucas");
        childUpdates.put("aaaa",true);
        childUpdates.put("uidSalão","xooWyYJq5DVmpDU5CTBioozu3723");
       // this.refProfissionaisSalao.child(this.refProfissionaisSalao.push().getKey()).updateChildren(childUpdates);
    }

    public void buscarProf(View view) {
        /*this.refProfissionaisSalao.addValueEventListener(new ValueEventListener() {
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
        });*/
    }

    public void exibirCadastroBAsico(){
        String cadastroB = "CADASTRO BASICO \n";
        if (this.cadastroBasico.getTipoUsuario() != null && !this.cadastroBasico.getTipoUsuario().isEmpty()){
            cadastroB += "tipo user = "+this.cadastroBasico.getTipoUsuario()+"\n";
        }
        if (this.cadastroBasico.getCodigoUnico() != null){
            cadastroB += "cod unico = "+this.cadastroBasico.getCodigoUnico()+"\n";
        }
        if (this.cadastroBasico.getNivelUsuario() != null){
            cadastroB += "nivel user = "+this.cadastroBasico.getNivelUsuario().toString()+"\n";
        }
        if (this.cadastroBasico.getUserMetadataUid() != null && !this.cadastroBasico.getUserMetadataUid().isEmpty()){
            cadastroB += "usermetadatauid = "+this.cadastroBasico.getUserMetadataUid()+"\n";
        }
        if (this.cadastroBasico.getNome() != null && !this.cadastroBasico.getNome().isEmpty()){
            cadastroB += "nome = "+this.cadastroBasico.getNome()+"\n";
        }
        if (this.cadastroBasico.getSobrenome() != null && !this.cadastroBasico.getSobrenome().isEmpty()){
            cadastroB += "sobrenome = "+this.cadastroBasico.getSobrenome()+"\n";
        }
        Log.i("script", cadastroB);
    }

    //GETTERS SETTERS
    public boolean isFuncionamentoSalaoOk() {
        return funcionamentoSalaoOk;
    }
    public void setFuncionamentoSalaoOk(boolean funcionamentoSalaoOk) {
        this.funcionamentoSalaoOk = funcionamentoSalaoOk;
    }

    public boolean isServicosSalaoOk() {
        return servicosSalaoOk;
    }
    public void setServicosSalaoOk(boolean servicosSalaoOk) {
        this.servicosSalaoOk = servicosSalaoOk;
    }

    public boolean isProfissionaisSalaoOk() {
        return profissionaisSalaoOk;
    }
    public void setProfissionaisSalaoOk(boolean profissionaisSalaoOk) {
        this.profissionaisSalaoOk = profissionaisSalaoOk;
    }

    public ViewPager getmViewPager() {
        return mViewPager;
    }
}

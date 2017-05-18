package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.domain.User;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.geral.Teste;
import com.example.lucas.salao20.intentServices.BackgroundIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.Map;

/**
 * Created by Lucas on 17/03/2017.
 */

public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener{
    private User user;
    private Context context;

    private Toolbar mToolbar;
    private FloatingActionButton fab;

    //ALERT DIALOG
    private ProgressDialog progressDialog;

    //CONTROLE
    private boolean fabProcessando;
    private boolean loggedProcessando;
    private boolean loggedRespondeu;
    private static boolean loginActivityAtiva;

    //OBJETOS
    private CadastroBasico cadastroBasico;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference refCadastroBasico;
    private ValueEventListener valueEventListenerCadastroBasico;

    //CONTROLE
    private boolean iniciandoSplash = false;

    //HANDLER
    private Handler handler;
    private Runnable runnableLimiteUsuarioLogado;

    private ArrayList<Teste> arrayList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() LOGIN");

        setContentView(R.layout.activity_login);

        this.context = this;
        mAuth = FirebaseAuth.getInstance();
        initControles();
        initHandler();
        verifyLogged();


        initViews();
        initUser();

        mAuthListener = getFirebaseAuthResultHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("script","onResume() LOGIN");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() LOGIN");
        loginActivityAtiva = true;
        if (this.mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().isEmpty()){ //USAURIO LOGADO
            this.progressDialog.show();
        }else{//USAURIO NAO LOGADO
            if (BackgroundIntentService.isBackgroundIntentServiceAtivo()){
                BackgroundIntentService.getRefCadastrobasico().removeEventListener(valueEventListenerCadastroBasico);
                stopService(new Intent(getApplicationContext(),BackgroundIntentService.class));
            }
        }

        mAuth.addAuthStateListener( mAuthListener );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() LOGIN");
        if( mAuthListener != null ){
            mAuth.removeAuthStateListener( mAuthListener );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loginActivityAtiva = false;
        if (valueEventListenerCadastroBasico != null){
            if (BackgroundIntentService.isBackgroundIntentServiceAtivo()){
                BackgroundIntentService.getRefCadastrobasico().removeEventListener(valueEventListenerCadastroBasico);
            }
            refCadastroBasico.removeEventListener(valueEventListenerCadastroBasico);
        }
        if (handler != null){
            this.handler.removeCallbacksAndMessages(null);
        }
        Log.i("script","onDestroy() LOGIN");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login_activity,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_ajuda:
                Toast.makeText(this,"ajuda não implementada",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void initViews() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_login);
        mToolbar.setTitle("LOGIN");
        mToolbar.setSubtitle("tela de login");
        mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);

        //FLOATING ACTION BUTTON
        fab = (FloatingActionButton) findViewById(R.id.fab_login);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fabProcessando){
                    fabProcessando = true;
                    progressDialog.show();
                    if (validaFormulario()){
                        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginData()");
                        initUser();
                        verifyLogin();
                    }else {
                        fabProcessando = false;
                        progressDialog.dismiss();
                    }
                }

            }
        });

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Autenticando login ...");
        this.progressDialog.setCancelable(false);
    }

    @Override
    protected void initUser() {
        user = new User();
        user.setEmail( email.getText().toString() );
        user.setPassword( password.getText().toString() );
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        FirebaseCrash
                .report(
                        new Exception(
                                connectionResult.getErrorCode()+": "+connectionResult.getErrorMessage()
                        )
                );
        showSnackbar( connectionResult.getErrorMessage() );
    }

    private void initControles(){
        this.fabProcessando = false;
        this.loggedProcessando = false;
        this.loggedRespondeu = false;
    }

    private void initHandler(){
        this.handler = new Handler();
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() login ");

        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged login");
                if(mAuth.getCurrentUser() == null){
                    Log.i("script","getFirebaseAuthResultHandler() getCurrentUser() == null login");
                    if (loggedRespondeu){
                        loggedRespondeu = false;
                        loggedProcessando = false;
                        if (runnableLimiteUsuarioLogado != null){
                            handler.removeCallbacksAndMessages(null);
                            runnableLimiteUsuarioLogado = null;
                        }
                        if (valueEventListenerCadastroBasico != null){
                            if (BackgroundIntentService.isBackgroundIntentServiceAtivo()){
                                BackgroundIntentService.getRefCadastrobasico().removeEventListener(valueEventListenerCadastroBasico);
                            }
                            refCadastroBasico.removeEventListener(valueEventListenerCadastroBasico);
                        }
                        if (BackgroundIntentService.isBackgroundIntentServiceAtivo()){
                            stopService(new Intent(getApplicationContext(),BackgroundIntentService.class));
                        }
                        progressDialog.dismiss();
                        showSnackbar("Login falhou");
                    }
                }else if (mAuth.getCurrentUser().getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() uid == null login");
                    if (loggedProcessando){
                        loggedRespondeu = true;
                        loggedProcessando = false;
                    }
                    if (fabProcessando){//Usuario acabou de logar
                        fabProcessando = false;
                        progressDialog.dismiss();
                        showSnackbar("Login falhou");
                    }
                    mAuth.signOut();
                }else {
                    Log.i("script","getFirebaseAuthResultHandler() uid != null");
                    if (fabProcessando){//Usuario acabou de logar
                        callSplashScreenActivity();
                    }else{//Login foi criada com o user logado
                        if (runnableLimiteUsuarioLogado == null){
                            runnableLimiteUsuarioLogado = new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("script","runnableLimiteUsuarioLogado() limite de tempo atingido");
                                    loggedRespondeu = true;
                                    mAuth.signOut();
                                }
                            };
                            handler.postDelayed(runnableLimiteUsuarioLogado,5000);
                        }
                    }
                }
            }
        };
        return( callback );
    }

    private void verifyLogged(){
        Log.i("script","verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            if(!mAuth.getCurrentUser().getUid().isEmpty()){
                loggedProcessando = true;
                if (BackgroundIntentService.isBackgroundIntentServiceAtivo()){
                    if (valueEventListenerCadastroBasico == null){
                        valueEventListenerCadastroBasico = new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists() && dataSnapshot.getValue(CadastroBasico.class) != null){
                                    cadastroBasico = dataSnapshot.getValue(CadastroBasico.class);
                                    direcionarUsuario();
                                }else {
                                    loggedRespondeu = true;
                                    mAuth.signOut();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                loggedRespondeu = true;
                                mAuth.signOut();
                            }
                        };
                    }
                    BackgroundIntentService.getRefCadastrobasico().addListenerForSingleValueEvent(valueEventListenerCadastroBasico);
                }else{
                    if (refCadastroBasico == null){
                        if (valueEventListenerCadastroBasico == null){
                            valueEventListenerCadastroBasico = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    startService(new Intent(getApplicationContext(), BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoInicial(),true));
                                    cadastroBasico = dataSnapshot.getValue(CadastroBasico.class);
                                    direcionarUsuario();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    loggedRespondeu = true;
                                    mAuth.signOut();
                                }
                            };
                        }
                        this.refCadastroBasico = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroBasico.getCADASTRO_BASICO());
                        this.refCadastroBasico.addListenerForSingleValueEvent(valueEventListenerCadastroBasico);
                    }
                }
            }else{
                loggedRespondeu = true;
                mAuth.signOut();
            }
        }
    }

    private void direcionarUsuario(){
        if (this.cadastroBasico != null && this.cadastroBasico.getNivelUsuario() != null){
            Bundle bundle = new Bundle();
            if (this.cadastroBasico.getNivelUsuario() == 1.0){
                bundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),this.cadastroBasico.getNivelUsuario());
                callConfiguracaoIncialActivity(bundle);
            }else {
                if (this.cadastroBasico.getTipoUsuario() != null && !this.cadastroBasico.getTipoUsuario().isEmpty()){
                    switch (this.cadastroBasico.getTipoUsuario()){
                        case TipoUsuarioENUM.SALAO:
                            bundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),this.cadastroBasico.getNivelUsuario());
                            bundle.putString(CadastroBasico.getTIPO_USUARIO(),this.cadastroBasico.getTipoUsuario());
                            startService(new Intent(getApplicationContext(), BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoSalao(),true));
                            if (this.cadastroBasico.getNivelUsuario() >= 2.0 && this.cadastroBasico.getNivelUsuario() < 3.0){
                                callConfiguracaoIncialActivity(bundle);
                            }else if (this.cadastroBasico.getNivelUsuario() == 3.0){//configuracao inicial completa
                                callHomeActivity(bundle);
                            }else{
                                loggedRespondeu = true;
                                mAuth.signOut();
                            }
                            break;
                        case TipoUsuarioENUM.CABELEIREIRO:
                            bundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),this.cadastroBasico.getNivelUsuario());
                            bundle.putString(CadastroBasico.getTIPO_USUARIO(),this.cadastroBasico.getTipoUsuario());
                            startService(new Intent(getApplicationContext(), BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoCabeleireiro(),true));
                            if (this.cadastroBasico.getNivelUsuario() >= 2.0 && this.cadastroBasico.getNivelUsuario() < 3.0){
                                callConfiguracaoIncialActivity(bundle);
                            }else if (this.cadastroBasico.getNivelUsuario() == 3.0){//configuracao inicial completa
                                callHomeActivity(bundle);
                            }else{
                                loggedRespondeu = true;
                                mAuth.signOut();
                            }
                            break;
                        case TipoUsuarioENUM.CLIENTE:
                            bundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),this.cadastroBasico.getNivelUsuario());
                            bundle.putString(CadastroBasico.getTIPO_USUARIO(),this.cadastroBasico.getTipoUsuario());
                            startService(new Intent(getApplicationContext(), BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoCliente(),true));
                            if (this.cadastroBasico.getNivelUsuario() >= 2.0 && this.cadastroBasico.getNivelUsuario() < 3.0){
                                callConfiguracaoIncialActivity(bundle);
                            }else if (this.cadastroBasico.getNivelUsuario() == 3.0){//configuracao inicial completa
                                callHomeActivity(bundle);
                            }else {
                                loggedRespondeu = true;
                                mAuth.signOut();
                            }
                            break;
                        default:
                            loggedRespondeu = true;
                            mAuth.signOut();
                            break;
                    }

                }else {
                    loggedRespondeu = true;
                    mAuth.signOut();
                }
            }
        }else{
            loggedRespondeu = true;
            mAuth.signOut();
        }
    }

    private void verifyLogin(){
        Log.i("script","verifyLogin()");
        FirebaseCrash.log("LoginActivity:verifyLogin()");
        mAuth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        )
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.i("script","verifyLogin() onComplete");

                        if( !task.isSuccessful() ){
                            Log.i("script","verifyLogin() onComplete !task.isSuccessful()");
                            showSnackbar("Login falhou");
                            progressDialog.dismiss();
                            fabProcessando = false;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("script","verifyLogin() onFailure");
                FirebaseCrash.report( e );
            }
        });
    }

    private Boolean validaFormulario(){
        if (emailIsValid() && passwordIsvalid()){
            return true;
        }else{
            return false;
        }
    }

    public  void irHome(){
        callHomeActivity(null);
    }

    //TEXT LINK
    public void callSignUp(View view) {
        Log.i("script","callSignUp()");
        callSignUpActivity(this.email.getText().toString());
    }

    public void callReset(View view){
        Log.i("script","callSignUp()");
        showToast("Recuperar acesso");
    }

    //GETTERS SETTERS
    public static boolean isLoginActivityAtiva() {
        return loginActivityAtiva;
    }

    private void teste(){
        DatabaseReference ref = LibraryClass.getFirebase().child("testes");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("logteste","onChildAdded");

                if (dataSnapshot.exists()){
                    Log.i("logteste","onChildAdded != null");
                    /*for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Log.i("logteste","dados recebidos : \n " + snap.toString());
                    }*/

                    Teste teste = dataSnapshot.getValue(Teste.class);
                    arrayList.add(teste);
                    Log.i("logteste","dados recebidos "+dataSnapshot.getKey()+" : \n " + teste.toString());
                }else{
                    Log.i("logteste","onChildAdded NULL");
                }

                Log.i("logteste","array size = "+arrayList.size());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.i("logteste","onChildChanged");

                if (dataSnapshot.exists()){
                    Log.i("logteste","onChildChanged != null");
                    /*for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Log.i("logteste","dados recebidos : \n " + snap.toString());
                    }*/

                    Teste teste = dataSnapshot.getValue(Teste.class);
                    Log.i("logteste","dados recebidos : \n " + teste.toString());
                }else{
                    Log.i("logteste","onChildChanged NULL");
                }

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.i("logteste","onChildRemoved");

                if (dataSnapshot.exists()){
                    Log.i("logteste","onChildRemoved != null");
                    Teste teste = dataSnapshot.getValue(Teste.class);
                    Log.i("logteste","dados recebidos : \n " + teste.toString());
                }else{
                    Log.i("logteste","onChildRemoved NULL");
                }
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

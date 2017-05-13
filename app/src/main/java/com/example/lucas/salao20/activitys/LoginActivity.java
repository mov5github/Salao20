package com.example.lucas.salao20.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.CadastroBasico;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Lucas on 17/03/2017.
 */

public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener{
    private User user;
    private Context context;

    private Toolbar mToolbar;
    private FloatingActionButton fab;

    //CONTROLE
    private boolean fabProcessando;
    private static boolean loginActivityAtiva;

    //OBJETOS
    private CadastroBasico cadastroBasico = new CadastroBasico();

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() LOGIN");

        setContentView(R.layout.activity_login);

        this.context = this;
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();

        verifyLogged();

        initControles();
        initViews();
        initUser();
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
        if (BackgroundIntentService.isBackgroundIntentServiceAtivo()){
            stopService(new Intent(getApplicationContext(),BackgroundIntentService.class));
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
                    openProgressBar();
                    if (validaFormulario()){
                        FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginData()");
                        initUser();
                        verifyLogin();
                    }else {
                        fabProcessando = false;
                        closeProgressBar();
                    }
                }

            }
        });

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.login_progress);
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
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() login ");

        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged login");
                if(mAuth.getCurrentUser() == null){
                    Log.i("script","getFirebaseAuthResultHandler() getCurrentUser() == null login");
                    if (fabProcessando){
                        fabProcessando = false;
                        closeProgressBar();
                        showSnackbar("Login falhou");
                    }
                }else if (mAuth.getCurrentUser().getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() uid == null login");
                    mAuth.signOut();
                    if (fabProcessando){
                        fabProcessando = false;
                        closeProgressBar();
                        showSnackbar("Login falhou");
                    }
                }else {
                    Log.i("script","getFirebaseAuthResultHandler() uid != null");
                    if (fabProcessando){
                        startService(new Intent(getApplicationContext(), BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoInicial(),true));
                        callSplashScreenActivity();
                    }
                }
            }
        };
        return( callback );
    }

    private void verifyLogged(){
        Log.i("script","verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            Log.i("script","verifyLogged() mAuth.getCurrentUser() != null LOGIN");
            if (!mAuth.getCurrentUser().getUid().isEmpty()){
                Log.i("script","verifyLogged() UID != null LOGIN");
                if (BackgroundIntentService.isBackgroundIntentServiceAtivo()){
                    if(BackgroundIntentService.getCadastroBasico() != null){
                        if(BackgroundIntentService.getCadastroBasico().getNivelUsuario() == 1.0){
                            callConfiguracaoIncialActivity(null);
                        }else if(BackgroundIntentService.getCadastroBasico().getNivelUsuario() > 1.0 && BackgroundIntentService.getCadastroBasico().getNivelUsuario() < 3.0){
                            if (BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CLIENTE) || BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.SALAO) || BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CABELEIREIRO)){
                                callConfiguracaoIncialActivity(null);
                            }else {
                                callSplashScreenActivity();
                            }
                        }else if (BackgroundIntentService.getCadastroBasico().getNivelUsuario() > 3.0){
                            if (BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CLIENTE) || BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.SALAO) || BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CABELEIREIRO)){
                                callHomeActivity();
                            }else {
                                callSplashScreenActivity();
                            }
                        }else {
                            callSplashScreenActivity();
                        }
                    }else{
                        callSplashScreenActivity();
                    }
                }else {
                    mAuth.signOut();
                    //passa para onstart
                }
            }else {
                Log.i("script","verifyLogged()  UID = null");
                mAuth.signOut();
                //passa para onstart
            }
        }else{
            Log.i("script","verifyLogged() mAuth.getCurrentUser() == null");
            //passa para onstart
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
                            closeProgressBar();
                            fabProcessando = false;
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i("script","verifyLogin() onFailure");
                FirebaseCrash.report( e );
                fabProcessando = false;
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
        callHomeActivity();
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
}

package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
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
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.Teste;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by Lucas on 17/03/2017.
 */

public class LoginActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener{
    private User user;
    private Toolbar mToolbar;
    private FloatingActionButton fab;

    //ALERT DIALOG
    private ProgressDialog progressDialog;

    //CONTROLE
    private static boolean loginActivityAtiva = false;

    //OBJETOS
    private CadastroBasico cadastroBasico;

    //FIREBASE
    private FirebaseAuth mAuth;
    private  DatabaseReference refCadastroBasico;
    private  ValueEventListener valueEventListenerCadastroBasico;

    //HANDLER
    private Handler handler;
    private Runnable runnableLimiteUsuarioLogado;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() LOGIN");
        setContentView(R.layout.activity_login);

        initFirebase();
        initViews();
        initHandler();
        verifyLogged();
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
        if(mAuth.getCurrentUser()!= null && mAuth.getCurrentUser().getUid().isEmpty()){
            mAuth.signOut();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() LOGIN");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy() LOGIN");
        loginActivityAtiva = false;
        if (refCadastroBasico != null){
            refCadastroBasico.removeEventListener(valueEventListenerCadastroBasico);
        }
        handler.removeCallbacksAndMessages(null);
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
                fab.setClickable(false);
                fab.setVisibility(View.INVISIBLE);
                progressDialog.show();
                if (validaFormulario()){
                    FirebaseCrash.log("LoginActivity:clickListener:button:sendLoginData()");
                    initUser();
                    verifyLogin();
                }else {
                    fab.setClickable(true);
                    fab.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
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

    private void initHandler(){
        this.handler = new Handler();
        this.runnableLimiteUsuarioLogado = new Runnable() {
            @Override
            public void run() {
                Log.i("script","runnableLimiteUsuarioLogado()");
                if (refCadastroBasico != null){
                    refCadastroBasico.removeEventListener(valueEventListenerCadastroBasico);
                }
                mAuth.signOut();
                progressDialog.dismiss();
                showSnackbar("Login falhou");
                fab.setClickable(true);
                fab.setVisibility(View.VISIBLE);
            }
        };
    }

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
        valueEventListenerCadastroBasico = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("script","valueEventListenerCadastroBasico -> "+dataSnapshot.toString());
                if (dataSnapshot.exists() && dataSnapshot.getValue(CadastroBasico.class) != null){
                    Log.i("script","valueEventListenerCadastroBasico -> dataSnapshot.exists()");

                    cadastroBasico = dataSnapshot.getValue(CadastroBasico.class);
                    Log.i("script","receberBundle Splash\n"+"tiposuaser = " +cadastroBasico.getTipoUsuario()+"\nnome = "+ cadastroBasico.getNome()+ "\nsobrenome = "+cadastroBasico.getSobrenome());
                    handler.removeCallbacks(runnableLimiteUsuarioLogado);
                    direcionarUsuario();
                }else {
                    Log.i("script","valueEventListenerCadastroBasico -> !dataSnapshot.exists()");
                    showSnackbar("Login falhou");
                    progressDialog.dismiss();
                    handler.removeCallbacks(runnableLimiteUsuarioLogado);
                    fab.setClickable(true);
                    fab.setVisibility(View.VISIBLE);
                    mAuth.signOut();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("script","valueEventListenerCadastroBasico -> onCancelled");

                showSnackbar("Login falhou");
                handler.removeCallbacks(runnableLimiteUsuarioLogado);
                progressDialog.dismiss();
                fab.setClickable(true);
                fab.setVisibility(View.VISIBLE);
                mAuth.signOut();
            }
        };
    }

    private void verifyLogged(){
        Log.i("script","verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            if(!mAuth.getCurrentUser().getUid().isEmpty()){
                this.progressDialog.show();
                this.fab.setClickable(false);
                this.fab.setVisibility(View.INVISIBLE);
                refCadastroBasico = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroBasico.getCADASTRO_BASICO());
                refCadastroBasico.addListenerForSingleValueEvent(valueEventListenerCadastroBasico);
                handler.postDelayed(runnableLimiteUsuarioLogado,5000);
            }else{
                mAuth.signOut();
            }
        }
    }

    private void direcionarUsuario(){
        if (cadastroBasico != null && cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() != 0.0
                && cadastroBasico.getNome() != null && !cadastroBasico.getNome().isEmpty() && cadastroBasico.getSobrenome() != null && !cadastroBasico.getSobrenome().isEmpty()) {
            Bundle bundle = new Bundle();
            Bundle auxBundle = new Bundle();
            auxBundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
            auxBundle.putString(CadastroBasico.getNOME(),cadastroBasico.getNome());
            auxBundle.putString(CadastroBasico.getSOBRENOME(),cadastroBasico.getSobrenome());

            if (cadastroBasico.getNivelUsuario() == 1.0){
                bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                callTipoUsuarioActivity(bundle);
            }else {
                if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
                    auxBundle.putString(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
                    switch (cadastroBasico.getTipoUsuario()){
                        case TipoUsuarioENUM.SALAO:
                            if (cadastroBasico.getNivelUsuario() >= 2.0 && cadastroBasico.getNivelUsuario() < 3.0){
                                if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty() && cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                                    auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                                    auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                                    bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                                    callConfiguracaoInicial(bundle);
                                }else {
                                    mAuth.signOut();
                                    showSnackbar("Login falhou");
                                    progressDialog.dismiss();
                                    fab.setClickable(true);
                                    fab.setVisibility(View.VISIBLE);
                                }
                            }else if (cadastroBasico.getNivelUsuario() >= 3.0){//configuracao inicial completa
                                if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty() && cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                                    auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                                    auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                                    bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                                    callHome(bundle);
                                }else {
                                    mAuth.signOut();
                                    showSnackbar("Login falhou");
                                    progressDialog.dismiss();
                                    fab.setClickable(true);
                                    fab.setVisibility(View.VISIBLE);
                                }
                            }else{
                                mAuth.signOut();
                                showSnackbar("Login falhou");
                                progressDialog.dismiss();
                                fab.setClickable(true);
                                fab.setVisibility(View.VISIBLE);
                            }
                            break;
                        case TipoUsuarioENUM.PROFISSIONAl:
                            if (cadastroBasico.getNivelUsuario() >= 2.0 && cadastroBasico.getNivelUsuario() < 3.0){
                                if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty() && cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                                    auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                                    auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                                    bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                                    callConfiguracaoInicial(bundle);
                                }else {
                                    mAuth.signOut();
                                    showSnackbar("Login falhou");
                                    progressDialog.dismiss();
                                    fab.setClickable(true);
                                    fab.setVisibility(View.VISIBLE);
                                }
                            }else if (cadastroBasico.getNivelUsuario() >= 3.0){//configuracao inicial completa
                                if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty() && cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                                    auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                                    auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                                    bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                                    callHome(bundle);
                                }else {
                                    mAuth.signOut();
                                    showSnackbar("Login falhou");
                                    progressDialog.dismiss();
                                    fab.setClickable(true);
                                    fab.setVisibility(View.VISIBLE);
                                }
                            }else{
                                mAuth.signOut();
                                showSnackbar("Login falhou");
                                progressDialog.dismiss();
                                fab.setClickable(true);
                                fab.setVisibility(View.VISIBLE);
                            }
                            break;
                        case TipoUsuarioENUM.CLIENTE:
                            if (cadastroBasico.getNivelUsuario() >= 2.0 && cadastroBasico.getNivelUsuario() < 3.0){
                                if (cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                                    auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                                    bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                                    callConfiguracaoInicial(bundle);
                                }else{
                                    mAuth.signOut();
                                    showSnackbar("Login falhou");
                                    progressDialog.dismiss();
                                    fab.setClickable(true);
                                    fab.setVisibility(View.VISIBLE);
                                }
                            }else if (cadastroBasico.getNivelUsuario() >= 3.0){//configuracao inicial completa
                                if (cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                                    auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                                    bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                                    callHome(bundle);
                                }else{
                                    mAuth.signOut();
                                    showSnackbar("Login falhou");
                                    progressDialog.dismiss();
                                    fab.setClickable(true);
                                    fab.setVisibility(View.VISIBLE);
                                }
                            }else{
                                this.mAuth.signOut();
                                showSnackbar("Login falhou");
                                progressDialog.dismiss();
                                fab.setClickable(true);
                                fab.setVisibility(View.VISIBLE);
                            }
                            break;
                        default:
                            this.mAuth.signOut();
                            showSnackbar("Login falhou");
                            progressDialog.dismiss();
                            fab.setClickable(true);
                            fab.setVisibility(View.VISIBLE);
                            break;
                    }
                }else {
                    this.mAuth.signOut();
                    showSnackbar("Login falhou");
                    progressDialog.dismiss();
                    fab.setClickable(true);
                    fab.setVisibility(View.VISIBLE);
                }
            }
        }else {
            Log.i("script","ERRO");

            this.mAuth.signOut();
            showSnackbar("Login falhou");
            progressDialog.dismiss();
            fab.setClickable(true);
            fab.setVisibility(View.VISIBLE);
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
                            fab.setClickable(true);
                            fab.setVisibility(View.VISIBLE);
                        }else {
                            refCadastroBasico = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroBasico.getCADASTRO_BASICO());
                            refCadastroBasico.addListenerForSingleValueEvent(valueEventListenerCadastroBasico);
                            handler.postDelayed(runnableLimiteUsuarioLogado,5000);
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
        return emailIsValid() && passwordIsvalid();
    }



    //CALL
    private void callTipoUsuarioActivity(Bundle bundle){
        Intent intent = new Intent(this, TipoUsuarioActivity.class);
        if (bundle != null){
            intent.putExtras(bundle);
        }
        progressDialog.dismiss();
        startActivity(intent);
        finish();
    }

    private void callHome(Bundle bundle){
        Intent intent;
        switch (cadastroBasico.getTipoUsuario()){
            case TipoUsuarioENUM.SALAO:
                intent = new Intent(this, HomeSalaoActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                progressDialog.dismiss();
                startActivity(intent);
                finish();
                break;
            case TipoUsuarioENUM.PROFISSIONAl:
                intent = new Intent(this, HomeProfissionalActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                progressDialog.dismiss();
                startActivity(intent);
                finish();
                break;
            case TipoUsuarioENUM.CLIENTE:
                intent = new Intent(this, HomeClienteActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                progressDialog.dismiss();
                startActivity(intent);
                finish();
                break;
            default:
                mAuth.signOut();
                break;
        }
    }

    private void callConfiguracaoInicial(Bundle bundle){
        Intent intent;
        switch (cadastroBasico.getTipoUsuario()){
            case TipoUsuarioENUM.SALAO:
                intent = new Intent(this, ConfiguracaoInicialSalaoActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                progressDialog.dismiss();
                startActivity(intent);
                finish();
                break;
            case TipoUsuarioENUM.PROFISSIONAl:
                intent = new Intent(this, ConfiguracaoInicialProfissionalActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                progressDialog.dismiss();
                startActivity(intent);
                finish();
                break;
            case TipoUsuarioENUM.CLIENTE:
                intent = new Intent(this, ConfiguracaoInicialClienteActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                progressDialog.dismiss();
                startActivity(intent);
                finish();
                break;
            default:
                mAuth.signOut();
                break;
        }
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

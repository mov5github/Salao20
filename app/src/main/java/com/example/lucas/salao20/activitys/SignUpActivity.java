package com.example.lucas.salao20.activitys;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
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
import com.example.lucas.salao20.fragments.signUp.FragmentCadastroBasico;
import com.example.lucas.salao20.geral.Acount;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.geral.Criptografia;
import com.example.lucas.salao20.intentServices.BackgroundIntentService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;


/**
 * Created by Lucas on 17/03/2017.
 */

public class SignUpActivity extends CommonActivity{
    private Toolbar mToolbar;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private User user;

    private String emailRecebido;

    //CONTROLE
    private boolean fabProcessando;
    private boolean mAuthStateListenerProcessando;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() SIGNUP");
        setContentView(R.layout.activity_sign_up);

        initViews();
        initUser();
        initControles();

        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener = getFirebaseAuthResultHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("script","onResume() SIGNUP");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() SIGNUP");
        mAuth.addAuthStateListener(mAuthStateListener);

        if (email == null && password == null && passwordAgain == null){
            //VIEWS
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentCadastroBasico frag = (FragmentCadastroBasico) fragmentManager.findFragmentById(R.id.content_sign_up);
            email = (AutoCompleteTextView) frag.getEmail();
            password = (EditText) frag.getPassword();
            passwordAgain = (EditText) frag.getPasswordAgain();
            if (emailRecebido != null && !emailRecebido.isEmpty()){
                email.setText(emailRecebido);
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() SIGNUP");

        if( mAuthStateListener != null ){
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_up_activity,menu);
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
        Log.i("script","initViews()");
        if (progressBar == null){
            progressBar = (ProgressBar) findViewById(R.id.sign_up_progress);
        }

        //EDITTEXT
        email = (AutoCompleteTextView)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        passwordAgain = (EditText)findViewById(R.id.password_again);

        //TOOLBAR
        if (mToolbar == null){
            mToolbar = (Toolbar) findViewById(R.id.toolbar_sign_up);
            mToolbar.setTitle("SIGN_UP");
            mToolbar.setSubtitle("tela de cadastro");
            mToolbar.setLogo(R.mipmap.ic_launcher);
            setSupportActionBar(mToolbar);
        }

        //FRAGMENT
        if (getSupportFragmentManager().getFragments() == null || getSupportFragmentManager().getFragments().size() == 0){
            Log.i("script","getSupportFragmentManager()== null set fragment cadastro basico  ");
            FragmentCadastroBasico fragmentCadastroBasico = new FragmentCadastroBasico();
            replaceFragment(fragmentCadastroBasico);
        }

        //FLOATING ACTION BUTTON
        if (fab == null){
            fab = (FloatingActionButton) findViewById(R.id.fab_sign_up);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!fabProcessando){
                        fabProcessando = true;
                        fab.setClickable(false);
                        fab.setVisibility(View.INVISIBLE);
                        openProgressBar();
                        if (formularioIsValid()) {
                            initUser();
                            saveUser();

                        }else {
                            fabProcessando = false;
                            fab.setClickable(true);
                            fab.setVisibility(View.VISIBLE);
                            closeProgressBar();
                        }
                    }
                }
            });
        }



    }

    @Override
    protected void initUser() {
        Log.i("teste","initUser() SIGNUP");
        if (user == null){
            Log.i("teste","initUser() SIGNUP user ==null");
            user = new User();
        }
        if (email != null && password != null){
            Log.i("teste","initUser() SIGNUP set name email password");
            user.setEmail( email.getText().toString() );
            user.setPassword( password.getText().toString() );
        }
    }


    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() login ");

        final FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() signup  user logado");
                    if (!mAuthStateListenerProcessando){
                        mAuthStateListenerProcessando = true;
                        DatabaseReference firebase = LibraryClass.getFirebase().child(GeralENUM.USERS).child( mAuth.getCurrentUser().getUid() );

                        Criptografia crip = new Criptografia();
                        crip.gerarChave();
                        Acount acount = new Acount();
                        acount.setEmail(user.getEmail());
                        acount.setSenha(user.getPassword());
                        CadastroBasico cadastroBasico = new CadastroBasico();
                        cadastroBasico.setNivelUsuario(1.0);

                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(Acount.getACOUNT(), acount.toMap());
                        childUpdates.put(Criptografia.getCRIPTOGRAFIA(), crip.toMap());
                        childUpdates.put(CadastroBasico.getCADASTRO_BASICO(), cadastroBasico.toMap());
                        firebase.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.i("script","onComplete() erro != null");
                                    removerUsuarioCriado();
                                }else{
                                    Log.i("script","onComplete() erro == null");
                                    mAuth.signOut();
                                    showToast( "Conta criada com sucesso!" );
                                    closeProgressBar();
                                    finish();
                                }
                            }
                        });
                    }
                }
            }
        };
        return( callback );
    }

    private void removerUsuarioCriado(){
        if ( mAuth.getCurrentUser() != null){
            DatabaseReference firebase = LibraryClass.getFirebase().child( mAuth.getCurrentUser().getUid() );
            firebase.setValue(null);
            mAuth.getCurrentUser().delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.i("script","removerUsuarioCriado() task.isSuccessful()");
                                showToast( "Erro, tentar novamente!" );
                                mAuth.signOut();
                                mAuthStateListenerProcessando = false;
                                fabProcessando = false;
                                fab.setVisibility(View.VISIBLE);
                                fab.setClickable(true);
                                closeProgressBar();
                            }else{
                                removerUsuarioCriado();
                            }
                        }
                    });
        }
    }

    private void initControles(){
        this.fabProcessando = false;
        this.mAuthStateListenerProcessando = false;

        Intent intent = getIntent();
        this.emailRecebido = intent.getStringExtra("email");
    }

    private void saveUser(){

        if (user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()){
            Log.w("BrokenLogic","Nao foi possivel saveUser getEmail || getPassword");
            closeProgressBar();
            fabProcessando = false;
            fab.setClickable(true);
            fab.setVisibility(View.VISIBLE);
        }else {
            mAuth.createUserWithEmailAndPassword(
                    user.getEmail(),
                    user.getPassword()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if( !task.isSuccessful() ){
                        closeProgressBar();
                        fabProcessando = false;
                        fab.setClickable(true);
                        fab.setVisibility(View.VISIBLE);
                    }
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrash.report( e );
                            showSnackbar( e.getMessage() );
                            fabProcessando = false;
                            fab.setClickable(true);
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
        }


    }


    private Boolean formularioIsValid(){
        if (emailIsValid() && passwordIsvalid() && passwordAgainIsvalid()){
            return true;
        }else return false;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_sign_up, fragment).commit();
    }
}

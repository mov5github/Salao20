package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.domain.User;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.fragments.signUp.FragmentSignUp;
import com.example.lucas.salao20.geral.geral.Acount;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by Lucas on 17/03/2017.
 */

public class SignUpActivity extends CommonActivity{
    private Toolbar mToolbar;
    private FloatingActionButton fab;
    private FirebaseAuth mAuth;
    private User user;
    private Handler handler;

    private AutoCompleteTextView nome;
    private AutoCompleteTextView sobrenome;

    private String emailRecebido;

    //ALERT DIALOG
    private ProgressDialog progressDialog;

    //FIREBASE REF
    private DatabaseReference ref;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() SIGNUP");
        setContentView(R.layout.activity_sign_up);

        initViews();
        initUser();
        initControles();

        this.handler = new Handler();
        this.mAuth = FirebaseAuth.getInstance();
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
        //VIEWS
        FragmentSignUp frag = (FragmentSignUp) getSupportFragmentManager().findFragmentById(R.id.content_sign_up);
        if (email == null){
            email = (AutoCompleteTextView) frag.getEmail();
        }
        if (password == null){
            password = (EditText) frag.getPassword();
        }
        if(passwordAgain == null){
            passwordAgain = (EditText) frag.getPasswordAgain();
        }
        if(nome == null){
            nome = (AutoCompleteTextView) frag.getNome();
        }
        if(sobrenome == null){
            sobrenome = (AutoCompleteTextView) frag.getSobrenome();
        }

        if (emailRecebido != null && !emailRecebido.isEmpty()){
            email.setText(emailRecebido);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() SIGNUP");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(null);
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
        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Criando conta ...");
        this.progressDialog.setCancelable(false);

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
            FragmentSignUp fragmentSignUp= new FragmentSignUp();
            replaceFragment(fragmentSignUp);
        }

        //FLOATING ACTION BUTTON
        if (fab == null){
            fab = (FloatingActionButton) findViewById(R.id.fab_sign_up);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fab.setClickable(false);
                    fab.setVisibility(View.INVISIBLE);
                    progressDialog.show();
                    if (formularioIsValid()) {
                        saveUser();
                    }else {
                        fab.setClickable(true);
                        fab.setVisibility(View.VISIBLE);
                        progressDialog.dismiss();
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

    private void deletarUsuario(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                mAuth.getCurrentUser().delete()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.i("script","removerUsuarioCriado() task.isSuccessful()");
                                    showToast( "Erro, tentar novamente!" );
                                    mAuth.signOut();
                                    fab.setVisibility(View.VISIBLE);
                                    fab.setClickable(true);
                                    progressDialog.dismiss();
                                }else{
                                    deletarUsuario();
                                }
                            }
                        });
            }
        });

    }

    private void initControles(){
        if (getIntent().hasExtra("email")){
            this.emailRecebido = getIntent().getStringExtra("email");
        }
    }

    private void saveUser(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                initUser();
                if (user.getEmail() == null || user.getEmail().isEmpty() || user.getPassword() == null || user.getPassword().isEmpty()){
                    Log.w("BrokenLogic","Nao foi possivel saveUser getEmail || getPassword");
                    progressDialog.dismiss();
                    fab.setClickable(true);
                    fab.setVisibility(View.VISIBLE);
                    showToast("Tentar novamente !");
                }else {
                    mAuth.createUserWithEmailAndPassword(
                            user.getEmail(),
                            user.getPassword()
                    ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if( !task.isSuccessful() ){
                                progressDialog.dismiss();
                                fab.setClickable(true);
                                fab.setVisibility(View.VISIBLE);
                                showToast("Tentar novamente !");
                            }else{
                                if (ref == null){
                                    ref = LibraryClass.getFirebase();
                                }
                                if (mAuth.getCurrentUser() == null || mAuth.getCurrentUser().getUid() == null || mAuth.getCurrentUser().getUid().isEmpty()){
                                    deletarUsuario();
                                }else {
                                    Acount acount = new Acount();
                                    acount.setEmail(user.getEmail());
                                    acount.setSenha(user.getPassword());
                                    CadastroBasico cadastroBasico = new CadastroBasico();
                                    cadastroBasico.setNivelUsuario(1.0);
                                    cadastroBasico.setNome(nome.getText().toString());
                                    cadastroBasico.setSobrenome(sobrenome.getText().toString());
                                    Map<String, Object> updates = new HashMap<>();
                                    updates.put("users/"+mAuth.getCurrentUser().getUid()+"/acount",acount.toMap());
                                    updates.put("users/"+mAuth.getCurrentUser().getUid()+"/cadastroBasico",cadastroBasico.toMap());
                                    ref.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null){
                                                Log.i("script","onComplete() erro != null");
                                                deletarUsuario();
                                            }else{
                                                Log.i("script","onComplete() erro == null");
                                                mAuth.signOut();
                                                showToast( "Conta criada com sucesso!" );
                                                progressDialog.dismiss();
                                                finish();
                                            }
                                        }
                                    });

                                }
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            FirebaseCrash.report( e );
                            //showSnackbar( "ERRO" );
                            showToast(e.getMessage());
                            //showSnackbar( e.getMessage() );
                            fab.setClickable(true);
                            fab.setVisibility(View.VISIBLE);
                        }
                    });
                }


            }
        });
    }

    private Boolean formularioIsValid(){
        if (emailIsValid() && passwordIsvalid() && passwordAgainIsvalid() && nomeIsvalid() && sobrenomeIsvalid()){
            return true;
        }else return false;
    }

    private Boolean nomeIsvalid(){
        if (nome.getText().length() < 3){
            nome.setError("Insira seu nome");
            nome.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    private Boolean sobrenomeIsvalid(){
        if (sobrenome.getText().toString().isEmpty()){
            sobrenome.setError("Insira seu sobrenome");
            sobrenome.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_sign_up, fragment).commit();
    }
}

package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.domain.User;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Lucas on 17/03/2017.
 */

public class ConfiguracaoInicialClienteActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FloatingActionButton fab;

    //ALERT DIALOG
    private ProgressDialog progressDialog;

    //CONTROLE
    private static boolean ConfiguracaoInicialClienteActivityAtiva = false;

    //OBJETOS
    private CadastroBasico cadastroBasico;

    //FIREBASE AUTH
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //FIREBASE REF
    private DatabaseReference refRaiz;

    //HANDLER
    private Handler handler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() onfiguracaoInicialCliente");
        setContentView(R.layout.activity_configuracao_inicial_cliente);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        mAuth.addAuthStateListener(mAuthListener);

        initViews();
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("script","onResume() onfiguracaoInicialCliente");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() onfiguracaoInicialCliente");
        ConfiguracaoInicialClienteActivityAtiva = true;
        if(mAuth.getCurrentUser()!= null && mAuth.getCurrentUser().getUid().isEmpty()){
            mAuth.signOut();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() onfiguracaoInicialCliente");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy() onfiguracaoInicialCliente");
        ConfiguracaoInicialClienteActivityAtiva = false;
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

    private void initViews() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_configuracao_inicial_cliente);
        mToolbar.setTitle("CONFIGURACAO INICIAL CLIENTE");
        mToolbar.setSubtitle("tela de confg cliente");
        mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);

        //FLOATING ACTION BUTTON
        fab = (FloatingActionButton) findViewById(R.id.fab_configuracao_inicial_cliente);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(getApplicationContext(),"teste",Toast.LENGTH_SHORT).show();
            }
        });

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Autenticando login ...");
        this.progressDialog.setCancelable(false);

        Button button = (Button) findViewById(R.id.btn_confg_cliente);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (refRaiz == null){
                    refRaiz = LibraryClass.getFirebase();
                }
                refRaiz.child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroBasico.getCADASTRO_BASICO()).child(CadastroBasico.getNIVEL_USUARIO()).setValue(3.0, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            callHome();
                        }else{
                            Toast.makeText(getApplicationContext(),"ERRO",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initHandler(){
        this.handler = new Handler();
    }

    //CALL
    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void callHome(){
        Intent intent = new Intent(this, HomeClienteActivity.class);
        startActivity(intent);
        finish();
    }

    //GETTERS SETTERS
    public static boolean isConfiguracaoInicialClienteActivityAtiva() {
        return ConfiguracaoInicialClienteActivityAtiva;
    }
}

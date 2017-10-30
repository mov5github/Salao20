package com.example.lucas.salao20.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.geral.CadastroComplementar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 17/03/2017.
 */

public class ConfiguracaoInicialProfissionalActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private FloatingActionButton fab;

    //PROGRESS DIALOG
    private ProgressDialog progressDialog;

    //ALERT DIALOG
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    //EDITTEXT
    private EditText apelidoProfissional;

    //CONTROLE
    private static boolean ConfiguracaoInicialProfissionalActivityAtiva = false;

    //OBJETOS
    private CadastroBasico cadastroBasico;
    private  CadastroComplementar cadastroComplementar = null;

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
        setContentView(R.layout.activity_configuracao_inicial_profissional);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = getFirebaseAuthResultHandler();
        mAuth.addAuthStateListener(mAuthListener);

        receberBundle();
        initViews();
        initHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("script","onResume() onfiguracaoInicialProfissional");

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            mToolbar.setElevation(4 * this.getResources().getDisplayMetrics().density);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() onfiguracaoInicialProfissional");
        ConfiguracaoInicialProfissionalActivityAtiva = true;
        if(mAuth.getCurrentUser()!= null && mAuth.getCurrentUser().getUid().isEmpty()){
            mAuth.signOut();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() onfiguracaoInicialProfsissional");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy() onfiguracaoInicialProfissional");
        ConfiguracaoInicialProfissionalActivityAtiva = false;
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

    private void initViews() {
        //TOOLBAR
        mToolbar = (Toolbar) findViewById(R.id.toolbar_configuracao_inicial_profissional);
        mToolbar.setTitle("CONFIGURACAO INICIAL Profissional");
        mToolbar.setSubtitle("tela de confg profissional");
        mToolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(mToolbar);

        //FLOATING ACTION BUTTON
        fab = (FloatingActionButton) findViewById(R.id.fab_configuracao_inicial_profissional);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Toast.makeText(getApplicationContext(),"teste",Toast.LENGTH_SHORT).show();
            }
        });

        this.progressDialog = new ProgressDialog(this);
        this.progressDialog.setMessage("Autenticando login ...");
        this.progressDialog.setCancelable(false);

        //EDITTEXT
        this.apelidoProfissional = new EditText(this);
        this.apelidoProfissional.setHint("Digite o apelido do profissional");
        this.apelidoProfissional.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        //ALERT DIALOG
        this.builder = new AlertDialog.Builder(this);
        //define um botão como positivo
        this.builder.setPositiveButton("SALVAR", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                salvarApelido();
            }
        });
        this.builder.setTitle("APELIDO DO PROFISSIONAL");
        this.builder.setMessage("Insira o seu apelido pelo qual os seus clientes lhe conhecem, caso não possua um apelido insira seu primeiro e segundo nome; O apelido do profissional será exibido em seu perfil juntamente com o seu nome, para que seus clientes possam te identificar e acessar sua agenda.");
        this.builder.setView(apelidoProfissional);
        this.alertDialog = builder.create();
        this.alertDialog.setCancelable(false);
        this.alertDialog.show();

        Button button = (Button) findViewById(R.id.btn_confg_profissional);
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

    private void initHandler(){
        this.handler = new Handler();
    }

    private void salvarApelido(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (apelidoProfissional.getText() == null || apelidoProfissional.getText().toString().isEmpty()){
                    showToast("Insira um apelido para o profissional");
                    alertDialog.show();
                }else {
                    showProgressDialog(true);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(apelidoProfissional.getWindowToken(), 0);
                    if (refRaiz == null){
                        refRaiz = LibraryClass.getFirebase();
                    }
                    Map<String,Object> updates = new HashMap<String,Object>();
                    updates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+cadastroBasico.getUserMetadataUid()+"/"+ CadastroComplementar.getCADASTRO_COMPLEMENTAR()+"/"+CadastroComplementar.getNICK_PROFISSIONAL(),apelidoProfissional.getText().toString());
                    updates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getNIVEL_USUARIO(),3.0);
                    refRaiz.updateChildren(updates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null){
                                showProgressDialog(false);
                                callHome();
                            }else {
                                showProgressDialog(false);
                                alertDialog.show();
                                showToast("Erro ao salvar apelido!");
                            }
                        }
                    });
                }

            }
        });
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
    }

    //PROGRESS DIALOG
    public void showProgressDialog(boolean exibir){
        if (exibir){
            this.progressDialog.show();
        }else {
            this.progressDialog.dismiss();
        }
    }

    private void showToast( String message ){
        Toast.makeText(this,
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    //CALL
    private void callLoginActivity(){
        Intent intent = new Intent(this, LoginActivity.class);
        progressDialog.dismiss();
        startActivity(intent);
        finish();
    }

    private void callHome(){
        Intent intent = new Intent(this, HomeProfissionalActivity.class);
        startActivity(intent);
        finish();
    }

    //GETTERS SETTERS
    public static boolean isConfiguracaoInicialProfissionalActivityAtiva() {
        return ConfiguracaoInicialProfissionalActivityAtiva;
    }
}

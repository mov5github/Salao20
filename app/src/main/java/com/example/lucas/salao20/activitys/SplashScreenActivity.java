package com.example.lucas.salao20.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.lucas.salao20.R;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.geral.Profissional;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class SplashScreenActivity extends CommonActivity{
    //VIEWS
    private ImageView splashLogoMov5;
    private ImageView splashLogoSalao20;
    private TextView labelPoweredBy;
    private TextView labelCarregando;
    private ProgressBar progressBarSalao20;

    //HANDLER
    private Handler handlerUIThread;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference refRaiz;
    private DatabaseReference refRegrasDeNegocio;
    private ChildEventListener childEventListenerRegrasDeNegocio;
    private ValueEventListener valueEventListenerRegrasDeNegocio;


    //CONTROLES
    private boolean splashIniciada;
    private boolean splashFinalizada;
    private boolean salvandoDados;
    private boolean dadosSalvos;
    private  boolean aguardadandoControladoresCodUnico;
    private static boolean splashScreenActivityAtiva;

    //OBJETOS
    private CadastroBasico cadastroBasico;

    //CONTROLADOR COD UNICO
    private Integer controladorCodigoProfissional;
    private Integer controladorCodigoSalao;
    private Integer controladorCodigoUnicoAux;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initViews();
        initControles();
        initHandler();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null){
                    callLoginActivity();
                }else if (mAuth.getCurrentUser().getUid().isEmpty()){
                    mAuth.signOut();
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);

        CadastroBasico cadastroBasico = null;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() SplashScreenActivity");
        splashScreenActivityAtiva = true;
        receberBundle();
        disparaSplashScreen();
        //buscarControladoresCodUnico();
        salvarEtapa();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() SplashScreenActivity");
        splashScreenActivityAtiva = false;
        //mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy() SplashScreenActivity");
        handlerUIThread.removeCallbacksAndMessages(null);

    }

    @Override
    protected void initViews() {
        splashLogoMov5 = (ImageView)findViewById(R.id.splash_logo_mov5);
        labelPoweredBy = (TextView) findViewById(R.id.label_powered_by);
        labelCarregando = (TextView) findViewById(R.id.label_carregando);
        splashLogoSalao20 = (ImageView) findViewById(R.id.splash_logo_salao20);
        progressBarSalao20 = (ProgressBar) findViewById(R.id.progress_splash_salao20);
    }

    @Override
    protected void initUser() {
    }

    private void initControles(){
        this.splashIniciada = false;
        this.splashFinalizada = false;
        this.dadosSalvos = false;
        this.salvandoDados = false;
        this.aguardadandoControladoresCodUnico = false;
        this.controladorCodigoSalao = 0;
        this.controladorCodigoProfissional = 0;
    }

    private void initHandler(){
        this.handlerUIThread = new Handler();
    }

    private void receberBundle(){
        if (cadastroBasico == null) {
            cadastroBasico = new CadastroBasico();
            if (getIntent().hasExtra(CadastroBasico.getCADASTRO_BASICO())){
                Bundle bundle = getIntent().getExtras().getBundle(CadastroBasico.getCADASTRO_BASICO());

                if (bundle == null || !bundle.containsKey(CadastroBasico.getTIPO_USUARIO()) || bundle.get(CadastroBasico.getTIPO_USUARIO()).toString().isEmpty()
                                    || !bundle.containsKey(CadastroBasico.getNOME()) || bundle.get(CadastroBasico.getNOME()).toString().isEmpty()
                                    || !bundle.containsKey(CadastroBasico.getSOBRENOME()) || bundle.get(CadastroBasico.getSOBRENOME()).toString().isEmpty()){
                    mAuth.signOut();
                }else {
                    cadastroBasico.setTipoUsuario(bundle.getString(CadastroBasico.getTIPO_USUARIO()));
                    cadastroBasico.setNome(bundle.getString(CadastroBasico.getNOME()));
                    cadastroBasico.setSobrenome(bundle.getString(CadastroBasico.getSOBRENOME()));
                }
            }else {
                mAuth.signOut();
            }
        }
    }

    private void disparaSplashScreen(){
        Log.i("script","disparaSplashScreen() splashChamada");
        if (!this.splashIniciada){
            this.splashIniciada = true;
            int tempoAnimacao = 2000;
            int tempoSplashCompleta = 7000;
            final AlphaAnimation animation1 = new AlphaAnimation(0.0f,1.0f);
            animation1.setDuration(tempoAnimacao);
            animation1.setFillAfter(true);

            final AlphaAnimation animation1Reverse = new AlphaAnimation(1.0f,0.0f);
            animation1Reverse.setDuration(tempoAnimacao);
            animation1Reverse.setFillAfter(true);

            this.splashLogoMov5.setVisibility(View.VISIBLE);
            this.splashLogoMov5.startAnimation(animation1);

            this.labelPoweredBy.setVisibility(View.VISIBLE);
            this.labelPoweredBy.startAnimation(animation1);

            this.labelCarregando.setVisibility(View.VISIBLE);
            this.progressBarSalao20.setVisibility(View.VISIBLE);

            this.handlerUIThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("script","splashScreenInicial() splash iniciada");
                    splashLogoMov5.startAnimation(animation1Reverse);
                    labelPoweredBy.startAnimation(animation1Reverse);
                }
            },(tempoAnimacao+250));

            this.handlerUIThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    splashLogoSalao20.setVisibility(View.VISIBLE);
                    splashLogoSalao20.startAnimation(animation1);
                }
            },(4500));

            this.handlerUIThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("script","splashScreenInicial() splash completa");
                    splashFinalizada = true;
                    if (dadosSalvos){
                        direcionarUsuario();
                    }

                }
            },(tempoSplashCompleta));
        }else {
            Log.i("script","disparaSplashScreen() splash ja iniciada");
        }
    }

    private void buscarControladoresCodUnico() {
        //VERIFICA NESCESSIDADE DE OBTER CONTROLADOR COD UNICO
        if (this.refRegrasDeNegocio == null){
            this.refRegrasDeNegocio = LibraryClass.getFirebase().child(GeralENUM.REGRAS_DE_NEGOCIO);
        }
        if (cadastroBasico != null && cadastroBasico.getTipoUsuario() != null && (cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.SALAO) || cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.PROFISSIONAl))){
            if (this.childEventListenerRegrasDeNegocio == null) {

                this.childEventListenerRegrasDeNegocio = new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.getKey().equals(TipoUsuarioENUM.SALAO)){
                                controladorCodigoProfissional = dataSnapshot.getValue(Integer.class);
                                if (aguardadandoControladoresCodUnico && controladorCodigoSalao != 0 && controladorCodigoProfissional != 0){
                                    aguardadandoControladoresCodUnico = false;
                                    salvarComCodigoUnico();
                                }
                            }else if (dataSnapshot.getKey().equals(TipoUsuarioENUM.PROFISSIONAl)){
                                controladorCodigoSalao = dataSnapshot.getValue(Integer.class);
                                if (aguardadandoControladoresCodUnico && controladorCodigoSalao != 0 && controladorCodigoProfissional != 0){
                                    aguardadandoControladoresCodUnico = false;
                                    salvarComCodigoUnico();
                                }
                            }else {
                                mAuth.signOut();
                            }
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        if (dataSnapshot.exists()){
                            if (dataSnapshot.getKey().equals(GeralENUM.CONTROLADOR_CODIGO_PROFISSIONAL)){
                                controladorCodigoProfissional = dataSnapshot.child(GeralENUM.CONTROLADOR_CODIGO_PROFISSIONAL).getValue(Integer.class);
                            }else if (dataSnapshot.getKey().equals(GeralENUM.CONTROLADOR_CODIGO_SALAO)){
                                controladorCodigoSalao = dataSnapshot.child(GeralENUM.CONTROLADOR_CODIGO_SALAO).getValue(Integer.class);
                            }else {
                                mAuth.signOut();
                            }
                        }

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
            }
            if (cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.SALAO)){
                this.refRegrasDeNegocio.child(TipoUsuarioENUM.SALAO).addChildEventListener(this.childEventListenerRegrasDeNegocio);
            }else if(cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.PROFISSIONAl)){
                this.refRegrasDeNegocio.child(TipoUsuarioENUM.PROFISSIONAl).addChildEventListener(this.childEventListenerRegrasDeNegocio);
            }
        }else {
            //TODO erro ao salvar
        }
    }

    private void salvarEtapa(){
        if (this.salvandoDados == false){
            this.salvandoDados = true;
            this.dadosSalvos = false;

            //VERIFICA TIPO USUARIO
            switch (cadastroBasico.getTipoUsuario()){
                case TipoUsuarioENUM.CLIENTE:
                    salvarCliente();
                    break;
                case TipoUsuarioENUM.SALAO:
                    salvarComCodigoUnico();
                    break;
                case TipoUsuarioENUM.PROFISSIONAl:
                    salvarComCodigoUnico();
                    break;
                default:
                    mAuth.signOut();
                    break;
            }
        }
    }

    private void salvarCliente(){
        if (this.refRaiz == null){
            this.refRaiz = LibraryClass.getFirebase();
        }

        Map<String, Object> childUpdates = new HashMap<>();
        cadastroBasico.setNivelUsuario(2.0);
        //childUpdates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
        //childUpdates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
        String push = refRaiz.child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).push().getKey();
        cadastroBasico.setUserMetadataUid(push);
        childUpdates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO(),cadastroBasico.toMap());
        childUpdates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+push+"/"+GeralENUM.OWNER,mAuth.getCurrentUser().getUid());
        Log.i("script","CLIENTE uid -> "+cadastroBasico.getUserMetadataUid());
        String nome = cadastroBasico.getNome()+ " "+ cadastroBasico.getSobrenome();
        Log.i("script","CLIENTE nome -> "+nome);
        childUpdates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+cadastroBasico.getUserMetadataUid()+"/"+GeralENUM.CADASTRO_COMPLEMENTAR+"/"+GeralENUM.NOME_DO_CLIENTE,(cadastroBasico.getNome() + " " + cadastroBasico.getSobrenome()));

        this.refRaiz.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null){
                    dadosSalvos = false;
                    salvandoDados = false;
                    salvarEtapa();
                }else {
                    if (splashFinalizada){
                        direcionarUsuario();
                    }else {
                        dadosSalvos = true;
                    }
                }
            }
        });

    }

    private void salvarComCodigoUnico(){
        if (this.refRaiz == null){
            this.refRaiz = LibraryClass.getFirebase();
        }

        if (this.valueEventListenerRegrasDeNegocio == null){
            this.valueEventListenerRegrasDeNegocio = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        Log.i("script","valueEventListenerRegrasDeNegocio dataSnapshot.exists()");
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
                        controladorCodigoUnicoAux = dataSnapshot.getValue(Integer.class);
                        childUpdates.put(GeralENUM.REGRAS_DE_NEGOCIO+"/"+cadastroBasico.getTipoUsuario()+"/"+GeralENUM.CONTROLADOR_CODIGO_UNICO,controladorCodigoUnicoAux+1);
                        cadastroBasico.setNivelUsuario(2.0);
                        childUpdates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
                        cadastroBasico.setCodigoUnico(formarCodUnico(controladorCodigoUnicoAux));
                        childUpdates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                        String push = refRaiz.child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).push().getKey();
                        cadastroBasico.setUserMetadataUid(push);
                        childUpdates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                        childUpdates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+cadastroBasico.getUserMetadataUid()+"/"+GeralENUM.OWNER,mAuth.getCurrentUser().getUid());
                        childUpdates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+cadastroBasico.getUserMetadataUid()+"/"+CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                        childUpdates.put(GeralENUM.METADATA+"/"+GeralENUM.CODIGOUNICO_USER_METADATA_UID+"/"+cadastroBasico.getTipoUsuario()+"/"+cadastroBasico.getCodigoUnico(),cadastroBasico.getUserMetadataUid());
                        if (cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.PROFISSIONAl)){
                            Log.i("script","PROFISSIONAL uid -> "+cadastroBasico.getUserMetadataUid());
                            String nome = cadastroBasico.getNome()+ " "+ cadastroBasico.getSobrenome();
                            Log.i("script","PROFISSIONAL nome -> "+nome);
                            childUpdates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+cadastroBasico.getUserMetadataUid()+"/"+GeralENUM.CADASTRO_COMPLEMENTAR+"/"+Profissional.getNOMEPROFISSIONAL(),(cadastroBasico.getNome() + " " + cadastroBasico.getSobrenome()));
                        }

                        refRaiz.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null){
                                    Log.i("script","valueEventListenerRegrasDeNegocio onComplete databaseError != null -> "+databaseError.toString());
                                    dadosSalvos = false;
                                    salvandoDados = false;
                                    salvarEtapa();
                                }else {
                                    Log.i("script","valueEventListenerRegrasDeNegocio onComplete databaseError == null");
                                    if (splashFinalizada){
                                        direcionarUsuario();
                                    }else {
                                        dadosSalvos = true;
                                    }
                                }
                            }
                        });

                    }else {
                        //TODO errro
                        Log.i("script","erro2");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        if (cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.SALAO)){
            this.refRaiz.child(GeralENUM.REGRAS_DE_NEGOCIO).child(TipoUsuarioENUM.SALAO).child(GeralENUM.CONTROLADOR_CODIGO_UNICO).addListenerForSingleValueEvent(this.valueEventListenerRegrasDeNegocio);
        }else if (cadastroBasico.getTipoUsuario().equals(TipoUsuarioENUM.PROFISSIONAl)){
            this.refRaiz.child(GeralENUM.REGRAS_DE_NEGOCIO).child(TipoUsuarioENUM.PROFISSIONAl).child(GeralENUM.CONTROLADOR_CODIGO_UNICO).addListenerForSingleValueEvent(this.valueEventListenerRegrasDeNegocio);
        }else {
            //TODO erro
            Log.i("script","erro");
        }
    }

    private void direcionarUsuario(){
        Bundle bundle = new Bundle();
        Bundle auxBundle = new Bundle();

        if (cadastroBasico != null && cadastroBasico.getNivelUsuario() != null && cadastroBasico.getNivelUsuario() >= 2.0){
            auxBundle.putDouble(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());
            if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
                auxBundle.putString(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
                switch (cadastroBasico.getTipoUsuario()){
                    case TipoUsuarioENUM.SALAO:
                        if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty() && cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                            auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                            auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                            bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                            callConfiguracaoInicial(bundle);
                        }else{
                            mAuth.signOut();
                        }
                        break;
                    case TipoUsuarioENUM.PROFISSIONAl:
                        if (cadastroBasico.getCodigoUnico() != null && !cadastroBasico.getCodigoUnico().isEmpty() && cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                            auxBundle.putString(CadastroBasico.getCODIGO_UNICO(),cadastroBasico.getCodigoUnico());
                            auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                            bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                            callConfiguracaoInicial(bundle);
                        }else{
                            mAuth.signOut();
                        }
                        break;
                    case TipoUsuarioENUM.CLIENTE:
                        if (cadastroBasico.getUserMetadataUid() != null && !cadastroBasico.getUserMetadataUid().isEmpty()){
                            auxBundle.putString(CadastroBasico.getUSER_METADATA_UID(),cadastroBasico.getUserMetadataUid());
                            bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
                            callConfiguracaoInicial(bundle);
                        }else{
                            mAuth.signOut();
                        }
                        break;
                    default:
                        mAuth.signOut();
                        break;
                }
            }else{
                mAuth.signOut();
            }

            bundle.putBundle(CadastroBasico.getCADASTRO_BASICO(),auxBundle);
        }else{
            mAuth.signOut();
        }
    }

    private String formarCodUnico(int num){
        if (String.valueOf(num).length() <= 5){
            String aux = "";
            for (int i = String.valueOf(num).length(); i <= 5; i++){
                aux += "0";
            }
            return aux.concat(String.valueOf(num));
        }else {
            return String.valueOf(num);
        }
    }

    //CALL
    private void callConfiguracaoInicial(Bundle bundle){
        Intent intent;
        switch (cadastroBasico.getTipoUsuario()){
            case TipoUsuarioENUM.SALAO:
                intent = new Intent(this, ConfiguracaoInicialSalaoActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                finish();
                break;
            case TipoUsuarioENUM.PROFISSIONAl:
                intent = new Intent(this, ConfiguracaoInicialProfissionalActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                finish();
                break;
            case TipoUsuarioENUM.CLIENTE:
                intent = new Intent(this, ConfiguracaoInicialClienteActivity.class);
                if (bundle != null){
                    intent.putExtras(bundle);
                }
                startActivity(intent);
                finish();
                break;
            default:
                mAuth.signOut();
                break;
        }
    }



    //GETTERS SETTERS
}

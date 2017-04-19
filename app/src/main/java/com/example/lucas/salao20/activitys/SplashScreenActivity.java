package com.example.lucas.salao20.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.model.CadastroBasico;
import com.example.lucas.salao20.intentServices.SincronizacaoInicialIntentService;
import com.example.lucas.salao20.intentServices.SincronizarBancosIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;

public class SplashScreenActivity extends CommonActivity implements GoogleApiClient.OnConnectionFailedListener{
    static String INTENT_SERVICE_SINCRONIZAR_BANCOS = "com.example.lucas.salao20.intentservice.sincronizarbancos";
    static String BRODCAST_RECEIVER_BANCOS_SINCRONIZADOS = "com.example.lucas.salao20.brodcastreceiver.bancossincronizados";

    //VIEWS
    private ImageView splashLogoMov5;
    private ImageView splashLogoSalao20;
    private TextView labelPoweredBy;
    private TextView labelCarregando;
    private ProgressBar progressBarSalao20;

    //HANDLER
    private Handler handlerUIThread;

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;

    //BRODCASTRECEIVER
    private BroadcastReceiver broadcastReceiverBancosSincronizados;

    //CONTROLES
    private boolean splashIniciada;
    private boolean novoUsuario;
    private boolean bancosSincronizados;
    static boolean splashScreenActivityAtiva;

    //CADASTROS INICIAL CRONTROLE
    static CadastroBasico cadastroBasicoBD;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth = FirebaseAuth.getInstance();

        initViews();
        initControles();
        initHandler();
        initBrodcastReceiver();
        verifyLogged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() SplashScreenActivity");
        splashScreenActivityAtiva = true;
        Intent intent = getIntent();
        disparaSplashScreen(intent.getBooleanExtra("novoUsuario",false));
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() SplashScreenActivity");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy() SplashScreenActivity");
        splashScreenActivityAtiva = false;

        //CENCELA SINCRONIZARBANCOSINTENTSERVICE
        Intent intent = new Intent(getApplicationContext(), SincronizarBancosIntentService.class);
        Bundle bundle = new Bundle();
        bundle.putInt("desligar",1);
        intent.putExtras(bundle);
        startService(intent);

        handlerUIThread.removeCallbacksAndMessages(null);

        unregisterReceiver(this.broadcastReceiverBancosSincronizados);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("noNull","noNull");
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
        this.splashIniciada = false;
        this.novoUsuario = false;
        this.bancosSincronizados = false;
    }

    private void initHandler(){
        this.handlerUIThread = new Handler();
    }

    private void initBrodcastReceiver(){
        this.broadcastReceiverBancosSincronizados = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                bancosSincronizados = true;
                if (!novoUsuario){
                    //TODO
                   /* if (cadastroBasicoBD == null || cadastroBasicoBD.getNivelUsuario() == null || (cadastroBasicoBD.getNivelUsuario() > 1.0 && (cadastroBasicoBD.getTipoUsuario() == null || cadastroBasicoBD.getTipoUsuario().isEmpty())) || (cadastroBasicoBD.getNivelUsuario() >= 2.1 && (cadastroBasicoBD.getCodigoUnico() == null || cadastroBasicoBD.getCodigoUnico() == 0)) ){
                        callErroActivity("broadcastReceiverBancosSincronizados cadastroInicialBd == null");
                    }else {
                        Bundle bundle = new Bundle();
                        bundle.putDouble(DatabaseHelper.CadastroBasico.NIVEL_USUARIO, cadastroBasicoBD.getNivelUsuario());
                        if (cadastroBasicoBD.getNivelUsuario() > 1.0){
                            bundle.putString(DatabaseHelper.CadastroBasico.TIPO_USUARIO, cadastroBasicoBD.getTipoUsuario());
                        }
                        if (cadastroBasicoBD.getNivelUsuario() >= 2.1){
                           // bundle.putInt(DatabaseHelper.CadastroBasico.CODIGO_UNICO, cadastroBasicoBD.getCodigoUnico());
                        }
                        callConfiguracaoIncialActivity(bundle);
                    }*/
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(BRODCAST_RECEIVER_BANCOS_SINCRONIZADOS);
        registerReceiver(this.broadcastReceiverBancosSincronizados, intentFilter);
    }

    private void verifyLogged(){
        Log.i("script","verifyLogged()");
        if( mAuth.getCurrentUser() != null ){
            Log.i("script","verifyLogged() mAuth.getCurrentUser() != null");
            if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                Log.i("script","verifyLogged() set UID");
                //passa para onStart
            }else {
                Log.i("script","verifyLogged()  UID = null");
                mAuth.signOut();
                callErroActivity("SPLASHSCREENuserFirebase.getUid()==null");
            }
        }
        else{
            Log.i("script","verifyLogged() mAuth.getCurrentUser() == null");
            callLoginActivity();
        }
    }

    private void disparaSplashScreen(Boolean novoUsuario){
        Log.i("script","disparaSplashScreen() splashChamada");
        if (!this.splashIniciada){
            this.splashIniciada = true;
            int tempoAnimacao = 2000;
            int maxTempoVerificacao = 7000;
            final AlphaAnimation animation1 = new AlphaAnimation(0.0f,1.0f);
            animation1.setDuration(tempoAnimacao);
            animation1.setFillAfter(true);

            final AlphaAnimation animation1Reverse = new AlphaAnimation(1.0f,0.0f);
            animation1Reverse.setDuration(tempoAnimacao);
            animation1Reverse.setFillAfter(true);

            this.handlerUIThread.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("script", "splashScreenInicial() sincronizar bancos");
                    Intent intent = new Intent(getApplicationContext(), SincronizacaoInicialIntentService.class);
                    Bundle bundle = new Bundle();
                    if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                        bundle.putString(DatabaseHelper.CadastroBasico.UID, mAuth.getCurrentUser().getUid());
                    }
                    intent.putExtras(bundle);
                    startService(intent);
                }
            });

            if (novoUsuario){
                this.novoUsuario = true;
                this.splashLogoMov5.setVisibility(View.VISIBLE);
                this.splashLogoMov5.startAnimation(animation1);

                this.labelPoweredBy.setVisibility(View.VISIBLE);
                this.labelPoweredBy.startAnimation(animation1);

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
                        direcionarUsuario();
                    }
                },(maxTempoVerificacao));
            }else {
                this.novoUsuario = false;
                splashLogoSalao20.setVisibility(View.VISIBLE);
                splashLogoSalao20.startAnimation(animation1);

                this.handlerUIThread.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("script","splashScreenInicial() splash completa");
                        direcionarUsuario();
                    }
                },(maxTempoVerificacao));
            }

        }else {
            Log.i("script","disparaSplashScreen() splash ja iniciada");
        }

        splashIniciada = true;
    }

    private void direcionarUsuario(){
        Log.i("script","direcionarUsuario() splash tempo limite");

        /*if (this.bancosSincronizados){
            //TODO
            /*if (cadastroBasicoBD == null || cadastroBasicoBD.getNivelUsuario() == null || (cadastroBasicoBD.getNivelUsuario() > 1.0 && (cadastroBasicoBD.getTipoUsuario() == null || cadastroBasicoBD.getTipoUsuario().isEmpty())) || (cadastroBasicoBD.getNivelUsuario() >= 2.1 && (cadastroBasicoBD.getCodigoUnico() == null || cadastroBasicoBD.getCodigoUnico() == 0)) ){
                callErroActivity("direcionarUsuario cadastroInicialBd == null");
            }else {
                Bundle bundle = new Bundle();
                bundle.putDouble(DatabaseHelper.CadastroInicial.NIVEL_USUARIO, cadastroBasicoBD.getNivelUsuario());
                if (cadastroBasicoBD.getNivelUsuario() > 1.0){
                    bundle.putString(DatabaseHelper.CadastroInicial.TIPO_USUARIO, cadastroBasicoBD.getTipoUsuario());
                }
                if (cadastroBasicoBD.getNivelUsuario() >= 2.1){
                    bundle.putInt(DatabaseHelper.CadastroInicial.CODIGO_UNICO, cadastroBasicoBD.getCodigoUnico());
                }
                callConfiguracaoIncialActivity(bundle);
            }
        }else {
            callErroActivity("tempo limite splash screen");
        }*/
    }

    //GETTERS SETTERS
    public static boolean isSplashScreenActivityAtiva() {
        return splashScreenActivityAtiva;
    }

    public static String getIntentServiceSincronizarBancos() {
        return INTENT_SERVICE_SINCRONIZAR_BANCOS;
    }

    public static String getBrodcastReceiverBancosSincronizados() {
        return BRODCAST_RECEIVER_BANCOS_SINCRONIZADOS;
    }

    public static void setCadastroBasicoBD(CadastroBasico cadastroBasicoBD) {
        SplashScreenActivity.cadastroBasicoBD = cadastroBasicoBD;
    }
}

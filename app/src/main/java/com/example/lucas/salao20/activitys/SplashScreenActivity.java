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
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.intentServices.BackgroundIntentService;
import com.example.lucas.salao20.intentServices.SincronizacaoInicialIntentService;
import com.example.lucas.salao20.intentServices.SincronizacaoIntentService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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

    //FIREBASE
    private FirebaseAuth mAuth;

    //FIREBASE

    //BRODCASTRECEIVER
    private BroadcastReceiver broadcastReceiverBancosSincronizados;

    //CONTROLES
    private boolean splashIniciada;
    static boolean splashScreenActivityAtiva;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        initViews();
        initControles();
        initHandler();
        initBrodcastReceiver();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() SplashScreenActivity");
        splashScreenActivityAtiva = true;
        disparaSplashScreen();
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

        handlerUIThread.removeCallbacksAndMessages(null);
        unregisterReceiver(this.broadcastReceiverBancosSincronizados);
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
    }

    private void initHandler(){
        this.handlerUIThread = new Handler();
    }

    private void initBrodcastReceiver(){
        this.broadcastReceiverBancosSincronizados = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //bancosSincronizados = true;
                //if (!novoUsuario){
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
                //}
            }
        };
        IntentFilter intentFilter = new IntentFilter(BRODCAST_RECEIVER_BANCOS_SINCRONIZADOS);
        registerReceiver(this.broadcastReceiverBancosSincronizados, intentFilter);
    }

    private void disparaSplashScreen(){
        Log.i("script","disparaSplashScreen() splashChamada");
        if (!this.splashIniciada){
            this.splashIniciada = true;
            int tempoAnimacao = 2000;
            int tempoSplashCompleta = 7000;
            int maxTempoVerificacao = 10000;
            final AlphaAnimation animation1 = new AlphaAnimation(0.0f,1.0f);
            animation1.setDuration(tempoAnimacao);
            animation1.setFillAfter(true);

            final AlphaAnimation animation1Reverse = new AlphaAnimation(1.0f,0.0f);
            animation1Reverse.setDuration(tempoAnimacao);
            animation1Reverse.setFillAfter(true);

            if (BackgroundIntentService.getCadastroBasico().getNivelUsuario() <= 1.0){
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
                        if (BackgroundIntentService.getCadastroBasico().getNivelUsuario() < 1.0){
                            //showSnackbar("Favor logar novamente!");
                            mAuth.signOut();
                        }else if(BackgroundIntentService.getCadastroBasico().getNivelUsuario() == 1.0){
                            callConfiguracaoIncialActivity(null);
                        }else {
                            executarVerificacoes();
                        }
                    }
                },(tempoSplashCompleta));

            }else {
                splashLogoSalao20.setVisibility(View.VISIBLE);
                splashLogoSalao20.startAnimation(animation1);

                this.handlerUIThread.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("script","splashScreenInicial() executarVerificacoes");
                        executarVerificacoes();
                    }
                });
            }

            this.handlerUIThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i("script","splashScreenInicial() tempo esgotado");
                    //showSnackbar("Favor logar novamente!");
                    mAuth.signOut();
                    if (BackgroundIntentService.verificarMauth()){
                        Log.i("script","splashScreenInicial() tempo esgotado BIS ativo");

                    }else{
                        Log.i("script","splashScreenInicial() tempo esgotado BIS desativado");

                    }
                }
            },(maxTempoVerificacao));
        }else {
            Log.i("script","disparaSplashScreen() splash ja iniciada");
        }

        splashIniciada = true;
    }


    private void executarVerificacoes(){
        if (BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.SALAO) || BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CABELEIREIRO) || BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CLIENTE)){
            //TODO
            if (BackgroundIntentService.getCadastroBasico().getNivelUsuario() >= 2.0 && BackgroundIntentService.getCadastroBasico().getNivelUsuario() < 3.0){
                startService(new Intent(getApplicationContext(), BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoConfiguracaoInicial(),true));
                callConfiguracaoIncialActivity(null);
            }else if (BackgroundIntentService.getCadastroBasico().getNivelUsuario() >= 3.0){
                startService(new Intent(getApplicationContext(), BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoHome(),true));
                callHomeActivity();
            }
        }else {
            //TODO ouvir mudança no tipo usuario e aguardar
        }
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
}

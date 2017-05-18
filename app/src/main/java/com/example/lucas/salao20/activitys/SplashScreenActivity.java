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
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.intentServices.BackgroundIntentService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


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
    private ValueEventListener valueEventListenerCadastroBasico;

    //CONTROLES
    private boolean splashIniciada;
    static boolean splashScreenActivityAtiva;

    private CadastroBasico cadastroBasico;


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
                    stopService(new Intent(getApplicationContext(),BackgroundIntentService.class));
                    callLoginActivity();
                }else if (mAuth.getCurrentUser().getUid().isEmpty()){
                    mAuth.signOut();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("script","onStart() SplashScreenActivity");
        splashScreenActivityAtiva = true;
        disparaSplashScreen();
        if (!BackgroundIntentService.isBackgroundIntentServiceAtivo()){
            startService(new Intent(getApplicationContext(),BackgroundIntentService.class).putExtra(BackgroundIntentService.getSincronizacaoInicial(),true));
        }
        if (this.valueEventListenerCadastroBasico == null){
            this.valueEventListenerCadastroBasico = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getValue(CadastroBasico.class) != null){
                        cadastroBasico = dataSnapshot.getValue(CadastroBasico.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            BackgroundIntentService.getRefCadastrobasico().addValueEventListener(this.valueEventListenerCadastroBasico);
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("script","onStop() SplashScreenActivity");
        splashScreenActivityAtiva = false;
        mAuth.removeAuthStateListener(mAuthListener);
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
    }

    private void initHandler(){
        this.handlerUIThread = new Handler();
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
                    direcionarUsuario();
                }
            },(tempoSplashCompleta));
        }else {
            Log.i("script","disparaSplashScreen() splash ja iniciada");
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
                                mAuth.signOut();
                            }
                            break;
                        default:
                            mAuth.signOut();
                            break;
                    }

                }else {
                    mAuth.signOut();
                }
            }
        }else{
            mAuth.signOut();
        }
    }

    //GETTERS SETTERS
}

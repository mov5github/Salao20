package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.activitys.LoginActivity;
import com.example.lucas.salao20.activitys.SplashScreenActivity;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Lucas on 02/05/2017.
 */

public class SincronizacaoIntentService extends IntentService {
    private static boolean sincronizacaoIntentServiceAtivo;
    //ENUM
    private static final String SINCRONIZACAO_NIVEL_USUARIO = "SincronizacaoNivelUsuario_SincronizacaoIntentService";

    private boolean ativo;
    private boolean stopAll;
    private Context context;
    private Bundle bundle;
    private final ThreadWork threadWork = new ThreadWork();
    private Handler handlerThreadWork;

    //FIREBASE
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //DADOS
    private static DatabaseReference refNivelUsuario;


    public SincronizacaoIntentService() {
        super("SincronizacaoIntentService");
        this.ativo = true;
        this.stopAll = false;
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            this.bundle = intent.getExtras();
            if (this.bundle.containsKey("desligar") && this.bundle.getInt("desligar") == 1) {
                this.stopAll = true;
                this.ativo = false;
            } else {
                this.stopAll = false;
                this.ativo = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (this.ativo && !this.stopAll) {
            if (context == null) {
                this.context = this;
            }
            if (this.threadWork.getState() != Thread.State.RUNNABLE){
                this.threadWork.start();
                synchronized (this.threadWork){
                    try {
                        this.threadWork.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (this.ativo && !this.stopAll) {
            if (this.bundle != null){
                if (this.bundle.containsKey(SINCRONIZACAO_NIVEL_USUARIO)){
                    Log.i("script", SINCRONIZACAO_NIVEL_USUARIO);
                    this.bundle.remove(SINCRONIZACAO_NIVEL_USUARIO);
                    if (this.handlerThreadWork != null){
                        this.handlerThreadWork.post(new RunnableSincronizarNivelUsuario());
                    }
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sincronizacaoIntentServiceAtivo = false;
        if (context == null) {
            this.context = null;
        }
        threadWork.interrupt();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sincronizacaoIntentServiceAtivo = true;
    }

    //AUXILIAR

    //CLASSES
    private class ThreadWork extends Thread{
        @Override
        public void run() {
            super.run();
            if (handlerThreadWork == null){
                handlerThreadWork = new Handler();
            }

            mAuth = FirebaseAuth.getInstance();
            mAuthListener = getFirebaseAuthResultHandler();
            if (mAuth.getCurrentUser() == null){
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                stopAll = true;
            }else if (mAuth.getCurrentUser().getUid() == null || mAuth.getCurrentUser().getUid().isEmpty()){
                mAuth.signOut();
                Intent intent = new Intent(context, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                stopAll = true;
            }else {
                mAuth.addAuthStateListener(mAuthListener);
            }

            notify();
        }

        @Override
        public void interrupt() {
            super.interrupt();
            if (handlerThreadWork != null){
                handlerThreadWork.removeCallbacksAndMessages(null);
                handlerThreadWork = null;
            }
            if( mAuthListener != null ){
                mAuth.removeAuthStateListener( mAuthListener );
                mAuthListener = null;
            }
            if (mAuth != null){
                mAuth = null;
            }
        }

        private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
            Log.i("script","getFirebaseAuthResultHandler() login ");

            FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged SincronizacaoIntentService");

                    FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                    if( userFirebase == null || userFirebase.getUid()== null || userFirebase.getUid().isEmpty()){
                        Log.i("script","getFirebaseAuthResultHandler() userFirebase == null SincronizacaoIntentService");
                        handlerThreadWork.post(new RunnableAutenticacaoFalhou());
                    }else {
                        Log.i("script","getFirebaseAuthResultHandler() userFirebase != null SincronizacaoIntentService");
                    }
                }
            };
            return( callback );
        }
    }

    //RUNNABLES
    private class RunnableSincronizarNivelUsuario implements Runnable {
        @Override
        public void run() {
            if (mAuth.getCurrentUser().getUid() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                refNivelUsuario = LibraryClass.getFirebase().child("users").child(mAuth.getCurrentUser().getUid()).child("cadastroBasico").child("nivelUsuario");
                refNivelUsuario.keepSynced(true);
            }
        }
    }

    private class RunnableAutenticacaoFalhou implements Runnable {
        @Override
        public void run() {
            mAuth.removeAuthStateListener(mAuthListener);
            mAuth.signOut();
            stopAll = true;
            fecharActivitysAbertasIrLogin();
        }

        private void fecharActivitysAbertasIrLogin(){
            Intent intent = new Intent(context, SplashScreenActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    //GETTERS AND SETTERS
    public static DatabaseReference getRefNivelUsuario() {
        return refNivelUsuario;
    }

    public static boolean isSincronizacaoIntentServiceAtivo() {
        return sincronizacaoIntentServiceAtivo;
    }

    public static String getSincronizacaoNivelUsuario() {
        return SINCRONIZACAO_NIVEL_USUARIO;
    }
}

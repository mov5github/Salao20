package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.activitys.ErroActivity;
import com.example.lucas.salao20.activitys.LoginActivity;
import com.example.lucas.salao20.activitys.SplashScreenActivity;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.geral.ProfissionaisSalao;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.example.lucas.salao20.geral.FuncionamentoSalao;
import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;
import com.example.lucas.salao20.geral.ServicosSalao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 02/05/2017.
 */

public class BackgroundIntentService extends IntentService {
    private static boolean backgroundIntentServiceAtivo;

    //ENUM
    private static final String SINCRONIZACAO_INICIAL = "SincronizacaoInicial_BackgroundIntentService";
    private static final String SINCRONIZACAO_CONFIGURACAO_INICIAL = "SincronizacaoConfiguracaoInicial_BackgroundIntentService";
    private static final String SINCRONIZACAO_HOME = "SincronizacaoHome_BackgroundIntentService";

    private boolean ativo;
    private boolean stopAll;
    private Context context;
    private Bundle bundle;
    private Handler handlerIntentService;

    private final ThreadWork threadWork = new ThreadWork();
    private static Handler handlerThreadWork;

    //FIREBASE
    private static FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //REFERENCIAS
    private static DatabaseReference refCadastrobasico;
    private static DatabaseReference refFuncionamento;
    private static DatabaseReference refServicos;
    private static DatabaseReference refProfissionais;

    //OBJETOS
    private static CadastroBasico cadastroBasico = new CadastroBasico();
    private static FuncionamentoSalao funcionamentoSalao = new FuncionamentoSalao();
    private static ServicosSalao servicosSalao = new ServicosSalao();
    private static ProfissionaisSalao profissionaisSalao = new ProfissionaisSalao();

    //RUNNABLES



    public BackgroundIntentService() {
        super("BackgroundIntentService");
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
            init();
        }

        if (this.ativo && !this.stopAll) {
            if (threadWork.getState() != Thread.State.RUNNABLE){
                handlerIntentService.post(new RunnableIniciarThreadWork());
            }
        }

        if (this.ativo && !this.stopAll) {
            if (this.bundle != null){
                if (this.bundle.containsKey(SINCRONIZACAO_INICIAL)){
                    Log.i("script", SINCRONIZACAO_INICIAL);
                    this.bundle.remove(SINCRONIZACAO_INICIAL);
                    handlerIntentService.post(new RunnableSincronizacaoInicial());
                }
            }
            if (this.bundle != null){
                if (this.bundle.containsKey(SINCRONIZACAO_CONFIGURACAO_INICIAL)){
                    Log.i("script", SINCRONIZACAO_CONFIGURACAO_INICIAL);
                    this.bundle.remove(SINCRONIZACAO_CONFIGURACAO_INICIAL);
                    handlerIntentService.post(new RunnableSincronizacaoConfiguracaoInicial());
                }
            }
            if (this.bundle != null){
                if (this.bundle.containsKey(SINCRONIZACAO_HOME)){
                    Log.i("script", SINCRONIZACAO_HOME);
                    this.bundle.remove(SINCRONIZACAO_HOME);
                    handlerIntentService.post(new RunnableSincronizacaoHome());
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("script","onDestroy()");
        backgroundIntentServiceAtivo = false;
        this.stopAll = true;
        if (handlerIntentService != null){
            handlerIntentService.removeCallbacksAndMessages(null);
        }
        threadWork.interrupt();
        mAuth.removeAuthStateListener(mAuthListener);
        mAuth.signOut();
        removerEventsFirebase();
        fecharActivitysAbertasIrLogin();
    }

    private void init(){
        backgroundIntentServiceAtivo = true;
        if (context == null) {
            this.context = this;
        }
        if (handlerIntentService == null){
            handlerIntentService = new Handler();
        }
        if (mAuth == null){
            mAuth = FirebaseAuth.getInstance();
        }
        if (mAuthListener == null){
            mAuthListener = getFirebaseAuthResultHandler();
        }
    }

    private FirebaseAuth.AuthStateListener getFirebaseAuthResultHandler(){
        Log.i("script","getFirebaseAuthResultHandler() login ");

        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.i("script","getFirebaseAuthResultHandler() onAuthStateChanged SincronizacaoIntentService");

                FirebaseUser userFirebase = firebaseAuth.getCurrentUser();

                if( userFirebase == null || userFirebase.getUid().isEmpty()){
                    Log.i("script","getFirebaseAuthResultHandler() userFirebase == null SincronizacaoIntentService");
                    //stopAll = true;
                    stopService(new Intent(getApplicationContext(),BackgroundIntentService.class));
                }else {
                    Log.i("script","getFirebaseAuthResultHandler() userFirebase != null SincronizacaoIntentService");
                }
            }
        };
        return( callback );
    }

    //AUXILIAR
    private void fecharActivitysAbertasIrLogin() {
        Log.i("script","fecharActivitysAbertasIrLogin()");

        Intent intent = new Intent(context, ErroActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public static Boolean verificarMauth(){
        if (mAuth != null && mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
            return true;
        }else {
            return false;
        }
    }

    private void removerEventsFirebase(){
        //TODO
    }

    //CLASSES
    private class ThreadWork extends Thread{
        @Override
        public void run() {
            super.run();
            if (handlerThreadWork == null){
                handlerThreadWork = new Handler();
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
        }
    }

    //RUNNABLES
    private class RunnableIniciarThreadWork implements Runnable {
        @Override
        public void run() {
            threadWork.start();
            synchronized (threadWork){
                try {
                    threadWork.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class RunnableSincronizacaoInicial implements Runnable {
        @Override
        public void run() {
            if (handlerThreadWork != null){
                handlerThreadWork.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mAuth.getCurrentUser() != null){
                            if (!mAuth.getCurrentUser().getUid().isEmpty()){
                                refCadastrobasico = LibraryClass.getFirebase().child("users").child(mAuth.getCurrentUser().getUid()).child("cadastroBasico");
                                refCadastrobasico.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(Map.class) != null) {
                                            cadastroBasico.receberDoFirebase(dataSnapshot.getValue(Map.class));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    private class RunnableSincronizacaoConfiguracaoInicial implements Runnable {
        @Override
        public void run() {
            if (handlerThreadWork != null){
                handlerThreadWork.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mAuth.getCurrentUser() != null){
                            if (!mAuth.getCurrentUser().getUid().isEmpty()){
                                if (BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.SALAO)){
                                    refFuncionamento = LibraryClass.getFirebase().child("users").child(mAuth.getCurrentUser().getUid()).child("configuracoes").child("funcionamento");
                                    refFuncionamento.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue(JSONObject.class) != null) {
                                                funcionamentoSalao.receberDoFirebase(dataSnapshot.getValue(JSONObject.class));
                                            }else {
                                                funcionamentoSalao.setFuncionamentoDoSalao(new ArrayList<Funcionamento>());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    refServicos = LibraryClass.getFirebase().child("users").child(mAuth.getCurrentUser().getUid()).child("configuracoes").child("servicos");
                                    refServicos.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue(JSONObject.class) != null) {
                                                servicosSalao.receberDoFirebase(dataSnapshot.getValue(JSONObject.class));
                                            }else {
                                                servicosSalao.setServicosSalao(new ArrayList<Servico>());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    refProfissionais = LibraryClass.getFirebase().child("users").child(mAuth.getCurrentUser().getUid()).child("configuracoes").child("profissionais");
                                    refProfissionais.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.getValue(JSONObject.class) != null) {
                                                profissionaisSalao.receberDoFirebase(dataSnapshot.getValue(JSONObject.class));
                                            }else {
                                                profissionaisSalao.setProfissionais(new ArrayList<Profissional>());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }else if (BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CABELEIREIRO)){
                                    //TODO
                                }else if (BackgroundIntentService.getCadastroBasico().getTipoUsuario().equals(TipoUsuarioENUM.CLIENTE)){
                                    //TODO
                                }
                            }
                        }
                    }
                });
            }
        }
    }

    private class RunnableSincronizacaoHome implements Runnable {
        @Override
        public void run() {
            if (handlerThreadWork != null){
                handlerThreadWork.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mAuth.getCurrentUser() != null){
                            if (!mAuth.getCurrentUser().getUid().isEmpty()){
                                refCadastrobasico = LibraryClass.getFirebase().child("users").child(mAuth.getCurrentUser().getUid()).child("cadastroBasico");
                                refCadastrobasico.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.getValue(Map.class) != null) {
                                            cadastroBasico.receberDoFirebase(dataSnapshot.getValue(Map.class));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }
                });
            }
        }
    }

    //ATUALIZADORES
    public static void salvarTipoUsuario(final String tipoUsuario){
        handlerThreadWork.post(new Runnable() {
            @Override
            public void run() {
                cadastroBasico.setTipoUsuario(tipoUsuario);
                cadastroBasico.setNivelUsuario(2.0);

                Map<String, Object> postValues = new HashMap<String, Object>();
                postValues.put("tipoUsuario",cadastroBasico.getTipoUsuario());
                postValues.put("nivelUsuario",cadastroBasico.getNivelUsuario());

                refCadastrobasico.updateChildren(postValues);
            }
        });
    }

    //GETTERS AND SETTERS
    public static boolean isBackgroundIntentServiceAtivo() {
        return backgroundIntentServiceAtivo;
    }

    public static String getSincronizacaoInicial() {
        return SINCRONIZACAO_INICIAL;
    }

    public static String getSincronizacaoConfiguracaoInicial() {
        return SINCRONIZACAO_CONFIGURACAO_INICIAL;
    }

    public static String getSincronizacaoHome() {
        return SINCRONIZACAO_HOME;
    }

    public static CadastroBasico getCadastroBasico() {
        return cadastroBasico;
    }

    public static DatabaseReference getRefCadastrobasico() {
        return refCadastrobasico;
    }

}

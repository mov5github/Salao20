package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.activitys.ErroActivity;
import com.example.lucas.salao20.activitys.LoginActivity;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.CadastroBasico;
import com.example.lucas.salao20.geral.ProfissionaisSalao;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.example.lucas.salao20.geral.FuncionamentoSalao;
import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;
import com.example.lucas.salao20.geral.ServicosSalao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
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
    private static final String SINCRONIZACAO_CLIENTE = "SincronizacaoCliente_BackgroundIntentService";
    private static final String SINCRONIZACAO_SALAO = "SincronizacaoSalao_BackgroundIntentService";
    private static final String SINCRONIZACAO_CABELEIREIRO = "SincronizacaoCabeleireiro_BackgroundIntentService";


    private boolean ativo;
    private boolean stopAll;
    private Context context;
    private Bundle bundle;
    private Handler handlerIntentService;

    private final ThreadWork threadWork = new ThreadWork();
    private static Handler handlerThreadWork;

    //FIREBASE
    private static FirebaseAuth mAuth;

    //REFERENCIAS
    private static DatabaseReference refCadastrobasico;
    private static DatabaseReference refFuncionamento;
    private static DatabaseReference refServicos;
    private static DatabaseReference refProfissionais;

    //OBJETOS
    private static CadastroBasico cadastroBasico = new CadastroBasico();
    private static FuncionamentoSalao funcionamentoSalao = new FuncionamentoSalao();
    private static ServicosSalao servicosSalao;
    private static ProfissionaisSalao profissionaisSalao;

    //RUNNABLES

    //CONTROLES
    private static boolean sincronizacaoInicialIniciada = false;
    private static boolean sincronizacaoConfiguracoesIniciaisiniciada = false;



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
                if (this.bundle.containsKey(SINCRONIZACAO_CLIENTE)){
                    Log.i("script", SINCRONIZACAO_CLIENTE);
                    this.bundle.remove(SINCRONIZACAO_CLIENTE);
                    handlerIntentService.post(new RunnableSincronizacaoCliente());
                }
            }
            if (this.bundle != null){
                if (this.bundle.containsKey(SINCRONIZACAO_SALAO)){
                    Log.i("script", SINCRONIZACAO_SALAO);
                    this.bundle.remove(SINCRONIZACAO_SALAO);
                    handlerIntentService.post(new RunnableSincronizacaoSalao());
                }
            }
            if (this.bundle != null){
                if (this.bundle.containsKey(SINCRONIZACAO_CABELEIREIRO)){
                    Log.i("script", SINCRONIZACAO_CABELEIREIRO);
                    this.bundle.remove(SINCRONIZACAO_CABELEIREIRO);
                    handlerIntentService.post(new RunnableSincronizacaoCabeleireiro());
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
    }


    //AUXILIAR
    private void fecharActivitysAbertasIrLogin() {
        Log.i("script","fecharActivitysAbertasIrLogin()");
        if (!LoginActivity.isLoginActivityAtiva()){
            Intent intent = new Intent(context, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
                        if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                            sincronizacaoInicialIniciada = true;
                            refCadastrobasico = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(CadastroBasico.getCADASTRO_BASICO());
                            refCadastrobasico.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.getValue(Map.class) != null){
                                        cadastroBasico.receberDoFirebase(dataSnapshot.getValue(Map.class));
                                    }
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    if (dataSnapshot.getValue(Map.class) != null){
                                        cadastroBasico.receberDoFirebase(dataSnapshot.getValue(Map.class));
                                    }
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.getValue(Map.class) != null){
                                        cadastroBasico.receberDoFirebaseRemover(dataSnapshot.getValue(Map.class));
                                    }
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                });
            }
        }
    }

    private class RunnableSincronizacaoCliente implements Runnable {
        @Override
        public void run() {
            if (handlerThreadWork != null){
                handlerThreadWork.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO
                    }
                });
            }
        }
    }

    private class RunnableSincronizacaoCabeleireiro implements Runnable {
        @Override
        public void run() {
            if (handlerThreadWork != null){
                handlerThreadWork.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO
                    }
                });
            }
        }
    }

    private class RunnableSincronizacaoSalao implements Runnable {
        @Override
        public void run() {
            if (handlerThreadWork != null){
                handlerThreadWork.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO
                    }
                });
            }
        }
    }

    private static class RunnableSincronizacaoConfiguracaoInicial implements Runnable {
        @Override
        public void run() {
            if (handlerThreadWork != null){
                handlerThreadWork.post(new Runnable() {
                    @Override
                    public void run() {
                        //TODO
                    }
                });
            }
        }
    }


    //SINCRONIZADORES
    public static void sincronizacaoCadastroInicial(){
        handlerThreadWork.post(new RunnableSincronizacaoConfiguracaoInicial());
    }

    //ATUALIZADORES
    public static void salvarTipoUsuario(final String tipoUsuario){
        handlerThreadWork.post(new Runnable() {
            @Override
            public void run() {
                cadastroBasico.setTipoUsuario(tipoUsuario);
                cadastroBasico.setNivelUsuario(2.0);

                Map<String, Object> postValues = new HashMap<String, Object>();
                postValues.put(CadastroBasico.getTIPO_USUARIO(),cadastroBasico.getTipoUsuario());
                postValues.put(CadastroBasico.getNIVEL_USUARIO(),cadastroBasico.getNivelUsuario());

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

    public static CadastroBasico getCadastroBasico() {
        return cadastroBasico;
    }

    public static DatabaseReference getRefCadastrobasico() {
        return refCadastrobasico;
    }

    public static String getSincronizacaoCliente() {
        return SINCRONIZACAO_CLIENTE;
    }

    public static String getSincronizacaoSalao() {
        return SINCRONIZACAO_SALAO;
    }

    public static String getSincronizacaoCabeleireiro() {
        return SINCRONIZACAO_CABELEIREIRO;
    }
}

package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.dao.CadastroInicialDAO;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.model.CadastroInicial;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Lucas on 17/03/2017.
 */

public class SincronizarBancosIntentService extends IntentService{
    static boolean intentServiceWait;

    private boolean ativo;
    private boolean stopAll;
    private Context mContext;

    private String uid;

    //CADASTROS INICIAIS
    private CadastroInicial cadastroInicialFirebase;
    private CadastroInicial cadastroInicialBD;
    private CadastroInicial cadastroInicialBDCloud;



    //CONTROLE
    private boolean encontrado;
    private boolean salvo;

    //THREAD
    private final ThreadBuscarCadastroInicialFirebase threadBuscarCadastroInicialFirebase = new ThreadBuscarCadastroInicialFirebase();
    private final ThreadSalvarCadastroInicialFirebase threadSalvarCadastroInicialFirebase = new ThreadSalvarCadastroInicialFirebase();

    //DAO
    private CadastroInicialDAO cadastroInicialDAO;


    public SincronizarBancosIntentService() {
        super("SincronizarBancosIntentService");
        this.ativo = true;
        this.stopAll = false;
        this.mContext = this;
        this.uid = "";
        this.encontrado = false;
        this.salvo = false;
        intentServiceWait = false;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("script","onHandleIntent");
        if (this.ativo && !this.stopAll){
            initCadastroIniciais();
            this.threadBuscarCadastroInicialFirebase.start();
        }

        while (this.ativo && !this.stopAll && !this.encontrado){
            synchronized(this.threadBuscarCadastroInicialFirebase) {
                Log.i("script", "onHandleIntent waiting buscar firebase");
                try {
                    intentServiceWait = true;
                    this.threadBuscarCadastroInicialFirebase.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            intentServiceWait = false;
            Log.i("script","onHandleIntent firebase buscar respondeu");
        }
        //TODO destruir threadBuscarCadastroInicialFirebase
        Log.i("script","onHandleIntent firebase buscar encontrado");

        //busca o cadastro inicial no Bd e BdCloud
        if (this.cadastroInicialDAO == null){
            this.cadastroInicialDAO = new CadastroInicialDAO(this);
        }
        this.cadastroInicialBD = this.cadastroInicialDAO.buscarCadastroInicialPorUID(this.uid);
        this.cadastroInicialBDCloud = this.cadastroInicialDAO.buscarCadastroInicialPorUIDCloud(this.uid);
        if (this.cadastroInicialBDCloud == null && this.ativo && !this.stopAll){
            this.cadastroInicialBDCloud = new CadastroInicial();
            this.cadastroInicialBDCloud.setUid(this.uid);
        }

        //sincroniza bdFirebase com bdCloud
        if(this.cadastroInicialFirebase.getVersao() != null && this.ativo && !this.stopAll){
            if ((this.cadastroInicialBDCloud.getVersao() == null || this.cadastroInicialBDCloud.getVersao() < this.cadastroInicialFirebase.getVersao()) && this.ativo && !this.stopAll){
                if (this.cadastroInicialBDCloud.get_id() != null){
                    this.cadastroInicialFirebase.set_id(this.cadastroInicialBDCloud.get_id());
                }
                long result = -1;
                while (this.ativo && !this.stopAll && result == -1){
                    result = this.cadastroInicialDAO.salvarCadastroInicialCloudNaoVersionando(this.cadastroInicialFirebase);
                }
            }
        }

        //sincroniza bd com bdFirebase
        if ((this.cadastroInicialBDCloud.getVersao() == null || this.cadastroInicialBD.getVersao() > this.cadastroInicialBDCloud.getVersao()) && this.ativo && !this.stopAll){
            if (this.ativo && !this.stopAll){
                this.threadSalvarCadastroInicialFirebase.start();
                // TODO passar este bloco para dentro do while abaixo eapos testar estado apos cancelamento da thread
            }
            while (this.ativo && !this.stopAll && !this.salvo){
                synchronized(this.threadSalvarCadastroInicialFirebase) {
                    Log.i("script", "onHandleIntent waiting salvar firebase");
                    try {
                        intentServiceWait = true;
                        this.threadSalvarCadastroInicialFirebase.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                intentServiceWait = false;
                Log.i("script","onHandleIntent firebase salvar respondeu");
            }
            Log.i("script","onHandleIntent firebase salvar salvo");
            //TODO destruir threadSalvarCadastroInicialFirebase

            if (this.cadastroInicialBDCloud.get_id() != null && this.ativo && !this.stopAll){
                int id = this.cadastroInicialBDCloud.get_id();
                this.cadastroInicialBDCloud = this.cadastroInicialBD;
                this.cadastroInicialBDCloud.set_id(id);
                long result = -1;
                while (this.ativo && !this.stopAll && result == -1){
                    result = this.cadastroInicialDAO.salvarCadastroInicialCloudNaoVersionando(this.cadastroInicialBDCloud);
                }
            }else if (this.ativo && !this.stopAll){
                this.cadastroInicialBDCloud = this.cadastroInicialBD;
                this.cadastroInicialBDCloud.set_id(null);
                long result = -1;
                while (this.ativo && !this.stopAll && result == -1){
                    result = this.cadastroInicialDAO.salvarCadastroInicialCloudNaoVersionando(this.cadastroInicialBDCloud);
                }
            }
        }else if (this.ativo && !this.stopAll){
            int id = this.cadastroInicialBD.get_id();
            this.cadastroInicialBD = this.cadastroInicialBDCloud;
            this.cadastroInicialBD.set_id(id);
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroInicialDAO.salvarCadastroInicialNaoVersionando(this.cadastroInicialBD);
            }
        }


    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.get("uid") != null){
                this.uid = bundle.getString("uid");
            }
            int desligar = bundle.getInt("desligar");
            if (desligar == 1){
                this.stopAll = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO
    }

    private void initCadastroIniciais(){
        if (this.cadastroInicialFirebase == null){
            this.cadastroInicialFirebase = new CadastroInicial();
        }
        if (this.cadastroInicialBD == null){
            this.cadastroInicialBD = new CadastroInicial();
        }
        if (this.cadastroInicialBDCloud == null){
            this.cadastroInicialBDCloud = new CadastroInicial();
        }
        this.cadastroInicialFirebase.setUid(this.uid);
        this.cadastroInicialBD.setUid(this.uid);
        this.cadastroInicialBDCloud.setUid(this.uid);
    }

    private class ThreadBuscarCadastroInicialFirebase extends Thread{
        //FIREBASE
        private DatabaseReference firebaseCadastroInicial;
        private ValueEventListener vELCadastroInicial;

        @Override
        public void run(){
            Log.i("script","ThreadBuscarCadastroInicialFirebase");
            initFirebase();
            this.firebaseCadastroInicial.addValueEventListener(this.vELCadastroInicial);
        }

        @Override
        public void interrupt() {
            super.interrupt();
            if (intentServiceWait){
                synchronized (threadBuscarCadastroInicialFirebase){
                    threadBuscarCadastroInicialFirebase.notify();
                }
            }
        }

        private void initFirebase(){
            if (this.firebaseCadastroInicial == null){
                this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.CadastroInicial.TABELA);
                //this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child("1").child(DatabaseHelper.CadastroInicial.TABELA);
            }
            if (this.vELCadastroInicial == null){
                this.vELCadastroInicial = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELCadastroInicial dataSnapshot == null");
                            encontrado = false;
                            if (intentServiceWait){
                                synchronized (threadBuscarCadastroInicialFirebase){
                                    threadBuscarCadastroInicialFirebase.notify();
                                }
                            }
                        }else {
                            Log.i("script","vELCadastroInicial dataSnapshot != null");
                            encontrado = true;
                            if (map.containsKey(DatabaseHelper.CadastroInicial.VERSAO)){
                                cadastroInicialFirebase.setVersao((Integer) map.get(DatabaseHelper.CadastroInicial.VERSAO));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO)){
                                cadastroInicialFirebase.setDataModificalao((String) map.get(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)){
                                cadastroInicialFirebase.setNivelUsuario((Double) map.get(DatabaseHelper.CadastroInicial.NIVEL_USUARIO));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.TIPO_USUARIO)){
                                cadastroInicialFirebase.setTipoUsuario((String) map.get(DatabaseHelper.CadastroInicial.TIPO_USUARIO));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.CODIGO_UNICO)){
                                cadastroInicialFirebase.setCodigoUnico((Integer) map.get(DatabaseHelper.CadastroInicial.CODIGO_UNICO));
                            }
                            if (intentServiceWait){
                                synchronized (threadBuscarCadastroInicialFirebase){
                                    threadBuscarCadastroInicialFirebase.notify();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELCadastroInicial onCancelled");
                        if (intentServiceWait){
                            synchronized (threadBuscarCadastroInicialFirebase){
                                threadBuscarCadastroInicialFirebase.notify();
                            }
                        }
                    }
                };
            }
        }
    }

    private class ThreadSalvarCadastroInicialFirebase extends Thread{
        private DatabaseReference firebaseCadastroInicial;
        private DatabaseReference.CompletionListener completionListenerCadastroInicial;

        @Override
        public void run(){
            Log.i("script","ThreadSalvarCadastroInicialFirebase");
            if (this.completionListenerCadastroInicial == null){
                this.completionListenerCadastroInicial = new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            Log.i("script","completionListenerCadastroInicial cadastroInicial nao foi salvo");
                            salvo = false;
                            if (intentServiceWait){
                                synchronized (threadSalvarCadastroInicialFirebase){
                                    threadSalvarCadastroInicialFirebase.notify();
                                }
                            }
                        }else{
                            Log.i("script","completionListenerCadastroInicial cadastroInicial foi salvo");
                            salvo = true;
                            if (intentServiceWait){
                                synchronized (threadSalvarCadastroInicialFirebase){
                                    threadSalvarCadastroInicialFirebase.notify();
                                }
                            }
                        }
                    }
                };
            }
            salvarNoFirebase();
        }

        @Override
        public void interrupt() {
            super.interrupt();
            if (intentServiceWait){
                synchronized (threadSalvarCadastroInicialFirebase){
                    threadSalvarCadastroInicialFirebase.notify();
                }
            }
        }

        private void salvarNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseCadastroInicial == null){
                this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.CadastroInicial.TABELA);
                //this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child("1").child(DatabaseHelper.CadastroInicial.TABELA);
            }
            if( completionListener.length == 0 ){
                if (cadastroInicialBD.getVersao() != null && (cadastroInicialBDCloud.getVersao() == null || cadastroInicialBD.getVersao() > cadastroInicialBDCloud.getVersao())){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.VERSAO).setValue(cadastroInicialBD.getVersao());
                }
                if (cadastroInicialBD.getDataModificalao() != null && (cadastroInicialBDCloud.getDataModificalao() == null || !cadastroInicialBD.getDataModificalao().equals(cadastroInicialBDCloud.getDataModificalao()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO).setValue(cadastroInicialBD.getDataModificalao());
                }
                if (cadastroInicialBD.getNivelUsuario() != null && (cadastroInicialBDCloud.getNivelUsuario() == null || !cadastroInicialBD.getNivelUsuario().equals(cadastroInicialBDCloud.getNivelUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroInicialBD.getNivelUsuario());
                }
                if (cadastroInicialBD.getTipoUsuario() != null && (cadastroInicialBDCloud.getTipoUsuario() == null || !cadastroInicialBD.getTipoUsuario().equals(cadastroInicialBDCloud.getTipoUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroInicialBD.getTipoUsuario());
                }
                if (cadastroInicialBD.getCodigoUnico() != null && (cadastroInicialBDCloud.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDCloud.getCodigoUnico()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroInicialBD.getCodigoUnico());
                }
            }
            else{
                if (cadastroInicialBD.getVersao() != null && (cadastroInicialBDCloud.getVersao() == null || cadastroInicialBD.getVersao() > cadastroInicialBDCloud.getVersao())){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.VERSAO).setValue(cadastroInicialBD.getVersao(), completionListener[0]);
                }
                if (cadastroInicialBD.getDataModificalao() != null && (cadastroInicialBDCloud.getDataModificalao() == null || !cadastroInicialBD.getDataModificalao().equals(cadastroInicialBDCloud.getDataModificalao()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO).setValue(cadastroInicialBD.getDataModificalao(), completionListener[0]);
                }
                if (cadastroInicialBD.getNivelUsuario() != null && (cadastroInicialBDCloud.getNivelUsuario() == null || !cadastroInicialBD.getNivelUsuario().equals(cadastroInicialBDCloud.getNivelUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroInicialBD.getNivelUsuario(), completionListener[0]);
                }
                if (cadastroInicialBD.getTipoUsuario() != null && (cadastroInicialBDCloud.getTipoUsuario() == null || !cadastroInicialBD.getTipoUsuario().equals(cadastroInicialBDCloud.getTipoUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroInicialBD.getTipoUsuario(), completionListener[0]);
                }
                if (cadastroInicialBD.getCodigoUnico() != null && (cadastroInicialBDCloud.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDCloud.getCodigoUnico()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroInicialBD.getCodigoUnico(), completionListener[0]);
                }
            }
        }
    }


}

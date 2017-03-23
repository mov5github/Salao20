package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.activitys.SplashScreenActivity;
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
    private boolean ativo;
    private boolean stopAll;

    private String uid;

    //CADASTROS INICIAIS
    private CadastroInicial cadastroInicialFirebase;
    private CadastroInicial cadastroInicialBD;
    private CadastroInicial cadastroInicialBDCloud;

    //THREAD
    private ThreadBuscarCadastroInicialFirebase threadBuscarCadastroInicialFirebase;
    private ThreadSalvarCadastroInicialFirebase threadSalvarCadastroInicialFirebase;

    //DAO
    private CadastroInicialDAO cadastroInicialDAO;


    public SincronizarBancosIntentService() {
        super("SincronizarBancosIntentService");
        this.ativo = true;
        this.stopAll = false;
        this.uid = "";
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.get("uid") != null){
                this.uid = bundle.getString("uid");
            }
            if (bundle.containsKey("desligar") && bundle.getInt("desligar") == 1){
                initCadastroIniciais();
                this.stopAll = true;
                this.ativo = false;
                if (this.threadBuscarCadastroInicialFirebase != null){
                    this.threadBuscarCadastroInicialFirebase.interrupt();
                    this.threadBuscarCadastroInicialFirebase = null;
                }
                if (this.threadSalvarCadastroInicialFirebase != null){
                    this.threadSalvarCadastroInicialFirebase.interrupt();
                    this.threadSalvarCadastroInicialFirebase = null;
                }
                if (this.cadastroInicialDAO != null){
                    this.cadastroInicialDAO.fechar();
                    this.cadastroInicialDAO = null;
                }
            }else {
                this.stopAll = false;
                this.ativo = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i("script","onHandleIntent");
        if (this.ativo && !this.stopAll){
            initCadastroIniciais();
            this.threadBuscarCadastroInicialFirebase = new ThreadBuscarCadastroInicialFirebase();
            this.threadBuscarCadastroInicialFirebase.start();
            try {
                this.threadBuscarCadastroInicialFirebase.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //busca o cadastro inicial no Bd e BdCloud
        if (this.ativo && !this.stopAll){
            if (this.cadastroInicialDAO == null){
                this.cadastroInicialDAO = new CadastroInicialDAO(this);
            }
            this.cadastroInicialBD = this.cadastroInicialDAO.buscarCadastroInicialPorUID(this.uid);
            this.cadastroInicialBDCloud = this.cadastroInicialDAO.buscarCadastroInicialPorUIDCloud(this.uid);
            if (this.cadastroInicialBDCloud == null && this.ativo && !this.stopAll){
                this.cadastroInicialBDCloud = new CadastroInicial();
                this.cadastroInicialBDCloud.setUid(this.uid);
            }
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
                this.threadSalvarCadastroInicialFirebase = new ThreadSalvarCadastroInicialFirebase();
                this.threadSalvarCadastroInicialFirebase.start();
                try {
                    this.threadSalvarCadastroInicialFirebase.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


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

        if (this.threadBuscarCadastroInicialFirebase != null){
            this.threadBuscarCadastroInicialFirebase.interrupt();
            this.threadBuscarCadastroInicialFirebase = null;
        }
        if (this.threadSalvarCadastroInicialFirebase != null){
            this.threadSalvarCadastroInicialFirebase.interrupt();
            this.threadSalvarCadastroInicialFirebase = null;
        }

        if (this.ativo && !this.stopAll && SplashScreenActivity.isSplashScreenActivityAtiva()){
            SplashScreenActivity.setCadastroInicialBD(this.cadastroInicialBD);
            sendBroadcast(new Intent(SplashScreenActivity.getBrodcastReceiverBancosSincronizados()));
        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.threadBuscarCadastroInicialFirebase != null){
            this.threadBuscarCadastroInicialFirebase.interrupt();
            this.threadBuscarCadastroInicialFirebase = null;
        }
        if (this.threadSalvarCadastroInicialFirebase != null){
            this.threadSalvarCadastroInicialFirebase.interrupt();
            this.threadSalvarCadastroInicialFirebase = null;
        }
        if (this.cadastroInicialDAO != null){
            this.cadastroInicialDAO.fechar();
            this.cadastroInicialDAO = null;
        }
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
            this.firebaseCadastroInicial.addListenerForSingleValueEvent(this.vELCadastroInicial);
            boolean msgExibida = false;
            while (!isInterrupted() && !stopAll){
                if (!msgExibida){
                    msgExibida = true;
                    Log.i("script","ThreadBuscarCadastroInicialFirebase aguardando resposta firebase ...");
                }
            }
            if (threadBuscarCadastroInicialFirebase != null){
                threadBuscarCadastroInicialFirebase.interrupt();
                threadBuscarCadastroInicialFirebase = null;
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            this.firebaseCadastroInicial.removeEventListener(this.vELCadastroInicial);
        }

        private void initFirebase(){
            if (this.firebaseCadastroInicial == null){
                this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.CadastroInicial.TABELA);
            }
            if (this.vELCadastroInicial == null){
                this.vELCadastroInicial = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELCadastroInicial dataSnapshot == null");
                            if (threadBuscarCadastroInicialFirebase != null){
                                threadBuscarCadastroInicialFirebase.interrupt();
                                threadBuscarCadastroInicialFirebase = null;
                            }
                        }else {
                            Log.i("script","vELCadastroInicial dataSnapshot != null");
                            if (map.containsKey(DatabaseHelper.CadastroInicial.VERSAO)){
                                cadastroInicialFirebase.setVersao(Integer.valueOf(map.get(DatabaseHelper.CadastroInicial.VERSAO).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO)){
                                cadastroInicialFirebase.setDataModificalao((String) map.get(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)){
                                cadastroInicialFirebase.setNivelUsuario(Double.valueOf(map.get(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.TIPO_USUARIO)){
                                cadastroInicialFirebase.setTipoUsuario((String) map.get(DatabaseHelper.CadastroInicial.TIPO_USUARIO));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroInicial.CODIGO_UNICO)){
                                cadastroInicialFirebase.setCodigoUnico(Integer.valueOf(map.get(DatabaseHelper.CadastroInicial.CODIGO_UNICO).toString()));
                            }
                            if (threadBuscarCadastroInicialFirebase != null){
                                threadBuscarCadastroInicialFirebase.interrupt();
                                threadBuscarCadastroInicialFirebase = null;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELCadastroInicial onCancelled");
                        if (threadBuscarCadastroInicialFirebase != null){
                            threadBuscarCadastroInicialFirebase.interrupt();
                            threadBuscarCadastroInicialFirebase = null;
                        }
                    }
                };
            }
        }
    }

    private class ThreadSalvarCadastroInicialFirebase extends Thread{
        private DatabaseReference firebaseCadastroInicial;
        private DatabaseReference.CompletionListener completionListenerCadastroInicial;

        //CONTROLE
        private boolean salvo;
        private boolean aguardando;

        @Override
        public void run(){
            Log.i("script","ThreadSalvarCadastroInicialFirebase");
            initFirebase();
            this.salvo = false;

            while (!isInterrupted() && !stopAll && !salvo){
                this.aguardando = true;
                salvarNoFirebase(this.completionListenerCadastroInicial);

                boolean msgExibida = false;
                while (!isInterrupted() && !stopAll && this.aguardando){
                    if (!msgExibida){
                        msgExibida = true;
                        Log.i("script","ThreadSalvarCadastroInicialFirebase aguardando resposta firebase ...");
                    }
                }
            }

            if (threadSalvarCadastroInicialFirebase != null){
                threadSalvarCadastroInicialFirebase.interrupt();
                threadSalvarCadastroInicialFirebase = null;
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
        }

        private void initFirebase(){
            if (this.completionListenerCadastroInicial == null){
                this.completionListenerCadastroInicial = new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            Log.i("script","completionListenerCadastroInicial cadastroInicial nao foi salvo");
                            salvo = false;
                            aguardando = false;
                        }else{
                            Log.i("script","completionListenerCadastroInicial cadastroInicial foi salvo");
                            salvo = true;
                            aguardando = false;
                            if (threadSalvarCadastroInicialFirebase != null){
                                threadSalvarCadastroInicialFirebase.interrupt();
                                threadSalvarCadastroInicialFirebase = null;
                            }
                        }
                    }
                };
            }
        }

        private void salvarNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseCadastroInicial == null){
                this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.CadastroInicial.TABELA);
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
                if (cadastroInicialBD.getCodigoUnico() != null && cadastroInicialBD.getCodigoUnico() != 0 && (cadastroInicialBDCloud.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDCloud.getCodigoUnico()))){
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
                if (cadastroInicialBD.getCodigoUnico() != null && cadastroInicialBD.getCodigoUnico() != 0 && (cadastroInicialBDCloud.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDCloud.getCodigoUnico()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroInicialBD.getCodigoUnico(), completionListener[0]);
                }
            }
        }
    }


}

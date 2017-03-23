package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.activitys.CadastroInicialActivity;
import com.example.lucas.salao20.dao.CadastroInicialDAO;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.model.CadastroInicial;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Lucas on 21/03/2017.
 */

public class AtualizarCadastroInicialIntentService extends IntentService {
    private boolean ativo;
    private boolean stopAll;
    private String uid;

    //CADASTROS INICIAIS
    private CadastroInicial cadastroInicialBD;
    private CadastroInicial cadastroInicialBDOld;
    private CadastroInicial cadastroInicialBDCloud;

    //DAO
    private CadastroInicialDAO cadastroInicialDAO;

    //THREAD
    private AtualizarCadastroInicialIntentService.ThreadSalvarCadastroInicialFirebase threadSalvarCadastroInicialFirebase;

    public AtualizarCadastroInicialIntentService() {
        super("AtualizarCadastroInicialIntentService");
        this.ativo = true;
        this.stopAll = false;
        this.uid = "";
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();

            if (bundle.containsKey("desligar") && bundle.getInt("desligar") == 1){
                initCadastroIniciais();
                this.stopAll = true;
                this.ativo = false;
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
        //INICIA OS CADASTROS INICIAIS
        if (this.ativo && !this.stopAll){
            if (intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                if (bundle.containsKey(DatabaseHelper.CadastroInicial.UID)){
                    this.uid = bundle.getString(DatabaseHelper.CadastroInicial.UID);
                }else {
                    this.ativo = false;
                    stopSelf();
                }
            }
            if (this.cadastroInicialDAO == null){
                this.cadastroInicialDAO = new CadastroInicialDAO(this);
            }
            this.cadastroInicialBD = this.cadastroInicialDAO.buscarCadastroInicialPorUID(this.uid);
            if (this.cadastroInicialBD == null){
                this.ativo = false;
                stopSelf();
            }

            this.cadastroInicialBDOld = this.cadastroInicialDAO.buscarCadastroInicialPorUID(this.uid);
            if (this.cadastroInicialBDOld == null){
                this.ativo = false;
                stopSelf();
            }

            this.cadastroInicialBDCloud = this.cadastroInicialDAO.buscarCadastroInicialPorUIDCloud(this.uid);
            if (this.cadastroInicialBDCloud == null){
                this.ativo = false;
                stopSelf();
            }
        }

        //ATUALIZA VALORES NO BD
        if (this.ativo && !this.stopAll){
            if (intent.getExtras() != null) {
                Bundle bundle = intent.getExtras();
                if (bundle.containsKey(DatabaseHelper.CadastroInicial.TIPO_USUARIO)){
                    this.cadastroInicialBD.setTipoUsuario(bundle.getString(DatabaseHelper.CadastroInicial.TIPO_USUARIO));
                }
                if (bundle.containsKey(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)){
                    this.cadastroInicialBD.setNivelUsuario(bundle.getDouble(DatabaseHelper.CadastroInicial.NIVEL_USUARIO));
                }
                if (bundle.containsKey(DatabaseHelper.CadastroInicial.CODIGO_UNICO)){
                    this.cadastroInicialBD.setCodigoUnico(bundle.getInt(DatabaseHelper.CadastroInicial.CODIGO_UNICO));
                }
            }

            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroInicialDAO.salvarCadastroInicial(this.cadastroInicialBD);
            }
        }

        //ATUALIZA VALORES NO FIREBASE
        if (this.ativo && !this.stopAll){
            if (this.ativo && !this.stopAll){
                this.threadSalvarCadastroInicialFirebase = new AtualizarCadastroInicialIntentService.ThreadSalvarCadastroInicialFirebase();
                this.threadSalvarCadastroInicialFirebase.start();
                try {
                    this.threadSalvarCadastroInicialFirebase.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // ATUALIZA BD CLOUD
        if (this.ativo && !this.stopAll){
            if (cadastroInicialBD.getVersao() != null && (cadastroInicialBDCloud.getVersao() == null || cadastroInicialBD.getVersao() > cadastroInicialBDCloud.getVersao())){
                if (cadastroInicialBD.getDataModificalao() != null && (cadastroInicialBDCloud.getDataModificalao() == null || !cadastroInicialBD.getDataModificalao().equals(cadastroInicialBDCloud.getDataModificalao()))){
                    this.cadastroInicialBDCloud.setDataModificalao(this.cadastroInicialBD.getDataModificalao());
                }
                if (cadastroInicialBD.getNivelUsuario() != null && (cadastroInicialBDCloud.getNivelUsuario() == null || !cadastroInicialBD.getNivelUsuario().equals(cadastroInicialBDCloud.getNivelUsuario()))){
                    this.cadastroInicialBDCloud.setNivelUsuario(this.cadastroInicialBD.getNivelUsuario());
                }
                if (cadastroInicialBD.getTipoUsuario() != null && (cadastroInicialBDCloud.getTipoUsuario() == null || !cadastroInicialBD.getTipoUsuario().equals(cadastroInicialBDCloud.getTipoUsuario()))){
                    this.cadastroInicialBDCloud.setTipoUsuario(this.cadastroInicialBD.getTipoUsuario());
                }
                if (cadastroInicialBD.getCodigoUnico() != null && cadastroInicialBD.getCodigoUnico() != 0 && (cadastroInicialBDCloud.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDCloud.getCodigoUnico()))){
                    this.cadastroInicialBDCloud.setCodigoUnico(this.cadastroInicialBD.getCodigoUnico());
                }
            }
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroInicialDAO.salvarCadastroInicialCloud(this.cadastroInicialBDCloud);
            }
        }

        if (this.threadSalvarCadastroInicialFirebase != null){
            this.threadSalvarCadastroInicialFirebase.interrupt();
            this.threadSalvarCadastroInicialFirebase = null;
        }

        if (this.ativo && !this.stopAll && CadastroInicialActivity.isCadastroInicialActivityAtiva()){
            CadastroInicialActivity.setCadastroInicialBD(this.cadastroInicialBD);
            sendBroadcast(new Intent(CadastroInicialActivity.getBrodcastReceiverAtualizarCadastroInicial()));
        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
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
        if (this.cadastroInicialBD == null){
            this.cadastroInicialBD = new CadastroInicial();
        }
        if (this.cadastroInicialBDOld == null){
            this.cadastroInicialBDOld = new CadastroInicial();
        }
        if (this.cadastroInicialBDCloud == null){
            this.cadastroInicialBDCloud = new CadastroInicial();
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
                if (cadastroInicialBD.getVersao() != null && (cadastroInicialBDOld.getVersao() == null || cadastroInicialBD.getVersao() > cadastroInicialBDOld.getVersao())){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.VERSAO).setValue(cadastroInicialBD.getVersao());
                }
                if (cadastroInicialBD.getDataModificalao() != null && (cadastroInicialBDOld.getDataModificalao() == null || !cadastroInicialBD.getDataModificalao().equals(cadastroInicialBDOld.getDataModificalao()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO).setValue(cadastroInicialBD.getDataModificalao());
                }
                if (cadastroInicialBD.getNivelUsuario() != null && (cadastroInicialBDOld.getNivelUsuario() == null || !cadastroInicialBD.getNivelUsuario().equals(cadastroInicialBDOld.getNivelUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroInicialBD.getNivelUsuario());
                }
                if (cadastroInicialBD.getTipoUsuario() != null && (cadastroInicialBDOld.getTipoUsuario() == null || !cadastroInicialBD.getTipoUsuario().equals(cadastroInicialBDOld.getTipoUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroInicialBD.getTipoUsuario());
                }
                if (cadastroInicialBD.getCodigoUnico() != null && cadastroInicialBD.getCodigoUnico() != 0 && (cadastroInicialBDOld.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDOld.getCodigoUnico()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroInicialBD.getCodigoUnico());
                }
            }
            else{
                if (cadastroInicialBD.getVersao() != null && (cadastroInicialBDOld.getVersao() == null || cadastroInicialBD.getVersao() > cadastroInicialBDOld.getVersao())){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.VERSAO).setValue(cadastroInicialBD.getVersao(), completionListener[0]);
                }
                if (cadastroInicialBD.getDataModificalao() != null && (cadastroInicialBDOld.getDataModificalao() == null || !cadastroInicialBD.getDataModificalao().equals(cadastroInicialBDOld.getDataModificalao()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.DATA_MODIFICACAO).setValue(cadastroInicialBD.getDataModificalao(), completionListener[0]);
                }
                if (cadastroInicialBD.getNivelUsuario() != null && (cadastroInicialBDOld.getNivelUsuario() == null || !cadastroInicialBD.getNivelUsuario().equals(cadastroInicialBDOld.getNivelUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroInicialBD.getNivelUsuario(), completionListener[0]);
                }
                if (cadastroInicialBD.getTipoUsuario() != null && (cadastroInicialBDOld.getTipoUsuario() == null || !cadastroInicialBD.getTipoUsuario().equals(cadastroInicialBDOld.getTipoUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroInicialBD.getTipoUsuario(), completionListener[0]);
                }
                if (cadastroInicialBD.getCodigoUnico() != null && cadastroInicialBD.getCodigoUnico() != 0 && (cadastroInicialBDOld.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDOld.getCodigoUnico()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroInicialBD.getCodigoUnico(), completionListener[0]);
                }
            }
        }
    }

}

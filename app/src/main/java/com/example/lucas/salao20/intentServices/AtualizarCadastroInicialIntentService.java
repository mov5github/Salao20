package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.activitys.CadastroInicialActivity;
import com.example.lucas.salao20.dao.CadastroBasicoDAO;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.VersaoDAO;
import com.example.lucas.salao20.dao.model.CadastroBasico;
import com.example.lucas.salao20.dao.model.Versao;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lucas on 29/03/2017.
 */

public class AtualizarCadastroInicialIntentService extends IntentService {
    public static final String ACTIVITY= "activity";
    private String activityOrigem;
    private Bundle bundle;
    private boolean ativo;
    private boolean stopAll;
    private String uid;

    //CADASTROS INICIAIS
    private CadastroBasico cadastroBasicoBD;

    //DAO
    private CadastroBasicoDAO cadastroBasicoDAO;
    private VersaoDAO versaoDAO;

    //VERSOES
    private ArrayList<Versao> versoesBD;
    private ArrayList<Versao> versoesBDCloud;
    private Versao versaoCadastroInicialBD;
    private Versao versaoCadastroInicialBDCloud;

    //THREAD
    private ThreadSalvarCadastroInicialFirebase threadSalvarCadastroInicialFirebase;

    public AtualizarCadastroInicialIntentService() {
        super("AtualizarCadastroInicialIntentService");
        this.ativo = true;
        this.stopAll = false;
        this.uid = "";
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent != null && intent.getExtras() != null) {
            this.bundle = intent.getExtras();

            if (this.bundle.containsKey("desligar") && bundle.getInt("desligar") == 1){
                this.stopAll = true;
                this.ativo = false;
                if (this.threadSalvarCadastroInicialFirebase != null){
                    this.threadSalvarCadastroInicialFirebase.interrupt();
                    this.threadSalvarCadastroInicialFirebase = null;
                }
                if (this.cadastroBasicoDAO != null){
                    this.cadastroBasicoDAO.fechar();
                    this.cadastroBasicoDAO = null;
                }
            }else {
                this.stopAll = false;
                this.ativo = true;
                if (this.bundle.containsKey(DatabaseHelper.CadastroInicial.UID)){
                    this.uid = this.bundle.getString(DatabaseHelper.CadastroInicial.UID);
                }
                if (this.bundle.containsKey(ACTIVITY)){
                    this.activityOrigem = this.bundle.getString(ACTIVITY);
                }else {
                    this.activityOrigem = "";
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //Busca versao
        if (this.ativo && !this.stopAll) {
            if (this.versaoDAO == null) {
                this.versaoDAO = new VersaoDAO(this);
            }
            if (this.versoesBD == null) {
                this.versoesBD = this.versaoDAO.listarVersoes();
            }
            if (this.versoesBDCloud == null) {
                this.versoesBDCloud = this.versaoDAO.listarVersoesCloud();
            }
            if (this.versaoCadastroInicialBD == null){
                this.versaoCadastroInicialBD = new Versao(0);
                this.versaoCadastroInicialBD.setUid(this.uid);
            }
            if (this.versaoCadastroInicialBDCloud == null){
                this.versaoCadastroInicialBDCloud = new Versao(0);
                this.versaoCadastroInicialBDCloud.setUid(this.uid);
            }
            for (Versao v : this.versoesBD){
                if (v.getUid().equals(this.uid)){
                    switch (v.getIdentificacaoTabela()){
                        case DatabaseHelper.CadastroInicial.TABELA:
                            this.versaoCadastroInicialBD = v;
                            break;
                        default:
                            break;
                    }
                }
            }
            for (Versao v : this.versoesBDCloud){
                if (v.getUid().equals(this.uid)){
                    switch (v.getIdentificacaoTabela()){
                        case DatabaseHelper.CadastroInicial.TABELA:
                            this.versaoCadastroInicialBDCloud = v;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //inicia cadastro inicialBD
        if (this.ativo && !this.stopAll){
            this.cadastroBasicoBD = this.cadastroBasicoDAO.buscarCadastroInicialPorUID(this.uid);
            if (this.cadastroBasicoBD == null){
                this.cadastroBasicoBD = new CadastroBasico();
            }
            if (this.bundle != null){
                if (bundle.containsKey(DatabaseHelper.CadastroInicial.NIVEL_USUARIO)){
                    this.cadastroBasicoBD.setNivelUsuario(bundle.getDouble(DatabaseHelper.CadastroInicial.NIVEL_USUARIO));
                }
                if (bundle.containsKey(DatabaseHelper.CadastroInicial.TIPO_USUARIO)){
                    this.cadastroBasicoBD.setTipoUsuario(bundle.getString(DatabaseHelper.CadastroInicial.TIPO_USUARIO));
                }
                if (bundle.containsKey(DatabaseHelper.CadastroInicial.CODIGO_UNICO)){
                    this.cadastroBasicoBD.setCodigoUnico(bundle.getInt(DatabaseHelper.CadastroInicial.CODIGO_UNICO));
                }
            }
        }

        //atualiza cadastro inicial no BD
        if (this.ativo && !this.stopAll){
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroBasicoDAO.salvarCadastroInicial(this.cadastroBasicoBD);
            }

            this.versaoCadastroInicialBD.setIdentificacaoTabela(DatabaseHelper.CadastroInicial.TABELA);
            this.versaoCadastroInicialBD.setVersao(this.versaoCadastroInicialBD.getVersao()+1);
            this.versaoCadastroInicialBD.setDataModificacao(getDateTime());
            result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.versaoDAO.salvarAtualizarVersao(this.versaoCadastroInicialBD);
            }
        }

        //atualiza cadastro inicial no firebase
        if (this.ativo && !this.stopAll){
            this.threadSalvarCadastroInicialFirebase = new ThreadSalvarCadastroInicialFirebase();
            this.threadSalvarCadastroInicialFirebase.start();
            try {
                this.threadSalvarCadastroInicialFirebase.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //atualiza bdcloud
        if (this.ativo && !this.stopAll){
            atualizaEstadoCadastroInicialBDCloud();
        }

        //finaliza as threads abertas
        encerrarAtividadesAbertas();

        //disparar brodcast
        if (this.ativo && !this.stopAll){
            switch (this.activityOrigem){
                case CadastroInicialActivity.ACTIVITY_CADASTRO_INICIAL:
                    if (this.ativo && !this.stopAll && CadastroInicialActivity.isCadastroInicialActivityAtiva()){
                        CadastroInicialActivity.setCadastroBasicoBD(this.cadastroBasicoBD);
                        sendBroadcast(new Intent(CadastroInicialActivity.getBrodcastReceiverAtualizarCadastroInicial()));
                    }
                    break;
                default:
                    break;
            }
        }

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        encerrarAtividadesAbertas();
    }

    //AUXILIARES
    private void atualizaEstadoCadastroInicialBDCloud(){

        CadastroBasico cadastroBasicoBDCloud = this.cadastroBasicoDAO.buscarCadastroInicialPorUIDCloud(this.uid);
        if (cadastroBasicoBDCloud != null){
            cadastroBasicoBDCloud.setTipoUsuario(this.cadastroBasicoBD.getTipoUsuario());
            cadastroBasicoBDCloud.setNivelUsuario(this.cadastroBasicoBD.getNivelUsuario());
            cadastroBasicoBDCloud.setCodigoUnico(this.cadastroBasicoBD.getCodigoUnico());
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroBasicoDAO.salvarCadastroInicialCloud(cadastroBasicoBDCloud);
            }
        }else {
            cadastroBasicoBDCloud = new CadastroBasico();
            cadastroBasicoBDCloud.setUid(this.uid);
            cadastroBasicoBDCloud.setTipoUsuario(this.cadastroBasicoBD.getTipoUsuario());
            cadastroBasicoBDCloud.setNivelUsuario(this.cadastroBasicoBD.getNivelUsuario());
            cadastroBasicoBDCloud.setCodigoUnico(this.cadastroBasicoBD.getCodigoUnico());
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroBasicoDAO.salvarCadastroInicialCloud(cadastroBasicoBDCloud);
            }
        }

        this.versaoCadastroInicialBDCloud.setVersao(this.versaoCadastroInicialBD.getVersao());
        this.versaoCadastroInicialBDCloud.setDataModificacao(this.versaoCadastroInicialBD.getDataModificacao());
        this.versaoCadastroInicialBDCloud.setIdentificacaoTabela(DatabaseHelper.CadastroInicial.TABELA);
        long result = -1;
        while (this.ativo && !this.stopAll && result == -1){
            result = this.versaoDAO.salvarAtualizarVersaoCloud(this.versaoCadastroInicialBDCloud);
        }
    }

    private void encerrarAtividadesAbertas(){
        if (this.threadSalvarCadastroInicialFirebase != null){
            this.threadSalvarCadastroInicialFirebase.interrupt();
            this.threadSalvarCadastroInicialFirebase = null;
        }
        if (this.cadastroBasicoDAO != null){
            this.cadastroBasicoDAO.fechar();
            this.cadastroBasicoDAO = null;
        }
        if (this.versaoDAO != null){
            this.versaoDAO.fechar();
            this.versaoDAO = null;
        }
    }

    //UTILIDADE
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private class ThreadSalvarCadastroInicialFirebase extends Thread{
        private DatabaseReference firebaseCadastroInicial;
        private DatabaseReference.CompletionListener completionListenerCadastroInicial;
        private DatabaseReference firebaseVersaoCadastroInicial;
        private DatabaseReference.CompletionListener completionListenerVersaoCadastroInicial;

        //CONTROLE
        private int numDadosCadastroInicialSalvos;
        private int numRespostasCadastroInicial;
        private int numDadosVersaoSalvos;
        private int numRespostasVersao;


        @Override
        public void run(){
            Log.i("script","ThreadSalvarCadastroInicialFirebase");
            initFirebase();
            initControles();

            while (!isInterrupted() && !stopAll && this.numDadosCadastroInicialSalvos < 3){
                initControles();
                salvarCadastroInicialNoFirebase(this.completionListenerCadastroInicial);

                boolean msgExibida = false;
                while (!isInterrupted() && !stopAll && this.numRespostasCadastroInicial < 3){
                    if (!msgExibida){
                        msgExibida = true;
                        Log.i("script","ThreadSalvarCadastroInicialFirebase aguardando resposta salvar cadastro inicial firebase ...");
                    }
                }
            }

            while (!isInterrupted() && !stopAll && this.numDadosVersaoSalvos < 2){
                initControles();
                salvarVersaoCadastroInicialNoFirebase(this.completionListenerVersaoCadastroInicial);

                boolean msgExibida = false;
                while (!isInterrupted() && !stopAll && this.numRespostasVersao < 2){
                    if (!msgExibida){
                        msgExibida = true;
                        Log.i("script","ThreadSalvarCadastroInicialFirebase aguardando resposta salvar versao cadastro inicial firebase ...");
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
                            Log.i("script","completionListenerCadastroInicial cadastroInicial nao foi cadastroInicialSalvo");
                            numRespostasCadastroInicial++;
                        }else{
                            Log.i("script","completionListenerCadastroInicial cadastroInicial foi cadastroInicialSalvo");
                            numRespostasCadastroInicial++;
                            numDadosCadastroInicialSalvos++;
                        }
                    }
                };
            }
            if (this.completionListenerVersaoCadastroInicial == null){
                this.completionListenerVersaoCadastroInicial = new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            Log.i("script","completionListenerCadastroInicial versao cadastroInicial nao foi Salvo");
                            numRespostasVersao++;
                        }else{
                            Log.i("script","completionListenerCadastroInicial versao cadastroInicial foi Salvo");
                            numRespostasVersao++;
                            numDadosVersaoSalvos++;
                        }
                    }
                };
            }
        }

        private void initControles(){
            this.numDadosCadastroInicialSalvos = 0;
            this.numRespostasCadastroInicial = 0;
            this.numDadosVersaoSalvos = 0;
            this.numRespostasVersao = 0;
        }

        private void salvarCadastroInicialNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseCadastroInicial == null){
                this.firebaseCadastroInicial = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.CadastroInicial.TABELA);
            }
            if( completionListener.length == 0 ){
                if (cadastroBasicoBD.getNivelUsuario() != null){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroBasicoBD.getNivelUsuario());
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroBasicoBD.getTipoUsuario() != null && !cadastroBasicoBD.getTipoUsuario().isEmpty()){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroBasicoBD.getTipoUsuario());
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroBasicoBD.getCodigoUnico() != null && cadastroBasicoBD.getCodigoUnico() != 0){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroBasicoBD.getCodigoUnico());
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
            }
            else{
                if (cadastroBasicoBD.getNivelUsuario() != null){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroBasicoBD.getNivelUsuario(), completionListener[0]);
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroBasicoBD.getTipoUsuario() != null && !cadastroBasicoBD.getTipoUsuario().isEmpty()){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroBasicoBD.getTipoUsuario(), completionListener[0]);
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroBasicoBD.getCodigoUnico() != null && cadastroBasicoBD.getCodigoUnico() != 0 ){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroBasicoBD.getCodigoUnico(), completionListener[0]);
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
            }
        }

        private void salvarVersaoCadastroInicialNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseVersaoCadastroInicial == null){
                this.firebaseVersaoCadastroInicial = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.CadastroInicial.TABELA);
            }
            if( completionListener.length == 0 ){
                if (versaoCadastroInicialBD != null && versaoCadastroInicialBD.getVersao() != 0){
                    firebaseVersaoCadastroInicial.child(DatabaseHelper.Versoes.VERSAO).setValue(versaoCadastroInicialBD.getVersao());
                }else {
                    this.numDadosVersaoSalvos++;
                    this.numRespostasVersao++;
                }
                if (versaoCadastroInicialBD != null && versaoCadastroInicialBD.getDataModificacao() != null && !versaoCadastroInicialBD.getDataModificacao().isEmpty()){
                    firebaseVersaoCadastroInicial.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(versaoCadastroInicialBD.getDataModificacao());
                }else {
                    firebaseVersaoCadastroInicial.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(getDateTime());
                }
            }
            else{
                if (versaoCadastroInicialBD != null && versaoCadastroInicialBD.getVersao() != 0){
                    firebaseVersaoCadastroInicial.child(DatabaseHelper.Versoes.VERSAO).setValue(versaoCadastroInicialBD.getVersao(), completionListener[0]);
                }
                else {
                    this.numDadosVersaoSalvos++;
                    this.numRespostasVersao++;
                }
                if (versaoCadastroInicialBD != null && versaoCadastroInicialBD.getDataModificacao() != null && !versaoCadastroInicialBD.getDataModificacao().isEmpty()){
                    firebaseVersaoCadastroInicial.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(versaoCadastroInicialBD.getDataModificacao(), completionListener[0]);
                }else {
                    firebaseVersaoCadastroInicial.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(getDateTime(), completionListener[0]);
                }
            }
        }
    }
}

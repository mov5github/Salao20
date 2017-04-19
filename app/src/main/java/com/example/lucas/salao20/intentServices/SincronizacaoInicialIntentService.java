package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.dao.CadastroBasicoDAO;
import com.example.lucas.salao20.dao.CadastroComplementarDAO;
import com.example.lucas.salao20.dao.CadastroComplementarExternoDAO;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.UserDAO;
import com.example.lucas.salao20.dao.VersaoDAO;
import com.example.lucas.salao20.dao.model.CadastroBasico;
import com.example.lucas.salao20.dao.model.CadastroComplementar;
import com.example.lucas.salao20.dao.model.User;
import com.example.lucas.salao20.dao.model.Versao;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

/**
 * Created by Lucas on 10/04/2017.
 */

public class SincronizacaoInicialIntentService extends IntentService {
    private boolean ativo;
    private boolean stopAll;
    private String uid;
    private Context context;

    //USER
    private User userFirebase;
    private User userBD;
    private User userBDCloud;

    //CADASTROS BASICO
    private CadastroBasico cadastroBasicoFirebase;
    private CadastroBasico cadastroBasicoBD;
    private CadastroBasico cadastroBasicoBDCloud;

    //CADASTROS COMPLEMENTAR
    private CadastroComplementar cadastroComplementarFirebase;
    private CadastroComplementar cadastroComplementarBD;
    private CadastroComplementar cadastroComplementarBDCloud;

    //DAO
    private UserDAO userDAO;
    private CadastroComplementarDAO cadastroComplementarDAO;
    private CadastroBasicoDAO cadastroBasicoDAO;
    private VersaoDAO versaoDAO;


    //VERSOES ARRAYS
    private ArrayList<Versao> versoesFirebase;
    private ArrayList<Versao> versoesBD;
    private ArrayList<Versao> versoesBDCloud;

    //VERSOES
    private Versao versaoUserBD;
    private Versao versaoUserBDCloud;
    private Versao versaoUserFirebase;
    private Versao versaoCadastroBasicoBD;
    private Versao versaoCadastroBasicoBDCloud;
    private Versao versaoCadastroBasicoFirebase;
    private Versao versaoCadastroComplementarBD;
    private Versao versaoCadastroComplementarBDCloud;
    private Versao versaoCadastroComplementarFirebase;

    //THREADS
    private ThreadWork threadWork;
    private final ThreadBuscarVersoesFirebase threadBuscarVersoesFirebase = new ThreadBuscarVersoesFirebase();
    private final ThreadBuscarUserFirebase threadBuscarUserFirebase = new ThreadBuscarUserFirebase();
    private final ThreadBuscarCadastroBasicoFirebase threadBuscarCadastroBasicoFirebase = new ThreadBuscarCadastroBasicoFirebase();
    private final ThreadBuscarCadastroComplementarFirebase threadBuscarCadastroComplementarFirebase = new ThreadBuscarCadastroComplementarFirebase();


    public SincronizacaoInicialIntentService() {
        super("SincronizacaoInicialIntentService");
        this.ativo = true;
        this.stopAll = false;
        this.uid = "";
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.get("uid") != null) {
                this.uid = bundle.getString("uid");
            }
            if (bundle.containsKey("desligar") && bundle.getInt("desligar") == 1) {
                this.stopAll = true;
                this.ativo = false;
                encerrarAtividadesAbertas();
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
            if (this.threadWork == null) {
                this.threadWork = new ThreadWork();
            }
            this.threadWork.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        encerrarAtividadesAbertas();
    }

    //UTILIDADE
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    //AUXILIAR
    private void encerrarAtividadesAbertas() {
        if (this.userDAO != null) {
            this.userDAO.fechar();
            this.userDAO = null;
        }
        if (this.cadastroBasicoDAO != null) {
            this.cadastroBasicoDAO.fechar();
            this.cadastroBasicoDAO = null;
        }
        if (this.cadastroComplementarDAO != null) {
            this.cadastroComplementarDAO.fechar();
            this.cadastroComplementarDAO = null;
        }
        if (this.versaoDAO != null) {
            this.versaoDAO.fechar();
            this.versaoDAO = null;
        }
        if (this.threadBuscarVersoesFirebase != null) {
            this.threadBuscarVersoesFirebase.interrupt();
        }
        if (this.threadBuscarUserFirebase != null) {
            this.threadBuscarUserFirebase.interrupt();
        }
        if (this.threadBuscarCadastroBasicoFirebase != null) {
            this.threadBuscarCadastroBasicoFirebase.interrupt();
        }
        if (this.threadBuscarCadastroComplementarFirebase != null) {
            this.threadBuscarCadastroComplementarFirebase.interrupt();
        }
        if (this.threadWork != null) {
            this.threadWork.interrupt();
        }

    }

    //CLASSES
    private class ThreadWork extends Thread {
        //CONTROLE

        @Override
        public void run() {
            Log.i("script", "ThreadWork run");
            if (ativo && !stopAll) {
                //BUSCAR VERSOES
                initVersoes();
                buscarVersoes();
            }
            if (ativo && !stopAll) {
                buscarTabelasFirebase();
            }

            if (ativo && !stopAll) {
                sincronizarBancos();
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            Log.i("script", "ThreadWork interrupt");
            if (versaoDAO != null) {
                versaoDAO.fechar();
                versaoDAO = null;
            }
            if (threadBuscarVersoesFirebase != null) {
                threadBuscarVersoesFirebase.interrupt();
            }
            if (threadBuscarUserFirebase != null) {
                threadBuscarUserFirebase.interrupt();
            }
            if (threadBuscarCadastroBasicoFirebase != null) {
                threadBuscarCadastroBasicoFirebase.interrupt();
            }
            if (threadBuscarCadastroComplementarFirebase != null) {
                threadBuscarCadastroComplementarFirebase.interrupt();
            }

        }

        //AUXILIARES
        private void initVersoes() {
            versaoUserBD = new Versao(0);
            versaoUserBDCloud = new Versao(0);
            versaoUserFirebase = new Versao(0);
            versaoCadastroBasicoBD = new Versao(0);
            versaoCadastroBasicoBDCloud = new Versao(0);
            versaoCadastroBasicoFirebase = new Versao(0);
            versaoCadastroComplementarBD = new Versao(0);
            versaoCadastroComplementarBDCloud = new Versao(0);
            versaoCadastroComplementarFirebase = new Versao(0);
        }

        private void buscarVersoes() {
            if (versaoDAO == null) {
                versaoDAO = new VersaoDAO(context);
            }
            if (versoesBD == null) {
                versoesBD = versaoDAO.listarVersoes();
            }
            if (versoesBDCloud == null) {
                versoesBDCloud = versaoDAO.listarVersoesCloud();
            }
            if (versoesFirebase == null) {
                versoesFirebase = new ArrayList<Versao>();
            }

            for (Versao v : versoesBD) {
                switch (v.getIdentificacaoTabela()) {
                    case DatabaseHelper.User.TABELA:
                        versaoUserBD = v;
                        break;
                    case DatabaseHelper.CadastroBasico.TABELA:
                        versaoCadastroBasicoBD = v;
                        break;
                    case DatabaseHelper.CadastroComplementar.TABELA:
                        versaoCadastroComplementarBD = v;
                        break;
                }
            }

            for (Versao v : versoesBDCloud) {
                switch (v.getIdentificacaoTabela()) {
                    case DatabaseHelper.User.TABELA_CLOUD:
                        versaoUserBDCloud = v;
                        break;
                    case DatabaseHelper.CadastroBasico.TABELA_CLOUD:
                        versaoCadastroBasicoBDCloud = v;
                        break;
                    case DatabaseHelper.CadastroComplementar.TABELA_CLOUD:
                        versaoCadastroComplementarBDCloud = v;
                        break;
                }
            }

            threadBuscarVersoesFirebase.start();
            synchronized (threadBuscarVersoesFirebase){
                try {
                    threadBuscarVersoesFirebase.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            for (Versao v : versoesFirebase) {
                switch (v.getIdentificacaoTabela()) {
                    case DatabaseHelper.User.TABELA:
                        versaoUserFirebase = v;
                        break;
                    case DatabaseHelper.CadastroBasico.TABELA:
                        versaoCadastroBasicoFirebase = v;
                        break;
                    case DatabaseHelper.CadastroComplementar.TABELA:
                        versaoCadastroComplementarFirebase = v;
                        break;
                }
            }
        }

        private void buscarTabelasFirebase() {
            if (userDAO == null){
                userDAO = new UserDAO(context);
            }
            if (cadastroBasicoDAO == null){
                cadastroBasicoDAO = new CadastroBasicoDAO(context);
            }
            if (cadastroComplementarDAO == null){
                cadastroComplementarDAO = new CadastroComplementarDAO(context);
            }
            long result;

            ArrayList<String> buscasARealizar = new ArrayList<String>();
            if (versaoUserFirebase.getVersao() != 0 && (versaoUserBDCloud.getVersao() == 0 || versaoUserBDCloud.getVersao() < versaoUserFirebase.getVersao())){
                buscasARealizar.add(DatabaseHelper.User.TABELA);
            }
            if (versaoCadastroBasicoFirebase.getVersao() != 0 && (versaoCadastroBasicoBDCloud.getVersao() == 0 || versaoCadastroBasicoBDCloud.getVersao() < versaoCadastroBasicoFirebase.getVersao())){
                buscasARealizar.add(DatabaseHelper.CadastroBasico.TABELA);
            }
            if (versaoCadastroComplementarFirebase.getVersao() != 0 && (versaoCadastroComplementarBDCloud.getVersao() == 0 || versaoCadastroComplementarBDCloud.getVersao() < versaoCadastroComplementarFirebase.getVersao())){
                buscasARealizar.add(DatabaseHelper.CadastroComplementar.TABELA);
            }

            if (buscasARealizar.contains(DatabaseHelper.User.TABELA)){
                threadBuscarUserFirebase.start();
                synchronized (threadBuscarUserFirebase){
                    try {
                        threadBuscarUserFirebase.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (userDAO.buscarUserUIDCloud(uid) == null){
                    result = -1;
                    while (ativo && !stopAll && result == -1){
                        result = userDAO.salvarUserCloud(userFirebase);
                    }
                }else {
                    result = -1;
                    while (ativo && !stopAll && result == -1){
                        result = userDAO.atualizarUserCloud(userFirebase);
                    }
                }
                if (versaoUserBDCloud.getVersao() != 0){
                    versaoUserFirebase.set_id(versaoUserBDCloud.get_id());
                }
                result = -1;
                while (ativo && !stopAll && result == -1){
                    result = versaoDAO.salvarAtualizarVersaoCloud(versaoUserFirebase);
                }
            }

            if (buscasARealizar.contains(DatabaseHelper.CadastroBasico.TABELA)){
                threadBuscarCadastroBasicoFirebase.start();
                synchronized (threadBuscarCadastroBasicoFirebase){
                    try {
                        threadBuscarCadastroBasicoFirebase.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (cadastroBasicoDAO.buscarCadastroBasicoPorUIDCloud(uid) == null){
                    result = -1;
                    while (ativo && !stopAll && result == -1){
                        result = cadastroBasicoDAO.salvarCadastroBasicoCloud(cadastroBasicoFirebase);
                    }
                }else {
                    result = -1;
                    while (ativo && !stopAll && result == -1){
                        result = cadastroBasicoDAO.atualizarCadastroBasicoCloud(cadastroBasicoFirebase);
                    }
                }
                if (versaoCadastroBasicoBDCloud.getVersao() != 0){
                    versaoCadastroBasicoFirebase.set_id(versaoCadastroBasicoBDCloud.get_id());
                }
                result = -1;
                while (ativo && !stopAll && result == -1){
                    result = versaoDAO.salvarAtualizarVersaoCloud(versaoCadastroBasicoFirebase);
                }
            }

            if (buscasARealizar.contains(DatabaseHelper.CadastroComplementar.TABELA)){
                threadBuscarCadastroComplementarFirebase.start();
                synchronized (threadBuscarCadastroComplementarFirebase){
                    try {
                        threadBuscarCadastroComplementarFirebase.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (cadastroComplementarDAO.buscarCadastroComplementarPorUIDCloud(uid) == null){
                    result = -1;
                    while (ativo && !stopAll && result == -1){
                        result = cadastroBasicoDAO.salvarCadastroBasicoCloud(cadastroBasicoFirebase);
                    }
                }else {
                    result = -1;
                    while (ativo && !stopAll && result == -1){
                        result = cadastroBasicoDAO.atualizarCadastroBasicoCloud(cadastroBasicoFirebase);
                    }
                }
                if (versaoCadastroBasicoBDCloud.getVersao() != 0){
                    versaoCadastroBasicoFirebase.set_id(versaoCadastroBasicoBDCloud.get_id());
                }
                result = -1;
                while (ativo && !stopAll && result == -1){
                    result = versaoDAO.salvarAtualizarVersaoCloud(versaoCadastroBasicoFirebase);
                }
            }
        }

        private void sincronizarBancos(){

        }
    }

    private class ThreadBuscarVersoesFirebase extends Thread {
        //CONTROLE
        int buscasRespondidas = 0;
        int numBuscas = 3;
        //FIREBASE
        private DatabaseReference firebaseVersaoUser;
        private DatabaseReference firebaseVersaoCadastroBasico;
        private DatabaseReference firebaseVersaoCadastroComplementar;
        private ValueEventListener vELVersoes;

        @Override
        public void run() {
            Log.i("script", "ThreadBuscarVersoesFirebase");
            initFirebase();
            this.firebaseVersaoUser.addListenerForSingleValueEvent(this.vELVersoes);
            this.firebaseVersaoCadastroBasico.addListenerForSingleValueEvent(this.vELVersoes);
            this.firebaseVersaoCadastroComplementar.addListenerForSingleValueEvent(this.vELVersoes);

            Log.i("script", "ThreadBuscarVersoesFirebase aguardando resposta firebase ...");
        }

        @Override
        public void interrupt() {
            super.interrupt();
            this.firebaseVersaoUser.removeEventListener(this.vELVersoes);
            this.firebaseVersaoCadastroBasico.removeEventListener(this.vELVersoes);
            this.firebaseVersaoCadastroComplementar.removeEventListener(this.vELVersoes);
            synchronized (this) {
                notify();
            }
        }

        //AUXILIARES
        private void initFirebase() {
            if (this.firebaseVersaoUser == null) {
                this.firebaseVersaoUser = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.User.TABELA);
            }
            if (this.firebaseVersaoCadastroBasico == null) {
                this.firebaseVersaoCadastroBasico = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.CadastroBasico.TABELA);
            }
            if (this.firebaseVersaoCadastroComplementar == null) {
                this.firebaseVersaoCadastroComplementar = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.CadastroComplementar.TABELA);
            }
            if (this.vELVersoes == null) {
                this.vELVersoes = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0) {
                            Log.i("script", "vELVersoes dataSnapshot == null");
                            buscasRespondidas++;
                            verificarDadosObtidos();
                        } else {
                            Log.i("script", "vELVersoes dataSnapshot != null");
                            if (versoesFirebase == null) {
                                versoesFirebase = new ArrayList<Versao>();
                            }

                            Versao versao = new Versao();
                            if (map.containsKey(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA)) {
                                versao.setIdentificacaoTabela(String.valueOf(map.get(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA)));
                            }
                            if (map.containsKey(DatabaseHelper.Versoes.DATA_MODIFICACAO)) {
                                versao.setDataModificacao(String.valueOf(map.get(DatabaseHelper.Versoes.DATA_MODIFICACAO)));
                            }
                            if (map.containsKey(DatabaseHelper.Versoes.VERSAO)) {
                                versao.setVersao(Integer.valueOf(map.get(DatabaseHelper.Versoes.VERSAO).toString()));
                            }
                            versoesFirebase.add(versao);
                            buscasRespondidas++;
                            verificarDadosObtidos();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script", "vELVersoes onCancelled");

                    }
                };
            }
        }

        private void verificarDadosObtidos() {
            if (this.buscasRespondidas == this.numBuscas) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    private class ThreadBuscarUserFirebase extends Thread {
        //CONTROLE
        int buscasRespondidas = 0;
        int numBuscas = 1;
        //FIREBASE
        private DatabaseReference firebaseUser;
        private ValueEventListener vELUser;

        @Override
        public void run() {
            Log.i("script", "ThreadBuscarVersoesFirebase");
            initFirebase();
            this.firebaseUser.addListenerForSingleValueEvent(this.vELUser);

            Log.i("script", "ThreadBuscarUserFirebase aguardando resposta firebase ...");
        }

        @Override
        public void interrupt() {
            super.interrupt();
            this.firebaseUser.removeEventListener(this.vELUser);

            synchronized (this) {
                notify();
            }
        }

        //AUXILIARES
        private void initFirebase() {
            if (this.firebaseUser == null) {
                this.firebaseUser = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.User.TABELA);
            }
            if (this.vELUser == null) {
                this.vELUser = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0) {
                            Log.i("script", "vELUser dataSnapshot == null");
                            buscasRespondidas++;
                            verificarDadosObtidos();
                        } else {
                            Log.i("script", "vELUser dataSnapshot != null");
                            if (userFirebase == null) {
                                userFirebase = new User();
                            }
                            userFirebase.setUid(uid);
                            if (map.containsKey(DatabaseHelper.User.COD_UNICO_USER_CABELEIREIRO)) {
                                userFirebase.setCodUnicoUserCabeleireiro(Integer.valueOf(map.get(DatabaseHelper.User.COD_UNICO_USER_CABELEIREIRO).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.User.COD_UNICO_USER_SALAO)) {
                                userFirebase.setCodUnicoUserSalao(Integer.valueOf(map.get(DatabaseHelper.User.COD_UNICO_USER_SALAO).toString()));
                            }
                            buscasRespondidas++;
                            verificarDadosObtidos();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script", "vELUser onCancelled");

                    }
                };
            }
        }

        private void verificarDadosObtidos() {
            if (this.buscasRespondidas == this.numBuscas) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    private class ThreadBuscarCadastroBasicoFirebase extends Thread {
        //CONTROLE
        int buscasRespondidas = 0;
        int numBuscas = 1;
        //FIREBASE
        private DatabaseReference firebaseCadastroBasico;
        private ValueEventListener vELCadastroBasico;

        @Override
        public void run() {
            Log.i("script", "ThreadBuscarCadastroBasicoFirebase");
            initFirebase();
            this.firebaseCadastroBasico.addListenerForSingleValueEvent(this.vELCadastroBasico);

            Log.i("script", "ThreadBuscarCadastroBasicoFirebase aguardando resposta firebase ...");
        }

        @Override
        public void interrupt() {
            super.interrupt();
            this.firebaseCadastroBasico.removeEventListener(this.vELCadastroBasico);
            synchronized (this) {
                notify();
            }
        }

        //AUXILIARES
        private void initFirebase() {
            if (this.firebaseCadastroBasico == null) {
                this.firebaseCadastroBasico = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.CadastroBasico.TABELA);
            }
            if (this.vELCadastroBasico == null) {
                this.vELCadastroBasico = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0) {
                            Log.i("script", "vELCadastroBasico dataSnapshot == null");
                            buscasRespondidas++;
                            verificarDadosObtidos();
                        } else {
                            Log.i("script", "vELCadastroBasico dataSnapshot != null");
                            if (cadastroBasicoFirebase == null) {
                                cadastroBasicoFirebase = new CadastroBasico();
                            }

                            cadastroBasicoFirebase.set_uid(uid);
                            if (map.containsKey(DatabaseHelper.CadastroBasico.NIVEL_USUARIO)) {
                                cadastroBasicoFirebase.setNivelUsuario(Double.valueOf(map.get(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroBasico.TIPO_USUARIO)) {
                                cadastroBasicoFirebase.setTipoUsuario(String.valueOf(map.get(DatabaseHelper.Versoes.DATA_MODIFICACAO)));
                            }

                            buscasRespondidas++;
                            verificarDadosObtidos();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script", "vELCadastroBasico onCancelled");

                    }
                };
            }
        }

        private void verificarDadosObtidos() {
            if (this.buscasRespondidas == this.numBuscas) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }

    private class ThreadBuscarCadastroComplementarFirebase extends Thread {
        //CONTROLE
        int buscasRespondidas = 0;
        int numBuscas = 1;
        //FIREBASE
        private DatabaseReference firebaseCadastroComplementar;
        private ValueEventListener vELCadastroComplementar;

        @Override
        public void run() {
            Log.i("script", "vELCadastroComplementar");
            initFirebase();
            this.firebaseCadastroComplementar.addListenerForSingleValueEvent(this.vELCadastroComplementar);

            Log.i("script", "vELCadastroComplementar aguardando resposta firebase ...");
        }

        @Override
        public void interrupt() {
            super.interrupt();
            this.firebaseCadastroComplementar.removeEventListener(this.vELCadastroComplementar);
            synchronized (this) {
                notify();
            }
        }

        //AUXILIARES
        private void initFirebase() {
            if (this.firebaseCadastroComplementar == null) {
                this.firebaseCadastroComplementar = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.CadastroComplementar.TABELA);
            }
            if (this.vELCadastroComplementar == null) {
                this.vELCadastroComplementar = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0) {
                            Log.i("script", "vELCadastroComplementar dataSnapshot == null");
                            buscasRespondidas++;
                            verificarDadosObtidos();
                        } else {
                            Log.i("script", "vELCadastroComplementar dataSnapshot != null");
                            if (cadastroComplementarFirebase == null) {
                                cadastroComplementarFirebase = new CadastroComplementar();
                            }

                            cadastroComplementarFirebase.setUid(uid);
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.NOME)) {
                                cadastroComplementarFirebase.setNome(String.valueOf(map.get(DatabaseHelper.CadastroComplementar.NOME)));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.ENDERECO)) {
                                cadastroComplementarFirebase.setEndereco(String.valueOf(map.get(DatabaseHelper.CadastroComplementar.ENDERECO)));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.NUMERO_ENDERECO)) {
                                cadastroComplementarFirebase.setNumeroEndereco(Integer.valueOf(map.get(DatabaseHelper.CadastroComplementar.NUMERO_ENDERECO).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.COMPLEMENTO_ENDERECO)) {
                                cadastroComplementarFirebase.setComplementoEndereco(String.valueOf(map.get(DatabaseHelper.CadastroComplementar.COMPLEMENTO_ENDERECO)));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.CEP)) {
                                cadastroComplementarFirebase.setCep(Integer.valueOf(map.get(DatabaseHelper.CadastroComplementar.CEP).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_1)) {
                                cadastroComplementarFirebase.setTelefoneFixo1(Integer.valueOf(map.get(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_1).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_2)) {
                                cadastroComplementarFirebase.setTelefoneFixo2(Integer.valueOf(map.get(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_2).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.WHATSAPP)) {
                                cadastroComplementarFirebase.setWhatsapp(Integer.valueOf(map.get(DatabaseHelper.CadastroComplementar.WHATSAPP).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.CELULAR_1)) {
                                cadastroComplementarFirebase.setCelular1(Integer.valueOf(map.get(DatabaseHelper.CadastroComplementar.CELULAR_1).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.CELULAR_2)) {
                                cadastroComplementarFirebase.setCelular2(Integer.valueOf(map.get(DatabaseHelper.CadastroComplementar.CELULAR_2).toString()));
                            }
                            if (map.containsKey(DatabaseHelper.CadastroComplementar.FACEBOOK)) {
                                cadastroComplementarFirebase.setFacebook(String.valueOf(map.get(DatabaseHelper.CadastroComplementar.FACEBOOK)));
                            }

                            buscasRespondidas++;
                            verificarDadosObtidos();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script", "vELCadastroComplementar onCancelled");

                    }
                };
            }
        }

        private void verificarDadosObtidos() {
            if (this.buscasRespondidas == this.numBuscas) {
                synchronized (this) {
                    notify();
                }
            }
        }
    }
}

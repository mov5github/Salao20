package com.example.lucas.salao20.intentServices;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lucas.salao20.activitys.SplashScreenActivity;
import com.example.lucas.salao20.dao.CadastroInicialDAO;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.FuncionamentoDAO;
import com.example.lucas.salao20.dao.VersaoDAO;
import com.example.lucas.salao20.dao.model.CadastroInicial;
import com.example.lucas.salao20.dao.model.Funcionamento;
import com.example.lucas.salao20.dao.model.Versao;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.FuncionamentoSalao;
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
    private ThreadBuscarVersoesFirebase threadBuscarVersoesFirebase;
    private ThreadBuscarFuncionamentoSalaoFirebase threadBuscarFuncionamentoSalaoFirebase;
    private ThreadSalvarFuncionamentoFirebase threadSalvarFuncionamentoFirebase;

    //DAO
    private CadastroInicialDAO cadastroInicialDAO;
    private VersaoDAO versaoDAO;
    private FuncionamentoDAO funcionamentoDAO;

    //FUNCIONAMENTO Salao
    private FuncionamentoSalao funcionamentoSalaoFirebase;
    private FuncionamentoSalao funcionamentoSalaoBD;
    private FuncionamentoSalao funcionamentoSalaoBDCloud;

    //VERSOES
    private ArrayList<Versao> versoesFirebase;
    private ArrayList<Versao> versoesBD;
    private ArrayList<Versao> versoesBDCloud;

    //VERSOES
    private Versao versaoCadastroInicialBD;
    private Versao versaoFuncionamentoBD;
    private Versao versaoServicosBD;
    private Versao versaoCabeleireirosBD;
    private Versao versaoCadastroInicialBDCloud;
    private Versao versaoFuncionamentoBDCLoud;
    private Versao versaoServicosBDCloud;
    private Versao versaoCabeleireirosBDCloud;
    private Versao versaoCadastroInicialFirebase;
    private Versao versaoFuncionamentoFirebase;
    private Versao versaoServicosFirebase;
    private Versao versaoCabeleireirosFirebase;


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
                encerrarAtividadesAbertas();
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

        //BUSCAR VERSOES
        if (this.ativo && !this.stopAll){
            initVersoes();
            if (this.versaoDAO == null){
                this.versaoDAO = new VersaoDAO(this);
            }
            if (this.versoesBD == null){
                this.versoesBD = this.versaoDAO.listarVersoes();
            }
            if (this.versoesBDCloud == null){
                this.versoesBDCloud = this.versaoDAO.listarVersoesCloud();
            }
            if (this.versoesFirebase == null){
                this.versoesFirebase = new ArrayList<Versao>();
                this.threadBuscarVersoesFirebase = new ThreadBuscarVersoesFirebase();
                this.threadBuscarVersoesFirebase.start();
                try {
                    this.threadBuscarVersoesFirebase.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        //verifica as versoes encontradas no bd
        if (this.ativo && !this.stopAll){
            for (Versao v : this.versoesBD){
                if (v.getUid().equals(this.uid)){
                    switch (v.getIdentificacaoTabela()){
                        case DatabaseHelper.CadastroInicial.TABELA:
                            this.versaoCadastroInicialBD = v;
                            break;
                        case DatabaseHelper.Funcionamento.TABELA:
                            this.versaoFuncionamentoBD = v;
                            break;
                        case DatabaseHelper.Servico.TABELA:
                            this.versaoServicosBD = v;
                            break;
                        case DatabaseHelper.Cabeleireiro.TABELA:
                            this.versaoCabeleireirosBD = v;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //verifica as versoes encontradas no bd cloud
        if (this.ativo && !this.stopAll){
            for (Versao v : this.versoesBDCloud){
                if (v.getUid().equals(this.uid)){
                    switch (v.getIdentificacaoTabela()){
                        case DatabaseHelper.CadastroInicial.TABELA:
                            this.versaoCadastroInicialBDCloud = v;
                            break;
                        case DatabaseHelper.Funcionamento.TABELA:
                            this.versaoFuncionamentoBDCLoud = v;
                            break;
                        case DatabaseHelper.Servico.TABELA:
                            this.versaoServicosBDCloud = v;
                            break;
                        case DatabaseHelper.Cabeleireiro.TABELA:
                            this.versaoCabeleireirosBDCloud = v;
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        //verifica as versoes encontradas no firebase
        if (this.ativo && !this.stopAll){
            for (Versao v : this.versoesFirebase){
                if (v.getUid().equals(this.uid)){
                    switch (v.getIdentificacaoTabela()){
                        case DatabaseHelper.CadastroInicial.TABELA:
                            this.versaoCadastroInicialFirebase = v;
                            break;
                        case DatabaseHelper.Funcionamento.TABELA:
                            this.versaoFuncionamentoFirebase = v;
                            if (funcionamentoSalaoFirebase == null){
                                this.funcionamentoSalaoFirebase = new FuncionamentoSalao();
                            }
                            this.funcionamentoSalaoFirebase.setVersao(v.getVersao());
                            break;
                        case DatabaseHelper.Servico.TABELA:
                            this.versaoServicosFirebase = v;
                            break;
                        case DatabaseHelper.Cabeleireiro.TABELA:
                            this.versaoCabeleireirosFirebase = v;
                            break;
                        default:
                            break;
                    }
                }
            }
        }



        //Sincroniza cadastro inicial do firebase o BD e BDCloud
        if (this.ativo && !this.stopAll){

            if (this.versaoCadastroInicialBD.getVersao() < this.versaoCadastroInicialFirebase.getVersao()){//atualiza o BD e BDCloud com o valores do Firebase
                this.threadBuscarCadastroInicialFirebase = new ThreadBuscarCadastroInicialFirebase();
                this.threadBuscarCadastroInicialFirebase.start();
                try {
                    this.threadBuscarCadastroInicialFirebase.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (this.cadastroInicialDAO == null){
                    this.cadastroInicialDAO = new CadastroInicialDAO(this);
                }
                if (this.versaoDAO == null){
                    this.versaoDAO = new VersaoDAO(this);
                }

                if(this.cadastroInicialFirebase == null){
                    //TODO erro ao obter cadastro inicial firebase
                }else {
                    inicializaEstadoCadastroInicialBD ("Firebase");

                    inicializaEstadoCadastroInicialBDCloud("Firebase");
                }

            }else if (this.versaoCadastroInicialBD.getVersao() > this.versaoCadastroInicialFirebase.getVersao()){//atualiza o firebase e BDCloud com os valores de BD
                if (this.cadastroInicialBD == null){
                    this.cadastroInicialBD = new CadastroInicial();
                }
                if (this.cadastroInicialBDCloud == null){
                    this.cadastroInicialBDCloud = new CadastroInicial();
                }
                this.threadSalvarCadastroInicialFirebase = new ThreadSalvarCadastroInicialFirebase();
                this.threadSalvarCadastroInicialFirebase.start();
                try {
                    this.threadSalvarCadastroInicialFirebase.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (this.cadastroInicialDAO == null){
                    this.cadastroInicialDAO = new CadastroInicialDAO(this);
                }
                if (this.versaoDAO == null){
                    this.versaoDAO = new VersaoDAO(this);
                }

                inicializaEstadoCadastroInicialBDCloud("BD");

            }else {
                if (this.versaoCadastroInicialBDCloud.getVersao() < this.versaoCadastroInicialFirebase.getVersao() || (this.versaoCadastroInicialBDCloud.getVersao().equals(this.versaoCadastroInicialFirebase.getVersao()) && !this.versaoCadastroInicialBDCloud.getDataModificacao().equals(this.versaoCadastroInicialFirebase.getDataModificacao())) || !this.versaoCadastroInicialBD.getDataModificacao().equals(this.versaoCadastroInicialFirebase.getDataModificacao())){
                    if (this.cadastroInicialBD == null){
                        this.cadastroInicialBD = new CadastroInicial();
                    }
                    if (this.cadastroInicialBDCloud == null){
                        this.cadastroInicialBDCloud = new CadastroInicial();
                    }
                    this.threadBuscarCadastroInicialFirebase = new ThreadBuscarCadastroInicialFirebase();
                    this.threadBuscarCadastroInicialFirebase.start();
                    try {
                        this.threadBuscarCadastroInicialFirebase.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (this.cadastroInicialDAO == null){
                        this.cadastroInicialDAO = new CadastroInicialDAO(this);
                    }
                    if (this.versaoDAO == null){
                        this.versaoDAO = new VersaoDAO(this);
                    }

                    if(this.cadastroInicialFirebase == null){
                        //TODO erro ao obter cadastro inicial firebase
                    }else {
                        inicializaEstadoCadastroInicialBD ("Firebase");

                        inicializaEstadoCadastroInicialBDCloud("Firebase");
                    }

                }
            }
        }


        if (this.ativo && !this.stopAll){
            if (this.cadastroInicialBD.getTipoUsuario() != null && !this.cadastroInicialBD.getTipoUsuario().isEmpty()){
                switch (this.cadastroInicialBD.getTipoUsuario()){
                    case TipoUsuarioENUM.CLIENTE:
                        //TODO
                        break;
                    case TipoUsuarioENUM.SALAO:
                        if (this.ativo && !this.stopAll){
                            //BUSCAR FUNCIONAMENTOS
                            if (this.funcionamentoDAO == null){
                                this.funcionamentoDAO = new FuncionamentoDAO(this);
                            }
                            this.funcionamentoSalaoBD = new FuncionamentoSalao();
                            this.funcionamentoSalaoBD.setFuncionamentoDoSalao(this.funcionamentoDAO.listarFuncionamentos());
                            this.funcionamentoSalaoBD.setVersao(this.versaoFuncionamentoBD.getVersao());
                            this.funcionamentoSalaoBDCloud = new FuncionamentoSalao();
                            this.funcionamentoSalaoBDCloud.setFuncionamentoDoSalao(this.funcionamentoDAO.listarFuncionamentosCloud());
                            this.funcionamentoSalaoBDCloud.setVersao(this.versaoFuncionamentoBDCLoud.getVersao());
                            this.funcionamentoSalaoFirebase = new FuncionamentoSalao();

                        }

                        //verifica a nescessidade de sincronizar funcioanmento salao
                        if (this.versaoFuncionamentoBD.getVersao() > this.versaoFuncionamentoFirebase.getVersao() && this.cadastroInicialBD.getCodigoUnico() != 0){
                            this.threadSalvarFuncionamentoFirebase = new ThreadSalvarFuncionamentoFirebase();
                            this.threadSalvarFuncionamentoFirebase.start();
                            try {
                                this.threadSalvarFuncionamentoFirebase.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            inicializaEstadoFuncionamentoSalaoBDCloud("BD");

                        }else if (this.versaoFuncionamentoBD.getVersao() < this.versaoFuncionamentoFirebase.getVersao() && this.cadastroInicialBD.getCodigoUnico() != 0){
                            this.threadBuscarFuncionamentoSalaoFirebase = new ThreadBuscarFuncionamentoSalaoFirebase();
                            this.threadBuscarFuncionamentoSalaoFirebase.start();
                            try {
                                this.threadBuscarFuncionamentoSalaoFirebase.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (this.funcionamentoDAO == null){
                                this.funcionamentoDAO = new FuncionamentoDAO(this);
                            }
                            if (this.versaoDAO == null){
                                this.versaoDAO = new VersaoDAO(this);
                            }

                            if(this.funcionamentoSalaoFirebase == null){
                                //TODO erro ao obter funcionamento firebase
                            }else {
                                inicializaEstadoFuncionamentoSalaoBD("Firebase");

                                inicializaEstadoFuncionamentoSalaoBDCloud("Firebase");
                            }

                        }else {
                            if ((this.versaoFuncionamentoBDCLoud.getVersao() < this.versaoFuncionamentoFirebase.getVersao() || (this.versaoFuncionamentoBDCLoud.getVersao().equals(this.versaoFuncionamentoFirebase.getVersao()) && !this.versaoFuncionamentoBDCLoud.getDataModificacao().equals(this.versaoFuncionamentoFirebase.getDataModificacao()))) && this.cadastroInicialBD.getCodigoUnico() != 0){
                                this.threadBuscarFuncionamentoSalaoFirebase = new ThreadBuscarFuncionamentoSalaoFirebase();
                                this.threadBuscarFuncionamentoSalaoFirebase.start();
                                try {
                                    this.threadBuscarFuncionamentoSalaoFirebase.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                if (this.funcionamentoDAO == null){
                                    this.funcionamentoDAO = new FuncionamentoDAO(this);
                                }
                                if (this.versaoDAO == null){
                                    this.versaoDAO = new VersaoDAO(this);
                                }

                                if(this.funcionamentoSalaoFirebase == null){
                                    //TODO erro ao obter funcionamento firebase
                                }else {
                                    inicializaEstadoFuncionamentoSalaoBD("Firebase");

                                    inicializaEstadoFuncionamentoSalaoBDCloud("Firebase");
                                }
                            }
                        }

                        //verifica a nescessidade de sincronizar servicos salao
                        if (this.versaoServicosBD.getVersao() > this.versaoServicosFirebase.getVersao()){
                            //TODO salvar funcionamento no firebase
                        }else if (this.versaoServicosBD.getVersao() < this.versaoServicosFirebase.getVersao()){
                            //TODO buscar funcionamento firebase e salvar no bd e bd cloud
                        }else {
                            if (this.versaoServicosBDCloud.getVersao() < this.versaoServicosFirebase.getVersao()){
                                //TODO buscar funcioanemnto firebase e salvar no bd cloud
                            }
                        }

                        //verifica a nescessidade de sincronizar cabeleireiros salao
                        if (this.versaoCabeleireirosBD.getVersao() > this.versaoCabeleireirosFirebase.getVersao()){
                            //TODO salvar funcionamento no firebase
                        }else if (this.versaoCabeleireirosBD.getVersao() < this.versaoCabeleireirosFirebase.getVersao()){
                            //TODO buscar funcionamento firebase e salvar no bd e bd cloud
                        }else {
                            if (this.versaoCabeleireirosBDCloud.getVersao() < this.versaoCabeleireirosFirebase.getVersao()){
                                //TODO buscar funcioanemnto firebase e salvar no bd cloud
                            }
                        }
                        break;
                    case TipoUsuarioENUM.CABELEIREIRO:
                        //TODO
                        break;
                    default:
                        break;
                }
            }
        }

        //finaliza as threads abertas
        encerrarAtividadesAbertas();

        //dispara resultado da sincronizaçao
        if (this.ativo && !this.stopAll && SplashScreenActivity.isSplashScreenActivityAtiva()){
            SplashScreenActivity.setCadastroInicialBD(this.cadastroInicialBD);
            sendBroadcast(new Intent(SplashScreenActivity.getBrodcastReceiverBancosSincronizados()));
        }

        stopSelf();
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
    private void initVersoes(){
        this.versaoCadastroInicialBD = new Versao(0);
        this.versaoFuncionamentoBD = new Versao(0);
        this.versaoServicosBD = new Versao(0);
        this.versaoCabeleireirosBD = new Versao(0);
        this.versaoCadastroInicialBDCloud = new Versao(0);
        this.versaoFuncionamentoBDCLoud = new Versao(0);
        this.versaoServicosBDCloud = new Versao(0);
        this.versaoCabeleireirosBDCloud = new Versao(0);
        this.versaoCadastroInicialFirebase = new Versao(0);
        this.versaoFuncionamentoFirebase = new Versao(0);
        this.versaoServicosFirebase = new Versao(0);
        this.versaoCabeleireirosFirebase = new Versao(0);
    }

    private void encerrarAtividadesAbertas(){
        if (this.threadBuscarCadastroInicialFirebase != null){
            this.threadBuscarCadastroInicialFirebase.interrupt();
            this.threadBuscarCadastroInicialFirebase = null;
        }
        if (this.threadSalvarCadastroInicialFirebase != null){
            this.threadSalvarCadastroInicialFirebase.interrupt();
            this.threadSalvarCadastroInicialFirebase = null;
        }
        if (this.threadBuscarVersoesFirebase != null){
            this.threadBuscarVersoesFirebase.interrupt();
            this.threadBuscarVersoesFirebase = null;
        }
        if (this.threadBuscarFuncionamentoSalaoFirebase != null){
            this.threadBuscarFuncionamentoSalaoFirebase.interrupt();
            this.threadBuscarFuncionamentoSalaoFirebase = null;
        }
        if (this.threadSalvarFuncionamentoFirebase != null){
            this.threadSalvarFuncionamentoFirebase.interrupt();
            this.threadSalvarFuncionamentoFirebase = null;
        }
        if (this.cadastroInicialDAO != null){
            this.cadastroInicialDAO.fechar();
            this.cadastroInicialDAO = null;
        }
        if (this.versaoDAO != null){
            this.versaoDAO.fechar();
            this.versaoDAO = null;
        }
    }

    private void inicializaEstadoCadastroInicialBD (String bdEspelho){
        Versao versao = new Versao();
        CadastroInicial cadastroInicial = new CadastroInicial();
        switch (bdEspelho){
            case "BDCloud":
                versao = this.versaoCadastroInicialBDCloud;
                cadastroInicial = this.cadastroInicialBDCloud;
                break;
            case "Firebase":
                versao = this.versaoCadastroInicialFirebase;
                cadastroInicial = this.cadastroInicialFirebase;
                break;
            default:
                break;
        }

        this.cadastroInicialBD = this.cadastroInicialDAO.buscarCadastroInicialPorUID(this.uid);
        if (this.cadastroInicialBD != null){
            this.cadastroInicialBD.setTipoUsuario(cadastroInicial.getTipoUsuario());
            this.cadastroInicialBD.setNivelUsuario(cadastroInicial.getNivelUsuario());
            this.cadastroInicialBD.setCodigoUnico(cadastroInicial.getCodigoUnico());
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroInicialDAO.salvarCadastroInicial(this.cadastroInicialBD);
            }
        }else {
            this.cadastroInicialBD = new CadastroInicial();
            this.cadastroInicialBD.setUid(this.uid);
            this.cadastroInicialBD.setTipoUsuario(cadastroInicial.getTipoUsuario());
            this.cadastroInicialBD.setNivelUsuario(cadastroInicial.getNivelUsuario());
            this.cadastroInicialBD.setCodigoUnico(cadastroInicial.getCodigoUnico());
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroInicialDAO.salvarCadastroInicial(this.cadastroInicialBD);
            }
        }

        if (this.versaoCadastroInicialBD == null){
            this.versaoCadastroInicialBD = new Versao();
        }
        this.versaoCadastroInicialBD.setVersao(versao.getVersao());
        this.versaoCadastroInicialBD.setUid(this.uid);
        this.versaoCadastroInicialBD.setDataModificacao(versao.getDataModificacao());
        this.versaoCadastroInicialBD.setIdentificacaoTabela(DatabaseHelper.CadastroInicial.TABELA);
        long result = -1;
        while (this.ativo && !this.stopAll && result == -1){
            result = this.versaoDAO.salvarVersao(versao);
        }
    }

    private void inicializaEstadoCadastroInicialBDCloud(String bdEspelho){
        Versao versao = new Versao();
        CadastroInicial cadastroInicial = new CadastroInicial();
        switch (bdEspelho){
            case "BD":
                versao = this.versaoCadastroInicialBD;
                cadastroInicial = this.cadastroInicialBD;
                break;
            case "Firebase":
                versao = this.versaoCadastroInicialFirebase;
                cadastroInicial = this.cadastroInicialFirebase;
                break;
            default:
                break;
        }

        this.cadastroInicialBDCloud = this.cadastroInicialDAO.buscarCadastroInicialPorUIDCloud(this.uid);
        if (this.cadastroInicialBDCloud != null){
            this.cadastroInicialBDCloud.setTipoUsuario(cadastroInicial.getTipoUsuario());
            this.cadastroInicialBDCloud.setNivelUsuario(cadastroInicial.getNivelUsuario());
            this.cadastroInicialBDCloud.setCodigoUnico(cadastroInicial.getCodigoUnico());
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroInicialDAO.salvarCadastroInicialCloud(this.cadastroInicialBDCloud);
            }
        }else {
            this.cadastroInicialBDCloud = new CadastroInicial();
            this.cadastroInicialBDCloud.setUid(this.uid);
            this.cadastroInicialBDCloud.setTipoUsuario(cadastroInicial.getTipoUsuario());
            this.cadastroInicialBDCloud.setNivelUsuario(cadastroInicial.getNivelUsuario());
            this.cadastroInicialBDCloud.setCodigoUnico(cadastroInicial.getCodigoUnico());
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.cadastroInicialDAO.salvarCadastroInicialCloud(this.cadastroInicialBDCloud);
            }
        }

        if (this.versaoCadastroInicialBDCloud == null){
            this.versaoCadastroInicialBDCloud = new Versao();
        }
        this.versaoCadastroInicialBDCloud.setVersao(versao.getVersao());
        this.versaoCadastroInicialBDCloud.setUid(this.uid);
        this.versaoCadastroInicialBDCloud.setDataModificacao(versao.getDataModificacao());
        this.versaoCadastroInicialBDCloud.setIdentificacaoTabela(DatabaseHelper.CadastroInicial.TABELA);
        long result = -1;
        while (this.ativo && !this.stopAll && result == -1){
            result = this.versaoDAO.salvarVersaoCloud(versao);
        }
    }

    private void inicializaEstadoFuncionamentoSalaoBD(String bdEspelho){
        Versao versao = new Versao();
        FuncionamentoSalao funcionamentoSalao = new FuncionamentoSalao();
        switch (bdEspelho){
            case "BDCloud":
                versao = this.versaoFuncionamentoBDCLoud;
                funcionamentoSalao = this.funcionamentoSalaoBDCloud;
                break;
            case "Firebase":
                versao = this.versaoFuncionamentoFirebase;
                funcionamentoSalao = this.funcionamentoSalaoFirebase;
                break;
            default:
                break;
        }

        //remover funcionamento de todos os dias
        this.funcionamentoDAO.removerFuncionamentoPorDia(DiasENUM.SEGUNDA);
        this.funcionamentoDAO.removerFuncionamentoPorDia(DiasENUM.TERCA);
        this.funcionamentoDAO.removerFuncionamentoPorDia(DiasENUM.QUARTA);
        this.funcionamentoDAO.removerFuncionamentoPorDia(DiasENUM.QUINTA);
        this.funcionamentoDAO.removerFuncionamentoPorDia(DiasENUM.SEXTA);
        this.funcionamentoDAO.removerFuncionamentoPorDia(DiasENUM.SABADO);
        this.funcionamentoDAO.removerFuncionamentoPorDia(DiasENUM.DOMINGO);

        //salvar os funcionamentos dos dias
        for (Funcionamento f : funcionamentoSalao.getFuncionamentoDoSalao()){
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.funcionamentoDAO.salvarFuncionamento(f);
            }
        }

        //atualizar a versao de funcionamento salva
        this.versaoFuncionamentoBD.setIdentificacaoTabela(DatabaseHelper.Funcionamento.TABELA);
        this.versaoFuncionamentoBD.setVersao(versao.getVersao());
        this.versaoFuncionamentoBD.setDataModificacao(versao.getDataModificacao());
        this.versaoFuncionamentoBD.setUid(this.uid);
        long result = -1;
        while (this.ativo && !this.stopAll && result == -1){
            result = this.versaoDAO.salvarVersao(this.versaoFuncionamentoBD);
        }
    }

    private void inicializaEstadoFuncionamentoSalaoBDCloud(String bdEspelho){
        Versao versao = new Versao();
        FuncionamentoSalao funcionamentoSalao = new FuncionamentoSalao();
        switch (bdEspelho){
            case "BD":
                versao = this.versaoFuncionamentoBD;
                funcionamentoSalao = this.funcionamentoSalaoBD;
                break;
            case "Firebase":
                versao = this.versaoFuncionamentoFirebase;
                funcionamentoSalao = this.funcionamentoSalaoFirebase;
                break;
            default:
                break;
        }

        //remover funcionamento de todos os dias
        this.funcionamentoDAO.removerFuncionamentoPorDiaCloud(DiasENUM.SEGUNDA);
        this.funcionamentoDAO.removerFuncionamentoPorDiaCloud(DiasENUM.TERCA);
        this.funcionamentoDAO.removerFuncionamentoPorDiaCloud(DiasENUM.QUARTA);
        this.funcionamentoDAO.removerFuncionamentoPorDiaCloud(DiasENUM.QUINTA);
        this.funcionamentoDAO.removerFuncionamentoPorDiaCloud(DiasENUM.SEXTA);
        this.funcionamentoDAO.removerFuncionamentoPorDiaCloud(DiasENUM.SABADO);
        this.funcionamentoDAO.removerFuncionamentoPorDiaCloud(DiasENUM.DOMINGO);

        //salvar os funcionamentos dos dias
        for (Funcionamento f : funcionamentoSalao.getFuncionamentoDoSalao()){
            long result = -1;
            while (this.ativo && !this.stopAll && result == -1){
                result = this.funcionamentoDAO.salvarFuncionamentoCloud(f);
            }
        }

        //atualizar a versao de funcionamento salva
        this.versaoFuncionamentoBDCLoud.setIdentificacaoTabela(DatabaseHelper.Funcionamento.TABELA);
        this.versaoFuncionamentoBDCLoud.setVersao(versao.getVersao());
        this.versaoFuncionamentoBDCLoud.setDataModificacao(versao.getDataModificacao());
        this.versaoFuncionamentoBDCLoud.setUid(this.uid);
        long result = -1;
        while (this.ativo && !this.stopAll && result == -1){
            result = this.versaoDAO.salvarVersaoCloud(this.versaoFuncionamentoBDCLoud);
        }

    }

    //INITS
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

    //THREADS
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
                            Log.i("script","vELCadastroinicial dataSnapshot == null");
                            cadastroInicialFirebase = null;
                            if (threadBuscarCadastroInicialFirebase != null){
                                threadBuscarCadastroInicialFirebase.interrupt();
                                threadBuscarCadastroInicialFirebase = null;
                            }
                        }else {
                            Log.i("script","vELCadastroinicial dataSnapshot != null");
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
                        Log.i("script","vELVersoes onCancelled");
                        cadastroInicialFirebase = null;
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
                if (cadastroInicialBD.getNivelUsuario() != null && (cadastroInicialBDCloud == null || cadastroInicialBDCloud.getNivelUsuario() == null || !cadastroInicialBD.getNivelUsuario().equals(cadastroInicialBDCloud.getNivelUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroInicialBD.getNivelUsuario());
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroInicialBD.getTipoUsuario() != null && (cadastroInicialBDCloud == null || cadastroInicialBDCloud.getTipoUsuario() == null || !cadastroInicialBD.getTipoUsuario().equals(cadastroInicialBDCloud.getTipoUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroInicialBD.getTipoUsuario());
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroInicialBD.getCodigoUnico() != null && cadastroInicialBD.getCodigoUnico() != 0 && (cadastroInicialBDCloud == null ||cadastroInicialBDCloud.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDCloud.getCodigoUnico()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroInicialBD.getCodigoUnico());
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
            }
            else{
                if (cadastroInicialBD.getNivelUsuario() != null && (cadastroInicialBDCloud == null || cadastroInicialBDCloud.getNivelUsuario() == null || !cadastroInicialBD.getNivelUsuario().equals(cadastroInicialBDCloud.getNivelUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.NIVEL_USUARIO).setValue(cadastroInicialBD.getNivelUsuario(), completionListener[0]);
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroInicialBD.getTipoUsuario() != null && (cadastroInicialBDCloud == null || cadastroInicialBDCloud.getTipoUsuario() == null || !cadastroInicialBD.getTipoUsuario().equals(cadastroInicialBDCloud.getTipoUsuario()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.TIPO_USUARIO).setValue(cadastroInicialBD.getTipoUsuario(), completionListener[0]);
                }else {
                    this.numDadosCadastroInicialSalvos++;
                    this.numRespostasCadastroInicial++;
                }
                if (cadastroInicialBD.getCodigoUnico() != null && cadastroInicialBD.getCodigoUnico() != 0 && (cadastroInicialBDCloud == null || cadastroInicialBDCloud.getCodigoUnico() == null || !cadastroInicialBD.getCodigoUnico().equals(cadastroInicialBDCloud.getCodigoUnico()))){
                    firebaseCadastroInicial.child(DatabaseHelper.CadastroInicial.CODIGO_UNICO).setValue(cadastroInicialBD.getCodigoUnico(), completionListener[0]);
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

    private class ThreadBuscarVersoesFirebase extends Thread{
        //CONTROLE
        int buscasRespondidas;
        //FIREBASE
        private DatabaseReference firebaseVersaoCadastroInicial;
        private DatabaseReference firebaseVersaoFuncionamentoSalao;
        private DatabaseReference firebaseVersaoServicosSalao;
        private DatabaseReference firebaseVersaoCabeleireirosSalao;

        private ValueEventListener vELVersoes;

        @Override
        public void run(){
            Log.i("script","ThreadBuscarVersoesFirebase");
            initFirebase();
            this.firebaseVersaoCadastroInicial.addListenerForSingleValueEvent(this.vELVersoes);
            this.firebaseVersaoFuncionamentoSalao.addListenerForSingleValueEvent(this.vELVersoes);
            this.firebaseVersaoServicosSalao.addListenerForSingleValueEvent(this.vELVersoes);
            this.firebaseVersaoCabeleireirosSalao.addListenerForSingleValueEvent(this.vELVersoes);


            boolean msgExibida = false;
            while (!isInterrupted() && !stopAll && this.buscasRespondidas < 4){
                if (!msgExibida){
                    msgExibida = true;
                    Log.i("script","ThreadBuscarVersoesFirebase aguardando resposta firebase ...");
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
            this.firebaseVersaoCadastroInicial.removeEventListener(this.vELVersoes);
            this.firebaseVersaoFuncionamentoSalao.removeEventListener(this.vELVersoes);
            this.firebaseVersaoServicosSalao.removeEventListener(this.vELVersoes);
            this.firebaseVersaoCabeleireirosSalao.removeEventListener(this.vELVersoes);
        }

        private void initFirebase(){
            if (this.firebaseVersaoCadastroInicial == null){
                this.firebaseVersaoCadastroInicial = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.CadastroInicial.TABELA);
            }
            if (this.firebaseVersaoFuncionamentoSalao == null){
                this.firebaseVersaoFuncionamentoSalao = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.Funcionamento.TABELA);
            }
            if (this.firebaseVersaoServicosSalao == null){
                this.firebaseVersaoServicosSalao = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.Servico.TABELA);
            }
            if (this.firebaseVersaoCabeleireirosSalao == null){
                this.firebaseVersaoCabeleireirosSalao = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.Cabeleireiro.TABELA);
            }
            if (this.vELVersoes == null){
                this.vELVersoes = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELVersoes dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELVersoes dataSnapshot != null");
                            if (versoesFirebase == null){
                                versoesFirebase = new ArrayList<Versao>();
                            }

                            Versao versao = new Versao();
                            if (map.containsKey(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA)){
                                versao.setIdentificacaoTabela(String.valueOf(map.get(DatabaseHelper.Versoes.IDENTIFICACAO_TABELA)));
                            }
                            if (map.containsKey(DatabaseHelper.Versoes.DATA_MODIFICACAO)){
                                versao.setDataModificacao(String.valueOf(map.get(DatabaseHelper.Versoes.DATA_MODIFICACAO)));
                            }
                            if (map.containsKey(DatabaseHelper.Versoes.VERSAO)){
                                versao.setVersao(Integer.valueOf(map.get(DatabaseHelper.Versoes.VERSAO).toString()));
                            }
                            versoesFirebase.add(versao);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELVersoes onCancelled");
                        if (threadBuscarCadastroInicialFirebase != null){
                            threadBuscarCadastroInicialFirebase.interrupt();
                            threadBuscarCadastroInicialFirebase = null;
                        }
                    }
                };
            }
        }
    }

    private class ThreadBuscarFuncionamentoSalaoFirebase extends Thread{
        //CONTROLE
        int buscasRespondidas;

        //FIREBASE
        private DatabaseReference firebaseFuncionamentoSalaoSegunda;
        private DatabaseReference firebaseFuncionamentoSalaoTerca;
        private DatabaseReference firebaseFuncionamentoSalaoQuarta;
        private DatabaseReference firebaseFuncionamentoSalaoQuinta;
        private DatabaseReference firebaseFuncionamentoSalaoSexta;
        private DatabaseReference firebaseFuncionamentoSalaoSabado;
        private DatabaseReference firebaseFuncionamentoSalaoDomingo;
        private ValueEventListener vELFuncionamentoSalaoSegunda;
        private ValueEventListener vELFuncionamentoSalaoTerca;
        private ValueEventListener vELFuncionamentoSalaoQuarta;
        private ValueEventListener vELFuncionamentoSalaoQuinta;
        private ValueEventListener vELFuncionamentoSalaoSexta;
        private ValueEventListener vELFuncionamentoSalaoSabado;
        private ValueEventListener vELFuncionamentoSalaoDomingo;

        @Override
        public void run(){
            Log.i("script","ThreadBuscarFuncionamentoSalaoFirebase");
            initFirebase();
            this.firebaseFuncionamentoSalaoSegunda.addListenerForSingleValueEvent(this.vELFuncionamentoSalaoSegunda);
            this.firebaseFuncionamentoSalaoTerca.addListenerForSingleValueEvent(this.vELFuncionamentoSalaoTerca);
            this.firebaseFuncionamentoSalaoQuarta.addListenerForSingleValueEvent(this.vELFuncionamentoSalaoQuarta);
            this.firebaseFuncionamentoSalaoQuinta.addListenerForSingleValueEvent(this.vELFuncionamentoSalaoQuinta);
            this.firebaseFuncionamentoSalaoSexta.addListenerForSingleValueEvent(this.vELFuncionamentoSalaoSexta);
            this.firebaseFuncionamentoSalaoSabado.addListenerForSingleValueEvent(this.vELFuncionamentoSalaoSabado);
            this.firebaseFuncionamentoSalaoDomingo.addListenerForSingleValueEvent(this.vELFuncionamentoSalaoDomingo);
            boolean msgExibida = false;
            while (!isInterrupted() && !stopAll && buscasRespondidas < 7){
                if (!msgExibida){
                    msgExibida = true;
                    Log.i("script","ThreadBuscarFuncionamentoSalaoFirebase aguardando resposta firebase ...");
                }
            }
            if (threadBuscarFuncionamentoSalaoFirebase != null){
                threadBuscarFuncionamentoSalaoFirebase.interrupt();
                threadBuscarFuncionamentoSalaoFirebase = null;
            }
        }

        @Override
        public void interrupt() {
            super.interrupt();
            this.firebaseFuncionamentoSalaoSegunda.removeEventListener(this.vELFuncionamentoSalaoSegunda);
            this.firebaseFuncionamentoSalaoTerca.removeEventListener(this.vELFuncionamentoSalaoTerca);
            this.firebaseFuncionamentoSalaoQuarta.removeEventListener(this.vELFuncionamentoSalaoQuarta);
            this.firebaseFuncionamentoSalaoQuinta.removeEventListener(this.vELFuncionamentoSalaoQuinta);
            this.firebaseFuncionamentoSalaoSexta.removeEventListener(this.vELFuncionamentoSalaoSexta);
            this.firebaseFuncionamentoSalaoSabado.removeEventListener(this.vELFuncionamentoSalaoSabado);
            this.firebaseFuncionamentoSalaoDomingo.removeEventListener(this.vELFuncionamentoSalaoDomingo);
        }

        private void initFirebase(){
            if (this.firebaseFuncionamentoSalaoSegunda == null){
                this.firebaseFuncionamentoSalaoSegunda = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.SEGUNDA);
            }
            if (this.firebaseFuncionamentoSalaoTerca == null){
                this.firebaseFuncionamentoSalaoTerca = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.TERCA);
            }
            if (this.firebaseFuncionamentoSalaoQuarta == null){
                this.firebaseFuncionamentoSalaoQuarta = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.QUARTA);
            }
            if (this.firebaseFuncionamentoSalaoQuinta == null){
                this.firebaseFuncionamentoSalaoQuinta = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.QUINTA);
            }
            if (this.firebaseFuncionamentoSalaoSexta == null){
                this.firebaseFuncionamentoSalaoSexta = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.SEXTA);
            }
            if (this.firebaseFuncionamentoSalaoSabado == null){
                this.firebaseFuncionamentoSalaoSabado = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.SABADO);
            }
            if (this.firebaseFuncionamentoSalaoDomingo == null){
                this.firebaseFuncionamentoSalaoDomingo = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.DOMINGO);
            }
            if (this.vELFuncionamentoSalaoSegunda == null){
                this.vELFuncionamentoSalaoSegunda = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELFuncionamentoSalaoSegunda dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELFuncionamentoSalaoSegunda dataSnapshot != null");
                            Funcionamento funcionamento = new Funcionamento();
                            funcionamento.setDia(DiasENUM.SEGUNDA);
                            if (map.containsKey(DiasENUM.ABRE)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setAbre(String.valueOf(map.get(DiasENUM.ABRE)));
                            }
                            if (map.containsKey(DiasENUM.FECHA)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setFecha(String.valueOf(map.get(DiasENUM.FECHA)));
                            }
                            funcionamentoSalaoFirebase.addFuncionamento(funcionamento);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELFuncionamentoSalao onCancelled");
                        funcionamentoSalaoFirebase = null;
                        if (threadBuscarFuncionamentoSalaoFirebase != null){
                            threadBuscarFuncionamentoSalaoFirebase.interrupt();
                            threadBuscarFuncionamentoSalaoFirebase = null;
                        }
                    }
                };
            }
            if (this.vELFuncionamentoSalaoTerca == null){
                this.vELFuncionamentoSalaoTerca = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELFuncionamentoSalaoTerca dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELFuncionamentoSalaoTerca dataSnapshot != null");
                            Funcionamento funcionamento = new Funcionamento();
                            funcionamento.setDia(DiasENUM.TERCA);
                            if (map.containsKey(DiasENUM.ABRE)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setAbre(String.valueOf(map.get(DiasENUM.ABRE)));
                            }
                            if (map.containsKey(DiasENUM.FECHA)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setFecha(String.valueOf(map.get(DiasENUM.FECHA)));
                            }
                            funcionamentoSalaoFirebase.addFuncionamento(funcionamento);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELFuncionamentoSalao onCancelled");
                        funcionamentoSalaoFirebase = null;
                        if (threadBuscarFuncionamentoSalaoFirebase != null){
                            threadBuscarFuncionamentoSalaoFirebase.interrupt();
                            threadBuscarFuncionamentoSalaoFirebase = null;
                        }
                    }
                };
            }
            if (this.vELFuncionamentoSalaoQuarta == null){
                this.vELFuncionamentoSalaoQuarta = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELFuncionamentoSalaoQuarta dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELFuncionamentoSalaoQuarta dataSnapshot != null");
                            Funcionamento funcionamento = new Funcionamento();
                            funcionamento.setDia(DiasENUM.QUARTA);
                            if (map.containsKey(DiasENUM.ABRE)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setAbre(String.valueOf(map.get(DiasENUM.ABRE)));
                            }
                            if (map.containsKey(DiasENUM.FECHA)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setFecha(String.valueOf(map.get(DiasENUM.FECHA)));
                            }
                            funcionamentoSalaoFirebase.addFuncionamento(funcionamento);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELFuncionamentoSalao onCancelled");
                        funcionamentoSalaoFirebase = null;
                        if (threadBuscarFuncionamentoSalaoFirebase != null){
                            threadBuscarFuncionamentoSalaoFirebase.interrupt();
                            threadBuscarFuncionamentoSalaoFirebase = null;
                        }
                    }
                };
            }
            if (this.vELFuncionamentoSalaoQuinta == null){
                this.vELFuncionamentoSalaoQuinta = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELFuncionamentoSalaoQuinta dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELFuncionamentoSalaoQuinta dataSnapshot != null");
                            Funcionamento funcionamento = new Funcionamento();
                            funcionamento.setDia(DiasENUM.QUINTA);
                            if (map.containsKey(DiasENUM.ABRE)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setAbre(String.valueOf(map.get(DiasENUM.ABRE)));
                            }
                            if (map.containsKey(DiasENUM.FECHA)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setFecha(String.valueOf(map.get(DiasENUM.FECHA)));
                            }
                            funcionamentoSalaoFirebase.addFuncionamento(funcionamento);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELFuncionamentoSalao onCancelled");
                        funcionamentoSalaoFirebase = null;
                        if (threadBuscarFuncionamentoSalaoFirebase != null){
                            threadBuscarFuncionamentoSalaoFirebase.interrupt();
                            threadBuscarFuncionamentoSalaoFirebase = null;
                        }
                    }
                };
            }
            if (this.vELFuncionamentoSalaoSexta == null){
                this.vELFuncionamentoSalaoSexta = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELFuncionamentoSalaoSexta dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELFuncionamentoSalaoSexta dataSnapshot != null");
                            Funcionamento funcionamento = new Funcionamento();
                            funcionamento.setDia(DiasENUM.SEXTA);
                            if (map.containsKey(DiasENUM.ABRE)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setAbre(String.valueOf(map.get(DiasENUM.ABRE)));
                            }
                            if (map.containsKey(DiasENUM.FECHA)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setFecha(String.valueOf(map.get(DiasENUM.FECHA)));
                            }
                            funcionamentoSalaoFirebase.addFuncionamento(funcionamento);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELFuncionamentoSalao onCancelled");
                        funcionamentoSalaoFirebase = null;
                        if (threadBuscarFuncionamentoSalaoFirebase != null){
                            threadBuscarFuncionamentoSalaoFirebase.interrupt();
                            threadBuscarFuncionamentoSalaoFirebase = null;
                        }
                    }
                };
            }
            if (this.vELFuncionamentoSalaoSabado == null){
                this.vELFuncionamentoSalaoSabado = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELFuncionamentoSalaoSabado dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELFuncionamentoSalaoSabado dataSnapshot != null");
                            Funcionamento funcionamento = new Funcionamento();
                            funcionamento.setDia(DiasENUM.SABADO);
                            if (map.containsKey(DiasENUM.ABRE)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setAbre(String.valueOf(map.get(DiasENUM.ABRE)));
                            }
                            if (map.containsKey(DiasENUM.FECHA)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setFecha(String.valueOf(map.get(DiasENUM.FECHA)));
                            }
                            funcionamentoSalaoFirebase.addFuncionamento(funcionamento);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELFuncionamentoSalao onCancelled");
                        funcionamentoSalaoFirebase = null;
                        if (threadBuscarFuncionamentoSalaoFirebase != null){
                            threadBuscarFuncionamentoSalaoFirebase.interrupt();
                            threadBuscarFuncionamentoSalaoFirebase = null;
                        }
                    }
                };
            }
            if (this.vELFuncionamentoSalaoDomingo == null){
                this.vELFuncionamentoSalaoDomingo = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                        if (map == null || map.size() == 0){
                            Log.i("script","vELFuncionamentoSalaoDomingo dataSnapshot == null");
                            buscasRespondidas++;
                        }else {
                            Log.i("script","vELFuncionamentoSalaoDomingo dataSnapshot != null");
                            Funcionamento funcionamento = new Funcionamento();
                            funcionamento.setDia(DiasENUM.DOMINGO);
                            if (map.containsKey(DiasENUM.ABRE)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setAbre(String.valueOf(map.get(DiasENUM.ABRE)));
                            }
                            if (map.containsKey(DiasENUM.FECHA)){
                                if (funcionamentoSalaoFirebase == null){
                                    funcionamentoSalaoFirebase = new FuncionamentoSalao();
                                    funcionamentoSalaoFirebase.setVersao(versaoFuncionamentoFirebase.getVersao());
                                }
                                funcionamento.setFecha(String.valueOf(map.get(DiasENUM.FECHA)));
                            }
                            funcionamentoSalaoFirebase.addFuncionamento(funcionamento);
                            buscasRespondidas++;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.i("script","vELFuncionamentoSalao onCancelled");
                        funcionamentoSalaoFirebase = null;
                        if (threadBuscarFuncionamentoSalaoFirebase != null){
                            threadBuscarFuncionamentoSalaoFirebase.interrupt();
                            threadBuscarFuncionamentoSalaoFirebase = null;
                        }
                    }
                };
            }
        }
    }

    private class ThreadSalvarFuncionamentoFirebase extends Thread{
        private DatabaseReference firebaseFuncionamentoSalaoSegunda;
        private DatabaseReference firebaseFuncionamentoSalaoTerca;
        private DatabaseReference firebaseFuncionamentoSalaoQuarta;
        private DatabaseReference firebaseFuncionamentoSalaoQuinta;
        private DatabaseReference firebaseFuncionamentoSalaoSexta;
        private DatabaseReference firebaseFuncionamentoSalaoSabado;
        private DatabaseReference firebaseFuncionamentoSalaoDomingo;
        private DatabaseReference firebaseVersaoFuncionamentoSalao;
        private DatabaseReference.CompletionListener completionListenerFuncionamento;
        private DatabaseReference.CompletionListener completionListenerVersaoFuncionamento;

        //CONTROLE
        private int numDadosFuncionamentosSalvos;
        private int numRespostasFuncionamentos;
        private int numDadosVersaoSalvos;
        private int numRespostasVersao;

        //FUNCIONAMENTOS
        private Funcionamento funcionamentoSegunda;
        private Funcionamento funcionamentoTerca;
        private Funcionamento funcionamentoQuarta;
        private Funcionamento funcionamentoQuinta;
        private Funcionamento funcionamentoSexta;
        private Funcionamento funcionamentoSabado;
        private Funcionamento funcionamentoDomingo;
        private Funcionamento funcionamentoSegundaCloud;
        private Funcionamento funcionamentoTercaCloud;
        private Funcionamento funcionamentoQuartaCloud;
        private Funcionamento funcionamentoQuintaCloud;
        private Funcionamento funcionamentoSextaCloud;
        private Funcionamento funcionamentoSabadoCloud;
        private Funcionamento funcionamentoDomingoCloud;


        @Override
        public void run(){
            Log.i("script","ThreadSalvarFuncionamentoFirebase");
            initFirebase();
            initControles();
            initFuncionamentos();

            //salvar funcionamentod e segunda
            if (this.funcionamentoSegunda != null){
                while (!isInterrupted() && !stopAll && this.numDadosFuncionamentosSalvos < 2){
                    salvarFuncionamentoSegundaNoFirebase(this.completionListenerFuncionamento);

                    boolean msgExibida = false;
                    while (!isInterrupted() && !stopAll && this.numRespostasFuncionamentos < 2){
                        if (!msgExibida){
                            msgExibida = true;
                            Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar funcionamento segunda firebase ...");
                        }
                    }
                }
            }

            //salvar funcionamentod e terca
            if (this.funcionamentoTerca != null){
                this.numRespostasFuncionamentos = 0;
                this.numDadosFuncionamentosSalvos = 0;
                while (!isInterrupted() && !stopAll && this.numDadosFuncionamentosSalvos < 2){
                    salvarFuncionamentoTercaNoFirebase(this.completionListenerFuncionamento);

                    boolean msgExibida = false;
                    while (!isInterrupted() && !stopAll && this.numRespostasFuncionamentos < 2){
                        if (!msgExibida){
                            msgExibida = true;
                            Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar funcionamento terca firebase ...");
                        }
                    }
                }
            }

            //salvar funcionamentod e quarta
            if (this.funcionamentoQuarta != null){
                this.numRespostasFuncionamentos = 0;
                this.numDadosFuncionamentosSalvos = 0;
                while (!isInterrupted() && !stopAll && this.numDadosFuncionamentosSalvos < 2){
                    salvarFuncionamentoQuartaNoFirebase(this.completionListenerFuncionamento);

                    boolean msgExibida = false;
                    while (!isInterrupted() && !stopAll && this.numRespostasFuncionamentos < 2){
                        if (!msgExibida){
                            msgExibida = true;
                            Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar funcionamento quarta firebase ...");
                        }
                    }
                }
            }

            //salvar funcionamentod e quinta
            if (this.funcionamentoQuinta != null){
                this.numRespostasFuncionamentos = 0;
                this.numDadosFuncionamentosSalvos = 0;
                while (!isInterrupted() && !stopAll && this.numDadosFuncionamentosSalvos < 2){
                    salvarFuncionamentoQuintaNoFirebase(this.completionListenerFuncionamento);

                    boolean msgExibida = false;
                    while (!isInterrupted() && !stopAll && this.numRespostasFuncionamentos < 2){
                        if (!msgExibida){
                            msgExibida = true;
                            Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar funcionamento quinta firebase ...");
                        }
                    }
                }
            }

            //salvar funcionamentod e sexta
            if (this.funcionamentoSexta != null){
                this.numRespostasFuncionamentos = 0;
                this.numDadosFuncionamentosSalvos = 0;
                while (!isInterrupted() && !stopAll && this.numDadosFuncionamentosSalvos < 2){
                    salvarFuncionamentoSextaNoFirebase(this.completionListenerFuncionamento);

                    boolean msgExibida = false;
                    while (!isInterrupted() && !stopAll && this.numRespostasFuncionamentos < 2){
                        if (!msgExibida){
                            msgExibida = true;
                            Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar funcionamento sexta firebase ...");
                        }
                    }
                }
            }

            //salvar funcionamentod e sabado
            if (this.funcionamentoSabado != null){
                this.numRespostasFuncionamentos = 0;
                this.numDadosFuncionamentosSalvos = 0;
                while (!isInterrupted() && !stopAll && this.numDadosFuncionamentosSalvos < 2){
                    salvarFuncionamentoSabadoNoFirebase(this.completionListenerFuncionamento);

                    boolean msgExibida = false;
                    while (!isInterrupted() && !stopAll && this.numRespostasFuncionamentos < 2){
                        if (!msgExibida){
                            msgExibida = true;
                            Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar funcionamento sabado firebase ...");
                        }
                    }
                }
            }

            //salvar funcionamentod e domingo
            if (this.funcionamentoDomingo != null){
                this.numRespostasFuncionamentos = 0;
                this.numDadosFuncionamentosSalvos = 0;
                while (!isInterrupted() && !stopAll && this.numDadosFuncionamentosSalvos < 2){
                    salvarFuncionamentoDomingoNoFirebase(this.completionListenerFuncionamento);

                    boolean msgExibida = false;
                    while (!isInterrupted() && !stopAll && this.numRespostasFuncionamentos < 2){
                        if (!msgExibida){
                            msgExibida = true;
                            Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar funcionamento domingo firebase ...");
                        }
                    }
                }
            }

            while (!isInterrupted() && !stopAll && this.numDadosVersaoSalvos < 2){
                salvarVersaoFuncionamentoNoFirebase(this.completionListenerVersaoFuncionamento);

                boolean msgExibida = false;
                while (!isInterrupted() && !stopAll && this.numRespostasVersao < 2){
                    if (!msgExibida){
                        msgExibida = true;
                        Log.i("script","ThreadSalvarFuncionamentoFirebase aguardando resposta salvar versao funcionamento firebase ...");
                    }
                }
            }

            if (threadSalvarFuncionamentoFirebase != null){
                threadSalvarFuncionamentoFirebase.interrupt();
                threadSalvarFuncionamentoFirebase = null;
            }

        }

        @Override
        public void interrupt() {
            super.interrupt();
        }

        private void initFirebase(){
            if (this.completionListenerFuncionamento == null){
                this.completionListenerFuncionamento = new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            Log.i("script","completionListenerFuncionamento funcionamento nao foi Salvo");
                            numRespostasFuncionamentos++;
                        }else{
                            Log.i("script","completionListenerFuncionamento funcionamento foi Salvo");
                            numDadosFuncionamentosSalvos++;
                            numRespostasFuncionamentos++;
                        }
                    }
                };
            }
            if (this.completionListenerVersaoFuncionamento == null){
                this.completionListenerVersaoFuncionamento = new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                        if (databaseError != null){
                            Log.i("script","completionListenerVersaoFuncionamento versao funcionamento nao foi Salvo");
                            numRespostasVersao++;
                        }else{
                            Log.i("script","completionListenerVersaoFuncionamento versao funcionamento foi Salvo");
                            numDadosVersaoSalvos++;
                            numRespostasVersao++;
                        }
                    }
                };
            }
        }

        private void initControles(){
            this.numDadosFuncionamentosSalvos = 0;
            this.numRespostasFuncionamentos = 0;
            this.numDadosVersaoSalvos = 0;
            this.numRespostasVersao = 0;
        }

        private void initFuncionamentos(){
            for (Funcionamento f : funcionamentoSalaoBD.getFuncionamentoDoSalao()){
                switch (f.getDia()){
                    case DiasENUM.SEGUNDA:
                        this.funcionamentoSegunda = f;
                        break;
                    case DiasENUM.TERCA:
                        this.funcionamentoTerca = f;
                        break;
                    case DiasENUM.QUARTA:
                        this.funcionamentoQuarta = f;
                        break;
                    case DiasENUM.QUINTA:
                        this.funcionamentoQuinta = f;
                        break;
                    case DiasENUM.SEXTA:
                        this.funcionamentoSexta = f;
                        break;
                    case DiasENUM.SABADO:
                        this.funcionamentoSabado = f;
                        break;
                    case DiasENUM.DOMINGO:
                        this.funcionamentoDomingo = f;
                        break;
                }
            }
            for (Funcionamento f : funcionamentoSalaoBDCloud.getFuncionamentoDoSalao()){
                switch (f.getDia()){
                    case DiasENUM.SEGUNDA:
                        this.funcionamentoSegundaCloud = f;
                        break;
                    case DiasENUM.TERCA:
                        this.funcionamentoTercaCloud = f;
                        break;
                    case DiasENUM.QUARTA:
                        this.funcionamentoQuartaCloud = f;
                        break;
                    case DiasENUM.QUINTA:
                        this.funcionamentoQuintaCloud = f;
                        break;
                    case DiasENUM.SEXTA:
                        this.funcionamentoSextaCloud = f;
                        break;
                    case DiasENUM.SABADO:
                        this.funcionamentoSabadoCloud = f;
                        break;
                    case DiasENUM.DOMINGO:
                        this.funcionamentoDomingoCloud = f;
                        break;
                }
            }
        }

        private void salvarFuncionamentoSegundaNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseFuncionamentoSalaoSegunda == null){
                this.firebaseFuncionamentoSalaoSegunda = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.SEGUNDA);
            }

            if( completionListener.length == 0 ){
                if (this.funcionamentoSegunda != null && (this.funcionamentoSegundaCloud == null || !this.funcionamentoSegunda.getAbre().equals(this.funcionamentoSegundaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoSegunda.child(DiasENUM.ABRE).setValue(funcionamentoSegunda.getAbre());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoSegunda != null && (this.funcionamentoSegundaCloud == null || !this.funcionamentoSegunda.getFecha().equals(this.funcionamentoSegundaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoSegunda.child(DiasENUM.FECHA).setValue(funcionamentoSegunda.getFecha());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
            else{
                if (this.funcionamentoSegunda != null && (this.funcionamentoSegundaCloud == null || !this.funcionamentoSegunda.getAbre().equals(this.funcionamentoSegundaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoSegunda.child(DiasENUM.ABRE).setValue(funcionamentoSegunda.getAbre(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoSegunda != null && (this.funcionamentoSegundaCloud == null || !this.funcionamentoSegunda.getFecha().equals(this.funcionamentoSegundaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoSegunda.child(DiasENUM.FECHA).setValue(funcionamentoSegunda.getFecha(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
        }

        private void salvarFuncionamentoTercaNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseFuncionamentoSalaoTerca == null){
                this.firebaseFuncionamentoSalaoTerca = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.TERCA);
            }

            if( completionListener.length == 0 ){
                if (this.funcionamentoTerca != null && (this.funcionamentoTercaCloud == null || !this.funcionamentoTerca.getAbre().equals(this.funcionamentoTercaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoTerca.child(DiasENUM.ABRE).setValue(funcionamentoTerca.getAbre());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoTerca != null && (this.funcionamentoTercaCloud == null || !this.funcionamentoTerca.getFecha().equals(this.funcionamentoTercaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoTerca.child(DiasENUM.FECHA).setValue(funcionamentoTerca.getFecha());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
            else{
                if (this.funcionamentoTerca != null && (this.funcionamentoTercaCloud == null || !this.funcionamentoTerca.getAbre().equals(this.funcionamentoTercaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoTerca.child(DiasENUM.ABRE).setValue(funcionamentoTerca.getAbre(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoTerca != null && (this.funcionamentoTercaCloud == null || !this.funcionamentoTerca.getFecha().equals(this.funcionamentoTercaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoTerca.child(DiasENUM.FECHA).setValue(funcionamentoTerca.getFecha(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
        }

        private void salvarFuncionamentoQuartaNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseFuncionamentoSalaoQuarta == null){
                this.firebaseFuncionamentoSalaoQuarta = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.QUARTA);
            }

            if( completionListener.length == 0 ){
                if (this.funcionamentoQuarta != null && (this.funcionamentoQuartaCloud == null || !this.funcionamentoQuarta.getAbre().equals(this.funcionamentoQuartaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoQuarta.child(DiasENUM.ABRE).setValue(funcionamentoQuarta.getAbre());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoQuarta != null && (this.funcionamentoQuartaCloud == null || !this.funcionamentoQuarta.getFecha().equals(this.funcionamentoQuartaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoQuarta.child(DiasENUM.FECHA).setValue(funcionamentoQuarta.getFecha());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
            else{
                if (this.funcionamentoQuarta != null && (this.funcionamentoQuartaCloud == null || !this.funcionamentoQuarta.getAbre().equals(this.funcionamentoQuartaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoQuarta.child(DiasENUM.ABRE).setValue(funcionamentoQuarta.getAbre(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoQuarta != null && (this.funcionamentoQuartaCloud == null || !this.funcionamentoQuarta.getFecha().equals(this.funcionamentoQuartaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoQuarta.child(DiasENUM.FECHA).setValue(funcionamentoQuarta.getFecha(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
        }

        private void salvarFuncionamentoQuintaNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseFuncionamentoSalaoQuinta == null){
                this.firebaseFuncionamentoSalaoQuinta = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.QUINTA);
            }

            if( completionListener.length == 0 ){
                if (this.funcionamentoQuinta != null && (this.funcionamentoQuintaCloud == null || !this.funcionamentoQuinta.getAbre().equals(this.funcionamentoQuintaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoQuinta.child(DiasENUM.ABRE).setValue(funcionamentoQuinta.getAbre());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoQuinta != null && (this.funcionamentoQuintaCloud == null || !this.funcionamentoQuinta.getFecha().equals(this.funcionamentoQuintaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoQuinta.child(DiasENUM.FECHA).setValue(funcionamentoQuinta.getFecha());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
            else{
                if (this.funcionamentoQuinta != null && (this.funcionamentoQuintaCloud == null || !this.funcionamentoQuinta.getAbre().equals(this.funcionamentoQuintaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoQuinta.child(DiasENUM.ABRE).setValue(funcionamentoQuinta.getAbre(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoQuinta != null && (this.funcionamentoQuintaCloud == null || !this.funcionamentoQuinta.getFecha().equals(this.funcionamentoQuintaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoQuinta.child(DiasENUM.FECHA).setValue(funcionamentoQuinta.getFecha(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
        }

        private void salvarFuncionamentoSextaNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseFuncionamentoSalaoSexta == null){
                this.firebaseFuncionamentoSalaoSexta = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.SEXTA);
            }

            if( completionListener.length == 0 ){
                if (this.funcionamentoSexta != null && (this.funcionamentoSextaCloud == null || !this.funcionamentoSexta.getAbre().equals(this.funcionamentoSextaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoSexta.child(DiasENUM.ABRE).setValue(funcionamentoSexta.getAbre());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoSexta != null && (this.funcionamentoSextaCloud == null || !this.funcionamentoSexta.getFecha().equals(this.funcionamentoSextaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoSexta.child(DiasENUM.FECHA).setValue(funcionamentoSexta.getFecha());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
            else{
                if (this.funcionamentoSexta != null && (this.funcionamentoSextaCloud == null || !this.funcionamentoSexta.getAbre().equals(this.funcionamentoSextaCloud.getAbre()))){
                    firebaseFuncionamentoSalaoSexta.child(DiasENUM.ABRE).setValue(funcionamentoSexta.getAbre(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoSexta != null && (this.funcionamentoSextaCloud == null || !this.funcionamentoSexta.getFecha().equals(this.funcionamentoSextaCloud.getFecha()))){
                    firebaseFuncionamentoSalaoSexta.child(DiasENUM.FECHA).setValue(funcionamentoSexta.getFecha(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
        }

        private void salvarFuncionamentoSabadoNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseFuncionamentoSalaoSabado == null){
                this.firebaseFuncionamentoSalaoSabado = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.SABADO);
            }

            if( completionListener.length == 0 ){
                if (this.funcionamentoSabado != null && (this.funcionamentoSabadoCloud== null || !this.funcionamentoSabado.getAbre().equals(this.funcionamentoSabadoCloud.getAbre()))){
                    firebaseFuncionamentoSalaoSabado.child(DiasENUM.ABRE).setValue(funcionamentoSabado.getAbre());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoSabado != null && (this.funcionamentoSabadoCloud == null || !this.funcionamentoSabado.getFecha().equals(this.funcionamentoSabadoCloud.getFecha()))){
                    firebaseFuncionamentoSalaoSabado.child(DiasENUM.FECHA).setValue(funcionamentoSabado.getFecha());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
            else{
                if (this.funcionamentoSabado != null && (this.funcionamentoSabadoCloud == null || !this.funcionamentoSabado.getAbre().equals(this.funcionamentoSabadoCloud.getAbre()))){
                    firebaseFuncionamentoSalaoSabado.child(DiasENUM.ABRE).setValue(funcionamentoSabado.getAbre(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoSabado != null && (this.funcionamentoSabadoCloud == null || !this.funcionamentoSabado.getFecha().equals(this.funcionamentoSabadoCloud.getFecha()))){
                    firebaseFuncionamentoSalaoSabado.child(DiasENUM.FECHA).setValue(funcionamentoSabado.getFecha(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
        }

        private void salvarFuncionamentoDomingoNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseFuncionamentoSalaoDomingo == null){
                this.firebaseFuncionamentoSalaoDomingo = LibraryClass.getFirebase().child("salões").child(cadastroInicialBD.getCodigoUnico().toString()).child(DatabaseHelper.Funcionamento.TABELA).child(DiasENUM.DOMINGO);
            }

            if( completionListener.length == 0 ){
                if (this.funcionamentoDomingo != null && (this.funcionamentoDomingoCloud == null || !this.funcionamentoDomingo.getAbre().equals(this.funcionamentoDomingoCloud.getAbre()))){
                    firebaseFuncionamentoSalaoDomingo.child(DiasENUM.ABRE).setValue(funcionamentoDomingo.getAbre());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoDomingo != null && (this.funcionamentoDomingoCloud == null || !this.funcionamentoDomingo.getFecha().equals(this.funcionamentoDomingoCloud.getFecha()))){
                    firebaseFuncionamentoSalaoDomingo.child(DiasENUM.FECHA).setValue(funcionamentoDomingo.getFecha());
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
            else{
                if (this.funcionamentoDomingo != null && (this.funcionamentoDomingoCloud == null || !this.funcionamentoDomingo.getAbre().equals(this.funcionamentoDomingoCloud.getAbre()))){
                    firebaseFuncionamentoSalaoDomingo.child(DiasENUM.ABRE).setValue(funcionamentoDomingo.getAbre(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
                if (this.funcionamentoDomingo != null && (this.funcionamentoDomingoCloud == null || !this.funcionamentoDomingo.getFecha().equals(this.funcionamentoDomingoCloud.getFecha()))){
                    firebaseFuncionamentoSalaoDomingo.child(DiasENUM.FECHA).setValue(funcionamentoDomingo.getFecha(), completionListener[0]);
                }else {
                    this.numDadosFuncionamentosSalvos++;
                    this.numRespostasFuncionamentos++;
                }
            }
        }

        private void salvarVersaoFuncionamentoNoFirebase(DatabaseReference.CompletionListener... completionListener){
            if (this.firebaseVersaoFuncionamentoSalao == null){
                this.firebaseVersaoFuncionamentoSalao = LibraryClass.getFirebase().child("users").child(uid).child(DatabaseHelper.Versoes.TABELA).child(DatabaseHelper.Funcionamento.TABELA);
            }
            if( completionListener.length == 0 ){
                if (versaoFuncionamentoBD != null && versaoFuncionamentoBD.getVersao() != 0){
                    firebaseVersaoFuncionamentoSalao.child(DatabaseHelper.Versoes.VERSAO).setValue(versaoFuncionamentoBD.getVersao());
                }else {
                    this.numDadosVersaoSalvos++;
                    this.numRespostasVersao++;
                }
                if (versaoFuncionamentoBD != null && versaoFuncionamentoBD.getDataModificacao() != null && !versaoFuncionamentoBD.getDataModificacao().isEmpty()){
                    firebaseVersaoFuncionamentoSalao.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(versaoFuncionamentoBD.getDataModificacao());
                }else {
                    firebaseVersaoFuncionamentoSalao.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(getDateTime());
                }
            }
            else{
                if (versaoFuncionamentoBD != null && versaoFuncionamentoBD.getVersao() != 0){
                    firebaseVersaoFuncionamentoSalao.child(DatabaseHelper.Versoes.VERSAO).setValue(versaoFuncionamentoBD.getVersao(), completionListener[0]);
                }
                else {
                    this.numDadosVersaoSalvos++;
                    this.numRespostasVersao++;
                }
                if (versaoFuncionamentoBD != null && versaoFuncionamentoBD.getDataModificacao() != null && !versaoFuncionamentoBD.getDataModificacao().isEmpty()){
                    firebaseVersaoFuncionamentoSalao.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(versaoFuncionamentoBD.getDataModificacao(), completionListener[0]);
                }else {
                    firebaseVersaoFuncionamentoSalao.child(DatabaseHelper.Versoes.DATA_MODIFICACAO).setValue(getDateTime(), completionListener[0]);
                }
            }
        }
    }

}

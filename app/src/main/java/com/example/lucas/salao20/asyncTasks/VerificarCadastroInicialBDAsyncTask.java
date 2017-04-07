package com.example.lucas.salao20.asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.salao20.activitys.LoginActivity;
import com.example.lucas.salao20.activitys.SplashScreenActivity;
import com.example.lucas.salao20.dao.CadastroInicialDAO;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.VersaoDAO;
import com.example.lucas.salao20.dao.model.CadastroInicial;
import com.example.lucas.salao20.dao.model.Versao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Lucas on 17/03/2017.
 */

public class VerificarCadastroInicialBDAsyncTask extends AsyncTask<CadastroInicial,Void,Boolean> {
    private Context context;
    private LoginActivity loginActivity;

    //DAO
    private CadastroInicialDAO cadastroInicialDAO;
    private VersaoDAO versaoDAO;

    //CONTROLE
    private boolean cadastroInicialCompleto;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public VerificarCadastroInicialBDAsyncTask(Context context, LoginActivity loginActivity) {
        this.context = context;
        this.loginActivity = loginActivity;
        this.cadastroInicialCompleto = false;
    }

    @Override
    protected Boolean doInBackground(CadastroInicial... params) {
        Log.i("script","VerificarCadastroInicialBDAsyncTask doInBackground");
        if (!isCancelled()){
            if (this.cadastroInicialDAO == null){
                this.cadastroInicialDAO = new CadastroInicialDAO(this.context);
            }
            if (this.versaoDAO == null){
                this.versaoDAO = new VersaoDAO(this.context);
            }
            ArrayList<Versao> versoes = this.versaoDAO.listarVersoes();
            boolean usuarioEncontrado = false;
            for (Versao v : versoes){
                if (v.getUid().equals(params[0].getUid()) && v.getIdentificacaoTabela().equals(DatabaseHelper.CadastroInicial.TABELA)){
                    usuarioEncontrado = true;
                }
            }
            if (!usuarioEncontrado){
                if (this.databaseHelper == null){
                    this.databaseHelper = new DatabaseHelper(context);
                }
                if (this.database == null){
                    this.database = databaseHelper.getWritableDatabase();
                }
                Log.i("script","VerificarCadastroInicialBDAsyncTask usuarioNaoEncontrado");

                //APAGAR BD EXISTENTES
                Cursor cursor;
                int resultDeletar;
                //apagar tabelas do cadastro inicial
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.CadastroInicial.TABELA+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.CadastroInicial.TABELA, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.CadastroInicial.TABELA_CLOUD+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.CadastroInicial.TABELA_CLOUD, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                //apagar tabelas do cabeleireiro
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Cabeleireiro.TABELA+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Cabeleireiro.TABELA, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Cabeleireiro.TABELA_CLOUD+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Cabeleireiro.TABELA_CLOUD, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                //apagar tabelas do funcionamento
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Funcionamento.TABELA+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Funcionamento.TABELA, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Funcionamento.TABELA_CLOUD+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Funcionamento.TABELA_CLOUD, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                //apagar tabelas do servico
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Servico.TABELA+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Servico.TABELA, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Servico.TABELA_CLOUD+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Servico.TABELA_CLOUD, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                //apagar tabelas do versoes
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Versoes.TABELA+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Versoes.TABELA, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }
                cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.Versoes.TABELA_CLOUD+" )", null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    if (cursor.getInt (0) != 0) {
                        do{
                            resultDeletar = database.delete(DatabaseHelper.Versoes.TABELA_CLOUD, "_id IS NOT NULL",null);
                        }while(resultDeletar < 1 && !isCancelled());
                    }
                }

                if (this.databaseHelper != null && !isCancelled()){
                    fecharConexaoBD();
                }

                //salva usuario no banco
                long result = -1;
                params[0].setNivelUsuario(1.0);
                while (!isCancelled() && result == -1){
                    result = this.cadastroInicialDAO.salvarCadastroInicial(params[0]);
                }
                Log.i("script","VerificarCadastroInicialBDAsyncTask salvol cadastro");

                Versao versao = new Versao();
                versao.setUid(params[0].getUid());
                versao.setDataModificacao(getDateTime());
                versao.setVersao(1);
                versao.setIdentificacaoTabela(DatabaseHelper.CadastroInicial.TABELA);
                result = -1;
                while (!isCancelled() && result == -1){
                    result = this.versaoDAO.salvarVersao(versao);
                }
                Log.i("script","VerificarCadastroInicialBDAsyncTask salvou versao");

                if (!isCancelled()){
                    return true;
                }
            }else {
                Log.i("script","VerificarCadastroInicialBDAsyncTask usuarioEncontrado");
                if (this.cadastroInicialDAO.buscarCadastroInicialPorUID(params[0].getUid()).getNivelUsuario() != null && this.cadastroInicialDAO.buscarCadastroInicialPorUID(params[0].getUid()).getNivelUsuario() >= 3.0){
                    this.cadastroInicialCompleto = true;
                }
                return false;
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean novoUsuario) {
        super.onPostExecute(novoUsuario);
        Log.i("script","VerificarCadastroInicialBDAsyncTask onPostExecute");

        if (!isCancelled()){
            if (this.cadastroInicialDAO != null){
                this.cadastroInicialDAO.fechar();
                this.cadastroInicialDAO = null;
            }
            if (this.versaoDAO != null){
                this.versaoDAO.fechar();
                this.versaoDAO = null;
            }
            if (this.databaseHelper != null){
                fecharConexaoBD();
            }
            if (this.cadastroInicialCompleto){
                loginActivity.irHome();
            }else {
                loginActivity.irSplashScreen(novoUsuario);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (this.cadastroInicialDAO != null){
            this.cadastroInicialDAO.fechar();
            this.cadastroInicialDAO = null;
        }
        if (this.versaoDAO != null){
            this.versaoDAO.fechar();
            this.versaoDAO = null;
        }
        if (this.databaseHelper != null){
            fecharConexaoBD();
        }
    }

    //UTILIDADE
    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void fecharConexaoBD(){
        this.databaseHelper.close();
        this.database = null;
        this.databaseHelper = null;
    }

    private  void exibirBancos(){
        if (this.cadastroInicialDAO.listarCadastrosIniciais().size() == 0){
            Log.i("script","bancos null");
        }else{
            Log.i("script","bancos existente ");

        }
    }
}

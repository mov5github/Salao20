package com.example.lucas.salao20.asyncTasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.example.lucas.salao20.activitys.LoginActivity;
import com.example.lucas.salao20.dao.CadastroBasicoDAO;
import com.example.lucas.salao20.dao.CadastroComplementarDAO;
import com.example.lucas.salao20.dao.CadastroComplementarExternoDAO;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.dao.UserDAO;
import com.example.lucas.salao20.dao.VersaoDAO;
import com.example.lucas.salao20.dao.model.CadastroBasico;
import com.example.lucas.salao20.dao.model.CadastroComplementar;
import com.example.lucas.salao20.dao.model.CadastroComplementarExterno;
import com.example.lucas.salao20.dao.model.User;
import com.example.lucas.salao20.dao.model.Versao;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lucas on 11/04/2017.
 */


public class VerificarUserBDAsyncTask extends AsyncTask<User,Void,Boolean> {
    private Context context;
    private LoginActivity loginActivity;

    //DAO
    private UserDAO userDAO;
    private VersaoDAO versaoDAO;
    private CadastroBasicoDAO cadastroBasicoDAO;
    private CadastroComplementarDAO cadastroComplementarDAO;

    //CONTROLE
    private boolean cadastroInicialCompleto;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public VerificarUserBDAsyncTask(Context context, LoginActivity loginActivity) {
        this.context = context;
        this.loginActivity = loginActivity;
        this.cadastroInicialCompleto = false;
    }

    @Override
    protected Boolean doInBackground(User... params) {
        Log.i("script","VerificarUserBDAsyncTask doInBackground");
        if (!isCancelled()) {
            if (this.userDAO == null) {
                this.userDAO = new UserDAO(this.context);
            }
            if (this.versaoDAO == null) {
                this.versaoDAO = new VersaoDAO(this.context);
            }
            if (this.cadastroBasicoDAO == null) {
                this.cadastroBasicoDAO = new CadastroBasicoDAO(this.context);
            }
            if (this.cadastroComplementarDAO == null) {
                this.cadastroComplementarDAO = new CadastroComplementarDAO(this.context);
            }
            User user = this.userDAO.buscarUserPorUID(params[0].getUid());
            if (user != null) {//USER EXISTENTE
                Log.i("script", "VerificarUserBDAsyncTask usuarioEncontradoNoBanco");
                CadastroBasico cadastroBasico = cadastroBasicoDAO.buscarCadastroBasicoPorUID(params[0].getUid());
                if (cadastroBasico != null) {//user com cadastroBasico
                    verificaNivelUsuario(user.getUid());
                }
                return false;

            } else {//NOVO USER
                Log.i("script", "VerificarUserBDAsyncTask usuarioNaoEncontradoNoBanco");
                if (this.databaseHelper == null) {
                    this.databaseHelper = new DatabaseHelper(context);
                }
                if (this.database == null) {
                    this.database = databaseHelper.getWritableDatabase();
                }
                deletarTodosRegistros();
                gerarNovoUser(params[0]);
                return true;
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Boolean novoUsuario) {
        super.onPostExecute(novoUsuario);
        Log.i("script","VerificarUserBDAsyncTask onPostExecute");

        if (!isCancelled()){
            if (this.userDAO != null){
                this.userDAO.fechar();
                this.userDAO = null;
            }
            if (this.versaoDAO != null){
                this.versaoDAO.fechar();
                this.versaoDAO = null;
            }
            if (this.cadastroBasicoDAO != null){
                this.cadastroBasicoDAO.fechar();
                this.cadastroBasicoDAO = null;
            }
            if (this.cadastroComplementarDAO != null){
                this.cadastroComplementarDAO.fechar();
                this.cadastroComplementarDAO = null;
            }
            if (this.databaseHelper != null){
                fecharConexaoBD();
            }
            if (this.cadastroInicialCompleto){
                loginActivity.irHome();
            }else {
               // loginActivity.irSplashScreen(novoUsuario);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (this.userDAO != null){
            this.userDAO.fechar();
            this.userDAO = null;
        }
        if (this.versaoDAO != null){
            this.versaoDAO.fechar();
            this.versaoDAO = null;
        }
        if (this.cadastroBasicoDAO != null){
            this.cadastroBasicoDAO.fechar();
            this.cadastroBasicoDAO = null;
        }
        if (this.cadastroComplementarDAO != null){
            this.cadastroComplementarDAO.fechar();
            this.cadastroComplementarDAO = null;
        }
        if (this.databaseHelper != null){
            fecharConexaoBD();
        }
    }

    //AUXILIARES
    private void deletarTodosRegistros(){
        //APAGAR BD EXISTENTES
        Cursor cursor;
        int resultDeletar;

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
        //apagar tabelas do user
        cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.User.TABELA+" )", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt (0) != 0) {
                do{
                    resultDeletar = database.delete(DatabaseHelper.User.TABELA, "uid IS NOT NULL",null);
                }while(resultDeletar < 1 && !isCancelled());
            }
        }
        cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.User.TABELA_CLOUD+" )", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt (0) != 0) {
                do{
                    resultDeletar = database.delete(DatabaseHelper.User.TABELA_CLOUD, "uid IS NOT NULL",null);
                }while(resultDeletar < 1 && !isCancelled());
            }
        }
        //apagar tabelas do cadastro basico
        cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.CadastroBasico.TABELA+" )", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt (0) != 0) {
                do{
                    resultDeletar = database.delete(DatabaseHelper.CadastroBasico.TABELA, "uid IS NOT NULL",null);
                }while(resultDeletar < 1 && !isCancelled());
            }
        }
        cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.CadastroBasico.TABELA_CLOUD+" )", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt (0) != 0) {
                do{
                    resultDeletar = database.delete(DatabaseHelper.CadastroBasico.TABELA_CLOUD, "uid IS NOT NULL",null);
                }while(resultDeletar < 1 && !isCancelled());
            }
        }
        //apagar tabelas do cadastro complementar
        cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.CadastroComplementar.TABELA+" )", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt (0) != 0) {
                do{
                    resultDeletar = database.delete(DatabaseHelper.CadastroComplementar.TABELA, "uid IS NOT NULL",null);
                }while(resultDeletar < 1 && !isCancelled());
            }
        }
        cursor = this.database.rawQuery("SELECT EXISTS (SELECT 1 FROM "+DatabaseHelper.CadastroComplementar.TABELA_CLOUD+" )", null);
        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getInt (0) != 0) {
                do{
                    resultDeletar = database.delete(DatabaseHelper.CadastroComplementar.TABELA_CLOUD, "uid IS NOT NULL",null);
                }while(resultDeletar < 1 && !isCancelled());
            }
        }

        if (this.databaseHelper != null && !isCancelled()){
            fecharConexaoBD();
        }
    }

    private void verificaNivelUsuario(String uid){
        CadastroBasico cadastroBasico = this.cadastroBasicoDAO.buscarCadastroBasicoPorUID(uid);
        if (cadastroBasico == null || cadastroBasico.getNivelUsuario() == null || cadastroBasico.getNivelUsuario() != 3.0){
            this.cadastroInicialCompleto = false;
        }else {
            this.cadastroInicialCompleto = true;
        }
    }

    private void gerarNovoUser(User user){
        //salva usuario no banco
        long result = -1;
        CadastroBasico cadastroBasico = new CadastroBasico();
        cadastroBasico.setNivelUsuario(1.0);
        cadastroBasico.set_uid(user.getUid());
        while (!isCancelled() && result == -1){
            result = this.cadastroBasicoDAO.salvarCadastroBasico(cadastroBasico);
        }
        Log.i("script","VerificarUserBDAsyncTask salvou cadastro basico");
        result = -1;
        Versao versao = new Versao();
        versao.setDataModificacao(getDateTime());
        versao.setVersao(1);
        versao.setIdentificacaoTabela(DatabaseHelper.CadastroBasico.TABELA);
        while (!isCancelled() && result == -1){
            result = this.versaoDAO.salvarAtualizarVersao(versao);
        }
        Log.i("script","VerificarUserBDAsyncTask salvou versao cadastroBasico");
        result = -1;
        CadastroComplementar cadastroComplementar = new CadastroComplementar();
        cadastroComplementar.setUid(user.getUid());
        while (!isCancelled() && result == -1){
            result = this.cadastroComplementarDAO.salvarCadastroComplementar(cadastroComplementar);
        }
        Log.i("script","VerificarUserBDAsyncTask salvou cadastro complementar");
        result = -1;
        versao = new Versao();
        versao.setDataModificacao(getDateTime());
        versao.setVersao(1);
        versao.setIdentificacaoTabela(DatabaseHelper.CadastroComplementar.TABELA);
        while (!isCancelled() && result == -1){
            result = this.versaoDAO.salvarAtualizarVersao(versao);
        }
        Log.i("script","VerificarUserBDAsyncTask salvou versao cadastroComplementar");
        result = -1;
        while (!isCancelled() && result == -1){
            result = this.userDAO.salvarUser(user);
        }
        Log.i("script","VerificarUserBDAsyncTask salvou user");
        result = -1;
        versao = new Versao();
        versao.setDataModificacao(getDateTime());
        versao.setVersao(1);
        versao.setIdentificacaoTabela(DatabaseHelper.User.TABELA);
        while (!isCancelled() && result == -1){
            result = this.versaoDAO.salvarAtualizarVersao(versao);
        }
        Log.i("script","VerificarUserBDAsyncTask salvou versao user");
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

    /*private  void exibirBancos(){
        if (this.cadastroBasicoDAO.listarCadastrosIniciais().size() == 0){
            Log.i("script","bancos null");
        }else{
            Log.i("script","bancos existente ");

        }
    }*/
}


package com.example.lucas.salao20.asyncTasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.example.lucas.salao20.activitys.LoginActivity;
import com.example.lucas.salao20.activitys.SplashScreenActivity;
import com.example.lucas.salao20.dao.CadastroInicialDAO;
import com.example.lucas.salao20.dao.model.CadastroInicial;

/**
 * Created by Lucas on 17/03/2017.
 */

public class VerificarCadastroInicialBDAsyncTask extends AsyncTask<CadastroInicial,Void,Boolean> {
    private CadastroInicialDAO cadastroInicialDAO;
    private Context context;
    private LoginActivity loginActivity;

    //CONTROLE
    private boolean cadastroInicialCompleto;

    public VerificarCadastroInicialBDAsyncTask(Context context, LoginActivity loginActivity) {
        this.context = context;
        this.loginActivity = loginActivity;
        this.cadastroInicialCompleto = false;
    }

    @Override
    protected Boolean doInBackground(CadastroInicial... params) {
        if (!isCancelled()){
            if (this.cadastroInicialDAO == null){
                this.cadastroInicialDAO = new CadastroInicialDAO(this.context);
            }
            if (this.cadastroInicialDAO.buscarCadastroInicialPorUID(params[0].getUid()) == null){
                long result = -1;
                params[0].setNivelUsuario(1.0);
                while (!isCancelled() && result == -1){
                    result = this.cadastroInicialDAO.salvarCadastroInicial(params[0]);
                }
                if (!isCancelled()){
                    return true;
                }
            }else {
                if (this.cadastroInicialDAO.buscarCadastroInicialPorUID(params[0].getUid()).getNivelUsuario() != null && this.cadastroInicialDAO.buscarCadastroInicialPorUID(params[0].getUid()).getNivelUsuario() == 3.0){
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
        if (!isCancelled()){
            if (this.cadastroInicialDAO != null){
                this.cadastroInicialDAO.fechar();
                this.cadastroInicialDAO = null;
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
    }
}

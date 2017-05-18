package com.example.lucas.salao20.geral;


import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.google.firebase.database.Exclude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FuncionamentoSalao {
    private HashMap<String,Funcionamento> funcionamentoDoSalao;


    public FuncionamentoSalao() {
        this.funcionamentoDoSalao = null;
    }

    public void receberDoFirebase(JSONObject jason){
        HashMap<String,Funcionamento> funcionamentos = new HashMap<String,Funcionamento>();
        try {
            if (!jason.isNull(DiasENUM.SEGUNDA)){
                funcionamentos.put(DiasENUM.SEGUNDA,new Funcionamento(DiasENUM.SEGUNDA, jason.getJSONObject(DiasENUM.SEGUNDA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SEGUNDA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.TERCA)){
                funcionamentos.put(DiasENUM.TERCA,new Funcionamento(DiasENUM.TERCA, jason.getJSONObject(DiasENUM.TERCA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.TERCA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.QUARTA)){
                funcionamentos.put(DiasENUM.QUARTA,new Funcionamento(DiasENUM.QUARTA, jason.getJSONObject(DiasENUM.QUARTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.QUARTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.QUINTA)){
                funcionamentos.put(DiasENUM.QUINTA,new Funcionamento(DiasENUM.QUINTA, jason.getJSONObject(DiasENUM.QUINTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.QUINTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.SEXTA)){
                funcionamentos.put(DiasENUM.SEXTA,new Funcionamento(DiasENUM.SEXTA, jason.getJSONObject(DiasENUM.SEXTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SEXTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.SABADO)){
                funcionamentos.put(DiasENUM.SABADO,new Funcionamento(DiasENUM.SABADO, jason.getJSONObject(DiasENUM.SABADO).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SABADO).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.DOMINGO)){
                funcionamentos.put(DiasENUM.DOMINGO,new Funcionamento(DiasENUM.DOMINGO, jason.getJSONObject(DiasENUM.DOMINGO).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.DOMINGO).getString(DiasENUM.FECHA)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.funcionamentoDoSalao = funcionamentos;
    }

    public void receberDoFirebaseAtualizar(JSONObject jason){
        HashMap<String,Funcionamento> funcionamentos = new HashMap<String,Funcionamento>();
        try {
            if (!jason.isNull(DiasENUM.SEGUNDA)){
                funcionamentos.put(DiasENUM.SEGUNDA,new Funcionamento(DiasENUM.SEGUNDA, jason.getJSONObject(DiasENUM.SEGUNDA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SEGUNDA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.TERCA)){
                funcionamentos.put(DiasENUM.TERCA,new Funcionamento(DiasENUM.TERCA, jason.getJSONObject(DiasENUM.TERCA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.TERCA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.QUARTA)){
                funcionamentos.put(DiasENUM.QUARTA,new Funcionamento(DiasENUM.QUARTA, jason.getJSONObject(DiasENUM.QUARTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.QUARTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.QUINTA)){
                funcionamentos.put(DiasENUM.QUINTA,new Funcionamento(DiasENUM.QUINTA, jason.getJSONObject(DiasENUM.QUINTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.QUINTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.SEXTA)){
                funcionamentos.put(DiasENUM.SEXTA,new Funcionamento(DiasENUM.SEXTA, jason.getJSONObject(DiasENUM.SEXTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SEXTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.SABADO)){
                funcionamentos.put(DiasENUM.SABADO,new Funcionamento(DiasENUM.SABADO, jason.getJSONObject(DiasENUM.SABADO).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SABADO).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.DOMINGO)){
                funcionamentos.put(DiasENUM.DOMINGO,new Funcionamento(DiasENUM.DOMINGO, jason.getJSONObject(DiasENUM.DOMINGO).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.DOMINGO).getString(DiasENUM.FECHA)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.funcionamentoDoSalao = funcionamentos;
    }

    public void receberDoFirebaseRemover(JSONObject jason){
        if (!jason.isNull(DiasENUM.SEGUNDA)){
            this.funcionamentoDoSalao.remove(DiasENUM.SEGUNDA);
        }
        if (!jason.isNull(DiasENUM.TERCA)){
            this.funcionamentoDoSalao.remove(DiasENUM.TERCA);
        }
        if (!jason.isNull(DiasENUM.QUARTA)){
            this.funcionamentoDoSalao.remove(DiasENUM.QUARTA);
        }
        if (!jason.isNull(DiasENUM.QUINTA)){
            this.funcionamentoDoSalao.remove(DiasENUM.QUINTA);
        }
        if (!jason.isNull(DiasENUM.SEXTA)){
            this.funcionamentoDoSalao.remove(DiasENUM.SEXTA);
        }
        if (!jason.isNull(DiasENUM.SABADO)){
            this.funcionamentoDoSalao.remove(DiasENUM.SABADO);
        }
        if (!jason.isNull(DiasENUM.DOMINGO)){
            this.funcionamentoDoSalao.remove(DiasENUM.DOMINGO);
        }
    }

    //GETTERS SETTERS
    public HashMap<String, Funcionamento> getFuncionamentoDoSalao() {
        return funcionamentoDoSalao;
    }
    public void setFuncionamentoDoSalao(HashMap<String, Funcionamento> funcionamentoDoSalao) {
        this.funcionamentoDoSalao = funcionamentoDoSalao;
    }
}

package com.example.lucas.salao20.geral;


import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.geral.geral.Funcionamento;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FuncionamentoSalao {
    private ArrayList<Funcionamento> funcionamentoDoSalao;

    public FuncionamentoSalao() {
        this.funcionamentoDoSalao = null;
    }

    public void receberDoFirebase(JSONObject jason){
        ArrayList<Funcionamento> funcionamentos = new ArrayList<Funcionamento>();
        try {
            if (!jason.isNull(DiasENUM.SEGUNDA)){
                funcionamentos.add(new Funcionamento(DiasENUM.SEGUNDA, jason.getJSONObject(DiasENUM.SEGUNDA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SEGUNDA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.TERCA)){
                funcionamentos.add(new Funcionamento(DiasENUM.TERCA, jason.getJSONObject(DiasENUM.TERCA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.TERCA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.QUARTA)){
                funcionamentos.add(new Funcionamento(DiasENUM.QUARTA, jason.getJSONObject(DiasENUM.QUARTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.QUARTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.QUINTA)){
                funcionamentos.add(new Funcionamento(DiasENUM.QUINTA, jason.getJSONObject(DiasENUM.QUINTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.QUINTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.SEXTA)){
                funcionamentos.add(new Funcionamento(DiasENUM.SEXTA, jason.getJSONObject(DiasENUM.SEXTA).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SEXTA).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.SABADO)){
                funcionamentos.add(new Funcionamento(DiasENUM.SABADO, jason.getJSONObject(DiasENUM.SABADO).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.SABADO).getString(DiasENUM.FECHA)));
            }
            if (!jason.isNull(DiasENUM.DOMINGO)){
                funcionamentos.add(new Funcionamento(DiasENUM.DOMINGO, jason.getJSONObject(DiasENUM.DOMINGO).getString(DiasENUM.ABRE), jason.getJSONObject(DiasENUM.DOMINGO).getString(DiasENUM.FECHA)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.funcionamentoDoSalao = funcionamentos;
    }

    //GETTERS SETTERS
    public void setFuncionamentoDoSalao(ArrayList<Funcionamento> funcionamentoDoSalao) {
        this.funcionamentoDoSalao = funcionamentoDoSalao;
    }
    public ArrayList<Funcionamento> getFuncionamentoDoSalao() {
        return funcionamentoDoSalao;
    }
}

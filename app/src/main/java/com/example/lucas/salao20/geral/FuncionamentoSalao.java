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
    }


    public void addFuncionamento(Funcionamento funcionamento){
        if (this.funcionamentoDoSalao == null){
            this.funcionamentoDoSalao = new HashMap<String,Funcionamento>();
        }
        if (this.funcionamentoDoSalao.containsKey(funcionamento.getDia())){
            this.funcionamentoDoSalao.remove(funcionamento.getDia());
        }
        this.funcionamentoDoSalao.put(funcionamento.getDia(),funcionamento);
    }

    public void removerFuncionamento(String dia){
        if (this.funcionamentoDoSalao != null){
            if (this.funcionamentoDoSalao.containsKey(dia)){
                this.funcionamentoDoSalao.remove(dia);
            }
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

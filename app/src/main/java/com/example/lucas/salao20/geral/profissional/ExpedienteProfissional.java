package com.example.lucas.salao20.geral.profissional;

import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.google.firebase.database.Exclude;

import java.util.HashMap;

/**
 * Created by Lucas on 19/06/2017.
 */

public class ExpedienteProfissional {
    private HashMap<String,Funcionamento> funcionamentos;

    public ExpedienteProfissional() {
    }

    public void addFuncionamento(Funcionamento funcionamento){
        if (funcionamentos == null){
            funcionamentos = new  HashMap<String,Funcionamento>();
        }
        if (funcionamentos.containsKey(funcionamento.getDia())){
            funcionamentos.remove(funcionamento.getDia());
        }
        funcionamentos.put(funcionamento.getDia(),funcionamento);
    }

    public void removerFuncionamento(String dia){
        if (funcionamentos.containsKey(dia)){
            funcionamentos.remove(dia);
        }
    }

    //GETTERS SETTERS
    public HashMap<String, Funcionamento> getFuncionamentos() {
        return funcionamentos;
    }
    public void setFuncionamentos(HashMap<String, Funcionamento> funcionamentos) {
        this.funcionamentos = funcionamentos;
    }
}

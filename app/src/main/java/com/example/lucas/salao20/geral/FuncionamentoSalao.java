package com.example.lucas.salao20.geral;

import com.example.lucas.salao20.dao.model.Funcionamento;
import com.example.lucas.salao20.enumeradores.DiasENUM;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FuncionamentoSalao {
    private int versao;
    private ArrayList<Funcionamento> funcionamentoDoSalao;

    public FuncionamentoSalao() {
        this.funcionamentoDoSalao = new ArrayList<Funcionamento>();
    }

    public void addFuncionamento(Funcionamento funcionamento){
        this.funcionamentoDoSalao.add(funcionamento);
    }

    //GETTERS SETTERS
    public void setFuncionamentoDoSalao(ArrayList<Funcionamento> funcionamentoDoSalao) {
        this.funcionamentoDoSalao = funcionamentoDoSalao;
    }
    public ArrayList<Funcionamento> getFuncionamentoDoSalao() {
        return funcionamentoDoSalao;
    }

    public int getVersao() {
        return versao;
    }
    public void setVersao(int versao) {
        this.versao = versao;
    }

}

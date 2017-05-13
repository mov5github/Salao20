package com.example.lucas.salao20.geral.geral;

import java.util.ArrayList;

/**
 * Created by Lucas on 09/05/2017.
 */

public class Profissional {
    private String dataInsercao;
    private String uIDSalao;
    private String uIDCabeleireiro;
    private ArrayList<Servico> servicos;
    private ArrayList<Funcionamento> expediente;
    private String idProfissional;

    public Profissional() {
    }


    //GETTERS SETTERS
    public String getDataInsercao() {
        return dataInsercao;
    }
    public void setDataInsercao(String dataInsercao) {
        this.dataInsercao = dataInsercao;
    }

    public String getuIDSalao() {
        return uIDSalao;
    }
    public void setuIDSalao(String uIDSalao) {
        this.uIDSalao = uIDSalao;
    }

    public String getuIDCabeleireiro() {
        return uIDCabeleireiro;
    }
    public void setuIDCabeleireiro(String uIDCabeleireiro) {
        this.uIDCabeleireiro = uIDCabeleireiro;
    }

    public ArrayList<Servico> getServicos() {
        return servicos;
    }
    public void setServicos(ArrayList<Servico> servicos) {
        this.servicos = servicos;
    }

    public ArrayList<Funcionamento> getExpediente() {
        return expediente;
    }
    public void setExpediente(ArrayList<Funcionamento> expediente) {
        this.expediente = expediente;
    }

    public String getIdProfissional() {
        return idProfissional;
    }
    public void setIdProfissional(String idProfissional) {
        this.idProfissional = idProfissional;
    }
}

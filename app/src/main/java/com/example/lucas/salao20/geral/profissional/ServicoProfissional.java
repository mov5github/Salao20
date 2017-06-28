package com.example.lucas.salao20.geral.profissional;

import com.example.lucas.salao20.geral.geral.Servico;
import com.google.firebase.database.Exclude;

/**
 * Created by Lucas on 19/06/2017.
 */

public class ServicoProfissional {
    private Servico servico;
    private String idServico;
    private int duracao;

    //ENUM
    @Exclude
    private static final String DURACAO = "duração";
    @Exclude
    private static final String ID_AUX_SERVICO = "idAuxServiço";

    public ServicoProfissional() {
    }

    //GETTERS SETTERS
    public Servico getServico() {
        return servico;
    }
    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public String getIdServico() {
        return idServico;
    }
    public void setIdServico(String idServico) {
        this.idServico = idServico;
    }

    public int getDuracao() {
        return duracao;
    }
    public void setDuracao(int duracao) {
        this.duracao = duracao;
    }

    public static String getDURACAO() {
        return DURACAO;
    }

    public static String getIdAuxServico() {
        return ID_AUX_SERVICO;
    }
}

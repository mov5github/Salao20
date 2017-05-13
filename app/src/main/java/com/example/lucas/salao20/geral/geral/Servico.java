package com.example.lucas.salao20.geral.geral;

import com.google.firebase.database.ServerValue;

import java.security.Timestamp;

/**
 * Created by Lucas on 21/03/2017.
 */

public class Servico {
    private String nome;
    private Integer icone;
    private Integer duracao;
    private Double preco;
    private String descricao;
    private String dataInsercao;
    private String idServico;


    public Servico() {
    }

    public Servico(String nome, Integer icone, Integer duracao, Double preco, String descricao) {
        this.nome = nome;
        this.icone = icone;
        this.duracao = duracao;
        this.preco = preco;
        this.descricao = descricao;
    }

    //GETTERS AND SETTERS
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }
    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public Integer getDuracao() {
        return duracao;
    }
    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public Integer getIcone() {
        return icone;
    }
    public void setIcone(Integer icone) {
        this.icone = icone;
    }

    public String getDataInsercao() {
        return dataInsercao;
    }
    public void setDataInsercao(String dataInsercao) {
        this.dataInsercao = dataInsercao;
    }

    public String getIdServico() {
        return idServico;
    }
    public void setIdServico(String idServico) {
        this.idServico = idServico;
    }
}

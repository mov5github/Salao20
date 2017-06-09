package com.example.lucas.salao20.geral.geral;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.security.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 21/03/2017.
 */

public class Servico {
    private String nome;
    private Integer icone;
    private Integer duracao;
    private Double preco;
    private String descricao;
    private Long dataInsercao;
    private String idServico;

    //ENUM
    @Exclude
    private static final String NOME = "nome";
    @Exclude
    private static final String ICONE = "icone";
    @Exclude
    private static final String DURACAO = "duração";
    @Exclude
    private static final String PRECO = "preço";
    @Exclude
    private static final String DESCRICAO = "descrição";
    @Exclude
    private static final String DATA_DE_INSERCAO = "dataDeInserção";


    public Servico() {
    }

    public Servico(String nome, Integer icone, Integer duracao, Double preco, String descricao) {
        this.nome = nome;
        this.icone = icone;
        this.duracao = duracao;
        this.preco = preco;
        this.descricao = descricao;
    }

    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        if (this.nome != null && !this.nome.isEmpty()){
            result.put(NOME,this.nome);
        }
        if (this.icone != null){
            result.put(ICONE,this.icone);
        }
        if (this.duracao != null){
            result.put(DURACAO,this.duracao);
        }
        if (this.preco != null){
            result.put(PRECO,this.preco);
        }
        if (this.descricao != null && !this.descricao.isEmpty()){
            result.put(DESCRICAO,this.descricao);
        }
        if (this.dataInsercao != null){
            result.put(DATA_DE_INSERCAO,this.dataInsercao);
        }
        return result;
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

    public Long getDataInsercao() {
        return dataInsercao;
    }
    public void setDataInsercao(Long dataInsercao) {
        this.dataInsercao = dataInsercao;
    }

    public String getIdServico() {
        return idServico;
    }
    public void setIdServico(String idServico) {
        this.idServico = idServico;
    }

    public static String getICONE() {
        return ICONE;
    }

    public static String getDURACAO() {
        return DURACAO;
    }

    public static String getPRECO() {
        return PRECO;
    }

    public static String getDESCRICAO() {
        return DESCRICAO;
    }

    public static String getDataDeInsercao() {
        return DATA_DE_INSERCAO;
    }

    public static String getNOME() {
        return NOME;
    }
}

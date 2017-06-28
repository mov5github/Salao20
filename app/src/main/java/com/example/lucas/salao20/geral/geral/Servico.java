package com.example.lucas.salao20.geral.geral;

import android.util.Log;

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

    public static boolean verificarServicosSaoIguais(Servico servico1, Servico servico2){
        if (servico1 != null && servico2 != null){
            //Verifica se id sao iguais
            if (servico1.getIdServico() != null && servico2.getIdServico() != null){
                if (!servico1.getIdServico().equals(servico2.getIdServico())){
                    Log.i("testeteste","id !=");
                    return false;
                }
            }else if (!(servico1.getIdServico() == null && servico2.getIdServico() == null)){
                Log.i("testeteste","null id !=");
                return false;
            }
            //Verifica se nomes sao iguais
            if (servico1.getNome() != null && servico2.getNome() != null){
                if (!servico1.getNome().equals(servico2.getNome())){
                    Log.i("testeteste","nome !=");
                    return false;
                }
            }else if (!(servico1.getNome() == null && servico2.getNome() == null)){
                Log.i("testeteste","null nome !=");
                return false;
            }
            //Verifica se icone sao iguais
            if (servico1.getIcone() != null && servico2.getIcone() != null){
                if (!servico1.getIcone().equals(servico2.getIcone())){
                    Log.i("testeteste","icone !=");
                    return false;
                }
            }else if (!(servico1.getIcone() == null && servico2.getIcone() == null)){
                Log.i("testeteste","null icone !=");
                return false;
            }
            //Verifica se duracao sao iguais
            if (servico1.getDuracao() != null && servico2.getDuracao() != null){
                if (!servico1.getDuracao().equals(servico2.getDuracao())){
                    Log.i("testeteste","duracao !=");
                    return false;
                }
            }else if (!(servico1.getDuracao() == null && servico2.getDuracao() == null)){
                Log.i("testeteste","null duracao !=");
                return false;
            }
            //Verifica se preço sao iguais
            if (servico1.getPreco() != null && servico2.getPreco() != null){
                if (!servico1.getPreco().equals(servico2.getPreco())){
                    Log.i("testeteste","preco !=");
                    return false;
                }
            }else if (!(servico1.getPreco() == null && servico2.getPreco() == null)){
                Log.i("testeteste","null preco !=");
                return false;
            }
            //Verifica se descriçao sao iguais
            if (servico1.getDescricao() != null && servico2.getDescricao() != null){
                if (!servico1.getDescricao().equals(servico2.getDescricao())){
                    Log.i("testeteste","desc !=");
                    return false;
                }
            }else if (!(servico1.getDescricao() == null && servico2.getDescricao() == null)){
                Log.i("testeteste","null desc !=");
                return false;
            }
            //Verifica se data sao iguais
            if (servico1.getDataInsercao() != null && servico2.getDataInsercao() != null){
                if (!servico1.getDataInsercao().equals(servico2.getDataInsercao())){
                    Log.i("testeteste","data !=");
                    return false;
                }
            }else if (!(servico1.getDataInsercao() == null && servico2.getDataInsercao() == null)){
                Log.i("testeteste","null data !=");
                return false;
            }
            return true;
        }else{
            Log.i("testeteste","null");
            return servico1 == null && servico2 == null;
        }
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

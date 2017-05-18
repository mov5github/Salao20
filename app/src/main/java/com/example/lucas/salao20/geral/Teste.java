package com.example.lucas.salao20.geral;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 15/05/2017.
 */

@IgnoreExtraProperties
public class Teste {
    private String nome;
    private Integer idade;
    private Map<String,Object> lista;

    public Teste() {
    }

    public Teste(String nome, Integer idade, Map<String,Object> lista) {
        this.nome = nome;
        this.idade = idade;
        this.lista = lista;
    }

    public String toString(){
        String string = "";

        if (this.nome != null && !this.nome.isEmpty()){
            string = string + "nome : " + this.nome + ";\n";
        }
        if (this.idade != null){
            string = string + "idade : " + this.idade + ";\n";
        }
        if (this.lista != null && this.lista.size() != 0){
            string = string + "lista : { \n";
            for (String key : this.lista.keySet()){
                string = string + key + " : " + this.lista.get(key) + ";\n";
            }
            string = string + "}";
        }

        return string;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }
    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Map<String, Object> getLista() {
        return lista;
    }
    public void setLista(Map<String, Object> lista) {
        this.lista = lista;
    }
}

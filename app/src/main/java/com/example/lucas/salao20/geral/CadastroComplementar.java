package com.example.lucas.salao20.geral;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 29/05/2017.
 */

public class CadastroComplementar {
    private String nome;

    //ENUM
    @Exclude
    private static final String CADASTRO_COMPLEMENTAR = "cadastroComplementar";
    @Exclude
    private static final String NOME = "nome";

    public CadastroComplementar() {
    }

    //AUXILIARES
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        if (this.nome != null){
            result.put(NOME, nome);
        }
        return result;
    }

    //GETTERS SETTERS
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public static String getCADASTRO_COMPLEMENTAR() {
        return CADASTRO_COMPLEMENTAR;
    }

    public static String getNOME() {
        return NOME;
    }
}

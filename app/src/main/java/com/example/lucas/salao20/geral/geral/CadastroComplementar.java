package com.example.lucas.salao20.geral.geral;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 29/05/2017.
 */

public class CadastroComplementar {
    private String nome;
    private int foto;

    //ENUM
    @Exclude
    private static final String CADASTRO_COMPLEMENTAR = "cadastroComplementar";
    @Exclude
    private static final String NOME = "nome";
    @Exclude
    private static final String FOTO = "foto";

    public CadastroComplementar() {
    }

    //AUXILIARES
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        if (this.nome != null){
            result.put(NOME, nome);
        }
        if (this.foto != 0){
            result.put(FOTO, foto);
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

    public int getFoto() {
        return foto;
    }
    public void setFoto(int foto) {
        this.foto = foto;
    }

    public static String getCADASTRO_COMPLEMENTAR() {
        return CADASTRO_COMPLEMENTAR;
    }

    public static String getNOME() {
        return NOME;
    }
}

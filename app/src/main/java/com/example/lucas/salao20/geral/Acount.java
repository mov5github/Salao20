package com.example.lucas.salao20.geral;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 12/05/2017.
 */

@IgnoreExtraProperties
public class Acount {
    private String email;
    private String senha;

    //ENEUM
    @Exclude
    private static final String ACOUNT = "acount";
    @Exclude
    private static final String EMAIL = "email";
    @Exclude
    private static final String SENHA = "senha";
    @Exclude
    private static final String DATA_CRIACAO = "dataCriação";


    public Acount() {
    }

    //AUXILIARES
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put(EMAIL, email);
        result.put(SENHA, senha);
        result.put(DATA_CRIACAO, ServerValue.TIMESTAMP);
        return result;
    }


    //GETTERS SETTERS
    @Exclude
    public String getEmail() {
        return email;
    }
    @Exclude
    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }
    @Exclude
    public void setSenha(String senha) {
        this.senha = senha;
    }

    @Exclude
    public static String getACOUNT() {
        return ACOUNT;
    }

    @Exclude
    public static String getEMAIL() {
        return EMAIL;
    }

    @Exclude
    public static String getSENHA() {
        return SENHA;
    }

    @Exclude
    public static String getDATA_CRIACAO() {
        return DATA_CRIACAO;
    }
}

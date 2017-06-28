package com.example.lucas.salao20.geral.salao;

import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;
import com.google.firebase.database.Exclude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Lucas on 09/05/2017.
 */

public class ProfissionaisSalao {
    private HashMap<String,Profissional> profissionais;

    //ENUM
    @Exclude
    private static final String PROFISSIONAIS = "profissionais";



    public ProfissionaisSalao() {
    }


    public void addProfissional(Profissional profissional){
        if (this.profissionais == null){
            this.profissionais = new HashMap<String, Profissional>();
        }
        if (profissionais.containsKey(profissional.getIdProfissional())){
            this.profissionais.remove(profissional.getIdProfissional());
        }
        this.profissionais.put(profissional.getIdProfissional(),profissional);
    }

    public void removerProfissional(String idProfissional){
        if (this.profissionais != null && this.profissionais.containsKey(idProfissional)){
            this.profissionais.remove(idProfissional);
        }
    }

    //GETTERS SETTERS
    public HashMap<String, Profissional> getProfissionais() {
        return profissionais;
    }
    public void setProfissionais(HashMap<String, Profissional> profissionais) {
        this.profissionais = profissionais;
    }

}

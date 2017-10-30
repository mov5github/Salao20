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
        if (profissionais.containsKey(profissional.getMetadataUidProfissional())){
            this.profissionais.remove(profissional.getMetadataUidProfissional());
        }
        this.profissionais.put(profissional.getMetadataUidProfissional(),profissional);
    }

    public void removerProfissional(String metadataUidProfissional){
        if (this.profissionais != null && this.profissionais.containsKey(metadataUidProfissional)){
            this.profissionais.remove(metadataUidProfissional);
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

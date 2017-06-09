package com.example.lucas.salao20.geral.salao;

import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;
import com.google.firebase.database.Exclude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Lucas on 09/05/2017.
 */

public class ProfissionaisSalao {
    private ArrayList<Profissional> profissionais;


    public ProfissionaisSalao() {
    }

    //GETTERS SETTERS
    public ArrayList<Profissional> getProfissionais() {
        return profissionais;
    }
    public void setProfissionais(ArrayList<Profissional> profissionais) {
        this.profissionais = profissionais;
    }
}

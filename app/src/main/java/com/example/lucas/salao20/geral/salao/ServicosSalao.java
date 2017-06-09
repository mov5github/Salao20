package com.example.lucas.salao20.geral.salao;

import com.example.lucas.salao20.geral.geral.Servico;
import com.google.firebase.database.Exclude;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Lucas on 08/05/2017.
 */

public class ServicosSalao {
    private HashMap<String,Servico> servicosSalao;



    public ServicosSalao() {
    }

    public void addServico(Servico servico){
        if (this.servicosSalao == null){
            this.servicosSalao = new HashMap<String, Servico>();
        }
        if (servicosSalao.containsKey(servico.getIdServico())){
            this.servicosSalao.remove(servico.getIdServico());
        }
        this.servicosSalao.put(servico.getIdServico(),servico);
    }

    public void removerServico(String idServico){
        if (this.servicosSalao != null && this.servicosSalao.containsKey(idServico)){
            this.servicosSalao.remove(idServico);
        }
    }

    //GETTERS
    public HashMap<String, Servico> getServicosSalao() {
        return servicosSalao;
    }
    public void setServicosSalao(HashMap<String, Servico> servicosSalao) {
        this.servicosSalao = servicosSalao;
    }

}

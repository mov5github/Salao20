package com.example.lucas.salao20.geral;

import com.example.lucas.salao20.geral.geral.Servico;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Lucas on 08/05/2017.
 */

public class ServicosSalao {
    private ArrayList<Servico> servicosSalao;


    public ServicosSalao() {
        this.servicosSalao = null;
    }

    public void receberDoFirebase(JSONObject jason){
        ArrayList<Servico> servicos = new ArrayList<Servico>();
        for (Iterator it = jason.keys(); it.hasNext(); ) {
            Servico servico = null;
            try {
                JSONObject servicoJason = jason.getJSONObject((String) it.next());
                if (!servicoJason.isNull("idServico")){
                    if (servico == null){
                        servico = new Servico();
                    }
                    servico.setIdServico(servicoJason.getString("idServico"));
                }
                if (!servicoJason.isNull("nome")){
                    if (servico == null){
                        servico = new Servico();
                    }
                    servico.setNome(servicoJason.getString("nome"));
                }
                if (!servicoJason.isNull("descricao")){
                    if (servico == null){
                        servico = new Servico();
                    }
                    servico.setDescricao(servicoJason.getString("descricao"));
                }
                if (!servicoJason.isNull("preco")){
                    if (servico == null){
                        servico = new Servico();
                    }
                    servico.setPreco(servicoJason.getDouble("preco"));
                }
                if (!servicoJason.isNull("dataInsercao")){
                    if (servico == null){
                        servico = new Servico();
                    }
                    servico.setDataInsercao(servicoJason.getString("dataInsercao"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (servico != null){
                servicos.add(servico);
            }
        }
        this.servicosSalao = servicos;
    }

    //GETTERS
    public void setServicosSalao(ArrayList<Servico> servicosSalao) {
        this.servicosSalao = servicosSalao;
    }
}

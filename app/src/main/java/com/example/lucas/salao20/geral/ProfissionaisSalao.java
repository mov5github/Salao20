package com.example.lucas.salao20.geral;

import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;

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

    public void receberDoFirebase(JSONObject jason){
        ArrayList<Profissional> profissionais = new ArrayList<Profissional>();
        for (Iterator it = jason.keys(); it.hasNext(); ) {
            Profissional profissional = null;
            try {
                JSONObject profissionalJason = jason.getJSONObject((String) it.next());
                if (!profissionalJason.isNull("idProfissional")){
                    if (profissional == null){
                        profissional = new Profissional();
                    }
                    profissional.setIdProfissional(profissionalJason.getString("idProfissional"));
                }
                if (!profissionalJason.isNull("dataInsercao")){
                    if (profissional == null){
                        profissional = new Profissional();
                    }
                    profissional.setDataInsercao(profissionalJason.getString("dataInsercao"));
                }
                if (!profissionalJason.isNull("UIDSalao")){
                    if (profissional == null){
                        profissional = new Profissional();
                    }
                    profissional.setuIDSalao(profissionalJason.getString("UIDSalao"));
                }
                if (!profissionalJason.isNull("UIDCabeleireiro")){
                    if (profissional == null){
                        profissional = new Profissional();
                    }
                    profissional.setuIDCabeleireiro(profissionalJason.getString("UIDCabeleireiro"));
                }
                if (!profissionalJason.isNull("servicos")){
                    if (profissional == null){
                        profissional = new Profissional();
                    }
                    JSONObject servicosJason = profissionalJason.getJSONObject("servicos");
                    ArrayList<Servico> servicos = null;
                    for (Iterator itAux = servicosJason.keys(); itAux.hasNext(); ) {
                        if (servicos == null){
                            servicos = new ArrayList<Servico>();
                        }
                        Servico servico = null;
                        if (servicosJason.getJSONObject((String)itAux.next()).isNull("idServico")){
                            if (servico == null){
                                servico = new Servico();
                            }
                            servico.setIdServico(servicosJason.getJSONObject((String)itAux.next()).getString("idServico"));
                        }
                        if (servicosJason.getJSONObject((String)itAux.next()).isNull("nome")){
                            if (servico == null){
                                servico = new Servico();
                            }
                            servico.setNome(servicosJason.getJSONObject((String)itAux.next()).getString("nome"));
                        }
                        if (servicosJason.getJSONObject((String)itAux.next()).isNull("descricao")){
                            if (servico == null){
                                servico = new Servico();
                            }
                            servico.setDescricao(servicosJason.getJSONObject((String)itAux.next()).getString("descricao"));
                        }
                        if (servicosJason.getJSONObject((String)itAux.next()).isNull("preco")){
                            if (servico == null){
                                servico = new Servico();
                            }
                            servico.setPreco(servicosJason.getJSONObject((String)itAux.next()).getDouble("preco"));
                        }
                        if (servicosJason.getJSONObject((String)itAux.next()).isNull("dataInsercao")){
                            if (servico == null){
                                servico = new Servico();
                            }
                            servico.setDataInsercao(servicosJason.getJSONObject((String)itAux.next()).getString("dataInsercao"));
                        }
                        if (servico != null){
                            servicos.add(servico);
                        }
                    }
                    if (servicos != null){
                        profissional.setServicos(servicos);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (profissional != null){
                profissionais.add(profissional);
            }
        }
        this.profissionais = profissionais;
    }

    //GETTERS SETTERS
    public ArrayList<Profissional> getProfissionais() {
        return profissionais;
    }
    public void setProfissionais(ArrayList<Profissional> profissionais) {
        this.profissionais = profissionais;
    }
}

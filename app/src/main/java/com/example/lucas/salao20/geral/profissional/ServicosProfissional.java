package com.example.lucas.salao20.geral.profissional;

import com.example.lucas.salao20.geral.geral.Servico;

import java.util.HashMap;

/**
 * Created by Lucas on 19/06/2017.
 */

public class ServicosProfissional {
    private HashMap<String,ServicoProfissional> servicosProfissional;

    public ServicosProfissional() {
    }

    public void addServicoProfissional(ServicoProfissional servicoProfissional){
        if (this.servicosProfissional == null){
            this.servicosProfissional = new HashMap<String, ServicoProfissional>();
        }
        if (servicosProfissional.containsKey(servicoProfissional.getIdServico())){
            this.servicosProfissional.remove(servicoProfissional.getIdServico());
        }
        this.servicosProfissional.put(servicoProfissional.getIdServico(),servicoProfissional);
    }

    public void removerServicoProfissional(String idServico){
        if (this.servicosProfissional != null && this.servicosProfissional.containsKey(idServico)){
            this.servicosProfissional.remove(idServico);
        }
    }

    //GETTERS SETTERS
    public HashMap<String, ServicoProfissional> getServicosProfissional() {
        return servicosProfissional;
    }
    public void setServicosProfissional(HashMap<String, ServicoProfissional> servicosProfissional) {
        this.servicosProfissional = servicosProfissional;
    }
}

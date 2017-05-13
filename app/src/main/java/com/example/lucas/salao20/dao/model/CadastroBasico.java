package com.example.lucas.salao20.dao.model;

/**
 * Created by Lucas on 17/03/2017.
 */

public class CadastroBasico {
    private String uid;
    private Double nivelUsuario;
    private String tipoUsuario;


    public CadastroBasico() {
    }

    public CadastroBasico(String _id, Double nivelUsuario, String tipoUsuario) {
        this.uid = _id;
        this.nivelUsuario = nivelUsuario;
        this.tipoUsuario = tipoUsuario;
    }

    //GETTERS AND SETTERS
    public Double getNivelUsuario() {
        return nivelUsuario;
    }
    public void setNivelUsuario(Double nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String get_uid() {
        return uid;
    }
    public void set_uid(String uid) {
        this.uid = uid;
    }


}

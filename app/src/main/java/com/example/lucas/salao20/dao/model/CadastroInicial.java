package com.example.lucas.salao20.dao.model;

/**
 * Created by Lucas on 17/03/2017.
 */

public class CadastroInicial {
    private Integer _id;
    private Double nivelUsuario;
    private String tipoUsuario;
    private Integer codigoUnico;
    private String uid;


    public CadastroInicial() {
    }

    public CadastroInicial(Integer _id, Double nivelUsuario, String tipoUsuario, Integer codigoUnico, String uid) {
        this._id = _id;
        this.nivelUsuario = nivelUsuario;
        this.tipoUsuario = tipoUsuario;
        this.codigoUnico = codigoUnico;
        this.uid = uid;
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

    public Integer get_id() {
        return _id;
    }
    public void set_id(Integer _id) {
        this._id = _id;
    }

    public Integer getCodigoUnico() {
        return codigoUnico;
    }
    public void setCodigoUnico(Integer codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

}

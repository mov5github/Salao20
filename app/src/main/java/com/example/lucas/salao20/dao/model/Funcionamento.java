package com.example.lucas.salao20.dao.model;

/**
 * Created by Lucas on 21/03/2017.
 */

public class Funcionamento {
    private String dia;
    private String abre;
    private String fecha;

    public Funcionamento() {

    }

    public Funcionamento(String dia, String abre, String fecha) {
        this.dia = dia;
        this.abre = abre;
        this.fecha = fecha;
    }

    //GETTERS AND SETTERS
    public String getAbre() {
        return abre;
    }
    public void setAbre(String abre) {
        this.abre = abre;
    }

    public String getDia() {
        return dia;
    }
    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getFecha() {
        return fecha;
    }
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}

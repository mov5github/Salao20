package com.example.lucas.salao20.geral.geral;

import com.example.lucas.salao20.enumeradores.DiasENUM;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 21/03/2017.
 */

public class Funcionamento {
    private String dia;
    private String abre;
    private String fecha;
    private int inicioAlmoco;
    private int duracaoAlmoco;

    public Funcionamento() {

    }

    public Funcionamento(String dia, String abre, String fecha) {
        this.dia = dia;
        this.abre = abre;
        this.fecha = fecha;
    }

    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        if (abre != null && !abre.isEmpty()){
            result.put(DiasENUM.ABRE,abre);
        }
        if (fecha != null && !fecha.isEmpty()){
            result.put(DiasENUM.FECHA,fecha);
        }
        if (inicioAlmoco != 0){
            result.put(DiasENUM.INICIO_ALMOCO,inicioAlmoco);
        }
        if (duracaoAlmoco != 0){
            result.put(DiasENUM.DURACAO_ALMOCO,duracaoAlmoco);
        }
        return result;
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

    public int getInicioAlmoco() {
        return inicioAlmoco;
    }
    public void setInicioAlmoco(int inicioAlmoco) {
        this.inicioAlmoco = inicioAlmoco;
    }

    public int getDuracaoAlmoco() {
        return duracaoAlmoco;
    }
    public void setDuracaoAlmoco(int duracaoAlmoco) {
        this.duracaoAlmoco = duracaoAlmoco;
    }
}

package com.example.lucas.salao20.geral;

import android.content.Intent;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 05/05/2017.
 */
@IgnoreExtraProperties
public class CadastroBasico {
    private Double nivelUsuario;
    private String tipoUsuario;
    private String codigoUnico;


    //ENUM
    @Exclude
    private static final String CADASTRO_BASICO = "cadastroBasico";
    @Exclude
    private static final String NIVEL_USUARIO = "nivelUsuario";
    @Exclude
    private static final String TIPO_USUARIO = "tipoUsuario";
    @Exclude
    private static final String CODIGO_UNICO = "codigoUnico";


    public CadastroBasico() {
    }

    //AUXILIARES
    @Exclude
    public void receberDoFirebase(Map map){
        if (map.containsKey(NIVEL_USUARIO) && map.get(NIVEL_USUARIO) != null){
            this.nivelUsuario = (Double) map.get(NIVEL_USUARIO);
        }
        if (map.containsKey(TIPO_USUARIO) && map.get(TIPO_USUARIO) != null){
            this.tipoUsuario = (String) map.get(TIPO_USUARIO);
        }
        if (map.containsKey(CODIGO_UNICO) && map.get(CODIGO_UNICO) != null){
            this.codigoUnico = (String) map.get(CODIGO_UNICO);
        }
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        if (nivelUsuario != null){
            result.put(NIVEL_USUARIO, nivelUsuario);
        }
        if (tipoUsuario != null && !tipoUsuario.isEmpty()){
            result.put(TIPO_USUARIO, tipoUsuario);
        }
        if (codigoUnico != null && !codigoUnico.isEmpty()){
            result.put(CODIGO_UNICO, codigoUnico);
        }
        return result;
    }

    //GETTERS SETTERS
    @Exclude
    public Double getNivelUsuario() {
        return nivelUsuario;
    }
    @Exclude
    public void setNivelUsuario(Double nivelUsuario) {
        this.nivelUsuario = nivelUsuario;
    }

    @Exclude
    public String getTipoUsuario() {
        return tipoUsuario;
    }
    @Exclude
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    @Exclude
    public String getCodigoUnico() {
        return codigoUnico;
    }
    @Exclude
    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    @Exclude
    public static String getCADASTRO_BASICO() {
        return CADASTRO_BASICO;
    }

    @Exclude
    public static String getNIVEL_USUARIO(){
        return NIVEL_USUARIO;
    }

    @Exclude
    public static String getTIPO_USUARIO(){
        return TIPO_USUARIO;
    }

    @Exclude
    public static String getCODIGO_UNICO(){
        return CODIGO_UNICO;
    }
}

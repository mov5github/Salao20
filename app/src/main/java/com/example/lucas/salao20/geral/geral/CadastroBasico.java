package com.example.lucas.salao20.geral.geral;

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
    private String userMetadataUid;
    private String nome;
    private String sobrenome;


    //ENUM
    @Exclude
    private static final String CADASTRO_BASICO = "cadastroBasico";
    @Exclude
    private static final String NIVEL_USUARIO = "nivelUsuario";
    @Exclude
    private static final String TIPO_USUARIO = "tipoUsuario";
    @Exclude
    private static final String CODIGO_UNICO = "codigoUnico";
    @Exclude
    private static final String USER_METADATA_UID = "userMetadataUid";
    @Exclude
    private static final String NOME = "nome";
    @Exclude
    private static final String SOBRENOME = "sobrenome";




    public CadastroBasico() {
    }

    //AUXILIARES
    public void receberDoFirebase(Map map){
        if (map.containsKey(NIVEL_USUARIO) && map.get(NIVEL_USUARIO) != null){
            this.nivelUsuario = (Double) map.get(NIVEL_USUARIO);
        }else if (map.containsKey(NIVEL_USUARIO) && map.get(NIVEL_USUARIO) == null){
            this.nivelUsuario = null;
        }
        if (map.containsKey(TIPO_USUARIO) && map.get(TIPO_USUARIO) != null){
            this.tipoUsuario = (String) map.get(TIPO_USUARIO);
        }else if (map.containsKey(TIPO_USUARIO) && map.get(TIPO_USUARIO) == null){
            this.tipoUsuario = null;
        }
        if (map.containsKey(CODIGO_UNICO) && map.get(CODIGO_UNICO) != null){
            this.codigoUnico = (String) map.get(CODIGO_UNICO);
        }else if (map.containsKey(CODIGO_UNICO) && map.get(CODIGO_UNICO) == null){
            this.codigoUnico = null;
        }
        if (map.containsKey(USER_METADATA_UID) && map.get(USER_METADATA_UID) != null){
            this.userMetadataUid = (String) map.get(USER_METADATA_UID);
        }else if (map.containsKey(USER_METADATA_UID) && map.get(USER_METADATA_UID) == null){
            this.userMetadataUid = null;
        }
        if (map.containsKey(NOME) && map.get(NOME) != null){
            this.nome = (String) map.get(NOME);
        }else if (map.containsKey(NOME) && map.get(NOME) == null){
            this.nome = null;
        }
        if (map.containsKey(SOBRENOME) && map.get(SOBRENOME) != null){
            this.sobrenome = (String) map.get(SOBRENOME);
        }else if (map.containsKey(SOBRENOME) && map.get(SOBRENOME) == null){
            this.sobrenome = null;
        }
    }

    public void receberDoFirebaseRemover(Map map){
        if (map.containsKey(NIVEL_USUARIO)){
            this.nivelUsuario = null;
        }
        if (map.containsKey(TIPO_USUARIO)){
            this.tipoUsuario = null;
        }
        if (map.containsKey(CODIGO_UNICO)){
            this.codigoUnico = null;
        }
    }

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
        if (userMetadataUid != null && !userMetadataUid.isEmpty()){
            result.put(USER_METADATA_UID, userMetadataUid);
        }
        if (nome != null && !nome.isEmpty()){
            result.put(NOME, nome);
        }
        if (sobrenome != null && !sobrenome.isEmpty()){
            result.put(SOBRENOME, sobrenome);
        }
        return result;
    }

    //GETTERS SETTERS
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

    public String getCodigoUnico() {
        return codigoUnico;
    }
    public void setCodigoUnico(String codigoUnico) {
        this.codigoUnico = codigoUnico;
    }

    public String getUserMetadataUid() {
        return userMetadataUid;
    }
    public void setUserMetadataUid(String userMetadataUid) {
        this.userMetadataUid = userMetadataUid;
    }

    public String getSobrenome() {
        return sobrenome;
    }
    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }


    public static String getCADASTRO_BASICO() {
        return CADASTRO_BASICO;
    }

    public static String getNIVEL_USUARIO(){
        return NIVEL_USUARIO;
    }

    public static String getTIPO_USUARIO(){
        return TIPO_USUARIO;
    }

    public static String getCODIGO_UNICO(){
        return CODIGO_UNICO;
    }

    public static String getUSER_METADATA_UID() {
        return USER_METADATA_UID;
    }

    public static String getNOME() {
        return NOME;
    }

    public static String getSOBRENOME() {
        return SOBRENOME;
    }
}

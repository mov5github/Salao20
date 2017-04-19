package com.example.lucas.salao20.dao.model;

/**
 * Created by Lucas on 14/04/2017.
 */

public class CadastroComplementar {
    private String uid;
    private String nome;
    private String endereco;
    private Integer numeroEndereco;
    private String complementoEndereco;
    private Integer cep;
    private Integer telefoneFixo1;
    private Integer telefoneFixo2;
    private Integer whatsapp;
    private Integer celular1;
    private Integer celular2;
    private String facebook;
    private Integer logo;


    public CadastroComplementar() {
    }

    public CadastroComplementar(String uid, String nome, String endereco, Integer numeroEndereco, String complementoEndereco, Integer cep, Integer telefoneFixo1, Integer telefoneFixo2, Integer whatsapp, Integer celular1, Integer celular2, String facebook, Integer logo) {
        this.uid = uid;
        this.nome = nome;
        this.endereco = endereco;
        this.numeroEndereco = numeroEndereco;
        this.complementoEndereco = complementoEndereco;
        this.cep = cep;
        this.telefoneFixo1 = telefoneFixo1;
        this.telefoneFixo2 = telefoneFixo2;
        this.whatsapp = whatsapp;
        this.celular1 = celular1;
        this.celular2 = celular2;
        this.facebook = facebook;
        this.logo = logo;

    }

    //GETTERS AND SETTERS
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getNumeroEndereco() {
        return numeroEndereco;
    }
    public void setNumeroEndereco(Integer numeroEndereco) {
        this.numeroEndereco = numeroEndereco;
    }

    public String getComplementoEndereco() {
        return complementoEndereco;
    }
    public void setComplementoEndereco(String complementoEndereco) {
        this.complementoEndereco = complementoEndereco;
    }

    public Integer getCep() {
        return cep;
    }
    public void setCep(Integer cep) {
        this.cep = cep;
    }

    public Integer getTelefoneFixo1() {
        return telefoneFixo1;
    }
    public void setTelefoneFixo1(Integer telefoneFixo1) {
        this.telefoneFixo1 = telefoneFixo1;
    }

    public Integer getTelefoneFixo2() {
        return telefoneFixo2;
    }
    public void setTelefoneFixo2(Integer telefoneFixo2) {
        this.telefoneFixo2 = telefoneFixo2;
    }

    public Integer getWhatsapp() {
        return whatsapp;
    }
    public void setWhatsapp(Integer whatsapp) {
        this.whatsapp = whatsapp;
    }

    public Integer getCelular1() {
        return celular1;
    }
    public void setCelular1(Integer celular1) {
        this.celular1 = celular1;
    }

    public Integer getCelular2() {
        return celular2;
    }
    public void setCelular2(Integer celular2) {
        this.celular2 = celular2;
    }

    public String getFacebook() {
        return facebook;
    }
    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public Integer getLogo() {
        return logo;
    }
    public void setLogo(Integer logo) {
        this.logo = logo;
    }
}

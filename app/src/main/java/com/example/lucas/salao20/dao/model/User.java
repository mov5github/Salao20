package com.example.lucas.salao20.dao.model;

/**
 * Created by Lucas on 10/04/2017.
 */

public class User {
    private String uid;
    private Integer codUnicoUserSalao;
    private Integer codUnicoUserCabeleireiro;

    public User() {
    }

    public User(String uid, Integer codUnicoUserSalao, Integer codUnicoUserCabeleireiro) {
        this.uid = uid;
        this.codUnicoUserSalao = codUnicoUserSalao;
        this.codUnicoUserCabeleireiro = codUnicoUserCabeleireiro;
    }

    //GETTERS AND SETTERS
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getCodUnicoUserSalao() {
        return codUnicoUserSalao;
    }
    public void setCodUnicoUserSalao(Integer codUnicoUserSalao) {
        this.codUnicoUserSalao = codUnicoUserSalao;
    }

    public Integer getCodUnicoUserCabeleireiro() {
        return codUnicoUserCabeleireiro;
    }
    public void setCodUnicoUserCabeleireiro(Integer codUnicoUserCabeleireiro) {
        this.codUnicoUserCabeleireiro = codUnicoUserCabeleireiro;
    }
}

package com.example.lucas.salao20.geral.geral;

import android.util.Base64;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 12/05/2017.
 */

@IgnoreExtraProperties
public class Criptografia {
    private String chavePublica;
    private String chavePrivada;

    //ENUM
    @Exclude
    private static final String CRIPTOGRAFIA = "criptografia";

    @Exclude
    private static final String CHAVE_PUBLICA = "chavePublica";

    @Exclude
    private static final String CHAVE_PRIVADA = "chavePrivada";


    public Criptografia() {
    }


    //AUXILIARES
    @Exclude
    public void gerarChave() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance ("SHA1PRNG");
            keyGen.initialize(1024,random);
            KeyPair pair = keyGen.generateKeyPair();

            this.chavePublica = Base64.encodeToString(pair.getPublic().getEncoded(),Base64.URL_SAFE);
            this.chavePrivada = Base64.encodeToString(pair.getPrivate().getEncoded(),Base64.URL_SAFE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        if (chavePublica != null && !chavePublica.isEmpty()){
            result.put(CHAVE_PUBLICA,chavePublica);
        }
        if (chavePrivada != null && !chavePrivada.isEmpty()){
            result.put(CHAVE_PRIVADA,chavePrivada);
        }
        return result;
    }


    //GETTERS SETTERS
    @Exclude
    public String getChavePublica() {
        return chavePublica;
    }
    @Exclude
    public void setChavePublica(String chavePublica) {
        this.chavePublica = chavePublica;
    }

    @Exclude
    public String getChavePrivada() {
        return chavePrivada;
    }
    @Exclude
    public void setChavePrivada(String chavePrivada) {
        this.chavePrivada = chavePrivada;
    }

    @Exclude
    public static String getCRIPTOGRAFIA() {
        return CRIPTOGRAFIA;
    }

    @Exclude
    public static String getCHAVE_PUBLICA() {
        return CHAVE_PUBLICA;
    }

    @Exclude
    public static String getCHAVE_PRIVADA() {
        return CHAVE_PRIVADA;
    }

}

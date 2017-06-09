package com.example.lucas.salao20.domain.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Lucas on 17/03/2017.
 */

public class LibraryClass {
    public static String PREF = "com.example.lucas.materialdesignteste.PREF";

    private static DatabaseReference firebase;

    private LibraryClass(){}

    public static DatabaseReference getFirebase(){
        if( firebase == null ){
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            firebase = FirebaseDatabase.getInstance().getReference();
        }

        return( firebase );
    }

    static public void saveSP(Context context, String key, String value ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();
    }

    static public String getSP(Context context, String key ){
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        String token = sp.getString(key, "");
        return( token );
    }

    public static String formatarCodUnico(Integer codUnico){
        String codUnicoFormatado = "";
        for (int i = 0; i < (6 - codUnico.toString().length()); i++){
            codUnicoFormatado += "0";
        }
        codUnicoFormatado += codUnico.toString();
        return codUnicoFormatado;
    }

}

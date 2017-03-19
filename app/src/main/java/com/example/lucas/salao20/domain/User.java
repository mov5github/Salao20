package com.example.lucas.salao20.domain;

import android.app.Activity;
import android.content.Context;

import com.example.lucas.salao20.activitys.SignUpActivity;
import com.example.lucas.salao20.dao.DatabaseHelper;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by Lucas on 17/03/2017.
 */

public class User {
    public static String PROVIDER = "com.example.lucas.salao20.domain.User.PROVIDER";


    private String id;
    private String email;
    private String password;
    private String newPassword;



    public User(){}

    public void saveDB(Activity activity, DatabaseReference.CompletionListener... completionListener){
        if (activity instanceof SignUpActivity){
            DatabaseReference firebase = null;
            if( completionListener.length == 0 ){
                firebase = LibraryClass.getFirebase().child("users").child( getId() ).child("acount").child("email");
                firebase.setValue(this.email);
                firebase = LibraryClass.getFirebase().child("users").child( getId() ).child("acount").child("password");
                firebase.setValue(this.password);
            }
            else{
                firebase = LibraryClass.getFirebase().child("users").child( getId() ).child("acount").child("email");
                firebase.setValue(this.email, completionListener[0]);
                firebase = LibraryClass.getFirebase().child("users").child( getId() ).child("acount").child("password");
                firebase.setValue(this.password, completionListener[0]);
            }
        }

    }

    public boolean isSocialNetworkLogged( Context context ){
        String token = getProviderSP( context );
        return( token.contains("facebook") || token.contains("google") || token.contains("twitter") || token.contains("github") );
    }

    public void setEmailIfNull(String email) {
        if( this.email == null ){
            this.email = email;
        }

    }


    public void saveProviderSP(Context context, String token ){
        LibraryClass.saveSP( context, PROVIDER, token );
    }
    public String getProviderSP(Context context ){
        return( LibraryClass.getSP( context, PROVIDER) );
    }


    //Getters and Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}

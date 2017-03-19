package com.example.lucas.salao20.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.example.lucas.salao20.R;

/**
 * Created by Lucas on 17/03/2017.
 */

public class FragmentCadastroBasico extends Fragment{
    private AutoCompleteTextView email;
    private EditText password;
    private EditText passwordAgain;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","onCreate() fragment cadastro basico");

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up,container,false);
        initView(view);
        return view;
    }

    private void initView(View view){
        email = (AutoCompleteTextView) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        passwordAgain = (EditText) view.findViewById(R.id.password_again);

    }

    //GETTERS
    public EditText getPassword() {
        return password;
    }

    public EditText getPasswordAgain() {
        return passwordAgain;
    }

    public AutoCompleteTextView getEmail() {
        return email;
    }
}

package com.example.lucas.salao20.fragments.configuracaoInicial.cliente;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lucas.salao20.R;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialClienteBasico extends Fragment{
    //ENUM
    private static final String TITULO = "Cliente.B";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_cliente_basico,container,false);
        initViews(view);
        return view;
    }


    private void initViews(View view){

    }

    //Getters Setters
    public static String getTITULO() {
        return TITULO;
    }
}

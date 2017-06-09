package com.example.lucas.salao20.fragments.home.salao;

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

public class FragmentHomeSalaoDados extends Fragment{
    //ENUM
    private static final String TITULO = "Agenda";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_salao_dados,container,false);
        initViews(view);
        return view;
    }

    public static String getTitulo() {
        String titulo = "HOME SALAO DADOS";
        return titulo;
    }

    private void initViews(View view){

    }
}

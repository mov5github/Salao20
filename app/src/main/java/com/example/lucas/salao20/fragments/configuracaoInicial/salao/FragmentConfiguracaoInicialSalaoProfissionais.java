package com.example.lucas.salao20.fragments.configuracaoInicial.salao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.lucas.salao20.R;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialSalaoProfissionais extends Fragment{
    //ENUM
    private static final String TITULO = "Profissionais";

    private ProgressBar progressCabeleireiros;
    private FloatingActionButton fabCabeleireiros;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_profissionais,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        this.fabCabeleireiros = (FloatingActionButton) view.findViewById(R.id.fab_fragment_cabeleireiros);
        this.progressCabeleireiros = (ProgressBar) view.findViewById(R.id.progress_fragment_cabeleireiros);
    }

    public void liberarPreenchimento(){
        //TODO
    }

    public boolean preenchimentoIsValid(){
            return false;
        }

    //GETTERS SETTERS
    public static String getTITULO() {
        return TITULO;
    }
}

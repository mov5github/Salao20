package com.example.lucas.salao20.fragments.configuracaoInicial.salao;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lucas.salao20.R;

import java.text.NumberFormat;
import java.util.Arrays;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialSalaoProfissionais extends Fragment{
    //ENUM
    private static final String TITULO = "Profissionais";

    private ProgressBar progressProfissionais;
    private FloatingActionButton fabProfissionais;
    private EditText codigoUnicoProfissional;
    private Button buttonAdicionarProfissional;
    private TextView adicionarProfissionalSemCodigoUnico;

    //CONTROLE


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_profissionais,container,false);
        initViews(view);
        return view;
    }

    private void initViews(View view){
        this.fabProfissionais = (FloatingActionButton) view.findViewById(R.id.fab_fragment_profissionais);
       /* this.fabProfissionais.setVisibility(View.INVISIBLE);
        this.fabProfissionais.setClickable(false);*/
        this.progressProfissionais = (ProgressBar) view.findViewById(R.id.progress_fragment_profissionais);
        /*this.progressProfissionais.setVisibility(View.VISIBLE);*/
        this.codigoUnicoProfissional = (EditText)view.findViewById(R.id.codigo_unico_profissional);
        this.codigoUnicoProfissional.setText("000000");
        codigoUnicoProfissional.setSelection(codigoUnicoProfissional.length());
        this.codigoUnicoProfissional.addTextChangedListener(new TextWatcher() {
            boolean ignoreChange = false;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!ignoreChange){
                    ignoreChange = true;
                    String retorno = s.toString();
                    if (s.toString().length() > 6){
                        char[] chars = s.toString().toCharArray();
                        retorno = "";
                        for (int i = 0; i < (s.toString().length()-1); i++) {
                            chars[i] = chars[i + 1];
                            retorno = retorno + chars[i];
                        }
                        retorno = retorno.substring(0,6);
                    }else{
                        for (int i = 0; i < (6 - s.toString().length()); i++){
                            retorno = "0" + retorno;
                        }
                    }
                    codigoUnicoProfissional.setText(retorno);
                }else {
                    ignoreChange = false;
                }
                codigoUnicoProfissional.setSelection(codigoUnicoProfissional.length());
            }
        });
        /*this.codigoUnicoProfissional.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return null;
            }
        }});*/

        /*this.codigoUnicoProfissional.setFilters(new InputFilter[]{new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                Log.i("string","source = " + source + "\ndest = " + dest + "\n start = " + start + "\n end = " + end + "\ndstart = " + dstart + "\ndend = " + dend);
                return null;

                /*if (end == 6){
                    return null;
                }else {
                    char[] chars = dest.toString().toCharArray();
                    chars[dstart] = source.toString().charAt(0);
                    return null;
                }
                if (controleMascaraCodUnico == 6 && start == 0 && end == 0){
                    //TODO
                    return null;
                }else if (controleMascaraCodUnico == 0 && start == 0 && end == 0){
                    return "0";
                }else{
                    if (start == 0 && end == 0){
                        controleMascaraCodUnico--;
                        return dest.toString().replace(dest.charAt(5 - controleMascaraCodUnico),'0');
                    }else if (!source.toString().matches("[^0-9]")){
                        controleMascaraCodUnico++;
                        String retorno = dest.toString();
                        retorno.replace(retorno.charAt(0),retorno.charAt(1));
                        retorno.replace(retorno.charAt(1),retorno.charAt(2));
                        retorno.replace(retorno.charAt(2),retorno.charAt(3));
                        retorno.replace(retorno.charAt(3),retorno.charAt(4));
                        retorno.replace(retorno.charAt(4),retorno.charAt(5));
                        retorno.replace(retorno.charAt(5),source.toString().charAt(0));
                        return retorno;
                    }else{
                        return dest.toString();
                    }
                    return "1";
                }

            }
        }});*/

        //this.buttonAdicionarProfissional = (Button) view.findViewById(R.id.btn_adicionar_profissional);
        //this.buttonAdicionarProfissional.setClickable(false);
        //this.buttonAdicionarProfissional.setVisibility(View.INVISIBLE);
        //this.adicionarProfissionalSemCodigoUnico = (TextView) view.findViewById(R.id.label_profissional_sem_cod_unico);
      //  this.adicionarProfissionalSemCodigoUnico.setVisibility(View.INVISIBLE);
       // this.adicionarProfissionalSemCodigoUnico.setClickable(false);
    }

    //CLASSES
    private class MascaraCodigoUnico implements TextWatcher {
        EditText campo;

        public MascaraCodigoUnico(EditText campo) {
            super();
            this.campo = campo;
        }

        private boolean isUpdating = false;
        // Pega a formatacao do sistema, se for brasil R$ se EUA US$


        @Override
        public void onTextChanged(CharSequence s, int start, int before, int after) {
            // Evita que o método seja executado varias vezes.
            // Se tirar ele entre em loop
            if (isUpdating) {
                isUpdating = false;
                return;
            }
            isUpdating = true;
            String str = s.toString();
            // Verifica se já existe a máscara no texto.
            boolean hasMask = (str.indexOf("#") > -1);
            // Verificamos se existe máscara
            if (hasMask) {
                // Retiramos a máscara.
                str = str.replaceAll("[#]", "");
            }
            try {
                // Transformamos o número que está escrito no EditText em
                // monetário.
                campo.setText(str);
                campo.setSelection(campo.getText().length());
            } catch (NumberFormatException e) {
                s = "";
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Não utilizado
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Não utilizado
        }



    }

    public boolean preenchimentoIsValid(){
            return false;
        }

    //GETTERS SETTERS
    public static String getTITULO() {
        return TITULO;
    }
}

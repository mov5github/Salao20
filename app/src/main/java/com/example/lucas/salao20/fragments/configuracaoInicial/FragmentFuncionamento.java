package com.example.lucas.salao20.fragments.configuracaoInicial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.dao.model.Funcionamento;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.geral.FuncionamentoSalao;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentFuncionamento extends Fragment{
    private TextView abreSegunda;
    private TextView abreTerca;
    private TextView abreQuarta;
    private TextView abreQuinta;
    private TextView abreSexta;
    private TextView abreSabado;
    private TextView abreDomingo;
    private TextView fechaSegunda;
    private TextView fechaTerca;
    private TextView fechaQuarta;
    private TextView fechaQuinta;
    private TextView fechaSexta;
    private TextView fechaSabado;
    private TextView fechaDomingo;
    private CheckBox segunda;
    private CheckBox terca;
    private CheckBox quarta;
    private CheckBox quinta;
    private CheckBox sexta;
    private CheckBox sabado;
    private CheckBox domingo;


    static FuncionamentoSalao funcionamentoSalao;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_funcionamento,container,false);
        initViews(view);
        return view;
    }

    public static String getTitulo() {
        String titulo = "Funcionamento";
        return titulo;
    }

    private void initViews(View view){
        abreSegunda = (TextView) view.findViewById(R.id.abre_segunda);
        abreTerca =  (TextView) view.findViewById(R.id.abre_terca);
        abreQuarta = (TextView) view.findViewById(R.id.abre_quarta);
        abreQuinta = (TextView) view.findViewById(R.id.abre_quinta);
        abreSexta = (TextView) view.findViewById(R.id.abre_sexta);
        abreSabado = (TextView) view.findViewById(R.id.abre_sabado);
        abreDomingo = (TextView) view.findViewById(R.id.abre_domingo);
        fechaSegunda = (TextView) view.findViewById(R.id.fecha_segunda);
        fechaTerca =  (TextView) view.findViewById(R.id.fecha_terca);
        fechaQuarta = (TextView) view.findViewById(R.id.fecha_quarta);
        fechaQuinta = (TextView) view.findViewById(R.id.fecha_quinta);
        fechaSexta = (TextView) view.findViewById(R.id.fecha_sexta);
        fechaSabado = (TextView) view.findViewById(R.id.fecha_sabado);
        fechaDomingo = (TextView) view.findViewById(R.id.fecha_domingo);
        segunda =(CheckBox) view.findViewById(R.id.segunda);
        terca =(CheckBox) view.findViewById(R.id.terca);
        quarta =(CheckBox) view.findViewById(R.id.quarta);
        quinta =(CheckBox) view.findViewById(R.id.quinta);
        sexta =(CheckBox) view.findViewById(R.id.sexta);
        sabado =(CheckBox) view.findViewById(R.id.sabado);
        domingo =(CheckBox) view.findViewById(R.id.domingo);
    }

    public boolean preenchimentoIsValid(){
        Log.i("script","entrou validacao");
        if (segunda.isChecked()) {
            Log.i("script","segunda check");
            if (abreSegunda.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das segundas!");
                return false;
            }else if (fechaSegunda.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das segundas!");
                return false;
            }
        }
        if (terca.isChecked()) {
            Log.i("script","terca check");
            if (abreTerca.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das terças!");
                return false;
            }else if (fechaTerca.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das terças!");
                return false;
            }
        }
        if (quarta.isChecked()) {
            Log.i("script","quarta check");
            if (abreQuarta.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das quartas!");
                return false;
            }else if (fechaQuarta.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das quartas!");
                return false;
            }
        }
        if (quinta.isChecked()) {
            Log.i("script","quinta check");
            if (abreQuinta.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das quintas!");
                return false;
            }else if (fechaQuinta.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das quintas!");
                return false;
            }
        }
        if (sexta.isChecked()) {
            Log.i("script","sexta check");
            if (abreSexta.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura das sextas!");
                return false;
            }else if (fechaSexta.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento das sextas!");
                return false;
            }
        }
        if (sabado.isChecked()) {
            Log.i("script","sabado check");
            if (abreSabado.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura dos sabados!");
                return false;
            }else if (fechaSabado.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento dos sabados!");
                return false;
            }
        }
        if (domingo.isChecked()) {
            Log.i("script","domingo check");
            if (abreDomingo.getText().toString().equals("--:--")){
                showToast("Preencher horario de abertura dos domingos!");
                return false;
            }else if (fechaDomingo.getText().toString().equals("--:--")){
                showToast("Preencher horario de encerramento dos domingos!");
                return false;
            }
        }

        if(!segunda.isChecked() && !terca.isChecked() && !quarta.isChecked() && !quinta.isChecked() && !sexta.isChecked() && !sabado.isChecked() && !domingo.isChecked()){
            showToast("Selecionar pelo menos um dia da semana!");
            return false;
        }
        return true;
    }

    public FuncionamentoSalao criarFuncionamentoSalao(){
        FuncionamentoSalao funcionamentoSalao = new FuncionamentoSalao();
        Funcionamento funcionamento = new Funcionamento();

        if (this.abreSegunda.getText() != null && !this.abreSegunda.getText().toString().isEmpty() && !this.abreSegunda.getText().toString().equals("--:--") && this.fechaSegunda.getText() != null && !this.fechaSegunda.getText().toString().isEmpty() && !this.fechaSegunda.getText().toString().equals("--:--")){
            funcionamento.setDia(DiasENUM.SEGUNDA);
            funcionamento.setAbre(this.abreSegunda.getText().toString());
            funcionamento.setFecha(this.fechaSegunda.getText().toString());
            //funcionamentoSalao.addFuncionamento(funcionamento);
        }

        if (this.abreTerca.getText() != null && !this.abreTerca.getText().toString().isEmpty() && !this.abreTerca.getText().toString().equals("--:--") && this.fechaTerca.getText() != null && !this.fechaTerca.getText().toString().isEmpty() && !this.fechaTerca.getText().toString().equals("--:--")){
            funcionamento = new Funcionamento();
            funcionamento.setAbre(this.abreTerca.getText().toString());
            funcionamento.setFecha(this.fechaTerca.getText().toString());
            //funcionamentoSalao.addFuncionamento(funcionamento);
        }

        if (this.abreQuarta.getText() != null && !this.abreQuarta.getText().toString().isEmpty() && !this.abreQuarta.getText().toString().equals("--:--") && this.fechaQuarta.getText() != null && !this.fechaQuarta.getText().toString().isEmpty() && !this.fechaQuarta.getText().toString().equals("--:--")){
            funcionamento = new Funcionamento();
            funcionamento.setAbre(this.abreQuarta.getText().toString());
            funcionamento.setFecha(this.fechaQuarta.getText().toString());
           // funcionamentoSalao.addFuncionamento(funcionamento);
        }

        if (this.abreQuinta.getText() != null && !this.abreQuinta.getText().toString().isEmpty() && !this.abreQuinta.getText().toString().equals("--:--") && this.fechaQuinta.getText() != null && !this.fechaQuinta.getText().toString().isEmpty() && !this.fechaQuinta.getText().toString().equals("--:--")){
            funcionamento = new Funcionamento();
            funcionamento.setAbre(this.abreQuinta.getText().toString());
            funcionamento.setFecha(this.fechaQuinta.getText().toString());
            //funcionamentoSalao.addFuncionamento(funcionamento);
        }

        if (this.abreSexta.getText() != null && !this.abreSexta.getText().toString().isEmpty() && !this.abreSexta.getText().toString().equals("--:--") && this.fechaSexta.getText() != null && !this.fechaSexta.getText().toString().isEmpty() && !this.fechaSexta.getText().toString().equals("--:--")){
            funcionamento = new Funcionamento();
            funcionamento.setAbre(this.abreSexta.getText().toString());
            funcionamento.setFecha(this.fechaSexta.getText().toString());
           // funcionamentoSalao.addFuncionamento(funcionamento);
        }

        if (this.abreSabado.getText() != null && !this.abreSabado.getText().toString().isEmpty() && !this.abreSabado.getText().toString().equals("--:--") && this.fechaSabado.getText() != null && !this.fechaSabado.getText().toString().isEmpty() && !this.fechaSabado.getText().toString().equals("--:--")){
            funcionamento = new Funcionamento();
            funcionamento.setAbre(this.abreSabado.getText().toString());
            funcionamento.setFecha(this.fechaSabado.getText().toString());
            //funcionamentoSalao.addFuncionamento(funcionamento);
        }

        if (this.abreDomingo.getText() != null && !this.abreDomingo.getText().toString().isEmpty() && !this.abreDomingo.getText().toString().equals("--:--") && this.fechaDomingo.getText() != null && !this.fechaDomingo.getText().toString().isEmpty() && !this.fechaDomingo.getText().toString().equals("--:--")){
            funcionamento = new Funcionamento();
            funcionamento.setAbre(this.abreDomingo.getText().toString());
            funcionamento.setFecha(this.fechaDomingo.getText().toString());
            //funcionamentoSalao.addFuncionamento(funcionamento);
        }

        return funcionamentoSalao;
    }

    public void aplicaVisibilidadeHorarios(View view){
        CheckBox checkBox = (CheckBox) view;
        switch (view.getId()){
            case R.id.segunda:
                if (checkBox.isChecked()){
                    abreSegunda.setVisibility(View.VISIBLE);
                    fechaSegunda.setVisibility(View.VISIBLE);
                }else{
                    abreSegunda.setVisibility(View.INVISIBLE);
                    fechaSegunda.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.terca:
                if (checkBox.isChecked()){
                    abreTerca.setVisibility(View.VISIBLE);
                    fechaTerca.setVisibility(View.VISIBLE);
                }else {
                    abreTerca.setVisibility(View.INVISIBLE);
                    fechaTerca.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.quarta:
                if (checkBox.isChecked()){
                    abreQuarta.setVisibility(View.VISIBLE);
                    fechaQuarta.setVisibility(View.VISIBLE);
                }else{
                    abreQuarta.setVisibility(View.INVISIBLE);
                    fechaQuarta.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.quinta:
                if (checkBox.isChecked()){
                    abreQuinta.setVisibility(View.VISIBLE);
                    fechaQuinta.setVisibility(View.VISIBLE);
                }else {
                    abreQuinta.setVisibility(View.INVISIBLE);
                    fechaQuinta.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.sexta:
                if (checkBox.isChecked()){
                    abreSexta.setVisibility(View.VISIBLE);
                    fechaSexta.setVisibility(View.VISIBLE);
                }else {
                    abreSexta.setVisibility(View.INVISIBLE);
                    fechaSexta.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.sabado:
                if (checkBox.isChecked()){
                    abreSabado.setVisibility(View.VISIBLE);
                    fechaSabado.setVisibility(View.VISIBLE);
                }else {
                    abreSabado.setVisibility(View.INVISIBLE);
                    fechaSabado.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.domingo:
                if (checkBox.isChecked()){
                    abreDomingo.setVisibility(View.VISIBLE);
                    fechaDomingo.setVisibility(View.VISIBLE);
                }else {
                    abreDomingo.setVisibility(View.INVISIBLE);
                    fechaDomingo.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    private void showToast( String message ){
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }


    //Getters and Setters
    public TextView getAbreSegunda() {
        return abreSegunda;
    }
    public void setAbreSegunda(TextView abreSegunda) {
        this.abreSegunda = abreSegunda;
    }

    public TextView getAbreTerca() {
        return abreTerca;
    }
    public void setAbreTerca(TextView abreTerca) {
        this.abreTerca = abreTerca;
    }

    public TextView getAbreQuarta() {
        return abreQuarta;
    }
    public void setAbreQuarta(TextView abreQuarta) {
        this.abreQuarta = abreQuarta;
    }

    public TextView getAbreQuinta() {
        return abreQuinta;
    }
    public void setAbreQuinta(TextView abreQuinta) {
        this.abreQuinta = abreQuinta;
    }

    public TextView getAbreSexta() {
        return abreSexta;
    }
    public void setAbreSexta(TextView abreSexta) {
        this.abreSexta = abreSexta;
    }

    public TextView getAbreSabado() {
        return abreSabado;
    }
    public void setAbreSabado(TextView abreSabado) {
        this.abreSabado = abreSabado;
    }

    public TextView getAbreDomingo() {
        return abreDomingo;
    }
    public void setAbreDomingo(TextView abreDomingo) {
        this.abreDomingo = abreDomingo;
    }

    public CheckBox getSegunda() {
        return segunda;
    }
    public void setSegunda(CheckBox segunda) {
        this.segunda = segunda;
    }

    public CheckBox getTerca() {
        return terca;
    }
    public void setTerca(CheckBox terca) {
        this.terca = terca;
    }

    public CheckBox getQuinta() {
        return quinta;
    }
    public void setQuinta(CheckBox quinta) {
        this.quinta = quinta;
    }

    public CheckBox getQuarta() {
        return quarta;
    }
    public void setQuarta(CheckBox quarta) {
        this.quarta = quarta;
    }

    public CheckBox getSexta() {
        return sexta;
    }
    public void setSexta(CheckBox sexta) {
        this.sexta = sexta;
    }

    public CheckBox getSabado() {
        return sabado;
    }
    public void setSabado(CheckBox sabado) {
        this.sabado = sabado;
    }

    public CheckBox getDomingo() {
        return domingo;
    }
    public void setDomingo(CheckBox domingo) {
        this.domingo = domingo;
    }

    public static FuncionamentoSalao getFuncionamentoSalao() {
        return funcionamentoSalao;
    }
    public static void setFuncionamentoSalao(FuncionamentoSalao funcionamentoSalao) {
        FragmentFuncionamento.funcionamentoSalao = funcionamentoSalao;
    }
}

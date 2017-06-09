package com.example.lucas.salao20.fragments.configuracaoInicial.salao;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.activitys.ConfiguracaoInicialActivity;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialSalaoFuncionamento extends Fragment{
    //ENUM
    private static final String TITULO = "Funcionamento";

    private ProgressBar progressFuncionamento;
    private FloatingActionButton fabFuncionamento;

    private ScrollView formFuncionamento;
    private TextInputLayout formNomeSalao;
    private AutoCompleteTextView nomeSalao;
    private TextView labelHorario;
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

    //FIREBASE AUTH
    private FirebaseAuth mAuth;

    //CONTROLE
    private static boolean formularioLiberado;
    private static boolean fragmentFuncionamentoSalaoAtivo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("frag","onCreateView");
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_funcionamento,container,false);
        formularioLiberado = false;
        initViews(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("frag","onCreate");
        fragmentFuncionamentoSalaoAtivo = false;

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("frag","onStart");
        fragmentFuncionamentoSalaoAtivo = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("frag","onStop");
        fragmentFuncionamentoSalaoAtivo = false;
    }

    private void initViews(View view){
        progressFuncionamento = (ProgressBar) view.findViewById(R.id.progress_fragment_funcionamento);
        progressFuncionamento.setVisibility(View.VISIBLE);
        fabFuncionamento = (FloatingActionButton) view.findViewById(R.id.fab_fragment_funcionamento);
        fabFuncionamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*fabFuncionamento.setVisibility(View.INVISIBLE);
                fabFuncionamento.setClickable(false);*/
                proximaEtapa();
            }
        });
        fabFuncionamento.setVisibility(View.INVISIBLE);
        fabFuncionamento.setClickable(false);
        formFuncionamento = (ScrollView) view.findViewById(R.id.form_funcionamento);
        formFuncionamento.setVisibility(View.INVISIBLE);
        formNomeSalao = (TextInputLayout) view.findViewById(R.id.form_nome_salao);
        formNomeSalao.setVisibility(View.INVISIBLE);
        labelHorario = (TextView) view.findViewById(R.id.label_horario_funcionamento);
        nomeSalao = (AutoCompleteTextView) view.findViewById(R.id.nome_salao) ;
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

        fragmentFuncionamentoSalaoAtivo = true;
        aplicarDadosFormulario();
    }

    private void proximaEtapa(){
        if (formularioIsValid()){
            showToast("Formulario valido");
            this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
            ConfiguracaoInicialActivity.getCadastroComplementar().setNome(this.nomeSalao.getText().toString());
            ConfiguracaoInicialActivity.setEtapa1Preenchida(true);
            ConfiguracaoInicialActivity.setEtapa1Salva(false);
            ((ConfiguracaoInicialActivity) getActivity()).saveEtapaFirebase(1);
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.nomeSalao.getWindowToken(), 0);
            if (!ConfiguracaoInicialActivity.isEtapa2Preenchida()){
                ((ConfiguracaoInicialActivity) getActivity()).getmViewPager().setCurrentItem(1);
            }else if (!ConfiguracaoInicialActivity.isEtapa3Preenchida()){
                ((ConfiguracaoInicialActivity) getActivity()).getmViewPager().setCurrentItem(2);
            }else {
                ((ConfiguracaoInicialActivity) getActivity()).showProgressDialog(true);
            }
        }else {
            this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
        }
    }

    public void aplicarDadosFormulario(){
        if (ConfiguracaoInicialActivity.isCadastroComplementarObtido() && ConfiguracaoInicialActivity.isFuncionamentoSalaoObtido() && fragmentFuncionamentoSalaoAtivo){
            if (ConfiguracaoInicialActivity.getCadastroComplementar() != null && ConfiguracaoInicialActivity.getCadastroComplementar().getNome() != null && !ConfiguracaoInicialActivity.getCadastroComplementar().getNome().isEmpty()){
                this.nomeSalao.setText(ConfiguracaoInicialActivity.getCadastroComplementar().getNome());
            }
            if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao() != null){
                if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.SEGUNDA)){
                    this.segunda.setChecked(true);
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre().isEmpty()){
                        this.abreSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre());
                    }else{
                        this.abreSegunda.setText("--:--");
                    }
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha().isEmpty()){
                        this.fechaSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha());
                    }
                    else{
                        this.fechaSegunda.setText("--:--");
                    }
                }else {
                    this.segunda.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.segunda);

                if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.TERCA)){
                    this.terca.setChecked(true);
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre().isEmpty()){
                        this.abreTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre());
                    }else{
                        this.abreTerca.setText("--:--");
                    }
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha().isEmpty()){
                        this.fechaTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha());
                    }else{
                        this.fechaTerca.setText("--:--");
                    }
                }else {
                    this.terca.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.terca);

                if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.QUARTA)){
                    this.quarta.setChecked(true);
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre().isEmpty()){
                        this.abreQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre());
                    }else{
                        this.abreQuarta.setText("--:--");
                    }
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha().isEmpty()){
                        this.fechaQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha());
                    }else{
                        this.fechaQuarta.setText("--:--");
                    }
                }else {
                    this.quarta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.quarta);

                if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.QUINTA)){
                    this.quinta.setChecked(true);
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre().isEmpty()){
                        this.abreQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre());
                    }else{
                        this.abreQuinta.setText("--:--");
                    }
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha().isEmpty()){
                        this.fechaQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha());
                    }else{
                        this.fechaQuinta.setText("--:--");
                    }
                }else {
                    this.quinta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.quinta);

                if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.SEXTA)){
                    this.sexta.setChecked(true);
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre().isEmpty()){
                        this.abreSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre());
                    }else{
                        this.abreSexta.setText("--:--");
                    }
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha().isEmpty()){
                        this.fechaSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha());
                    }else{
                        this.fechaSexta.setText("--:--");
                    }
                }else {
                    this.sexta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.sexta);

                if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.SABADO)){
                    this.sabado.setChecked(true);
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre().isEmpty()){
                        this.abreSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre());
                    }else{
                        this.abreSabado.setText("--:--");
                    }
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha().isEmpty()){
                        this.fechaSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha());
                    }else{
                        this.fechaSabado.setText("--:--");
                    }
                }else {
                    this.sabado.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.sabado);

                if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.DOMINGO)){
                    this.domingo.setChecked(true);
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre().isEmpty()){
                        this.abreDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre());
                    }else{
                        this.abreDomingo.setText("--:--");
                    }
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha() != null && !ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha().isEmpty()){
                        this.fechaDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha());
                    }else{
                        this.fechaDomingo.setText("--:--");
                    }
                }else {
                    this.domingo.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.domingo);
            }
            liberarPreenchimento();
        }
    }

    public void diaSelecionado(CheckBox checkBox){
        switch (checkBox.getId()){
            case R.id.segunda:
                if (checkBox.isChecked()){
                    abreSegunda.setVisibility(View.VISIBLE);
                    fechaSegunda.setVisibility(View.VISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.SEGUNDA,this.abreSegunda.getText().toString(),this.fechaSegunda.getText().toString()));
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SEGUNDA);
                }else{
                    abreSegunda.setVisibility(View.INVISIBLE);
                    fechaSegunda.setVisibility(View.INVISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.SEGUNDA);
                    ((ConfiguracaoInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.SEGUNDA);
                }
                break;
            case R.id.terca:
                if (checkBox.isChecked()){
                    abreTerca.setVisibility(View.VISIBLE);
                    fechaTerca.setVisibility(View.VISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.TERCA,this.abreTerca.getText().toString(),this.fechaTerca.getText().toString()));
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SEGUNDA);
                }else {
                    abreTerca.setVisibility(View.INVISIBLE);
                    fechaTerca.setVisibility(View.INVISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.TERCA);
                    ((ConfiguracaoInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.TERCA);
                }
                break;
            case R.id.quarta:
                if (checkBox.isChecked()){
                    abreQuarta.setVisibility(View.VISIBLE);
                    fechaQuarta.setVisibility(View.VISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.QUARTA,this.abreQuarta.getText().toString(),this.fechaQuarta.getText().toString()));
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.QUARTA);
                }else{
                    abreQuarta.setVisibility(View.INVISIBLE);
                    fechaQuarta.setVisibility(View.INVISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.QUARTA);
                    ((ConfiguracaoInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.QUARTA);
                }
                break;
            case R.id.quinta:
                if (checkBox.isChecked()){
                    abreQuinta.setVisibility(View.VISIBLE);
                    fechaQuinta.setVisibility(View.VISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.QUINTA,this.abreQuinta.getText().toString(),this.fechaQuinta.getText().toString()));
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.QUINTA);
                }else {
                    abreQuinta.setVisibility(View.INVISIBLE);
                    fechaQuinta.setVisibility(View.INVISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.QUINTA);
                    ((ConfiguracaoInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.QUINTA);
                }
                break;
            case R.id.sexta:
                if (checkBox.isChecked()){
                    abreSexta.setVisibility(View.VISIBLE);
                    fechaSexta.setVisibility(View.VISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.SEXTA,this.abreSexta.getText().toString(),this.fechaSexta.getText().toString()));
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SEXTA);
                }else {
                    abreSexta.setVisibility(View.INVISIBLE);
                    fechaSexta.setVisibility(View.INVISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.SEXTA);
                    ((ConfiguracaoInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.SEXTA);
                }
                break;
            case R.id.sabado:
                if (checkBox.isChecked()){
                    abreSabado.setVisibility(View.VISIBLE);
                    fechaSabado.setVisibility(View.VISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.SABADO,this.abreSabado.getText().toString(),this.fechaSabado.getText().toString()));
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SABADO);
                }else {
                    abreSabado.setVisibility(View.INVISIBLE);
                    fechaSabado.setVisibility(View.INVISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.SABADO);
                    ((ConfiguracaoInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.SABADO);
                }
                break;
            case R.id.domingo:
                if (checkBox.isChecked()){
                    abreDomingo.setVisibility(View.VISIBLE);
                    fechaDomingo.setVisibility(View.VISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.DOMINGO,this.abreDomingo.getText().toString(),this.fechaDomingo.getText().toString()));
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.DOMINGO);
                }else {
                    abreDomingo.setVisibility(View.INVISIBLE);
                    fechaDomingo.setVisibility(View.INVISIBLE);
                    ConfiguracaoInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.DOMINGO);
                    ((ConfiguracaoInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.DOMINGO);
                }
                break;
            default:
                break;
        }
    }

    public void setHorarioTextView(int idView, String texto){
        switch (idView){
            case R.id.abre_segunda:
                this.abreSegunda.setText(texto);
                break;
            case R.id.fecha_segunda:
                this.fechaSegunda.setText(texto);
                break;
            case R.id.abre_terca:
                this.abreTerca.setText(texto);
                break;
            case R.id.fecha_terca:
                this.fechaTerca.setText(texto);
                break;
            case R.id.abre_quarta:
                this.abreQuarta.setText(texto);
                break;
            case R.id.fecha_quarta:
                this.fechaQuarta.setText(texto);
                break;
            case R.id.abre_quinta:
                this.abreQuinta.setText(texto);
                break;
            case R.id.fecha_quinta:
                this.fechaQuinta.setText(texto);
                break;
            case R.id.abre_sexta:
                this.abreSexta.setText(texto);
                break;
            case R.id.fecha_sexta:
                this.fechaSexta.setText(texto);
                break;
            case R.id.abre_sabado:
                this.abreSabado.setText(texto);
                break;
            case R.id.fecha_sabado:
                this.fechaSabado.setText(texto);
                break;
            case R.id.abre_domingo:
                this.abreDomingo.setText(texto);
                break;
            case R.id.fecha_domingo:
                this.fechaDomingo.setText(texto);
                break;
            default:
                break;
        }
    }

    private void aplicaVisibilidadeHorarios(View view){
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

    private void liberarPreenchimento(){
        if (!formularioLiberado){
            if (!(ConfiguracaoInicialActivity.isEtapa1Preenchida() && !ConfiguracaoInicialActivity.isEtapa1Salva())){
                this.fabFuncionamento.setVisibility(View.VISIBLE);
                this.fabFuncionamento.setClickable(true);
            }
            this.labelHorario.setVisibility(View.VISIBLE);
            this.formFuncionamento.setClickable(true);
            this.formFuncionamento.setVisibility(View.VISIBLE);
            this.progressFuncionamento.setVisibility(View.INVISIBLE);
            this.nomeSalao.setVisibility(View.VISIBLE);
            this.formNomeSalao.setVisibility(View.VISIBLE);
            formularioLiberado = true;
            ((ConfiguracaoInicialActivity)getActivity()).manterObjetosAtualizados();
        }
    }

    private boolean formularioIsValid(){
        if (this.nomeSalao.getText().toString().isEmpty() || this.nomeSalao.getText().toString().matches("[^\\S]+")){
            showToast("Preencher o nome do salão!");
            return false;
        }else{
            if (this.segunda.isChecked() || this.terca.isChecked() || this.quarta.isChecked() || this.quinta.isChecked() ||
                    this.sexta.isChecked() || this.sabado.isChecked() || this.domingo.isChecked()){
                if (this.segunda.isChecked()){
                    if (this.abreSegunda.getText().equals("--:--") || this.abreSegunda.getText().toString().isEmpty()){
                        showToast("Preencher a abertura de segunda!");
                        return false;
                    }else if (this.fechaSegunda.getText().equals("--:--") || this.fechaSegunda.getText().toString().isEmpty()){
                        showToast("Preencher o fechamento de segunda!");
                        return false;
                    }
                }
                if (this.terca.isChecked()){
                    if (this.abreTerca.getText().equals("--:--") || this.abreTerca.getText().toString().isEmpty()){
                        showToast("Preencher a abertura de terça!");
                        return false;
                    }else if (this.fechaTerca.getText().equals("--:--") || this.fechaTerca.getText().toString().isEmpty()) {
                        showToast("Preencher o fechamento de terça!");
                        return false;
                    }
                }
                if (this.quarta.isChecked()){
                    if (this.abreQuarta.getText().equals("--:--") || this.abreQuarta.getText().toString().isEmpty()){
                        showToast("Preencher a abertura de quarta!");
                        return false;
                    }else if (this.fechaQuarta.getText().equals("--:--") || this.fechaQuarta.getText().toString().isEmpty()){
                        showToast("Preencher o fechamento de quarta!");
                        return false;
                    }
                }
                if (this.quinta.isChecked()){
                    if (this.abreQuinta.getText().equals("--:--") || this.abreQuinta.getText().toString().isEmpty()){
                        showToast("Preencher a abertura de quinta!");
                        return false;
                    }else if (this.fechaQuinta.getText().equals("--:--") || this.fechaQuinta.getText().toString().isEmpty()){
                        showToast("Preencher o fechamento de quinta!");
                        return false;
                    }
                }
                if (this.sexta.isChecked()){
                    if (this.abreSexta.getText().equals("--:--") || this.abreSexta.getText().toString().isEmpty()){
                        showToast("Preencher a abertura de sexta!");
                        return false;
                    }else if (this.fechaSexta.getText().equals("--:--") || this.fechaSexta.getText().toString().isEmpty()){
                        showToast("Preencher o fechamento de sexta!");
                        return false;
                    }
                }
                if (this.sabado.isChecked()){
                    if (this.abreSabado.getText().equals("--:--") || this.abreSabado.getText().toString().isEmpty()){
                        showToast("Preencher a abertura de sábado!");
                        return false;
                    }else if (this.fechaSabado.getText().equals("--:--") || this.fechaSabado.getText().toString().isEmpty()){
                        showToast("Preencher o fechamento de sábado!");
                        return false;
                    }
                }
                if (this.domingo.isChecked()){
                    if (this.abreDomingo.getText().equals("--:--") || this.abreDomingo.getText().toString().isEmpty()){
                        showToast("Preencher a abertura de domingo!");
                        return false;
                    }else if (this.fechaDomingo.getText().equals("--:--") || this.fechaDomingo.getText().toString().isEmpty()){
                        showToast("Preencher o fechamento de domingo!");
                        return false;
                    }
                }
                return true;
            }else {
                showToast("Selecione ao menos um dia!");
                return false;
            }
        }
    }

    private void showToast( String message ){
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    public void liberarFab(){
        this.fabFuncionamento.setVisibility(View.VISIBLE);
        this.fabFuncionamento.setClickable(true);
    }


    //Getters and Setters
    public static String getTITULO() {
        return TITULO;
    }

    public static boolean isFragmentFuncionamentoSalaoAtivo() {
        return fragmentFuncionamentoSalaoAtivo;
    }
}

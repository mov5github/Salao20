package com.example.lucas.salao20.fragments.configuracaoInicial.salao;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.lucas.salao20.adapters.RecyclerAdapter;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.example.lucas.salao20.geral.geral.Servico;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialSalaoFuncionamento extends Fragment{
    //ENUM
    private static final String TITULO = "Funcionamento";

    //HANDLER
    private Handler handler;

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
    private static boolean fragmentFuncionamentoSalaoAtivo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("frag","onCreateView");
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_funcionamento,container,false);
        this.handler = new Handler();
        initControles();
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
        iniciarFormulario();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("frag","onStop");
        fragmentFuncionamentoSalaoAtivo = false;
        this.handler.removeCallbacksAndMessages(null);
    }

    private void initViews(View view){
        progressFuncionamento = (ProgressBar) view.findViewById(R.id.progress_fragment_funcionamento);
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
        formFuncionamento.setClickable(false);
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
    }

    private void initControles(){
        fragmentFuncionamentoSalaoAtivo = false;
    }

    private void proximaEtapa(){
        /*if (formularioIsValid()){
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
        }*/
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
                    ((ConfiguracaoInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.TERCA);
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

    private void iniciarFormulario(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                segunda.setChecked(false);
                aplicaVisibilidadeHorarios(segunda);
                terca.setChecked(false);
                aplicaVisibilidadeHorarios(terca);
                quarta.setChecked(false);
                aplicaVisibilidadeHorarios(quarta);
                quinta.setChecked(false);
                aplicaVisibilidadeHorarios(quinta);
                sexta.setChecked(false);
                aplicaVisibilidadeHorarios(sexta);
                sabado.setChecked(false);
                aplicaVisibilidadeHorarios(sabado);
                domingo.setChecked(false);
                aplicaVisibilidadeHorarios(domingo);

                if (ConfiguracaoInicialActivity.getFuncionamentoSalao() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao() != null){
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().size() > 0){
                        for (String key : ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet()){
                            switch (key){
                                case DiasENUM.SEGUNDA:
                                    segunda.setChecked(true);
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre().length() > 0){
                                        abreSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre());
                                    }
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha().length() > 0){
                                        fechaSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha());
                                    }
                                    aplicaVisibilidadeHorarios(segunda);
                                    break;
                                case DiasENUM.TERCA:
                                    terca.setChecked(true);
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre().length() > 0){
                                        abreTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre());
                                    }
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha().length() > 0){
                                        fechaTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha());
                                    }
                                    aplicaVisibilidadeHorarios(terca);
                                    break;
                                case DiasENUM.QUARTA:
                                    quarta.setChecked(true);
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre().length() > 0){
                                        abreQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre());
                                    }
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha().length() > 0){
                                        fechaQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha());
                                    }
                                    aplicaVisibilidadeHorarios(quarta);
                                    break;
                                case DiasENUM.QUINTA:
                                    quinta.setChecked(true);
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre().length() > 0){
                                        abreQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre());
                                    }
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha().length() > 0){
                                        fechaQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha());
                                    }
                                    aplicaVisibilidadeHorarios(quinta);
                                    break;
                                case DiasENUM.SEXTA:
                                    sexta.setChecked(true);
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre().length() > 0){
                                        abreSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre());
                                    }
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha().length() > 0){
                                        fechaSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha());
                                    }
                                    aplicaVisibilidadeHorarios(sexta);
                                    break;
                                case DiasENUM.SABADO:
                                    sabado.setChecked(true);
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre().length() > 0){
                                        abreSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre());
                                    }
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha().length() > 0){
                                        fechaSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha());
                                    }
                                    aplicaVisibilidadeHorarios(sabado);
                                    break;
                                case DiasENUM.DOMINGO:
                                    domingo.setChecked(true);
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre().length() > 0){
                                        abreDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getAbre());
                                    }
                                    if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha().length() > 0){
                                        fechaDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(key).getFecha());
                                    }
                                    aplicaVisibilidadeHorarios(domingo);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    liberarFormulario();
                }else{
                    Log.i("testeteste","iniciarFormulario funcionamentoSalao == null");
                }
                if (ConfiguracaoInicialActivity.getCadastroComplementar() != null && ConfiguracaoInicialActivity.getCadastroComplementar().getNome() != null && ConfiguracaoInicialActivity.getCadastroComplementar().getNome().length() > 0){
                    nomeSalao.setText(ConfiguracaoInicialActivity.getCadastroComplementar().getNome());
                }else{
                    nomeSalao.setText("");
                }
            }
        });
    }

    public void funcionamentoAdicionado(final String dia){
        if(this.handler != null){
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().containsKey(dia)){
                        switch (dia){
                            case DiasENUM.SEGUNDA:
                                segunda.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreSegunda.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaSegunda.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(segunda);
                                break;
                            case DiasENUM.TERCA:
                                terca.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreTerca.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaTerca.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(terca);
                                break;
                            case DiasENUM.QUARTA:
                                quarta.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreQuarta.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaQuarta.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(quarta);
                                break;
                            case DiasENUM.QUINTA:
                                quinta.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreQuinta.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaQuinta.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(quinta);
                                break;
                            case DiasENUM.SEXTA:
                                sexta.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreSexta.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaSexta.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(sexta);
                                break;
                            case DiasENUM.SABADO:
                                sabado.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreSabado.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaSabado.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(sabado);
                                break;
                            case DiasENUM.DOMINGO:
                                domingo.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreDomingo.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaDomingo.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(domingo);
                                break;
                            default:
                                break;
                        }
                    }
                    liberarFormulario();
                }
            });
        }
    }

    public void funcionamentoRemovido(final String dia){
        if(this.handler != null){
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    switch (dia){
                        case DiasENUM.SEGUNDA:
                            segunda.setChecked(false);
                            aplicaVisibilidadeHorarios(segunda);
                            break;
                        case DiasENUM.TERCA:
                            terca.setChecked(false);
                            aplicaVisibilidadeHorarios(terca);
                            break;
                        case DiasENUM.QUARTA:
                            quarta.setChecked(false);
                            aplicaVisibilidadeHorarios(quarta);
                            break;
                        case DiasENUM.QUINTA:
                            quinta.setChecked(false);
                            aplicaVisibilidadeHorarios(quinta);
                            break;
                        case DiasENUM.SEXTA:
                            sexta.setChecked(false);
                            aplicaVisibilidadeHorarios(sexta);
                            break;
                        case DiasENUM.SABADO:
                            sabado.setChecked(false);
                            aplicaVisibilidadeHorarios(sabado);
                            break;
                        case DiasENUM.DOMINGO:
                            domingo.setChecked(false);
                            aplicaVisibilidadeHorarios(domingo);
                            break;
                        default:
                            break;
                    }
                    liberarFab();
                }
            });
        }
    }

    public void funcionamentoAlterado(final String dia){
        if(this.handler != null){
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().containsKey(dia)){
                        switch (dia){
                            case DiasENUM.SEGUNDA:
                                segunda.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreSegunda.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaSegunda.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaSegunda.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(segunda);
                                break;
                            case DiasENUM.TERCA:
                                terca.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreTerca.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaTerca.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaTerca.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(terca);
                                break;
                            case DiasENUM.QUARTA:
                                quarta.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreQuarta.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaQuarta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaQuarta.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(quarta);
                                break;
                            case DiasENUM.QUINTA:
                                quinta.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreQuinta.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaQuinta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaQuinta.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(quinta);
                                break;
                            case DiasENUM.SEXTA:
                                sexta.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreSexta.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaSexta.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaSexta.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(sexta);
                                break;
                            case DiasENUM.SABADO:
                                sabado.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreSabado.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaSabado.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaSabado.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(sabado);
                                break;
                            case DiasENUM.DOMINGO:
                                domingo.setChecked(true);
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre().length() > 0){
                                    abreDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getAbre());
                                }else{
                                    abreDomingo.setText("--:--");
                                }
                                if(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha() != null && ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha().length() > 0){
                                    fechaDomingo.setText(ConfiguracaoInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(dia).getFecha());
                                }else{
                                    fechaDomingo.setText("--:--");
                                }
                                aplicaVisibilidadeHorarios(domingo);
                                break;
                            default:
                                break;
                        }
                    }else{
                        switch (dia){
                            case DiasENUM.SEGUNDA:
                                segunda.setChecked(false);
                                aplicaVisibilidadeHorarios(segunda);
                                break;
                            case DiasENUM.TERCA:
                                terca.setChecked(false);
                                aplicaVisibilidadeHorarios(terca);
                                break;
                            case DiasENUM.QUARTA:
                                quarta.setChecked(false);
                                aplicaVisibilidadeHorarios(quarta);
                                break;
                            case DiasENUM.QUINTA:
                                quinta.setChecked(false);
                                aplicaVisibilidadeHorarios(quinta);
                                break;
                            case DiasENUM.SEXTA:
                                sexta.setChecked(false);
                                aplicaVisibilidadeHorarios(sexta);
                                break;
                            case DiasENUM.SABADO:
                                sabado.setChecked(false);
                                aplicaVisibilidadeHorarios(sabado);
                                break;
                            case DiasENUM.DOMINGO:
                                domingo.setChecked(false);
                                aplicaVisibilidadeHorarios(domingo);
                                break;
                            default:
                                break;
                        }
                    }
                    liberarFab();
                }
            });
        }
    }

    public void nomeAtualizado(){
        if (ConfiguracaoInicialActivity.getCadastroComplementar() != null && ConfiguracaoInicialActivity.getCadastroComplementar().getNome() != null && ConfiguracaoInicialActivity.getCadastroComplementar().getNome().length() > 0){
            this.nomeSalao.setText(ConfiguracaoInicialActivity.getCadastroComplementar().getNome());
        }
    }

    public void liberarFormulario(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                formFuncionamento.setClickable(true);
                formFuncionamento.setVisibility(View.VISIBLE);
                formNomeSalao.setVisibility(View.VISIBLE);
                progressFuncionamento.setVisibility(View.INVISIBLE);
                liberarFab();
            }
        });
    }

    private void liberarFab(){
        if (this.nomeSalao.getText().toString().length() > 0 || this.segunda.isChecked() || this.terca.isChecked() || this.quarta.isChecked() || this.quinta.isChecked() || this.sexta.isChecked() || this.sabado.isChecked() || this.domingo.isChecked()){
            this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
        }else{
            this.fabFuncionamento.setClickable(false);
            this.fabFuncionamento.setVisibility(View.INVISIBLE);
        }
    }


    //Getters and Setters
    public static String getTITULO() {
        return TITULO;
    }

    public static boolean isFragmentFuncionamentoSalaoAtivo() {
        return fragmentFuncionamentoSalaoAtivo;
    }
}

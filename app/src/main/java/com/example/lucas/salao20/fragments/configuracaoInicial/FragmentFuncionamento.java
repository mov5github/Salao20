package com.example.lucas.salao20.fragments.configuracaoInicial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.activitys.CadastroInicialActivity;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentFuncionamento extends Fragment{
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
        View view = inflater.inflate(R.layout.fragment_funcionamento,container,false);
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
                fabFuncionamento.setVisibility(View.INVISIBLE);
                fabFuncionamento.setClickable(false);
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
            /*this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
            CadastroInicialActivity.setEtapaFuncionamentoPreenchida(true);
            CadastroInicialActivity.setEtapaFuncionamentoSalvo(false);
            CadastroInicialActivity.atualizarGeral();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.nomeSalao.getWindowToken(), 0);
            if (!CadastroInicialActivity.isEtapaServicosPreenchida()){
                ((CadastroInicialActivity) getActivity()).getmViewPager().setCurrentItem(1);
            }else if (!CadastroInicialActivity.isEtapaProfissionaisPreenchida()){
                ((CadastroInicialActivity) getActivity()).getmViewPager().setCurrentItem(2);
            }else {
                if (CadastroInicialActivity.isEtapaFuncionamentoSalvo() || CadastroInicialActivity.isEtapaServicosSalvo() || CadastroInicialActivity.isEtapaProfissionaisSalvo()){
                    ((CadastroInicialActivity) getActivity()).callHomeActivity();
                }else {
                    ((CadastroInicialActivity) getActivity()).showProgressDialog(true);
                }
            }*/

        }else {
            this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
        }
    }

    public void aplicarDadosFormulario(){
        if (CadastroInicialActivity.isCadastroComplementarObtido() && CadastroInicialActivity.isFuncionamentoSalaoObtido() && fragmentFuncionamentoSalaoAtivo){
            if (CadastroInicialActivity.getCadastroComplementar() != null && CadastroInicialActivity.getCadastroComplementar().getNome() != null && !CadastroInicialActivity.getCadastroComplementar().getNome().isEmpty()){
                this.nomeSalao.setText(CadastroInicialActivity.getCadastroComplementar().getNome());
            }
            if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao() != null){
                if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.SEGUNDA)){
                    this.segunda.setChecked(true);
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre().isEmpty()){
                        this.abreSegunda.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre());
                    }
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha().isEmpty()){
                        this.fechaSegunda.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha());
                    }
                }else {
                    this.segunda.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.segunda);

                if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.TERCA)){
                    this.terca.setChecked(true);
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre().isEmpty()){
                        this.abreTerca.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre());
                    }
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha().isEmpty()){
                        this.fechaTerca.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha());
                    }
                }else {
                    this.terca.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.terca);

                if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.QUARTA)){
                    this.quarta.setChecked(true);
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre().isEmpty()){
                        this.abreQuarta.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre());
                    }
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha().isEmpty()){
                        this.fechaQuarta.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha());
                    }
                }else {
                    this.quarta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.quarta);

                if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.QUINTA)){
                    this.quinta.setChecked(true);
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre().isEmpty()){
                        this.abreQuinta.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre());
                    }
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha().isEmpty()){
                        this.fechaQuinta.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha());
                    }
                }else {
                    this.quinta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.quinta);

                if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.SEXTA)){
                    this.sexta.setChecked(true);
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre().isEmpty()){
                        this.abreSexta.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre());
                    }
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha().isEmpty()){
                        this.fechaSexta.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha());
                    }
                }else {
                    this.sexta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.sexta);

                if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.SABADO)){
                    this.sabado.setChecked(true);
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre().isEmpty()){
                        this.abreSabado.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre());
                    }
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha().isEmpty()){
                        this.fechaSabado.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha());
                    }
                }else {
                    this.sabado.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.sabado);

                if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().keySet().contains(DiasENUM.DOMINGO)){
                    this.domingo.setChecked(true);
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre().isEmpty()){
                        this.abreDomingo.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre());
                    }
                    if (CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha() != null && !CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha().isEmpty()){
                        this.fechaDomingo.setText(CadastroInicialActivity.getFuncionamentoSalao().getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha());
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
                    CadastroInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.SEGUNDA,this.abreSegunda.getText().toString(),this.fechaSegunda.getText().toString()));
                    ((CadastroInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SEGUNDA);
                }else{
                    abreSegunda.setVisibility(View.INVISIBLE);
                    fechaSegunda.setVisibility(View.INVISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.SEGUNDA);
                    ((CadastroInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.SEGUNDA);
                }
                break;
            case R.id.terca:
                if (checkBox.isChecked()){
                    abreTerca.setVisibility(View.VISIBLE);
                    fechaTerca.setVisibility(View.VISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.TERCA,this.abreTerca.getText().toString(),this.fechaTerca.getText().toString()));
                    ((CadastroInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SEGUNDA);
                }else {
                    abreTerca.setVisibility(View.INVISIBLE);
                    fechaTerca.setVisibility(View.INVISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.TERCA);
                    ((CadastroInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.TERCA);
                }
                break;
            case R.id.quarta:
                if (checkBox.isChecked()){
                    abreQuarta.setVisibility(View.VISIBLE);
                    fechaQuarta.setVisibility(View.VISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.QUARTA,this.abreQuarta.getText().toString(),this.fechaQuarta.getText().toString()));
                    ((CadastroInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.QUARTA);
                }else{
                    abreQuarta.setVisibility(View.INVISIBLE);
                    fechaQuarta.setVisibility(View.INVISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.QUARTA);
                    ((CadastroInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.QUARTA);
                }
                break;
            case R.id.quinta:
                if (checkBox.isChecked()){
                    abreQuinta.setVisibility(View.VISIBLE);
                    fechaQuinta.setVisibility(View.VISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.QUINTA,this.abreQuinta.getText().toString(),this.fechaQuinta.getText().toString()));
                    ((CadastroInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.QUINTA);
                }else {
                    abreQuinta.setVisibility(View.INVISIBLE);
                    fechaQuinta.setVisibility(View.INVISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.QUINTA);
                    ((CadastroInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.QUINTA);
                }
                break;
            case R.id.sexta:
                if (checkBox.isChecked()){
                    abreSexta.setVisibility(View.VISIBLE);
                    fechaSexta.setVisibility(View.VISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.SEXTA,this.abreSexta.getText().toString(),this.fechaSexta.getText().toString()));
                    ((CadastroInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SEXTA);
                }else {
                    abreSexta.setVisibility(View.INVISIBLE);
                    fechaSexta.setVisibility(View.INVISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.SEXTA);
                    ((CadastroInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.SEXTA);
                }
                break;
            case R.id.sabado:
                if (checkBox.isChecked()){
                    abreSabado.setVisibility(View.VISIBLE);
                    fechaSabado.setVisibility(View.VISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.SABADO,this.abreSabado.getText().toString(),this.fechaSabado.getText().toString()));
                    ((CadastroInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.SABADO);
                }else {
                    abreSabado.setVisibility(View.INVISIBLE);
                    fechaSabado.setVisibility(View.INVISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.SABADO);
                    ((CadastroInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.SABADO);
                }
                break;
            case R.id.domingo:
                if (checkBox.isChecked()){
                    abreDomingo.setVisibility(View.VISIBLE);
                    fechaDomingo.setVisibility(View.VISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().addFuncionamento(new Funcionamento(DiasENUM.DOMINGO,this.abreDomingo.getText().toString(),this.fechaDomingo.getText().toString()));
                    ((CadastroInicialActivity) getActivity()).adicionaFuncionamentoFirebase(DiasENUM.DOMINGO);
                }else {
                    abreDomingo.setVisibility(View.INVISIBLE);
                    fechaDomingo.setVisibility(View.INVISIBLE);
                    CadastroInicialActivity.getFuncionamentoSalao().removerFuncionamento(DiasENUM.DOMINGO);
                    ((CadastroInicialActivity) getActivity()).removeFuncionamentoFirebase(DiasENUM.DOMINGO);
                }
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
            this.fabFuncionamento.setVisibility(View.VISIBLE);
            this.fabFuncionamento.setClickable(true);
            this.labelHorario.setVisibility(View.VISIBLE);
            this.formFuncionamento.setClickable(true);
            this.formFuncionamento.setVisibility(View.VISIBLE);
            this.progressFuncionamento.setVisibility(View.INVISIBLE);
            this.nomeSalao.setVisibility(View.VISIBLE);
            this.formNomeSalao.setVisibility(View.VISIBLE);
            formularioLiberado = true;
            ((CadastroInicialActivity)getActivity()).manterObjetosAtualizados();
        }
    }

    private boolean formularioIsValid(){
        if (this.nomeSalao.getText().toString().isEmpty()){
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


    //Getters and Setters
    public static String getTitulo() {
        return TITULO;
    }

    public static boolean isFragmentFuncionamentoSalaoAtivo() {
        return fragmentFuncionamentoSalaoAtivo;
    }
}

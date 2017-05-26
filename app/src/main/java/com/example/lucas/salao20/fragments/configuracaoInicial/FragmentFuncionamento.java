package com.example.lucas.salao20.fragments.configuracaoInicial;

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
import com.example.lucas.salao20.activitys.CadastroInicialActivity;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.geral.FuncionamentoSalao;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentFuncionamento extends Fragment implements DatabaseReference.CompletionListener{
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

    //FIREBASE REF
    private DatabaseReference refFuncionamento;
    private DatabaseReference refNomeSalao;

    //FIREBASE VEL
    private ValueEventListener valueEventListenerFuncionamento;
    private ValueEventListener valueEventListenerNomeSalao;


    //OBJETOS
    private static FuncionamentoSalao funcionamentoSalao;

    //CONTROLE
    private static boolean diasRecebidos;
    private static boolean nomeRecebido;





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("frag","onCreateView");
        View view = inflater.inflate(R.layout.fragment_funcionamento,container,false);
        initViews(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("frag","onCreate");
        if (funcionamentoSalao == null){
            funcionamentoSalao = new FuncionamentoSalao();
        }
        initFirebase();
        initControles();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("frag","onStart");
        if (this.refFuncionamento != null){
            this.refFuncionamento.addValueEventListener(this.valueEventListenerFuncionamento);
        }
        if (this.refNomeSalao != null){
            this.refNomeSalao.addValueEventListener(this.valueEventListenerNomeSalao);
        }
        /*if (this.refFuncionamento != null){
           this.refFuncionamento.addChildEventListener(this.childEventListenerFuncionamento);
        }
        if (this.refNomeSalao != null){
            this.refNomeSalao.addChildEventListener(this.childEventListenerNomeSalao);
        }*/
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("frag","onStop");
        removerFirebase();
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError == null){
            CadastroInicialActivity.setEtapaFuncionamentoSalvo(true);
            CadastroInicialActivity.notificarSalvamentoConcluido();
        }else {
            this.refNomeSalao.setValue(this.nomeSalao.getText().toString(), this);
        }
    }

    private void initViews(View view){
        progressFuncionamento = (ProgressBar) view.findViewById(R.id.progress_fragment_funcionamento) ;
        fabFuncionamento = (FloatingActionButton) view.findViewById(R.id.fab_fragment_funcionamento);
        fabFuncionamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabFuncionamento.setVisibility(View.INVISIBLE);
                fabFuncionamento.setClickable(false);
                proximaEtapa();
            }
        });
        formFuncionamento = (ScrollView) view.findViewById(R.id.form_funcionamento);
        formNomeSalao = (TextInputLayout) view.findViewById(R.id.form_nome_salao);
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

    private void initFirebase(){
        this.mAuth = FirebaseAuth.getInstance();
        if (this.mAuth.getCurrentUser() != null && !this.mAuth.getCurrentUser().getUid().isEmpty()){
            if (this.refFuncionamento == null){
                this.refFuncionamento = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.FUNCIONAMENTO);
            }
            if (this.refNomeSalao == null){
                this.refNomeSalao = LibraryClass.getFirebase().child(GeralENUM.USERS).child(mAuth.getCurrentUser().getUid()).child(GeralENUM.CADASTRO_COMPLEMENTAR).child(GeralENUM.NOME);
            }
        }

        if (this.valueEventListenerFuncionamento == null){
            this.valueEventListenerFuncionamento = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    funcionamentoSalao.setFuncionamentoDoSalao(new HashMap<String, Funcionamento>());
                    if (dataSnapshot.exists()){
                        Funcionamento funcionamento;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            funcionamento = new Funcionamento();
                            funcionamento.setDia(child.getKey());
                            if (child.hasChild(DiasENUM.ABRE)){
                                funcionamento.setAbre(child.child(DiasENUM.ABRE).getValue(String.class));
                            }
                            if (child.hasChild(DiasENUM.FECHA)){
                                funcionamento.setFecha(child.child(DiasENUM.FECHA).getValue(String.class));
                            }
                            funcionamentoSalao.addFuncionamento(funcionamento);
                        }
                    }
                    diasRecebidos = true;
                    atualizarFormulario();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }

        if (this.valueEventListenerNomeSalao == null){
            this.valueEventListenerNomeSalao = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        nomeSalao.setText(dataSnapshot.getValue(String.class));
                    }
                    nomeRecebido = true;
                    liberarPreenchimento();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
        }

        /*if (this.childEventListenerFuncionamento == null){
            this.childEventListenerFuncionamento = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i("fire1","childEventListenerFuncionamento");
                    if (dataSnapshot.exists()){
                        Log.i("fire1","childEventListenerFuncionamento existe");
                        Funcionamento funcionamento;
                        for (DataSnapshot child : dataSnapshot.getChildren()) {
                            Log.i("fire1","childEventListenerFuncionamento for");
                            funcionamento = new Funcionamento();
                            funcionamento.setDia(child.getKey());
                            if (child.hasChild(DiasENUM.ABRE)){
                                funcionamento.setAbre(child.child(DiasENUM.ABRE).getValue(String.class));
                            }
                            if (child.hasChild(DiasENUM.FECHA)){
                                funcionamento.setFecha(child.child(DiasENUM.FECHA).getValue(String.class));
                            }
                            funcionamentoSalao.addFuncionamento(funcionamento);
                        }
                    }
                    diasRecebidos = true;
                    atualizarFormulario();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        if (this.childEventListenerNomeSalao == null){
            this.childEventListenerNomeSalao = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i("fire1","childEventListenerNomeSalao");
                    if (dataSnapshot.exists()){
                        Log.i("fire1","childEventListenerNomeSalao existe");
                        nomeSalao.setText(dataSnapshot.getValue(String.class));
                    }
                    nomeRecebido = true;
                    atualizarFormulario();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }*/
    }

    private void initControles(){
        diasRecebidos = false;
        nomeRecebido = false;
    }

    private void removerFirebase(){
        if (this.refFuncionamento != null){
            this.refFuncionamento.removeEventListener(this.valueEventListenerFuncionamento);
        }
        if (this.refNomeSalao != null){
            this.refNomeSalao.removeEventListener(this.valueEventListenerNomeSalao);
        }
        /*if (this.refFuncionamento != null){
           this.refFuncionamento.removeEventListener(this.childEventListenerFuncionamento);
        }
        if (this.refNomeSalao != null){
            this.refNomeSalao.removeEventListener(this.childEventListenerNomeSalao);
        }*/
    }

    private void proximaEtapa(){
        if (formularioIsValid()){
            this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
            CadastroInicialActivity.setEtapaFuncionamentoPreenchida(true);
            CadastroInicialActivity.setEtapaFuncionamentoSalvo(false);
            atualizarGeral();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.nomeSalao.getWindowToken(), 0);
            if (!CadastroInicialActivity.isEtapaServicosPreenchida()){
                ((CadastroInicialActivity) getActivity()).getmViewPager().setCurrentItem(1);
            }else if (!CadastroInicialActivity.isEtapaProfissionaisPreenchida()){
                ((CadastroInicialActivity) getActivity()).getmViewPager().setCurrentItem(2);
            }else {
                ((CadastroInicialActivity) getActivity()).aguardarSalvar();
            }

        }else {
            this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
        }
    }

    private void atualizarFormulario(){
            if (funcionamentoSalao.getFuncionamentoDoSalao() != null){
                if (funcionamentoSalao.getFuncionamentoDoSalao().keySet().contains(DiasENUM.SEGUNDA)){
                    this.segunda.setChecked(true);
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre().isEmpty()){
                        this.abreSegunda.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre());
                    }
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha().isEmpty()){
                        this.fechaSegunda.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha());
                    }
                }else {
                    this.segunda.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.segunda);

                if (funcionamentoSalao.getFuncionamentoDoSalao().keySet().contains(DiasENUM.TERCA)){
                    this.terca.setChecked(true);
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre().isEmpty()){
                        this.abreTerca.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre());
                    }
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha().isEmpty()){
                        this.fechaTerca.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha());
                    }
                }else {
                    this.terca.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.terca);

                if (funcionamentoSalao.getFuncionamentoDoSalao().keySet().contains(DiasENUM.QUARTA)){
                    this.quarta.setChecked(true);
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre().isEmpty()){
                        this.abreQuarta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre());
                    }
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha().isEmpty()){
                        this.fechaQuarta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha());
                    }
                }else {
                    this.quarta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.quarta);

                if (funcionamentoSalao.getFuncionamentoDoSalao().keySet().contains(DiasENUM.QUINTA)){
                    this.quinta.setChecked(true);
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre().isEmpty()){
                        this.abreQuinta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre());
                    }
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha().isEmpty()){
                        this.fechaQuinta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha());
                    }
                }else {
                    this.quinta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.quinta);

                if (funcionamentoSalao.getFuncionamentoDoSalao().keySet().contains(DiasENUM.SEXTA)){
                    this.sexta.setChecked(true);
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre().isEmpty()){
                        this.abreSexta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre());
                    }
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha().isEmpty()){
                        this.fechaSexta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha());
                    }
                }else {
                    this.sexta.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.sexta);

                if (funcionamentoSalao.getFuncionamentoDoSalao().keySet().contains(DiasENUM.SABADO)){
                    this.sabado.setChecked(true);
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre().isEmpty()){
                        this.abreSabado.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre());
                    }
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha().isEmpty()){
                        this.fechaSabado.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha());
                    }
                }else {
                    this.sabado.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.sabado);

                if (funcionamentoSalao.getFuncionamentoDoSalao().keySet().contains(DiasENUM.DOMINGO)){
                    this.domingo.setChecked(true);
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre().isEmpty()){
                        this.abreDomingo.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre());
                    }
                    if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha().isEmpty()){
                        this.fechaDomingo.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha());
                    }
                }else {
                    this.domingo.setChecked(false);
                }
                aplicaVisibilidadeHorarios(this.domingo);
            }
            liberarPreenchimento();
    }

    public void diaSelecionado(CheckBox checkBox){
        switch (checkBox.getId()){
            case R.id.segunda:
                if (checkBox.isChecked()){
                    abreSegunda.setVisibility(View.VISIBLE);
                    fechaSegunda.setVisibility(View.VISIBLE);
                    funcionamentoSalao.addFuncionamento(new Funcionamento(DiasENUM.SEGUNDA,this.abreSegunda.getText().toString(),this.fechaSegunda.getText().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(DiasENUM.SEGUNDA, funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).toMap());
                    this.refFuncionamento.updateChildren(childUpdates);
                }else{
                    abreSegunda.setVisibility(View.INVISIBLE);
                    fechaSegunda.setVisibility(View.INVISIBLE);
                    funcionamentoSalao.removerFuncionamento(DiasENUM.SEGUNDA);
                    this.refFuncionamento.child(DiasENUM.SEGUNDA).removeValue();
                }
                break;
            case R.id.terca:
                if (checkBox.isChecked()){
                    abreTerca.setVisibility(View.VISIBLE);
                    fechaTerca.setVisibility(View.VISIBLE);
                    funcionamentoSalao.addFuncionamento(new Funcionamento(DiasENUM.TERCA,this.abreTerca.getText().toString(),this.fechaTerca.getText().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(DiasENUM.TERCA, funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).toMap());
                    this.refFuncionamento.updateChildren(childUpdates);
                }else {
                    abreTerca.setVisibility(View.INVISIBLE);
                    fechaTerca.setVisibility(View.INVISIBLE);
                    funcionamentoSalao.removerFuncionamento(DiasENUM.TERCA);
                    this.refFuncionamento.child(DiasENUM.TERCA).removeValue();
                }
                break;
            case R.id.quarta:
                if (checkBox.isChecked()){
                    abreQuarta.setVisibility(View.VISIBLE);
                    fechaQuarta.setVisibility(View.VISIBLE);
                    funcionamentoSalao.addFuncionamento(new Funcionamento(DiasENUM.QUARTA,this.abreQuarta.getText().toString(),this.fechaQuarta.getText().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(DiasENUM.QUARTA, funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).toMap());
                    this.refFuncionamento.updateChildren(childUpdates);
                }else{
                    abreQuarta.setVisibility(View.INVISIBLE);
                    fechaQuarta.setVisibility(View.INVISIBLE);
                    funcionamentoSalao.removerFuncionamento(DiasENUM.QUARTA);
                    this.refFuncionamento.child(DiasENUM.QUARTA).removeValue();
                }
                break;
            case R.id.quinta:
                if (checkBox.isChecked()){
                    abreQuinta.setVisibility(View.VISIBLE);
                    fechaQuinta.setVisibility(View.VISIBLE);
                    funcionamentoSalao.addFuncionamento(new Funcionamento(DiasENUM.QUINTA,this.abreQuinta.getText().toString(),this.fechaQuinta.getText().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(DiasENUM.QUINTA, funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).toMap());
                    this.refFuncionamento.updateChildren(childUpdates);
                }else {
                    abreQuinta.setVisibility(View.INVISIBLE);
                    fechaQuinta.setVisibility(View.INVISIBLE);
                    funcionamentoSalao.removerFuncionamento(DiasENUM.QUINTA);
                    this.refFuncionamento.child(DiasENUM.QUINTA).removeValue();
                }
                break;
            case R.id.sexta:
                if (checkBox.isChecked()){
                    abreSexta.setVisibility(View.VISIBLE);
                    fechaSexta.setVisibility(View.VISIBLE);
                    funcionamentoSalao.addFuncionamento(new Funcionamento(DiasENUM.SEXTA,this.abreSexta.getText().toString(),this.fechaSexta.getText().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(DiasENUM.SEXTA, funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).toMap());
                    this.refFuncionamento.updateChildren(childUpdates);
                }else {
                    abreSexta.setVisibility(View.INVISIBLE);
                    fechaSexta.setVisibility(View.INVISIBLE);
                    funcionamentoSalao.removerFuncionamento(DiasENUM.SEXTA);
                    this.refFuncionamento.child(DiasENUM.SEXTA).removeValue();
                }
                break;
            case R.id.sabado:
                if (checkBox.isChecked()){
                    abreSabado.setVisibility(View.VISIBLE);
                    fechaSabado.setVisibility(View.VISIBLE);
                    funcionamentoSalao.addFuncionamento(new Funcionamento(DiasENUM.SABADO,this.abreSabado.getText().toString(),this.fechaSabado.getText().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(DiasENUM.SABADO, funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).toMap());
                    this.refFuncionamento.updateChildren(childUpdates);
                }else {
                    abreSabado.setVisibility(View.INVISIBLE);
                    fechaSabado.setVisibility(View.INVISIBLE);
                    funcionamentoSalao.removerFuncionamento(DiasENUM.SABADO);
                    this.refFuncionamento.child(DiasENUM.SABADO).removeValue();
                }
                break;
            case R.id.domingo:
                if (checkBox.isChecked()){
                    abreDomingo.setVisibility(View.VISIBLE);
                    fechaDomingo.setVisibility(View.VISIBLE);
                    funcionamentoSalao.addFuncionamento(new Funcionamento(DiasENUM.DOMINGO,this.abreDomingo.getText().toString(),this.fechaDomingo.getText().toString()));
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(DiasENUM.DOMINGO, funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).toMap());
                    this.refFuncionamento.updateChildren(childUpdates);
                }else {
                    abreDomingo.setVisibility(View.INVISIBLE);
                    fechaDomingo.setVisibility(View.INVISIBLE);
                    funcionamentoSalao.removerFuncionamento(DiasENUM.DOMINGO);
                    this.refFuncionamento.child(DiasENUM.DOMINGO).removeValue();
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
        if (nomeRecebido && diasRecebidos){
            this.fabFuncionamento.setVisibility(View.VISIBLE);
            this.fabFuncionamento.setClickable(true);
            this.labelHorario.setVisibility(View.VISIBLE);
            this.formFuncionamento.setClickable(true);
            this.formFuncionamento.setVisibility(View.VISIBLE);
            this.progressFuncionamento.setVisibility(View.INVISIBLE);
            this.nomeSalao.setVisibility(View.VISIBLE);
            this.formNomeSalao.setVisibility(View.VISIBLE);
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

    private void atualizarGeral(){
        if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SEGUNDA)){
            this.refFuncionamento.child(DiasENUM.SEGUNDA).removeValue();
        }
        if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.TERCA)){
            this.refFuncionamento.child(DiasENUM.TERCA).removeValue();
        }
        if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.QUARTA)){
            this.refFuncionamento.child(DiasENUM.QUARTA).removeValue();
        }
        if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.QUINTA)){
            this.refFuncionamento.child(DiasENUM.QUINTA).removeValue();
        }
        if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SEXTA)){
            this.refFuncionamento.child(DiasENUM.SEXTA).removeValue();
        }
        if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SABADO)){
            this.refFuncionamento.child(DiasENUM.SABADO).removeValue();
        }
        if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.DOMINGO)){
            this.refFuncionamento.child(DiasENUM.DOMINGO).removeValue();
        }
        this.refNomeSalao.setValue(this.nomeSalao.getText().toString(), this);
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
}

package com.example.lucas.salao20.fragments.configuracaoInicial.salao;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.activitys.ConfiguracaoInicialSalaoActivity;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.DiasENUM;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.geral.Funcionamento;
import com.example.lucas.salao20.geral.salao.FuncionamentoSalao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialSalaoFuncionamento extends Fragment{


    //ENUM
    private static final String TITULO = "Funcionamento";

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;

    //HANDLER
    private Handler handler;

    //VIEWS
    private ProgressBar progressFuncionamento;
    private FloatingActionButton fabFuncionamento;
    private ScrollView formFuncionamento;
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

    //TIMEPICKER
    private TimePickerDialog timePickerDialogAbertura;
    private TimePickerDialog timePickerDialogFechamento;

    //AUXILIARES


    //CONTROLE
    private static boolean fragmentFuncionamentoSalaoAtivo;
    private boolean sincrozizarFuncionamento;

    //FIREBASE REF
    private DatabaseReference refServicosSalao;
    private DatabaseReference refProfissionaisSalao;
    private DatabaseReference refFuncionamentoSalao;
    private DatabaseReference refRaiz;

    //FIREBASE VEL
    private ValueEventListener valueEventListenerFuncionamentoSalao;
    private ChildEventListener childEventListenerFuncionamentoSalao;

    //OBJETOS
    private FuncionamentoSalao funcionamentoSalao = null;

    //RUNABLES
    private Runnable runnableIniciarFormulario;





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("frag","onCreate");
        Log.i("script","frag funcionamneto onCreate");

        if (this.handler == null){
            this.handler = new Handler();
        }
        if (this.mAuth == null){
            this.mAuth = FirebaseAuth.getInstance();
        }

        initControles();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("frag","onCreateView");
        Log.i("script","frag funcionamneto onCreateView");
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_funcionamento,container,false);
        initViews(view);
        this.sincrozizarFuncionamento = true;
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("frag","onStart");
        Log.i("script","frag funcionamneto onStart");
        fragmentFuncionamentoSalaoAtivo = true;


        if (funcionamentoSalao == null){
            funcionamentoSalao = new FuncionamentoSalao();
        }
        funcionamentoSalao.setFuncionamentoDoSalao(new HashMap<String, Funcionamento>());

        sincronizarFormulario();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("frag","onStop");
        Log.i("script","frag funcionamneto onStop");
        fragmentFuncionamentoSalaoAtivo = false;
        this.handler.removeCallbacksAndMessages(null);
        if (this.refFuncionamentoSalao != null && this.valueEventListenerFuncionamentoSalao != null){
            this.refFuncionamentoSalao.removeEventListener(this.valueEventListenerFuncionamentoSalao);
        }
        if (this.refFuncionamentoSalao != null && this.childEventListenerFuncionamentoSalao != null){
            this.refFuncionamentoSalao.removeEventListener(this.childEventListenerFuncionamentoSalao);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("script","frag funcionamneto onDestroy");
    }

    private void initViews(View view){
        progressFuncionamento = (ProgressBar) view.findViewById(R.id.progress_fragment_funcionamento);
        formFuncionamento = (ScrollView) view.findViewById(R.id.form_funcionamento);
        formFuncionamento.setVisibility(View.INVISIBLE);
        formFuncionamento.setClickable(false);
        labelHorario = (TextView) view.findViewById(R.id.label_horario_funcionamento);
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
        fabFuncionamento = (FloatingActionButton) view.findViewById(R.id.fab_fragment_funcionamento);
        fabFuncionamento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("script","onClick");
                proximaEtapa();
            }
        });
        fabFuncionamento.setVisibility(View.INVISIBLE);
        fabFuncionamento.setClickable(false);
    }

    private void initControles(){
        fragmentFuncionamentoSalaoAtivo = false;
        this.sincrozizarFuncionamento = true;
    }

    private void sincronizarFormulario(){
        this.formFuncionamento.setClickable(false);
        this.formFuncionamento.setVisibility(View.INVISIBLE);
        this.progressFuncionamento.setVisibility(View.VISIBLE);
        this.fabFuncionamento.setVisibility(View.INVISIBLE);
        this.fabFuncionamento.setClickable(false);
        ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);

        if (this.refFuncionamentoSalao == null){
            this.refFuncionamentoSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(FuncionamentoSalao.getFUNCIONAMENTO_DO_SALAO());
            this.refFuncionamentoSalao.keepSynced(true);
        }


        if (this.childEventListenerFuncionamentoSalao == null){
            this.childEventListenerFuncionamentoSalao = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()){
                        funcionamentoAdded(dataSnapshot);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()){
                        funcionamentoChanged(dataSnapshot);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        funcionamentoRemoved(dataSnapshot);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        if (this.valueEventListenerFuncionamentoSalao == null){
            this.valueEventListenerFuncionamentoSalao = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.i("script","valueEventListenerFuncionamentoSalao onDataChange");
                    if(!dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0){
                        Log.i("script","valueEventListenerFuncionamentoSalao !dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0");
                        iniciarFormulario();
                    }else{
                        Log.i("script","valueEventListenerFuncionamentoSalao dataSnapshot.exists()");
                        sincronizarFuncionamento(dataSnapshot);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //TODO
                    Log.i("script","valueEventListenerFuncionamentoSalao onCancelled");
                }
            };
        }

        if(this.refFuncionamentoSalao != null){
            this.refFuncionamentoSalao.addListenerForSingleValueEvent(this.valueEventListenerFuncionamentoSalao);
        }
    }

    private void sincronizarFuncionamento(final DataSnapshot dataSnapshot){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()){
                    Log.i("script","runnableSincronizarFuncionamneto dataSnapshot = "+dataSnapshot.toString());
                    for (DataSnapshot auxDataSnapshot : dataSnapshot.getChildren()){
                        Log.i("script","auxDataSnapshot = "+auxDataSnapshot.toString());
                        Funcionamento funcionamento = new Funcionamento();
                        funcionamento.setDia(auxDataSnapshot.getKey());
                        if (auxDataSnapshot.hasChild(DiasENUM.ABRE)){
                            funcionamento.setAbre(auxDataSnapshot.child(DiasENUM.ABRE).getValue(String.class));
                        }else {
                            funcionamento.setAbre(GeralENUM.PADRAO_HORARIO);
                        }
                        if (auxDataSnapshot.hasChild(DiasENUM.FECHA)){
                            funcionamento.setFecha(auxDataSnapshot.child(DiasENUM.FECHA).getValue(String.class));
                        }else {
                            funcionamento.setFecha(GeralENUM.PADRAO_HORARIO);
                        }
                        if (funcionamentoSalao != null){
                            funcionamentoSalao.addFuncionamento(funcionamento);
                        }
                    }
                    iniciarFormulario();
                }
            }
        });
    }

    private void iniciarFormulario(){
        if (this.runnableIniciarFormulario == null){
            this.runnableIniciarFormulario = new Runnable() {
                @Override
                public void run() {
                    if (funcionamentoSalao != null && funcionamentoSalao.getFuncionamentoDoSalao() != null){
                        //Atualizando Segunda
                        if (funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SEGUNDA)){
                            segunda.setChecked(true);
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                                abreSegunda.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre());
                            }else {
                                if (abreSegunda.getText() == null || !abreSegunda.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    abreSegunda.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaSegunda.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha());
                            }else {
                                if (fechaSegunda.getText() == null || !fechaSegunda.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    fechaSegunda.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                        }else {
                            segunda.setChecked(false);
                            if (abreSegunda.getText() == null || !abreSegunda.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                abreSegunda.setText(GeralENUM.PADRAO_HORARIO);
                            }
                            if (fechaSegunda.getText() == null || !fechaSegunda.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaSegunda.setText(GeralENUM.PADRAO_HORARIO);
                            }
                        }
                        aplicaVisibilidadeHorarios(segunda);
                        //Atualizando Terca
                        if (funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.TERCA)){
                            terca.setChecked(true);
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                                abreTerca.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre());
                            }else {
                                if (abreTerca.getText() == null || !abreTerca.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    abreTerca.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaTerca.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha());
                            }else {
                                if (fechaTerca.getText() == null || !fechaTerca.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    fechaTerca.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                        }else {
                            terca.setChecked(false);
                            if (abreTerca.getText() == null || !abreTerca.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                abreTerca.setText(GeralENUM.PADRAO_HORARIO);
                            }
                            if (fechaTerca.getText() == null || !fechaTerca.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaTerca.setText(GeralENUM.PADRAO_HORARIO);
                            }
                        }
                        aplicaVisibilidadeHorarios(terca);
                        //Atualizando Quarta
                        if (funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.QUARTA)){
                            quarta.setChecked(true);
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                                abreQuarta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre());
                            }else {
                                if (abreQuarta.getText() == null || !abreQuarta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    abreQuarta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaQuarta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha());
                            }else {
                                if (fechaQuarta.getText() == null || !fechaQuarta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    fechaQuarta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                        }else {
                            quarta.setChecked(false);
                            if (abreQuarta.getText() == null || !abreQuarta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                abreQuarta.setText(GeralENUM.PADRAO_HORARIO);
                            }
                            if (fechaQuarta.getText() == null || !fechaQuarta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaQuarta.setText(GeralENUM.PADRAO_HORARIO);
                            }
                        }
                        aplicaVisibilidadeHorarios(quarta);
                        //Atualizando Quinta
                        if (funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.QUINTA)){
                            quinta.setChecked(true);
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                                abreQuinta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre());
                            }else {
                                if (abreQuinta.getText() == null || !abreQuinta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    abreQuinta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaQuinta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha());
                            }else {
                                if (fechaQuinta.getText() == null || !fechaQuinta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    fechaQuinta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                        }else {
                            quinta.setChecked(false);
                            if (abreQuinta.getText() == null || !abreQuinta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                abreQuinta.setText(GeralENUM.PADRAO_HORARIO);
                            }
                            if (fechaQuinta.getText() == null || !fechaQuinta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaQuinta.setText(GeralENUM.PADRAO_HORARIO);
                            }
                        }
                        aplicaVisibilidadeHorarios(quinta);
                        //Atualizando Sexta
                        if (funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SEXTA)){
                            sexta.setChecked(true);
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                                abreSexta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre());
                            }else {
                                if (abreSexta.getText() == null || !abreSexta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    abreSexta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaSexta.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha());
                            }else {
                                if (fechaSexta.getText() == null || !fechaSexta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    fechaSexta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                        }else {
                            sexta.setChecked(false);
                            if (abreSexta.getText() == null || !abreSexta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                abreSexta.setText(GeralENUM.PADRAO_HORARIO);
                            }
                            if (fechaSexta.getText() == null || !fechaSexta.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaSexta.setText(GeralENUM.PADRAO_HORARIO);
                            }
                        }
                        aplicaVisibilidadeHorarios(sexta);
                        //Atualizando Sabado
                        if (funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SABADO)){
                            sabado.setChecked(true);
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                                abreSabado.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre());
                            }else {
                                if (abreSabado.getText() == null || !abreSabado.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    abreSabado.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaSabado.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha());
                            }else {
                                if (fechaSabado.getText() == null || !fechaSabado.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    fechaSabado.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                        }else {
                            sabado.setChecked(false);
                            if (abreSabado.getText() == null || !abreSabado.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                abreSabado.setText(GeralENUM.PADRAO_HORARIO);
                            }
                            if (fechaSabado.getText() == null || !fechaSabado.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaSabado.setText(GeralENUM.PADRAO_HORARIO);
                            }
                        }
                        aplicaVisibilidadeHorarios(sabado);
                        //Atualizando Domingo
                        if (funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.DOMINGO)){
                            domingo.setChecked(true);
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                                abreDomingo.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre());
                            }else {
                                if (abreDomingo.getText() == null || !abreDomingo.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    abreDomingo.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                            if (funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha() != null && !funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaDomingo.setText(funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha());
                            }else {
                                if (fechaDomingo.getText() == null || !fechaDomingo.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                    fechaDomingo.setText(GeralENUM.PADRAO_HORARIO);
                                }
                            }
                        }else {
                            domingo.setChecked(false);
                            if (abreDomingo.getText() == null || !abreDomingo.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                abreDomingo.setText(GeralENUM.PADRAO_HORARIO);
                            }
                            if (fechaDomingo.getText() == null || !fechaDomingo.getText().toString().equals(GeralENUM.PADRAO_HORARIO)){
                                fechaDomingo.setText(GeralENUM.PADRAO_HORARIO);
                            }
                        }
                        aplicaVisibilidadeHorarios(domingo);
                        liberarFormulario();
                    }
                    if (refFuncionamentoSalao != null){
                        refFuncionamentoSalao.addChildEventListener(childEventListenerFuncionamentoSalao);
                    }
                }
            };
        }
        this.handler.post(this.runnableIniciarFormulario);
    }

    private void liberarFormulario(){
        formFuncionamento.setClickable(true);
        formFuncionamento.setVisibility(View.VISIBLE);
        progressFuncionamento.setVisibility(View.INVISIBLE);
        alterarVisivilidadeFab(liberacaoDoFabIsValid());
    }

    private boolean liberacaoDoFabIsValid(){
        Log.i("script","liberacaoDoFabIsValid ");
        if (!this.segunda.isChecked() && !this.terca.isChecked() && !this.quarta.isChecked() && !this.quinta.isChecked() && !this.sexta.isChecked() && !this.sabado.isChecked() && !this.domingo.isChecked()){
            ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
            return false;
        }

        if (this.segunda.isChecked()) {
            Log.i("script","this.segunda.isChecked(");
            Log.i("script","abre segunda "+abreSegunda.getText());
            Log.i("script","fecha segumnda "+fechaSegunda.getText());
            if (this.abreSegunda.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaSegunda.getText().equals(GeralENUM.PADRAO_HORARIO)) {
                ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                Log.i("script","if");
                return false;
            }else {
                Log.i("script","else");
                if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SEGUNDA) || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getAbre().equals(GeralENUM.PADRAO_HORARIO)
                                                                                                 || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEGUNDA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                    Log.i("script","else if");
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                    return  false;
                }
            }
        }
        if (this.terca.isChecked()) {
            if (this.abreTerca.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaTerca.getText().equals(GeralENUM.PADRAO_HORARIO)) {
                ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                return false;
            }else {
                if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.TERCA) || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getAbre().equals(GeralENUM.PADRAO_HORARIO)
                        || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.TERCA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                    return  false;
                }
            }
        }
        if (this.quarta.isChecked()) {
            if (this.abreQuarta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaQuarta.getText().equals(GeralENUM.PADRAO_HORARIO)) {
                ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                return false;
            }else {
                if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.QUARTA) || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getAbre().equals(GeralENUM.PADRAO_HORARIO)
                        || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUARTA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                    return  false;
                }
            }
        }
        if (this.quinta.isChecked()) {
            if (this.abreQuinta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaQuinta.getText().equals(GeralENUM.PADRAO_HORARIO)) {
                ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                return false;
            }else {
                if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.QUINTA) || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getAbre().equals(GeralENUM.PADRAO_HORARIO)
                        || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.QUINTA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                    return  false;
                }
            }
        }
        if (this.sexta.isChecked()) {
            if (this.abreSexta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaSexta.getText().equals(GeralENUM.PADRAO_HORARIO)) {
                ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                return false;
            }else {
                if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SEXTA) || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getAbre().equals(GeralENUM.PADRAO_HORARIO)
                        || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SEXTA).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                    return  false;
                }
            }
        }
        if (this.sabado.isChecked()) {
            if (this.abreSabado.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaSabado.getText().equals(GeralENUM.PADRAO_HORARIO)) {
                ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                return false;
            }else {
                if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.SABADO) || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getAbre().equals(GeralENUM.PADRAO_HORARIO)
                        || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.SABADO).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                    return  false;
                }
            }
        }
        if (this.domingo.isChecked()) {
            if (this.abreDomingo.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaDomingo.getText().equals(GeralENUM.PADRAO_HORARIO)) {
                ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                return false;
            }else {
                if (!funcionamentoSalao.getFuncionamentoDoSalao().containsKey(DiasENUM.DOMINGO) || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getAbre().equals(GeralENUM.PADRAO_HORARIO)
                        || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha() == null || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha().isEmpty() || funcionamentoSalao.getFuncionamentoDoSalao().get(DiasENUM.DOMINGO).getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(false);
                    return  false;
                }
            }
        }
        ((ConfiguracaoInicialSalaoActivity)getActivity()).setFuncionamentoSalaoOk(true);
        return true;
    }

    private void alterarVisivilidadeFab(boolean visivel){
        Log.i("script","alterarVisivilidadeFab "+visivel);
        if (visivel){
            this.fabFuncionamento.setClickable(true);
            this.fabFuncionamento.setVisibility(View.VISIBLE);
        }else{
            this.fabFuncionamento.setClickable(false);
            this.fabFuncionamento.setVisibility(View.INVISIBLE);
        }
    }

    private void funcionamentoAdded(final DataSnapshot dataSnapshot){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (funcionamentoSalao != null && funcionamentoSalao.getFuncionamentoDoSalao() != null){
                    Funcionamento funcionamento = new Funcionamento();
                    funcionamento.setDia(dataSnapshot.getKey());
                    if (dataSnapshot.hasChild(DiasENUM.ABRE) && dataSnapshot.child(DiasENUM.ABRE).getValue(String.class) != null && !dataSnapshot.child(DiasENUM.ABRE).getValue(String.class).isEmpty()){
                        funcionamento.setAbre(dataSnapshot.child(DiasENUM.ABRE).getValue(String.class));
                    }else {
                        funcionamento.setAbre(GeralENUM.PADRAO_HORARIO);
                    }
                    if (dataSnapshot.hasChild(DiasENUM.FECHA) && dataSnapshot.child(DiasENUM.FECHA).getValue(String.class) != null && !dataSnapshot.child(DiasENUM.FECHA).getValue(String.class).isEmpty()){
                        funcionamento.setFecha(dataSnapshot.child(DiasENUM.FECHA).getValue(String.class));
                    }else {
                        funcionamento.setFecha(GeralENUM.PADRAO_HORARIO);
                    }
                    funcionamentoSalao.addFuncionamento(funcionamento);
                    if (funcionamento.getDia() != null && !funcionamento.getDia().isEmpty() && ((funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()) || (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()))){
                        switch (funcionamento.getDia()){
                            case DiasENUM.SEGUNDA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreSegunda.setText(funcionamento.getAbre());
                                }else {
                                    abreSegunda.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaSegunda.setText(funcionamento.getFecha());
                                }else {
                                    fechaSegunda.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                segunda.setChecked(true);
                                aplicaVisibilidadeHorarios(segunda);
                                break;
                            case DiasENUM.TERCA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreTerca.setText(funcionamento.getAbre());
                                }else {
                                    abreTerca.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaTerca.setText(funcionamento.getFecha());
                                }else {
                                    fechaTerca.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                terca.setChecked(true);
                                aplicaVisibilidadeHorarios(terca);
                                break;
                            case DiasENUM.QUARTA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreQuarta.setText(funcionamento.getAbre());
                                }else {
                                    abreQuarta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaQuarta.setText(funcionamento.getFecha());
                                }else {
                                    fechaQuarta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                quarta.setChecked(true);
                                aplicaVisibilidadeHorarios(quarta);
                                break;
                            case DiasENUM.QUINTA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreQuinta.setText(funcionamento.getAbre());
                                }else {
                                    abreQuinta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaQuinta.setText(funcionamento.getFecha());
                                }else {
                                    fechaQuinta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                quinta.setChecked(true);
                                aplicaVisibilidadeHorarios(quinta);
                                break;
                            case DiasENUM.SEXTA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreSexta.setText(funcionamento.getAbre());
                                }else {
                                    abreSexta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaSexta.setText(funcionamento.getFecha());
                                }else {
                                    fechaSexta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                sexta.setChecked(true);
                                aplicaVisibilidadeHorarios(sexta);
                                break;
                            case DiasENUM.SABADO:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreSabado.setText(funcionamento.getAbre());
                                }else {
                                    abreSabado.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaSabado.setText(funcionamento.getFecha());
                                }else {
                                    fechaSabado.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                sabado.setChecked(true);
                                aplicaVisibilidadeHorarios(sabado);
                                break;
                            case DiasENUM.DOMINGO:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreDomingo.setText(funcionamento.getAbre());
                                }else {
                                    abreDomingo.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaDomingo.setText(funcionamento.getFecha());
                                }else {
                                    fechaDomingo.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                domingo.setChecked(true);
                                aplicaVisibilidadeHorarios(domingo);
                                break;
                            default:
                                break;
                        }
                    }
                    alterarVisivilidadeFab(liberacaoDoFabIsValid());
                }
            }
        });
    }

    private void funcionamentoChanged(final DataSnapshot dataSnapshot){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("script","funcionamentoChanged dataSnapshot = "+dataSnapshot.toString());
                if (funcionamentoSalao != null && funcionamentoSalao.getFuncionamentoDoSalao() != null){
                    Funcionamento funcionamento = new Funcionamento();
                    funcionamento.setDia(dataSnapshot.getKey());
                    if (dataSnapshot.hasChild(DiasENUM.ABRE) && dataSnapshot.child(DiasENUM.ABRE).getValue(String.class) != null && !dataSnapshot.child(DiasENUM.ABRE).getValue(String.class).isEmpty()){
                        funcionamento.setAbre(dataSnapshot.child(DiasENUM.ABRE).getValue(String.class));
                    }else {
                        funcionamento.setAbre(GeralENUM.PADRAO_HORARIO);
                    }
                    if (dataSnapshot.hasChild(DiasENUM.FECHA) && dataSnapshot.child(DiasENUM.FECHA).getValue(String.class) != null && !dataSnapshot.child(DiasENUM.FECHA).getValue(String.class).isEmpty()){
                        funcionamento.setFecha(dataSnapshot.child(DiasENUM.FECHA).getValue(String.class));
                    }else {
                        funcionamento.setFecha(GeralENUM.PADRAO_HORARIO);
                    }
                    funcionamentoSalao.addFuncionamento(funcionamento);
                    if (funcionamento.getDia() != null && !funcionamento.getDia().isEmpty() && ((funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()) || (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()))){
                        switch (funcionamento.getDia()){
                            case DiasENUM.SEGUNDA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreSegunda.setText(funcionamento.getAbre());
                                }else {
                                    abreSegunda.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaSegunda.setText(funcionamento.getFecha());
                                }else {
                                    fechaSegunda.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                segunda.setChecked(true);
                                aplicaVisibilidadeHorarios(segunda);
                                break;
                            case DiasENUM.TERCA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreTerca.setText(funcionamento.getAbre());
                                }else {
                                    abreTerca.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaTerca.setText(funcionamento.getFecha());
                                }else {
                                    fechaTerca.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                terca.setChecked(true);
                                aplicaVisibilidadeHorarios(terca);
                                break;
                            case DiasENUM.QUARTA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreQuarta.setText(funcionamento.getAbre());
                                }else {
                                    abreQuarta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaQuarta.setText(funcionamento.getFecha());
                                }else {
                                    fechaQuarta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                quarta.setChecked(true);
                                aplicaVisibilidadeHorarios(quarta);
                                break;
                            case DiasENUM.QUINTA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreQuinta.setText(funcionamento.getAbre());
                                }else {
                                    abreQuinta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaQuinta.setText(funcionamento.getFecha());
                                }else {
                                    fechaQuinta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                quinta.setChecked(true);
                                aplicaVisibilidadeHorarios(quinta);
                                break;
                            case DiasENUM.SEXTA:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreSexta.setText(funcionamento.getAbre());
                                }else {
                                    abreSexta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaSexta.setText(funcionamento.getFecha());
                                }else {
                                    fechaSexta.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                sexta.setChecked(true);
                                aplicaVisibilidadeHorarios(sexta);
                                break;
                            case DiasENUM.SABADO:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreSabado.setText(funcionamento.getAbre());
                                }else {
                                    abreSabado.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaSabado.setText(funcionamento.getFecha());
                                }else {
                                    fechaSabado.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                sabado.setChecked(true);
                                aplicaVisibilidadeHorarios(sabado);
                                break;
                            case DiasENUM.DOMINGO:
                                if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty()){
                                    abreDomingo.setText(funcionamento.getAbre());
                                }else {
                                    abreDomingo.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty()){
                                    fechaDomingo.setText(funcionamento.getFecha());
                                }else {
                                    fechaDomingo.setText(GeralENUM.PADRAO_HORARIO);
                                }
                                domingo.setChecked(true);
                                aplicaVisibilidadeHorarios(domingo);
                                break;
                            default:
                                break;
                        }
                    }
                    alterarVisivilidadeFab(liberacaoDoFabIsValid());
                }
            }
        });
    }

    private void funcionamentoRemoved(final DataSnapshot dataSnapshot){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                Log.i("script","funcionamentoRemoved dataSnapshot = "+dataSnapshot.toString());
                if (dataSnapshot.getKey() != null && !dataSnapshot.getKey().isEmpty() && funcionamentoSalao != null && funcionamentoSalao.getFuncionamentoDoSalao() != null){
                    funcionamentoSalao.removerFuncionamento(dataSnapshot.getKey());
                    switch (dataSnapshot.getKey()){
                        case DiasENUM.SEGUNDA:
                            abreSegunda.setText(GeralENUM.PADRAO_HORARIO);
                            fechaSegunda.setText(GeralENUM.PADRAO_HORARIO);
                            segunda.setChecked(false);
                            aplicaVisibilidadeHorarios(segunda);
                            break;
                        case DiasENUM.TERCA:
                            abreTerca.setText(GeralENUM.PADRAO_HORARIO);
                            fechaTerca.setText(GeralENUM.PADRAO_HORARIO);
                            terca.setChecked(false);
                            aplicaVisibilidadeHorarios(terca);
                            break;
                        case DiasENUM.QUARTA:
                            abreQuarta.setText(GeralENUM.PADRAO_HORARIO);
                            fechaQuarta.setText(GeralENUM.PADRAO_HORARIO);
                            quarta.setChecked(false);
                            aplicaVisibilidadeHorarios(quarta);
                            break;
                        case DiasENUM.QUINTA:
                            abreQuinta.setText(GeralENUM.PADRAO_HORARIO);
                            fechaQuinta.setText(GeralENUM.PADRAO_HORARIO);
                            quinta.setChecked(false);
                            aplicaVisibilidadeHorarios(quinta);
                            break;
                        case DiasENUM.SEXTA:
                            abreSexta.setText(GeralENUM.PADRAO_HORARIO);
                            fechaSexta.setText(GeralENUM.PADRAO_HORARIO);
                            sexta.setChecked(false);
                            aplicaVisibilidadeHorarios(sexta);
                            break;
                        case DiasENUM.SABADO:
                            abreSabado.setText(GeralENUM.PADRAO_HORARIO);
                            fechaSabado.setText(GeralENUM.PADRAO_HORARIO);
                            sabado.setChecked(false);
                            aplicaVisibilidadeHorarios(sabado);
                            break;
                        case DiasENUM.DOMINGO:
                            abreDomingo.setText(GeralENUM.PADRAO_HORARIO);
                            fechaDomingo.setText(GeralENUM.PADRAO_HORARIO);
                            domingo.setChecked(false);
                            aplicaVisibilidadeHorarios(domingo);
                            break;
                        default:
                            break;
                    }
                    alterarVisivilidadeFab(liberacaoDoFabIsValid());
                }
            }
        });
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

    private void adicionaFuncionamentoFirebase(Funcionamento funcionamento){
        if (funcionamento != null && funcionamento.getDia() != null && !funcionamento.getDia().isEmpty() && ((funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty() && !funcionamento.getAbre().equals(GeralENUM.PADRAO_HORARIO)) || (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty() && !funcionamento.getFecha().equals(GeralENUM.PADRAO_HORARIO)))){
            Map<String,Object> updates = new HashMap<String,Object>();
            Map<String,Object> auxUpdates = new HashMap<String,Object>();
            if (funcionamento.getAbre() != null && !funcionamento.getAbre().isEmpty() && !funcionamento.getAbre().equals(GeralENUM.PADRAO_HORARIO)){
                auxUpdates.put(DiasENUM.ABRE,funcionamento.getAbre());
            }else {
                auxUpdates.put(DiasENUM.ABRE,null);
            }
            if (funcionamento.getFecha() != null && !funcionamento.getFecha().isEmpty() && !funcionamento.getFecha().equals(GeralENUM.PADRAO_HORARIO)){
                auxUpdates.put(DiasENUM.FECHA,funcionamento.getFecha());
            }else {
                auxUpdates.put(DiasENUM.FECHA,null);
            }
            updates.put(funcionamento.getDia(),auxUpdates);

            if (this.refFuncionamentoSalao == null && mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                this.refFuncionamentoSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(FuncionamentoSalao.getFUNCIONAMENTO_DO_SALAO());
                this.refFuncionamentoSalao.keepSynced(true);
            }

            this.refFuncionamentoSalao.updateChildren(updates);
        }
    }

    private void removeFuncionamentoFirebase(String dia){
        this.refFuncionamentoSalao.child(dia).removeValue();
    }


    //AUX CLIQUES
    public void diaSelecionado(final CheckBox checkBox){
        Log.i("script","diaSelecionado");
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                switch (checkBox.getId()){
                    case R.id.segunda:
                        Log.i("script","diaSelecionado SEGUNDA");

                        if (checkBox.isChecked()){
                            abreSegunda.setVisibility(View.VISIBLE);
                            fechaSegunda.setVisibility(View.VISIBLE);
                            adicionaFuncionamentoFirebase(new Funcionamento(DiasENUM.SEGUNDA,abreSegunda.getText().toString(),fechaSegunda.getText().toString()));
                        }else{
                            abreSegunda.setVisibility(View.INVISIBLE);
                            fechaSegunda.setVisibility(View.INVISIBLE);
                            removeFuncionamentoFirebase(DiasENUM.SEGUNDA);
                        }
                        break;
                    case R.id.terca:
                        Log.i("script","diaSelecionado TERCA");

                        if (checkBox.isChecked()){
                            abreTerca.setVisibility(View.VISIBLE);
                            fechaTerca.setVisibility(View.VISIBLE);
                            adicionaFuncionamentoFirebase(new Funcionamento(DiasENUM.TERCA,abreTerca.getText().toString(),fechaTerca.getText().toString()));
                        }else {
                            abreTerca.setVisibility(View.INVISIBLE);
                            fechaTerca.setVisibility(View.INVISIBLE);
                            removeFuncionamentoFirebase(DiasENUM.TERCA);
                        }
                        break;
                    case R.id.quarta:
                        if (checkBox.isChecked()){
                            abreQuarta.setVisibility(View.VISIBLE);
                            fechaQuarta.setVisibility(View.VISIBLE);
                            adicionaFuncionamentoFirebase(new Funcionamento(DiasENUM.QUARTA,abreQuarta.getText().toString(),fechaQuarta.getText().toString()));
                        }else{
                            abreQuarta.setVisibility(View.INVISIBLE);
                            fechaQuarta.setVisibility(View.INVISIBLE);
                            removeFuncionamentoFirebase(DiasENUM.QUARTA);
                        }
                        break;
                    case R.id.quinta:
                        if (checkBox.isChecked()){
                            abreQuinta.setVisibility(View.VISIBLE);
                            fechaQuinta.setVisibility(View.VISIBLE);
                            adicionaFuncionamentoFirebase(new Funcionamento(DiasENUM.QUINTA,abreQuinta.getText().toString(),fechaQuinta.getText().toString()));
                        }else {
                            abreQuinta.setVisibility(View.INVISIBLE);
                            fechaQuinta.setVisibility(View.INVISIBLE);
                            removeFuncionamentoFirebase(DiasENUM.QUINTA);
                        }
                        break;
                    case R.id.sexta:
                        if (checkBox.isChecked()){
                            abreSexta.setVisibility(View.VISIBLE);
                            fechaSexta.setVisibility(View.VISIBLE);
                            adicionaFuncionamentoFirebase(new Funcionamento(DiasENUM.SEXTA,abreSexta.getText().toString(),fechaSexta.getText().toString()));
                        }else {
                            abreSexta.setVisibility(View.INVISIBLE);
                            fechaSexta.setVisibility(View.INVISIBLE);
                            removeFuncionamentoFirebase(DiasENUM.SEXTA);
                        }
                        break;
                    case R.id.sabado:
                        if (checkBox.isChecked()){
                            abreSabado.setVisibility(View.VISIBLE);
                            fechaSabado.setVisibility(View.VISIBLE);
                            adicionaFuncionamentoFirebase(new Funcionamento(DiasENUM.SABADO,abreSabado.getText().toString(),fechaSabado.getText().toString()));
                        }else {
                            abreSabado.setVisibility(View.INVISIBLE);
                            fechaSabado.setVisibility(View.INVISIBLE);
                            removeFuncionamentoFirebase(DiasENUM.SABADO);
                        }
                        break;
                    case R.id.domingo:
                        if (checkBox.isChecked()){
                            abreDomingo.setVisibility(View.VISIBLE);
                            fechaDomingo.setVisibility(View.VISIBLE);
                            adicionaFuncionamentoFirebase(new Funcionamento(DiasENUM.DOMINGO,abreDomingo.getText().toString(),fechaDomingo.getText().toString()));
                        }else {
                            abreDomingo.setVisibility(View.INVISIBLE);
                            fechaDomingo.setVisibility(View.INVISIBLE);
                            removeFuncionamentoFirebase(DiasENUM.DOMINGO);
                        }
                        break;
                    default:
                        break;
                }
                alterarVisivilidadeFab(liberacaoDoFabIsValid());
            }
        });
    }

    public void definirHorarioAbertura(final View textView){
        Log.i("script","definirHorarioAbertura");
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (refFuncionamentoSalao == null && mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                    refFuncionamentoSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(FuncionamentoSalao.getFUNCIONAMENTO_DO_SALAO());
                    refFuncionamentoSalao.keepSynced(true);
                }

                timePickerDialogAbertura = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            String horario = "";
                            if (hourOfDay <= 9 && minute <= 9){
                                horario = (new StringBuilder().append("0").append(hourOfDay).append(":").append("0").append(minute)).toString();
                            }else if(hourOfDay <= 9){
                                horario = (new StringBuilder().append("0").append(hourOfDay).append(":").append(minute)).toString();
                            }else if(minute <= 9){
                                horario = (new StringBuilder().append(hourOfDay).append(":").append("0").append(minute)).toString();
                            }else{
                                horario = (new StringBuilder().append(hourOfDay).append(":").append(minute)).toString();
                            }
                            Log.i("script","horario == "+horario);

                            if (refFuncionamentoSalao != null){
                                switch (textView.getId()){
                                    case R.id.abre_segunda:
                                        Log.i("script"," R.id.abre_segunda");
                                        refFuncionamentoSalao.child(DiasENUM.SEGUNDA).child(DiasENUM.ABRE).setValue(horario);
                                        abreSegunda.setText(horario);
                                        break;
                                    case R.id.abre_terca:
                                        refFuncionamentoSalao.child(DiasENUM.TERCA).child(DiasENUM.ABRE).setValue(horario);
                                        abreTerca.setText(horario);
                                        break;
                                    case R.id.abre_quarta:
                                        refFuncionamentoSalao.child(DiasENUM.QUARTA).child(DiasENUM.ABRE).setValue(horario);
                                        abreQuarta.setText(horario);
                                        break;
                                    case R.id.abre_quinta:
                                        refFuncionamentoSalao.child(DiasENUM.QUINTA).child(DiasENUM.ABRE).setValue(horario);
                                        abreQuinta.setText(horario);
                                        break;
                                    case R.id.abre_sexta:
                                        refFuncionamentoSalao.child(DiasENUM.SEXTA).child(DiasENUM.ABRE).setValue(horario);
                                        abreSexta.setText(horario);
                                        break;
                                    case R.id.abre_sabado:
                                        refFuncionamentoSalao.child(DiasENUM.SABADO).child(DiasENUM.ABRE).setValue(horario);
                                        abreSabado.setText(horario);
                                        break;
                                    case R.id.abre_domingo:
                                        refFuncionamentoSalao.child(DiasENUM.DOMINGO).child(DiasENUM.ABRE).setValue(horario);
                                        abreDomingo.setText(horario);
                                        break;
                                    default:
                                        Log.i("script"," defaut");
                                        break;
                                }
                            }
                        }
                    }
                },0,0,true);
                timePickerDialogAbertura.show();
            }
        });
    }

    public void definirHorarioFechamento(final View textView) {
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (refFuncionamentoSalao == null && mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().getUid().isEmpty()){
                    refFuncionamentoSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(FuncionamentoSalao.getFUNCIONAMENTO_DO_SALAO());
                    refFuncionamentoSalao.keepSynced(true);
                }
                timePickerDialogFechamento = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (view.isShown()) {
                            String horario = "";
                            if (hourOfDay <= 9 && minute <= 9){
                                horario = (new StringBuilder().append("0").append(hourOfDay).append(":").append("0").append(minute)).toString();
                            }else if(hourOfDay <= 9){
                                horario = (new StringBuilder().append("0").append(hourOfDay).append(":").append(minute)).toString();
                            }else if(minute <= 9){
                                horario = (new StringBuilder().append(hourOfDay).append(":").append("0").append(minute)).toString();
                            }else{
                                horario = (new StringBuilder().append(hourOfDay).append(":").append(minute)).toString();
                            }

                            if (refFuncionamentoSalao != null){
                                switch (textView.getId()){
                                    case R.id.fecha_segunda:
                                        refFuncionamentoSalao.child(DiasENUM.SEGUNDA).child(DiasENUM.FECHA).setValue(horario);
                                        fechaSegunda.setText(horario);
                                        break;
                                    case R.id.fecha_terca:
                                        refFuncionamentoSalao.child(DiasENUM.TERCA).child(DiasENUM.FECHA).setValue(horario);
                                        fechaTerca.setText(horario);
                                        break;
                                    case R.id.fecha_quarta:
                                        refFuncionamentoSalao.child(DiasENUM.QUARTA).child(DiasENUM.FECHA).setValue(horario);
                                        fechaQuarta.setText(horario);
                                        break;
                                    case R.id.fecha_quinta:
                                        refFuncionamentoSalao.child(DiasENUM.QUINTA).child(DiasENUM.FECHA).setValue(horario);
                                        fechaQuinta.setText(horario);
                                        break;
                                    case R.id.fecha_sexta:
                                        refFuncionamentoSalao.child(DiasENUM.SEXTA).child(DiasENUM.FECHA).setValue(horario);
                                        fechaSexta.setText(horario);
                                        break;
                                    case R.id.fecha_sabado:
                                        refFuncionamentoSalao.child(DiasENUM.SABADO).child(DiasENUM.FECHA).setValue(horario);
                                        fechaSabado.setText(horario);
                                        break;
                                    case R.id.fecha_domingo:
                                        refFuncionamentoSalao.child(DiasENUM.DOMINGO).child(DiasENUM.FECHA).setValue(horario);
                                        fechaDomingo.setText(horario);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                },23,59,true);
                timePickerDialogFechamento.show();
            }
        });
    }

    private void proximaEtapa(){
        Log.i("script","clique");
        if (formularioPreenchidoCorretamente() && ((ConfiguracaoInicialSalaoActivity)getActivity()).isFuncionamentoSalaoOk()){
            if (!((ConfiguracaoInicialSalaoActivity)getActivity()).isServicosSalaoOk()){
                ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(1);
            }else if (!((ConfiguracaoInicialSalaoActivity)getActivity()).isProfissionaisSalaoOk()){
                ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(2);
            }else{
                ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(true);
                if (refServicosSalao == null){
                    refServicosSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(GeralENUM.SERVICOS_SALAO);
                }
                if (this.refFuncionamentoSalao == null){
                    this.refFuncionamentoSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(FuncionamentoSalao.getFUNCIONAMENTO_DO_SALAO());
                }
                if (refProfissionaisSalao == null){
                    refProfissionaisSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(GeralENUM.PROFISSIONAIS_DO_SALAO);
                }
                if (refRaiz == null){
                    refRaiz = LibraryClass.getFirebase();
                }

                refFuncionamentoSalao.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                            refServicosSalao.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                                        refProfissionaisSalao.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                                                    Map<String,Object> updates = new HashMap<String, Object>();
                                                    updates.put(GeralENUM.USERS+"/"+mAuth.getCurrentUser().getUid()+"/"+ CadastroBasico.getCADASTRO_BASICO()+"/"+CadastroBasico.getNIVEL_USUARIO(),3.0);
                                                    refRaiz.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                                        @Override
                                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                            if (databaseError == null){
                                                                ((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().setNivelUsuario(3.0);
                                                                ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                                                                ((ConfiguracaoInicialSalaoActivity)getActivity()).callHome();
                                                            }else{
                                                                if (isFragmentFuncionamentoSalaoAtivo()){
                                                                    showToast("Erro ao salvar tentar novamente.");
                                                                    ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                                                                }
                                                            }
                                                        }
                                                    });
                                                }else{
                                                    showToast("Erro ao salvar tentar novamente.");
                                                    ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(2);
                                                    ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                                if (isFragmentFuncionamentoSalaoAtivo()){
                                                    showToast("Erro ao salvar tentar novamente.");
                                                    ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                                                }
                                            }
                                        });
                                    }else{
                                        showToast("Erro ao salvar tentar novamente.");
                                        ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(1);
                                        ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    if (isFragmentFuncionamentoSalaoAtivo()){
                                        showToast("Erro ao salvar tentar novamente.");
                                        ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                                    }
                                }
                            });
                        }else{
                            showToast("Erro ao salvar tentar novamente.");
                            ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(0);
                            ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        if (isFragmentFuncionamentoSalaoAtivo()){
                            showToast("Erro ao salvar tentar novamente.");
                            ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                        }
                    }
                });
            }
        }
    }

    private boolean formularioPreenchidoCorretamente(){
        if (this.segunda.isChecked() || this.terca.isChecked() || this.quarta.isChecked() || this.quinta.isChecked() ||
                this.sexta.isChecked() || this.sabado.isChecked() || this.domingo.isChecked()){
            if (this.segunda.isChecked()){
                if (this.abreSegunda.getText().equals(GeralENUM.PADRAO_HORARIO) || this.abreSegunda.getText().toString().isEmpty()){
                    showToast("Preencher a abertura de segunda!");
                    return false;
                }else if (this.fechaSegunda.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaSegunda.getText().toString().isEmpty()){
                    showToast("Preencher o fechamento de segunda!");
                    return false;
                }
            }
            if (this.terca.isChecked()){
                if (this.abreTerca.getText().equals(GeralENUM.PADRAO_HORARIO) || this.abreTerca.getText().toString().isEmpty()){
                    showToast("Preencher a abertura de terça!");
                    return false;
                }else if (this.fechaTerca.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaTerca.getText().toString().isEmpty()) {
                    showToast("Preencher o fechamento de terça!");
                    return false;
                }
            }
            if (this.quarta.isChecked()){
                if (this.abreQuarta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.abreQuarta.getText().toString().isEmpty()){
                    showToast("Preencher a abertura de quarta!");
                    return false;
                }else if (this.fechaQuarta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaQuarta.getText().toString().isEmpty()){
                    showToast("Preencher o fechamento de quarta!");
                    return false;
                }
            }
            if (this.quinta.isChecked()){
                if (this.abreQuinta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.abreQuinta.getText().toString().isEmpty()){
                    showToast("Preencher a abertura de quinta!");
                    return false;
                }else if (this.fechaQuinta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaQuinta.getText().toString().isEmpty()){
                    showToast("Preencher o fechamento de quinta!");
                    return false;
                }
            }
            if (this.sexta.isChecked()){
                if (this.abreSexta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.abreSexta.getText().toString().isEmpty()){
                    showToast("Preencher a abertura de sexta!");
                    return false;
                }else if (this.fechaSexta.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaSexta.getText().toString().isEmpty()){
                    showToast("Preencher o fechamento de sexta!");
                    return false;
                }
            }
            if (this.sabado.isChecked()){
                if (this.abreSabado.getText().equals(GeralENUM.PADRAO_HORARIO) || this.abreSabado.getText().toString().isEmpty()){
                    showToast("Preencher a abertura de sábado!");
                    return false;
                }else if (this.fechaSabado.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaSabado.getText().toString().isEmpty()){
                    showToast("Preencher o fechamento de sábado!");
                    return false;
                }
            }
            if (this.domingo.isChecked()){
                if (this.abreDomingo.getText().equals(GeralENUM.PADRAO_HORARIO) || this.abreDomingo.getText().toString().isEmpty()){
                    showToast("Preencher a abertura de domingo!");
                    return false;
                }else if (this.fechaDomingo.getText().equals(GeralENUM.PADRAO_HORARIO) || this.fechaDomingo.getText().toString().isEmpty()){
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

    private void showToast( String message ){
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }



    //Getters and Setters
    public static String getTITULO() {
        return TITULO;
    }

    public static boolean isFragmentFuncionamentoSalaoAtivo() {
        return fragmentFuncionamentoSalaoAtivo;
    }
}

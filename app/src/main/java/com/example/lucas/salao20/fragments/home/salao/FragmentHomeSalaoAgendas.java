package com.example.lucas.salao20.fragments.home.salao;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.activitys.HomeSalaoActivity;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.geral.geral.CadastroComplementar;
import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;
import com.example.lucas.salao20.geral.salao.ProfissionaisSalao;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentHomeSalaoAgendas extends Fragment{
    //ENUM
    private static final String TITULO = "Agenda";

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;

    //HANDLER
    private Handler handler;

    //VIEWS
    private LinearLayout lLFiltros;
    private ProgressBar progressBar;
    private RecyclerView mRecyclerView;
    private TextView labelTituloAgendamentos;
    private TextView labelNaoHaAgendamentos;
    private TextView filtroDataInicial;
    private TextView filtroDataFinal;
    private Spinner spinnerProfissionais;

    //CONTROLES
    private static boolean fragmentHomeSalaoAgendaAtivo;
    private long qtdProfissionaisDoSalao;

    //ARRAYS
    private List<Servico> mList;
    private List<String> mListKeyIdAgendamentos;

    //ADAPTER
    private ArrayAdapter<String> arrayAdapterProfissionais;

    //DATAPICKER
    private DatePickerDialog dataPickerDialogInicial;
    private DatePickerDialog dataPickerDialogFinal;
    private SimpleDateFormat dateFormatter;
    private Calendar newCalendar;

    //FIREBASE REF
    private DatabaseReference refRaiz;

    //FIREBASE VEL
    private ValueEventListener valueEventListenerProfissionaisSalao;

    //OBJETOS
    private ProfissionaisSalao profissionaisSalao;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_salao_agendas,container,false);
        initViews(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.handler == null){
            this.handler = new Handler();
        }
        if (this.mAuth == null){
            this.mAuth = FirebaseAuth.getInstance();
        }
        initControles();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.fragmentHomeSalaoAgendaAtivo = true;
        sincronizarLista();
    }

    @Override
    public void onStop() {
        super.onStop();
        this.fragmentHomeSalaoAgendaAtivo = false;
    }


    private void initViews(View view){
        if (newCalendar == null){
            newCalendar = Calendar.getInstance();
        }
        if (dateFormatter == null){
            dateFormatter = new SimpleDateFormat("dd-MM-yyyy", new Locale("pt", "BR"));
        }
        this.progressBar = (ProgressBar)view.findViewById(R.id.progress_fragment_home_salao_agenda);
        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.recycler_view_fragment_home_salao_agenda);
        this.labelTituloAgendamentos = (TextView)view.findViewById(R.id.label_agendamentos_fragment_home_salao_agenda);
        this.labelNaoHaAgendamentos = (TextView)view.findViewById(R.id.label_nao_ha_agendamentos_fragment_home_salao_agenda);
        this.lLFiltros = (LinearLayout) view.findViewById(R.id.ll_filtros_fragment_home_salao_agenda) ;
        this.spinnerProfissionais = (Spinner)view.findViewById(R.id.spinner_filtro_profissionais_fragment_home_salao_agenda);
        this.filtroDataInicial = (TextView)view.findViewById(R.id.label_filtro_data_inicial_fragment_home_salao_agenda);
        this.filtroDataInicial.setText(dateFormatter.format(newCalendar.getTime()));
        this.filtroDataInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataPickerDialogInicial == null){
                    dataPickerDialogInicial = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            filtroDataInicial.setText(dateFormatter.format(newDate.getTime()));
                        }

                    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                }
                dataPickerDialogInicial.show();
            }
        });
        this.filtroDataFinal = (TextView)view.findViewById(R.id.label_filtro_data_final_fragment_home_salao_agenda);
        this.filtroDataFinal.setText(dateFormatter.format(newCalendar.getTime()));
        this.filtroDataFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dataPickerDialogFinal == null){
                    dataPickerDialogFinal = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            Calendar newDate = Calendar.getInstance();
                            newDate.set(year, monthOfYear, dayOfMonth);
                            filtroDataFinal.setText(dateFormatter.format(newDate.getTime()));
                        }

                    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                }
                dataPickerDialogFinal.show();            }
        });
    }

    private void initControles(){
        this.fragmentHomeSalaoAgendaAtivo = false;
        this.qtdProfissionaisDoSalao = 0;
    }

    private void sincronizarLista(){
        this.progressBar.setVisibility(View.VISIBLE);
        this.mRecyclerView.setVisibility(View.INVISIBLE);
        this.mRecyclerView.setClickable(false);
        this.labelTituloAgendamentos.setVisibility(View.INVISIBLE);
        this.labelNaoHaAgendamentos.setVisibility(View.INVISIBLE);
        buscarProfissionais();
       // this.lLFiltros.setClickable(false);
       // this.lLFiltros.setVisibility(View.INVISIBLE);
    }

    private void buscarProfissionais(){
        if (this.refRaiz == null){
            this.refRaiz = LibraryClass.getFirebase();
        }
        if (this.profissionaisSalao == null){
            this.profissionaisSalao = new ProfissionaisSalao();
        }
        this.profissionaisSalao.setProfissionais(new HashMap<String, Profissional>());

        if (this.valueEventListenerProfissionaisSalao == null){
            this.valueEventListenerProfissionaisSalao = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists() && dataSnapshot.getChildrenCount() > 0){
                        Log.i("script",dataSnapshot.getChildrenCount()+" kkk  -> "+dataSnapshot.toString());

                        qtdProfissionaisDoSalao = dataSnapshot.getChildrenCount();
                        for (DataSnapshot aux : dataSnapshot.getChildren()){
                            Log.i("script"," TTT  -> ");

                            final String metadtaUidProfissional = aux.getKey();
                            Profissional profissional = new Profissional();
                            profissional.setMetadataUidProfissional(aux.getKey());
                            profissionaisSalao.getProfissionais().put(aux.getKey(),profissional);
                            refRaiz.child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(aux.getKey()).child(GeralENUM.CADASTRO_COMPLEMENTAR).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    qtdProfissionaisDoSalao --;
                                    if (dataSnapshot.exists()){
                                        Log.i("script",metadtaUidProfissional+" JJJ  -> "+dataSnapshot.toString());
                                        if (dataSnapshot.hasChild(CadastroComplementar.getNOME_PROFISSIONAL())){
                                            profissionaisSalao.getProfissionais().get(metadtaUidProfissional).setNomeProfissional(dataSnapshot.child(CadastroComplementar.getNOME_PROFISSIONAL()).getValue(String.class));
                                        }
                                        if (dataSnapshot.hasChild(CadastroComplementar.getNICK_PROFISSIONAL())){
                                            profissionaisSalao.getProfissionais().get(metadtaUidProfissional).setNickProfissional(dataSnapshot.child(CadastroComplementar.getNICK_PROFISSIONAL()).getValue(String.class));
                                        }
                                    }
                                    if (qtdProfissionaisDoSalao == 0){
                                        //TODO ja achou todos profissionais
                                        Log.i("script","FIM");
                                        gerarSpinner();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }

                    }else{
                        //TODO nao ha profissionais neste salao
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (fragmentHomeSalaoAgendaAtivo){
                        buscarProfissionais();
                    }
                }
            };
        }

        this.refRaiz.child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((HomeSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(GeralENUM.PROFISSIONAIS_DO_SALAO).addListenerForSingleValueEvent(this.valueEventListenerProfissionaisSalao);

        ArrayList<String> profissionais = new ArrayList<String>();
        //this.adapter = new AdapterSpinnerIcones(getActivity(),icones);
       // this.spinnerIcones.setAdapter(adapter);
    }

    private void gerarSpinner(){
        this.spinnerProfissionais.setPrompt("cacxa");
        if (this.arrayAdapterProfissionais == null){
            this.profissionaisSalao.getProfissionais().keySet().toArray();
            String[] keyArray = this.profissionaisSalao.getProfissionais().keySet().toArray(new String[this.profissionaisSalao.getProfissionais().keySet().size()]);
            ArrayList<String> aLNomeProfissionais = new ArrayList<String>();
            aLNomeProfissionais.add("Todos os profissionais");
            for (String key : this.profissionaisSalao.getProfissionais().keySet()){
                aLNomeProfissionais.add(this.profissionaisSalao.getProfissionais().get(key).getNomeProfissional());
            }
            this.arrayAdapterProfissionais = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, aLNomeProfissionais);
            this.arrayAdapterProfissionais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            this.spinnerProfissionais.setAdapter(this.arrayAdapterProfissionais);
        }else {
            //TODO ATUALIZAR
        }
    }



    private void showToast( String message ){
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }

    //GETERS SETTERS
    public static String getTITULO() {
        return TITULO;
    }
}

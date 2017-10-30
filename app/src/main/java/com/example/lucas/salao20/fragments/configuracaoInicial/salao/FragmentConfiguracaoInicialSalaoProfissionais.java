package com.example.lucas.salao20.fragments.configuracaoInicial.salao;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.activitys.ConfiguracaoInicialSalaoActivity;
import com.example.lucas.salao20.adapters.RecyclerAdapterProfissionais;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.geral.geral.Acount;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.salao.FuncionamentoSalao;
import com.example.lucas.salao20.geral.salao.ProfissionaisSalao;
import com.example.lucas.salao20.interfaces.RecyclerViewOnClickListenerHack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialSalaoProfissionais extends Fragment implements RecyclerViewOnClickListenerHack {
    //ENUM
    private static final String TITULO = "Profissionais";

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;

    //HANDLER
    private Handler handler;

    private ProgressBar progressProfissionais;
    private ProgressBar progressBtnSalvar;
    private FloatingActionButton fabProfissionais;
    private EditText codigoUnicoProfissional;
    private Button buttonAdicionarProfissional;
    private TextView adicionarProfissionalSemCodigoUnico;
    private TextView labelSemProfissionais;

    //OBJETOS
    private ProfissionaisSalao profissionaisSalao;

    //RECYCLERVIEW
    private RecyclerView mRecyclerView;
    private List<Profissional> mList;
    private List<String> mListKeyMetadataUidProfissionais;

    //CONTROLES
    private static boolean fragmentProfissionaisSalaoAtivo;
    private List<String> listUidProfissionais;


    //FIREBASE REF
    private DatabaseReference refRaiz;
    private DatabaseReference refMetadaUidProfissional;
    private DatabaseReference refServicosSalao;
    private DatabaseReference refFuncionamentoSalao;
    private DatabaseReference refProfissionaisSalao;


    //QUERY
    private Query queryCodUnicoUserMetadaUidProfissional;
    private Query queryCadeiras;


    //FIREBASE VEL
    private ValueEventListener valueEventListenerCadeirasSalao;
    private ValueEventListener valueEventListenerCodUnicoUserMetadataUid;
    private ValueEventListener valueEventListenerMetadataUidProfissional;
    private ChildEventListener childEventListenerCadeirasSalao;

    //ALERT DIALOG
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","frag profissionais onCreate");

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
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_profissionais,container,false);
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentProfissionaisSalaoAtivo = true;
        sincronizarLista();
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentProfissionaisSalaoAtivo = false;
    }

    @Override
    public void onClickListener(View view, int position) {

    }

    private void initViews(View view){
        this.fabProfissionais = (FloatingActionButton) view.findViewById(R.id.fab_fragment_profissionais);
        this.fabProfissionais.setVisibility(View.INVISIBLE);
        this.fabProfissionais.setClickable(false);
        this.fabProfissionais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proximaEtapa();
            }
        });
        this.labelSemProfissionais = (TextView) view.findViewById(R.id.label_nao_ha_profissionais_adicionados);
        this.labelSemProfissionais.setVisibility(View.INVISIBLE);
        this.progressProfissionais = (ProgressBar) view.findViewById(R.id.progress_fragment_profissionais);
        this.progressProfissionais.setVisibility(View.VISIBLE);
        this.progressBtnSalvar = (ProgressBar) view.findViewById(R.id.progress_btn_add_prof__frag_profissionais);
        this.progressBtnSalvar.setVisibility(View.VISIBLE);
        this.codigoUnicoProfissional = (EditText)view.findViewById(R.id.codigo_unico_profissional);
        this.codigoUnicoProfissional.setText("000000");
        this.codigoUnicoProfissional.setSelection(codigoUnicoProfissional.length());
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
        this.buttonAdicionarProfissional = (Button) view.findViewById(R.id.btn_adicionar_profissional);
        this.buttonAdicionarProfissional.setText("");
        this.buttonAdicionarProfissional.setClickable(false);
        this.buttonAdicionarProfissional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verificarProfissionalExiste(codigoUnicoProfissional.getText().toString());
            }
        });
        this.adicionarProfissionalSemCodigoUnico = (TextView) view.findViewById(R.id.label_adicionar_profissional_sem_codigo);
        this.adicionarProfissionalSemCodigoUnico.setClickable(false);
        this.adicionarProfissionalSemCodigoUnico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"nao implementado",Toast.LENGTH_SHORT).show();
            }
        });
        this.mRecyclerView = (RecyclerView)view.findViewById(R.id.profissionais_recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.mRecyclerView.setLayoutManager(llm);
        this.mList = new ArrayList<Profissional>();
        this.mListKeyMetadataUidProfissionais = new ArrayList<String>();
        RecyclerAdapterProfissionais recyclerAdapter = new RecyclerAdapterProfissionais(this.mList,getContext());
        recyclerAdapter.setRecyclerViewOnClickListenerHack(this);
        this.mRecyclerView.setAdapter(recyclerAdapter);

        //ALERT DIALOG
        this.builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Adicionar este profissional ?");






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

    private void initControles(){
        fragmentProfissionaisSalaoAtivo = false;
    }

    private void sincronizarLista(){
        this.buttonAdicionarProfissional.setClickable(false);
        this.buttonAdicionarProfissional.setText("");
        this.progressBtnSalvar.setVisibility(View.VISIBLE);
        this.progressProfissionais.setVisibility(View.VISIBLE);
        this.mRecyclerView.setVisibility(View.INVISIBLE);
        this.mRecyclerView.setClickable(false);
        this.labelSemProfissionais.setVisibility(View.INVISIBLE);
        this.fabProfissionais.setClickable(false);
        this.fabProfissionais.setVisibility(View.INVISIBLE);
        ((ConfiguracaoInicialSalaoActivity)getActivity()).setProfissionaisSalaoOk(false);

        if (profissionaisSalao == null){
            profissionaisSalao = new ProfissionaisSalao();
            profissionaisSalao.setProfissionais(new HashMap<String, Profissional>());
        }
        profissionaisSalao.setProfissionais(new HashMap<String, Profissional>());

        zerarRecyclerView();
    }

    private void zerarRecyclerView(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (mList != null && mList.size() > 0){
                    Iterator<Profissional> it = mList.iterator();
                    while (it.hasNext()) {
                        Profissional profissional = it.next();
                        int position = mList.indexOf(profissional);
                        ((RecyclerAdapterProfissionais) mRecyclerView.getAdapter()).removeItemList(position);
                        it.remove();
                    }

                }
                if (mListKeyMetadataUidProfissionais != null && mListKeyMetadataUidProfissionais.size() > 0){
                    mListKeyMetadataUidProfissionais = new ArrayList<String>();
                }

                if (queryCadeiras == null){
                    queryCadeiras = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.CADEIRAS);
                }

                if (valueEventListenerCadeirasSalao == null){
                    valueEventListenerCadeirasSalao = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("script","valueEventListenerCadeirasSalao onDataChange");
                            if(!dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0){
                                Log.i("script","valueEventListenerCadeirasSalao !dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0");
                                labelSemProfissionais.setVisibility(View.VISIBLE);
                                liberarRecyclerView();
                                queryCadeiras.orderByChild(GeralENUM.USER_METADATA_UID_SALAO).equalTo(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).addChildEventListener(childEventListenerCadeirasSalao);
                            }else{
                                Log.i("script","valueEventListenerCadeirasSalao dataSnapshot.exists()");
                                labelSemProfissionais.setVisibility(View.INVISIBLE);
                                sincronizarProfissionais(dataSnapshot);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //TODO
                            Log.i("script","valueEventListenerCadeirasSalao onCancelled");
                        }
                    };
                }

                if (childEventListenerCadeirasSalao == null){
                    childEventListenerCadeirasSalao = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.i("script","onChildAdded -> "+dataSnapshot.toString());
                            if (dataSnapshot.exists()){
                                profissionalAdded(dataSnapshot);
                            }

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            Log.i("script","onChildChanged -> "+dataSnapshot.toString());
                            if (dataSnapshot.exists()){
                                profissionalChanged(dataSnapshot);
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            Log.i("script","onChildRemoved -> "+dataSnapshot.toString());
                            if (dataSnapshot.exists()){
                                profissionalRemoved(dataSnapshot);
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

                queryCadeiras.orderByChild(GeralENUM.USER_METADATA_UID_SALAO).equalTo(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).addListenerForSingleValueEvent(valueEventListenerCadeirasSalao);
            }
        });
    }

    private void liberarRecyclerView(){
        this.buttonAdicionarProfissional.setText("Adicionar");
        this.buttonAdicionarProfissional.setClickable(true);
        this.mRecyclerView.setVisibility(View.VISIBLE);
        this.mRecyclerView.setClickable(true);
        this.progressProfissionais.setVisibility(View.INVISIBLE);
        this.progressBtnSalvar.setVisibility(View.INVISIBLE);
        alterarVisivilidadeFab(liberacaoDoFabIsValid());
    }

    private void sincronizarProfissionais(final DataSnapshot dataSnapshot){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()){
                    Log.i("script","runnableSincronizarProfissionais dataSnapshot = "+dataSnapshot.toString());
                    listUidProfissionais = new ArrayList<String>();
                    profissionaisSalao.setProfissionais(new HashMap<String, Profissional>());

                    for (DataSnapshot auxDataSnapshot : dataSnapshot.getChildren()){
                        Log.i("script","auxDataSnapshot = "+auxDataSnapshot.toString());
                        if (auxDataSnapshot.hasChild(GeralENUM.USER_METADATA_UID_PROFISSIONAL)){
                            listUidProfissionais.add(auxDataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class));
                        }
                    }

                    if (refMetadaUidProfissional == null){
                        refMetadaUidProfissional = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID);
                    }

                    for (Iterator it = listUidProfissionais.iterator();it.hasNext();){
                        final String metadataUidProfissional = (String)it.next();

                        refMetadaUidProfissional.child(metadataUidProfissional).child(GeralENUM.CADASTRO_COMPLEMENTAR).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.i("script","refMetadaUidProfissional onDataChange -> "+dataSnapshot.toString());
                                if (dataSnapshot.hasChild(Profissional.getNOMEPROFISSIONAL())){
                                    Profissional profissional = new Profissional();
                                    profissional.setMetadataUidProfissional(metadataUidProfissional);
                                    if (dataSnapshot.hasChild(Profissional.getNOMEPROFISSIONAL())){
                                        profissional.setNomeProfissional(dataSnapshot.child(Profissional.getNOMEPROFISSIONAL()).getValue(String.class));
                                    }
                                    if (dataSnapshot.hasChild(Profissional.getNICKPROFISSIONAL())){
                                        profissional.setNickProfissional(dataSnapshot.child(Profissional.getNICKPROFISSIONAL()).getValue(String.class));
                                    }

                                    profissionaisSalao.addProfissional(profissional);

                                    if (profissionaisSalao.getProfissionais().size() == listUidProfissionais.size()){
                                        for (String key : profissionaisSalao.getProfissionais().keySet()){
                                            if(mListKeyMetadataUidProfissionais.contains(key)){
                                                //REMOVE
                                                removerProfissionalRecyclerView(key);
                                                //INSERT
                                                adicionarProfissionalRecyclerView(profissionaisSalao.getProfissionais().get(key));
                                            }else {
                                                adicionarProfissionalRecyclerView(profissionaisSalao.getProfissionais().get(key));
                                            }
                                        }
                                        liberarRecyclerView();
                                        queryCadeiras.orderByChild(GeralENUM.USER_METADATA_UID_SALAO).equalTo(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).addChildEventListener(childEventListenerCadeirasSalao);
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.i("script","onCancelled ");
                            }
                        });
                    }
                }
            }
        });
    }

    private void profissionalAdded(final DataSnapshot dataSnapshot){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot != null && dataSnapshot.hasChild(GeralENUM.USER_METADATA_UID_PROFISSIONAL) && dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class) != null && !dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class).isEmpty()){
                    final String userMetadataUidProfissional = dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class);
                    if (refMetadaUidProfissional == null){
                        refMetadaUidProfissional = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID);
                    }
                    refMetadaUidProfissional.child(userMetadataUidProfissional).child(GeralENUM.CADASTRO_COMPLEMENTAR).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot auxDataSnapshot) {
                            if (auxDataSnapshot.exists()){
                                Log.i("script","auxDataSnapshot --> "+auxDataSnapshot);
                                Profissional profissional = new Profissional();
                                profissional.setMetadataUidProfissional(userMetadataUidProfissional);
                                if (auxDataSnapshot.hasChild(Profissional.getNOMEPROFISSIONAL())){
                                    profissional.setNomeProfissional(auxDataSnapshot.child(Profissional.getNOMEPROFISSIONAL()).getValue(String.class));
                                }
                                if (auxDataSnapshot.hasChild(Profissional.getNICKPROFISSIONAL())){
                                    profissional.setNickProfissional(auxDataSnapshot.child(Profissional.getNICKPROFISSIONAL()).getValue(String.class));
                                }

                                if (profissionaisSalao != null){
                                    if (profissionaisSalao.getProfissionais().containsKey(profissional.getMetadataUidProfissional())){
                                        if ((profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNomeProfissional() != null && profissional.getNomeProfissional() != null && !profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNomeProfissional().equals(profissional.getNomeProfissional()))
                                                || (profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNickProfissional() != null && profissional.getNickProfissional() != null && !profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNickProfissional().equals(profissional.getNickProfissional()))){
                                            if(mListKeyMetadataUidProfissionais.contains(profissional.getMetadataUidProfissional())){
                                                removerProfissionalRecyclerView(profissional.getMetadataUidProfissional());
                                            }
                                            profissionaisSalao.removerProfissional(profissional.getMetadataUidProfissional());
                                            profissionaisSalao.addProfissional(profissional);
                                            adicionarProfissionalRecyclerView(profissional);
                                        }
                                    }else {
                                        profissionaisSalao.addProfissional(profissional);
                                        adicionarProfissionalRecyclerView(profissional);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("script","onCancelled ");
                        }
                    });
                }
            }
        });
    }

    private void profissionalChanged(final DataSnapshot dataSnapshot){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot != null && dataSnapshot.hasChild(GeralENUM.USER_METADATA_UID_PROFISSIONAL) && dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class) != null && !dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class).isEmpty()){
                    final String userMetadataUidProfissional = dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class);
                    if (refMetadaUidProfissional == null){
                        refMetadaUidProfissional = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID);
                    }
                    refMetadaUidProfissional.child(userMetadataUidProfissional).child(GeralENUM.CADASTRO_COMPLEMENTAR).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(Profissional.getNOMEPROFISSIONAL())){
                                Profissional profissional = new Profissional();
                                profissional.setMetadataUidProfissional(userMetadataUidProfissional);
                                if (dataSnapshot.hasChild(Profissional.getNOMEPROFISSIONAL())){
                                    profissional.setNomeProfissional(dataSnapshot.child(Profissional.getNOMEPROFISSIONAL()).getValue(String.class));
                                }
                                if (dataSnapshot.hasChild(Profissional.getNICKPROFISSIONAL())){
                                    profissional.setNickProfissional(dataSnapshot.child(Profissional.getNICKPROFISSIONAL()).getValue(String.class));
                                }

                                if (profissionaisSalao != null){
                                    if (profissionaisSalao.getProfissionais().containsKey(profissional.getMetadataUidProfissional())){
                                        if ((profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNomeProfissional() != null && profissional.getNomeProfissional() != null && !profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNomeProfissional().equals(profissional.getNomeProfissional()))
                                                || (profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNickProfissional() != null && profissional.getNickProfissional() != null && !profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()).getNickProfissional().equals(profissional.getNickProfissional()))){

                                            if(mListKeyMetadataUidProfissionais.contains(profissional.getMetadataUidProfissional())){
                                                removerProfissionalRecyclerView(profissional.getMetadataUidProfissional());
                                            }
                                            profissionaisSalao.removerProfissional(profissional.getMetadataUidProfissional());
                                            profissionaisSalao.addProfissional(profissional);
                                            adicionarProfissionalRecyclerView(profissional);
                                        }
                                    }else {
                                        profissionaisSalao.addProfissional(profissional);
                                        adicionarProfissionalRecyclerView(profissional);
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.i("script","onCancelled ");
                        }
                    });
                }
            }
        });
    }

    private void profissionalRemoved(final DataSnapshot dataSnapshot){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot != null && dataSnapshot.hasChild(GeralENUM.USER_METADATA_UID_PROFISSIONAL) && dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class) != null && !dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class).isEmpty()){
                    if(mListKeyMetadataUidProfissionais.contains(dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class))) {
                        //REMOVE
                        removerProfissionalRecyclerView(dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class));
                    }
                    profissionaisSalao.removerProfissional(dataSnapshot.child(GeralENUM.USER_METADATA_UID_PROFISSIONAL).getValue(String.class));
                }
            }
        });

    }

    private void adicionarProfissionalRecyclerView(Profissional profissional){
        Log.i("script","adicionarProfissionalRecyclerView");
        labelSemProfissionais.setVisibility(View.INVISIBLE);
        mListKeyMetadataUidProfissionais.add(profissional.getMetadataUidProfissional());
        mList.add(profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()));
        int newPosition = mList.indexOf(profissionaisSalao.getProfissionais().get(profissional.getMetadataUidProfissional()));
        ((RecyclerAdapterProfissionais) mRecyclerView.getAdapter()).addItemList(newPosition);
        alterarVisivilidadeFab(liberacaoDoFabIsValid());
    }

    private void removerProfissionalRecyclerView(String metadataUidProfissional){
        mListKeyMetadataUidProfissionais.remove(metadataUidProfissional);
        int position = mList.indexOf(profissionaisSalao.getProfissionais().get(metadataUidProfissional));
        mList.remove(profissionaisSalao.getProfissionais().get(metadataUidProfissional));
        ((RecyclerAdapterProfissionais) mRecyclerView.getAdapter()).removeItemList(position);
        if (mList.size() == 0){
            labelSemProfissionais.setVisibility(View.VISIBLE);
        }
        alterarVisivilidadeFab(liberacaoDoFabIsValid());
    }

    private void verificarProfissionalExiste(final String codUnicoProfissional){
        Log.i("script"," verificarProfissionalExiste codUnicoProfissional = "+codUnicoProfissional);
        if (this.queryCodUnicoUserMetadaUidProfissional == null){
            this.queryCodUnicoUserMetadaUidProfissional = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.CODIGOUNICO_USER_METADATA_UID).child(TipoUsuarioENUM.PROFISSIONAl);
        }

        this.valueEventListenerCodUnicoUserMetadataUid = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && dataSnapshot.getChildrenCount() == 1){
                    Log.i("script","valueEventListenerCodUnicoUserMetadataUid dataSnapshot.exists() -> "+dataSnapshot.toString());

                    for (DataSnapshot auxDataSnapshot : dataSnapshot.getChildren()){
                        buscarProfissional(auxDataSnapshot.getValue(String.class),codUnicoProfissional);
                    }
                }else{
                    Log.i("script","valueEventListenerCodUnicoUserMetadataUid !dataSnapshot.exists()");
                    Toast.makeText(getActivity(),"Profissional não encontrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("script","valueEventListenerCodUnicoUserMetadataUid onCancelled");
            }
        };

        this.queryCodUnicoUserMetadaUidProfissional.orderByKey().equalTo(codUnicoProfissional).addListenerForSingleValueEvent(this.valueEventListenerCodUnicoUserMetadataUid);
    }

    private void buscarProfissional(final String userMetadataUidProfissional, final String codUnico){
        Log.i("script"," buscarProfissional userMetadataUid = "+userMetadataUidProfissional+"\n cod "+codUnico);
        if (this.refMetadaUidProfissional == null){
            //this.queryMetadaUidProfissional = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(userMetadataUidProfissional).child(GeralENUM.CADASTRO_COMPLEMENTAR);
            this.refMetadaUidProfissional = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID);
        }

        this.valueEventListenerMetadataUidProfissional = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()  && dataSnapshot.hasChild(Profissional.getNOMEPROFISSIONAL())){
                    Log.i("script"," dataSnapshot.exists()  = "+dataSnapshot.toString());

                    if (refRaiz == null){
                        refRaiz = LibraryClass.getFirebase();
                    }
                    final String push = refRaiz.child(GeralENUM.METADATA).child(GeralENUM.CADEIRAS).push().getKey();
                    builder.setMessage("Código do Profissional: #"+codUnico+"\nNome do Profissional: "+dataSnapshot.child(Profissional.getNOMEPROFISSIONAL()).getValue(String.class));
                    //define um botão como positivo
                    builder.setPositiveButton("ADICIONAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Map<String,Object> updates = new HashMap<String, Object>();
                            updates.put(GeralENUM.METADATA+"/"+GeralENUM.CADEIRAS+"/"+push+"/"+GeralENUM.USER_METADATA_UID_SALAO,((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid());
                            updates.put(GeralENUM.METADATA+"/"+GeralENUM.CADEIRAS+"/"+push+"/"+GeralENUM.USER_METADATA_UID_PROFISSIONAL, userMetadataUidProfissional);
                            updates.put(GeralENUM.METADATA+"/"+GeralENUM.CADEIRAS+"/"+push+"/"+ Acount.getDATA_CRIACAO(), ServerValue.TIMESTAMP);
                            updates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()+"/"+GeralENUM.PROFISSIONAIS_DO_SALAO+"/"+userMetadataUidProfissional,push);
                            updates.put(GeralENUM.METADATA+"/"+GeralENUM.USER_METADATA_UID+"/"+userMetadataUidProfissional+"/"+GeralENUM.SALOES_DO_PROFISSIONAL+"/"+((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid(),push);


                            refRaiz.updateChildren(updates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null){
                                        Log.i("script","databaseError == null");
                                        Toast.makeText(getActivity(),"Profissional adicionado.",Toast.LENGTH_SHORT).show();
                                    }else {
                                        Log.i("script","databaseError != null -> "+databaseError.toString());
                                        Toast.makeText(getActivity(),"O profissional ja esta cadastrado no salão.",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    //define um botão como negativo.
                    builder.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //TODO
                        }
                    });
                    alertDialog = builder.create();
                    alertDialog.show();

                }else{
                    Log.i("script","valueEventListenerMetadataUidProfissional !dataSnapshot.exists()");
                    Toast.makeText(getActivity(),"Profissional não encontrado!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.i("script","valueEventListenerMetadataUidProfissional onCancelled");
            }
        };

        this.refMetadaUidProfissional.child(userMetadataUidProfissional).child(GeralENUM.CADASTRO_COMPLEMENTAR).addListenerForSingleValueEvent(this.valueEventListenerMetadataUidProfissional);
    }

    private boolean liberacaoDoFabIsValid(){
        Log.i("script","liberacaoDoFabIsValid ");
        if (profissionaisSalao == null || profissionaisSalao.getProfissionais() == null || profissionaisSalao.getProfissionais().isEmpty()){
            ((ConfiguracaoInicialSalaoActivity)getActivity()).setProfissionaisSalaoOk(false);
            return false;
        }else{
            ((ConfiguracaoInicialSalaoActivity)getActivity()).setProfissionaisSalaoOk(true);
            return true;
        }
    }

    private void alterarVisivilidadeFab(boolean visivel){
        Log.i("script","alterarVisivilidadeFab "+visivel);
        if (visivel){
            this.fabProfissionais.setClickable(true);
            this.fabProfissionais.setVisibility(View.VISIBLE);
        }else{
            this.fabProfissionais.setClickable(false);
            this.fabProfissionais.setVisibility(View.INVISIBLE);
        }
    }

    private void proximaEtapa(){
        Log.i("script","clique");
        if (formularioPreenchidoCorretamente() && ((ConfiguracaoInicialSalaoActivity)getActivity()).isProfissionaisSalaoOk()){
            if (!((ConfiguracaoInicialSalaoActivity)getActivity()).isFuncionamentoSalaoOk()){
                ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(0);
            }else if (!((ConfiguracaoInicialSalaoActivity)getActivity()).isServicosSalaoOk()){
                ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(1);
            }else {
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
                                                                if (isFragmentProfissionaisSalaoAtivo()){
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
                                                if (isFragmentProfissionaisSalaoAtivo()){
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
                                    if (isFragmentProfissionaisSalaoAtivo()){
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
                        if (isFragmentProfissionaisSalaoAtivo()){
                            showToast("Erro ao salvar tentar novamente.");
                            ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                        }
                    }
                });
            }
        }
    }

    private boolean formularioPreenchidoCorretamente(){
        if (profissionaisSalao == null || profissionaisSalao.getProfissionais() == null || profissionaisSalao.getProfissionais().isEmpty()){
            showToast("Cadastre ao menos 1 profissional.");
            return false;
        }else{
            return true;
        }
    }

    private void showToast( String message ){
        Toast.makeText(getActivity(),
                message,
                Toast.LENGTH_LONG)
                .show();
    }


    private void aguardarDados(){

        this.progressProfissionais.setVisibility(View.VISIBLE);
        this.fabProfissionais.setVisibility(View.INVISIBLE);
        this.fabProfissionais.setClickable(false);
    }

    //GETTERS SETTERS
    public static String getTITULO() {
        return TITULO;
    }

    public static boolean isFragmentProfissionaisSalaoAtivo() {
        return fragmentProfissionaisSalaoAtivo;
    }
}

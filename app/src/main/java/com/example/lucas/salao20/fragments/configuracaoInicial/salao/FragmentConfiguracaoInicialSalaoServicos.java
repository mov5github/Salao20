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
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.activitys.ConfiguracaoInicialSalaoActivity;
import com.example.lucas.salao20.adapters.AdapterSpinnerIcones;
import com.example.lucas.salao20.adapters.RecyclerAdapterServicos;
import com.example.lucas.salao20.domain.util.LibraryClass;
import com.example.lucas.salao20.enumeradores.GeralENUM;
import com.example.lucas.salao20.geral.geral.CadastroBasico;
import com.example.lucas.salao20.geral.geral.Servico;
import com.example.lucas.salao20.geral.salao.FuncionamentoSalao;
import com.example.lucas.salao20.geral.salao.ServicosSalao;
import com.example.lucas.salao20.interfaces.RecyclerViewOnClickListenerHack;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Lucas on 21/03/2017.
 */

public class FragmentConfiguracaoInicialSalaoServicos extends Fragment implements RecyclerViewOnClickListenerHack {
    //ENUM
    private static final String TITULO = "Serviços";

    //  FIREBASE AUTH
    private FirebaseAuth mAuth;

    //HANDLER
    private Handler handler;

    private ProgressBar progressServicos;
    private FloatingActionButton fabServicos;
    private LinearLayout formServicos;
    private TableRow trButtons;

    private AdapterSpinnerIcones adapter;
    private RecyclerView mRecyclerView;

    private AutoCompleteTextView nomeServico;
    private EditText precoServico;
    private Spinner spinnerIcones;
    private Spinner spinnerHoras;
    private Spinner spinnerMinutos;
    private AutoCompleteTextView descricaoServico;
    private Button buttonAddServico;
    private TextView labelSemServicos;

    //CONTROLES
    private static boolean fragmentServicosSalaoAtivo;
    private boolean criandoServico;

    //ARRAYS
    private List<Servico> mList;
    private List<String> mListKeyIdServicos;

    //FIREBASE REF
    private DatabaseReference refServicosSalao;
    private DatabaseReference refProfissionaisSalao;
    private DatabaseReference refFuncionamentoSalao;
    private DatabaseReference refRaiz;


    //FIREBASE VEL
    private ValueEventListener valueEventListenerServicosSalao;
    private ChildEventListener childEventListenerServicosSalao;

    //OBJETOS
    private ServicosSalao servicosSalao = null;

    //RUNABLES
    private Runnable runnableIniciarFormulario;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("script","frag servicos onCreate");

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
        Log.i("script","frag servicos onCreateView");
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_servicos,container,false);
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("script","FragmentConfiguracaoInicialSalaoServicos onStart");
        fragmentServicosSalaoAtivo = true;

        sincronizarLista();

        //aguardarDados();
        //sincronizarRecyclerView();
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentServicosSalaoAtivo = false;
        Log.i("testeteste","FragmentConfiguracaoInicialSalaoServicos onStop");
        refServicosSalao.removeEventListener(valueEventListenerServicosSalao);
        refServicosSalao.removeEventListener(childEventListenerServicosSalao);
        this.handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClickListener(View view, final int position) {
        //Toast.makeText(getActivity(),"POSITION " + position, Toast.LENGTH_SHORT).show();
        //final int posiçao = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setPositiveButton("SIM",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String idServico = mList.get(position).getIdServico();
                        mList.remove(position);
                        mListKeyIdServicos.remove(idServico);
                        servicosSalao.getServicosSalao().remove(idServico);
                        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                        refServicosSalao.child(idServico).removeValue();

                    }
                });
        builder.setNegativeButton("NÃO",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //APENAS FECHA O DIALOG
                    }
                });

        builder.setTitle("Excluir Serviço ?");
        if (mList.get(position).getIcone() != null){
            //TODO
            //builder.setIcon(mList.get(position).getIcone());
        }
        builder.setCancelable(true);
        alertDialogBuilderMessage(builder, mList.get(position).getNome(), this.mList.get(position).getPreco().toString(), this.mList.get(position).getDuracao(), this.mList.get(position).getDescricao());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initViews(View view){
        this.trButtons = (TableRow) view.findViewById(R.id.tr_buttons);
        this.formServicos = (LinearLayout) view.findViewById(R.id.form_servicos);
        this.fabServicos = (FloatingActionButton) view.findViewById(R.id.fab_fragment_servicos);
        this.fabServicos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               proximaEtapa();
            }
        });
        this.progressServicos = (ProgressBar) view.findViewById(R.id.progress_fragment_servicos);
        this.nomeServico = (AutoCompleteTextView) view.findViewById(R.id.nome_servico);
        this.precoServico = (EditText) view.findViewById(R.id.preco_servico);
        this.precoServico.addTextChangedListener(new MascaraMonetaria(this.precoServico));
        this.precoServico.setText("0");
        this.spinnerHoras = (Spinner) view.findViewById(R.id.spinner_duracao_servico_horas);
        this.spinnerMinutos = (Spinner) view.findViewById(R.id.spinner_duracao_servico_minutos);
        this.descricaoServico = (AutoCompleteTextView) view.findViewById(R.id.descricao_servico);
        this.spinnerIcones = (Spinner) view.findViewById(R.id.spinner_icones);
        this.buttonAddServico = (Button) view.findViewById(R.id.btn_adicionar_servico);
        this.labelSemServicos = (TextView) view.findViewById(R.id.label_nao_ha_servicos_adicionados);

        createSpinnerIcones();
        createRecyclerViewServicosAdicionados(view);
    }

    private void initControles(){
        fragmentServicosSalaoAtivo = false;
        this.criandoServico = false;
    }

    private void sincronizarLista(){
        this.progressServicos.setVisibility(View.VISIBLE);
        this.mRecyclerView.setVisibility(View.INVISIBLE);
        this.mRecyclerView.setClickable(false);
        this.labelSemServicos.setVisibility(View.INVISIBLE);
        //this.trButtons.setVisibility(View.INVISIBLE);
        //this.trButtons.setClickable(false);
        this.fabServicos.setClickable(false);
        this.fabServicos.setVisibility(View.INVISIBLE);
        ((ConfiguracaoInicialSalaoActivity)getActivity()).setServicosSalaoOk(false);

        if (servicosSalao == null){
            servicosSalao = new ServicosSalao();
        }
        servicosSalao.setServicosSalao(new HashMap<String, Servico>());

        zerarRecyclerView();
    }

    private void zerarRecyclerView(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (mList != null && mList.size() > 0){
                    Iterator<Servico> it = mList.iterator();
                    while (it.hasNext()) {
                        Servico servico = it.next();
                        int position = mList.indexOf(servico);
                        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                        it.remove();
                    }

                }
                if (mListKeyIdServicos != null && mListKeyIdServicos.size() > 0){
                    mListKeyIdServicos = new ArrayList<String>();
                }

                if (refServicosSalao == null){
                    refServicosSalao = LibraryClass.getFirebase().child(GeralENUM.METADATA).child(GeralENUM.USER_METADATA_UID).child(((ConfiguracaoInicialSalaoActivity)getActivity()).getCadastroBasico().getUserMetadataUid()).child(GeralENUM.SERVICOS_SALAO);
                }

                if (valueEventListenerServicosSalao == null){
                    valueEventListenerServicosSalao = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i("script","valueEventListenerservicosSalao onDataChange");
                            if(!dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0){
                                Log.i("script","valueEventListenerServicosSalao !dataSnapshot.exists() || dataSnapshot.getChildrenCount() == 0");
                                //sincronizarRecyclerView();
                                labelSemServicos.setVisibility(View.VISIBLE);
                                liberarRecyclerView();
                                refServicosSalao.addChildEventListener(childEventListenerServicosSalao);
                            }else{
                                Log.i("script","valueEventListenerServicosSalao dataSnapshot.exists()");
                                labelSemServicos.setVisibility(View.INVISIBLE);
                                sincronizarServicos(dataSnapshot);
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            //TODO
                            Log.i("script","valueEventListenerServicosSalao onCancelled");
                        }
                    };
                }

                if (childEventListenerServicosSalao == null){
                    childEventListenerServicosSalao = new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.exists()){
                        /*Funcionamento funcionamento = new Funcionamento();
                        funcionamento.setDia(dataSnapshot.getKey());
                        if (dataSnapshot.hasChild(DiasENUM.ABRE)){
                            funcionamento.setAbre(dataSnapshot.child(DiasENUM.ABRE).getValue(String.class));
                        }
                        if (dataSnapshot.hasChild(DiasENUM.FECHA)){
                            funcionamento.setFecha(dataSnapshot.child(DiasENUM.FECHA).getValue(String.class));
                        }
                        funcionamentoSalao.addFuncionamento(funcionamento);
                        if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialSalaoAdapter)mViewPager.getAdapter()).getFragment(0)).funcionamentoAdicionado(funcionamento.getDia());
                        }*/
                                servicoAdded(dataSnapshot);
                            }

                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                            if (dataSnapshot.exists()){
                        /*Funcionamento funcionamento = new Funcionamento();
                        funcionamento.setDia(dataSnapshot.getKey());
                        if (dataSnapshot.hasChild(DiasENUM.ABRE)){
                            funcionamento.setAbre(dataSnapshot.child(DiasENUM.ABRE).getValue(String.class));
                        }
                        if (dataSnapshot.hasChild(DiasENUM.FECHA)){
                            funcionamento.setFecha(dataSnapshot.child(DiasENUM.FECHA).getValue(String.class));
                        }
                        funcionamentoSalao.addFuncionamento(funcionamento);
                        /*if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialSalaoAdapter)mViewPager.getAdapter()).getFragment(0)).funcionamentoAlterado(funcionamento.getDia());
                        }*/
                                servicoChanged(dataSnapshot);
                            }
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                //funcionamentoSalao.removerFuncionamento(dataSnapshot.getKey());
                       /* if (mViewPager != null && FragmentConfiguracaoInicialSalaoFuncionamento.isFragmentFuncionamentoSalaoAtivo()){
                            ((FragmentConfiguracaoInicialSalaoFuncionamento)((ConfiguracaoInicialSalaoAdapter)mViewPager.getAdapter()).getFragment(0)).funcionamentoRemovido(dataSnapshot.getKey());
                        }*/
                                servicoRemoved(dataSnapshot);
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

                if(refServicosSalao != null){
                    refServicosSalao.addListenerForSingleValueEvent(valueEventListenerServicosSalao);
                }
            }

        });

    }

    private void liberarRecyclerView(){
        this.mRecyclerView.setVisibility(View.VISIBLE);
        this.mRecyclerView.setClickable(true);
        this.progressServicos.setVisibility(View.INVISIBLE);
        this.trButtons.setVisibility(View.VISIBLE);
        this.trButtons.setClickable(true);
        alterarVisivilidadeFab(liberacaoDoFabIsValid());
    }

    private void sincronizarServicos(final DataSnapshot dataSnapshot){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()){
                    Log.i("script","runnableSincronizarServicos dataSnapshot = "+dataSnapshot.toString());
                    for (DataSnapshot auxDataSnapshot : dataSnapshot.getChildren()){
                        Log.i("script","auxDataSnapshot = "+auxDataSnapshot.toString());
                        Servico servico = new Servico();
                        if (auxDataSnapshot.getKey() != null && !auxDataSnapshot.getKey().isEmpty()){
                            servico.setIdServico(auxDataSnapshot.getKey());
                        }
                        if (auxDataSnapshot.hasChild(Servico.getNOME())){
                            servico.setNome(auxDataSnapshot.child(Servico.getNOME()).getValue(String.class));
                        }
                        if (auxDataSnapshot.hasChild(Servico.getDATA_INSERCAO())){
                            servico.setDataInsercao(auxDataSnapshot.child(Servico.getDATA_INSERCAO()).getValue(Long.class));
                        }
                        if (auxDataSnapshot.hasChild(Servico.getDESCRICAO())){
                            servico.setDescricao(auxDataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                        }
                        if (auxDataSnapshot.hasChild(Servico.getDURACAO())){
                            servico.setDuracao(auxDataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                        }
                        if (auxDataSnapshot.hasChild(Servico.getICONE_NOME())){
                            servico.setIconeNome(auxDataSnapshot.child(Servico.getICONE_NOME()).getValue(String.class));
                            servico.setIcone(getResources().getIdentifier(servico.getIconeNome(),"mipmap","com.example.lucas.salao20"));
                        }
                        if (auxDataSnapshot.hasChild(Servico.getPRECO())){
                            servico.setPreco(auxDataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                        }

                        if (servicosSalao != null){
                            servicosSalao.addServico(servico);
                        }
                    }

                    for (String key : servicosSalao.getServicosSalao().keySet()){
                        if(mListKeyIdServicos.contains(key)){
                            //REMOVE
                            removerServicoRecyclerView(key);
                            //INSERT
                            adicionarServicoRecyclerView(servicosSalao.getServicosSalao().get(key));
                        }else {
                            adicionarServicoRecyclerView(servicosSalao.getServicosSalao().get(key));
                        }
                    }

                    liberarRecyclerView();

                    refServicosSalao.addChildEventListener(childEventListenerServicosSalao);
                }
            }
        });
    }

    private void servicoAdded(final DataSnapshot dataSnapshot){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()){
                    Log.i("script","servicoAdded dataSnapshot = "+dataSnapshot.toString());
                    Servico servico = new Servico();
                    if (dataSnapshot.getKey() != null && !dataSnapshot.getKey().isEmpty()){
                        servico.setIdServico(dataSnapshot.getKey());
                    }
                    if (dataSnapshot.hasChild(Servico.getNOME())){
                        servico.setNome(dataSnapshot.child(Servico.getNOME()).getValue(String.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getDATA_INSERCAO())){
                        servico.setDataInsercao(dataSnapshot.child(Servico.getDATA_INSERCAO()).getValue(Long.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getDESCRICAO())){
                        servico.setDescricao(dataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getDURACAO())){
                        servico.setDuracao(dataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getICONE_NOME())){
                        servico.setIconeNome(dataSnapshot.child(Servico.getICONE_NOME()).getValue(String.class));
                        servico.setIcone(getResources().getIdentifier(servico.getIconeNome(),"mipmap","com.example.lucas.salao20"));
                    }
                    if (dataSnapshot.hasChild(Servico.getPRECO())){
                        servico.setPreco(dataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                    }

                    if (servicosSalao != null){
                        if (servicosSalao.getServicosSalao().containsKey(servico.getIdServico())){
                            if (!servicosSalao.getServicosSalao().get(servico.getIdServico()).getNome().equals(servico.getNome()) || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getDataInsercao().equals(servico.getDataInsercao())
                                    || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getDescricao().equals(servico.getDescricao()) || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getDuracao().equals(servico.getDuracao())
                                    || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getIconeNome().equals(servico.getIconeNome()) || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getPreco().equals(servico.getPreco())){

                                if(mListKeyIdServicos.contains(servico.getIdServico())){
                                    removerServicoRecyclerView(servico.getIdServico());
                                }
                                servicosSalao.removerServico(servico.getIdServico());
                                servicosSalao.addServico(servico);
                                adicionarServicoRecyclerView(servico);
                            }
                        }else {
                            servicosSalao.addServico(servico);
                            adicionarServicoRecyclerView(servico);
                        }
                    }
                    alterarVisivilidadeFab(liberacaoDoFabIsValid());
                }

            }
        });
    }

    private void servicoChanged(final DataSnapshot dataSnapshot){
        Log.i("script","servicoChanged dataSnapshot = "+dataSnapshot.toString());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()){
                    Log.i("script","servicoAdded dataSnapshot = "+dataSnapshot.toString());
                    Servico servico = new Servico();
                    if (dataSnapshot.getKey() != null && !dataSnapshot.getKey().isEmpty()){
                        servico.setIdServico(dataSnapshot.getKey());
                    }
                    if (dataSnapshot.hasChild(Servico.getNOME())){
                        servico.setNome(dataSnapshot.child(Servico.getNOME()).getValue(String.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getDATA_INSERCAO())){
                        servico.setDataInsercao(dataSnapshot.child(Servico.getDATA_INSERCAO()).getValue(Long.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getDESCRICAO())){
                        servico.setDescricao(dataSnapshot.child(Servico.getDESCRICAO()).getValue(String.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getDURACAO())){
                        servico.setDuracao(dataSnapshot.child(Servico.getDURACAO()).getValue(Integer.class));
                    }
                    if (dataSnapshot.hasChild(Servico.getICONE_NOME())){
                        servico.setIconeNome(dataSnapshot.child(Servico.getICONE_NOME()).getValue(String.class));
                        servico.setIcone(getResources().getIdentifier(servico.getIconeNome(),"mipmap","com.example.lucas.salao20"));
                    }
                    if (dataSnapshot.hasChild(Servico.getPRECO())){
                        servico.setPreco(dataSnapshot.child(Servico.getPRECO()).getValue(Double.class));
                    }



                    if (servicosSalao != null){
                        if (servicosSalao.getServicosSalao().containsKey(servico.getIdServico())){
                            if (!servicosSalao.getServicosSalao().get(servico.getIdServico()).getNome().equals(servico.getNome()) || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getPreco().equals(servico.getPreco())
                                    || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getDescricao().equals(servico.getDescricao()) || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getDuracao().equals(servico.getDuracao())
                                    || !servicosSalao.getServicosSalao().get(servico.getIdServico()).getIconeNome().equals(servico.getIconeNome())){

                                if(mListKeyIdServicos.contains(servico.getIdServico())){
                                    removerServicoRecyclerView(servico.getIdServico());
                                }
                                servicosSalao.removerServico(servico.getIdServico());
                                servicosSalao.addServico(servico);
                                adicionarServicoRecyclerView(servico);
                            }
                        }else {
                            servicosSalao.addServico(servico);
                            adicionarServicoRecyclerView(servico);
                        }
                    }
                    alterarVisivilidadeFab(liberacaoDoFabIsValid());
                }

            }
        });

    }

    private void servicoRemoved(final DataSnapshot dataSnapshot){
        Log.i("script","servicoRemoved dataSnapshot = "+dataSnapshot.toString());
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()){
                    Log.i("script","servicoAdded dataSnapshot = "+dataSnapshot.toString());
                    if (dataSnapshot.getKey() != null && !dataSnapshot.getKey().isEmpty()){
                        if (servicosSalao != null){
                            if(mListKeyIdServicos.contains(dataSnapshot.getKey())){
                                removerServicoRecyclerView(dataSnapshot.getKey());
                            }
                            servicosSalao.removerServico(dataSnapshot.getKey());
                        }
                    }
                    alterarVisivilidadeFab(liberacaoDoFabIsValid());
                }
            }
        });
    }

    private void adicionarServicoRecyclerView(Servico servico){
        Log.i("script","adicionarServicoRecyclerView ");
        labelSemServicos.setVisibility(View.INVISIBLE);
        mListKeyIdServicos.add(servico.getIdServico());
        mList.add(servicosSalao.getServicosSalao().get(servico.getIdServico()));
        int newPosition = mList.indexOf(servicosSalao.getServicosSalao().get(servico.getIdServico()));
        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(newPosition);
    }

    private void removerServicoRecyclerView(String idServico){
        Log.i("script","removerServicoRecyclerView ");
        mListKeyIdServicos.remove(idServico);
        int position = mList.indexOf(servicosSalao.getServicosSalao().get(idServico));
        mList.remove(servicosSalao.getServicosSalao().get(idServico));
        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
        if (mList.size() == 0){
            labelSemServicos.setVisibility(View.VISIBLE);
        }
    }

    public void criarServico(){
        if (preenchimentoIsValid()){
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    ((ConfiguracaoInicialSalaoActivity)getActivity()).showProgressDialog(true);
                    Servico servico = new Servico();
                    servico.setNome(nomeServico.getText().toString());
                    servico.setIcone(adapter.getItem(spinnerIcones.getSelectedItemPosition()));
                    servico.setIconeNome(getResources().getResourceEntryName(servico.getIcone()));
                    servico.setPreco(gerarPrecoFloat());
                    servico.setDuracao(gerarDuracao());
                    servico.setDescricao(descricaoServico.getText().toString());
                    String push = refServicosSalao.push().getKey();
                    servico.setIdServico(push);
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(servico.getIdServico(),servico.toMap());
                    refServicosSalao.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            ((ConfiguracaoInicialSalaoActivity)getActivity()).showProgressDialog(false);
                            if (databaseError != null){
                                Log.i("script","updateChildren onComplete databaseError != null -> "+databaseError.toString());
                                Toast.makeText(getActivity(),
                                        "serviço não salvo",
                                        Toast.LENGTH_LONG)
                                        .show();

                            }else{
                                Log.i("script","updateChildren onComplete databaseError == null");
                                limparCampos();
                                Toast.makeText(getActivity(),
                                        "serviço salvo",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });



                    // ConfiguracaoInicialActivity.getServicosSalao().addServico(servico);
                    /// mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(servico.getIdServico()));
                    // int position = mList.indexOf(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(servico.getIdServico()));
                    // mListKeyIdServicos.add(servico.getIdServico());
                    // ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position);
                    // Map<String, Object> childUpdates = new HashMap<>();
                    // childUpdates.put(servico.getIdServico(), ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(servico.getIdServico()).toMap());
                    //((ConfiguracaoInicialActivity)getActivity()).getRefServicosSalao().updateChildren(childUpdates);
                    // limparCampos();
                }
            });
        }
    }

    private boolean liberacaoDoFabIsValid(){
        Log.i("script","liberacaoDoFabIsValid ");
        if (servicosSalao == null || servicosSalao.getServicosSalao() == null || servicosSalao.getServicosSalao().isEmpty()){
            ((ConfiguracaoInicialSalaoActivity)getActivity()).setServicosSalaoOk(false);
            return false;
        }else{
            ((ConfiguracaoInicialSalaoActivity)getActivity()).setServicosSalaoOk(true);
            return true;
        }
    }

    private void alterarVisivilidadeFab(boolean visivel){
        Log.i("script","alterarVisivilidadeFab "+visivel);
        if (visivel){
            this.fabServicos.setClickable(true);
            this.fabServicos.setVisibility(View.VISIBLE);
        }else{
            this.fabServicos.setClickable(false);
            this.fabServicos.setVisibility(View.INVISIBLE);
        }
    }

    private void proximaEtapa(){
        Log.i("script","clique");
        if (formularioPreenchidoCorretamente() && ((ConfiguracaoInicialSalaoActivity)getActivity()).isServicosSalaoOk()){
            if (!((ConfiguracaoInicialSalaoActivity)getActivity()).isFuncionamentoSalaoOk()){
                ((ConfiguracaoInicialSalaoActivity) getActivity()).getmViewPager().setCurrentItem(0);
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
                                                               if (isFragmentServicosSalaoAtivo()){
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
                                               if (isFragmentServicosSalaoAtivo()){
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
                                   if (isFragmentServicosSalaoAtivo()){
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
                        if (isFragmentServicosSalaoAtivo()){
                            showToast("Erro ao salvar tentar novamente.");
                            ((ConfiguracaoInicialSalaoActivity) getActivity()).showProgressDialog(false);
                        }
                    }
                });
            }
        }
    }

    private boolean formularioPreenchidoCorretamente(){
        if (servicosSalao == null || servicosSalao.getServicosSalao() == null || servicosSalao.getServicosSalao().isEmpty()){
            showToast("Cadastre ao menos 1 serviço.");
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

    //AUXILIARES
    private ArrayList<Integer> createArrayListIcones(){
        ArrayList<Integer> icones = new ArrayList<Integer>();
        icones.add(R.mipmap.ic_launcher);
        icones.add(R.mipmap.icone_servico_1);
        icones.add(R.mipmap.icone_servico_2);
        icones.add(R.mipmap.icone_servico_3);
        icones.add(R.mipmap.icone_servico_4);
        icones.add(R.mipmap.icone_servico_5);
        icones.add(R.mipmap.icone_servico_6);
        icones.add(R.mipmap.icone_servico_7);
        icones.add(R.mipmap.icone_servico_8);
        icones.add(R.mipmap.icone_servico_9);
        icones.add(R.mipmap.icone_servico_10);
        icones.add(R.mipmap.icone_servico_11);
        icones.add(R.mipmap.icone_servico_12);
        icones.add(R.mipmap.icone_servico_13);
        icones.add(R.mipmap.icone_servico_14);
        icones.add(R.mipmap.icone_servico_15);
        icones.add(R.mipmap.icone_servico_16);
        icones.add(R.mipmap.icone_servico_17);
        icones.add(R.mipmap.icone_servico_18);
        icones.add(R.mipmap.icone_servico_19);
        icones.add(R.mipmap.icone_servico_20);
        icones.add(R.mipmap.icone_servico_31);
        icones.add(R.mipmap.icone_servico_32);
        icones.add(R.mipmap.icone_servico_33);
        icones.add(R.mipmap.icone_servico_34);
        icones.add(R.mipmap.icone_servico_35);
        icones.add(R.mipmap.icone_servico_36);

        return icones;
    }

    private void createSpinnerIcones(){
        ArrayList<Integer> icones = createArrayListIcones();
        this.adapter = new AdapterSpinnerIcones(getActivity(),icones);
        this.spinnerIcones.setAdapter(adapter);
    }

    private void createRecyclerViewServicosAdicionados(View view){
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.servicos_recycler_view);
        this.mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.mRecyclerView.setLayoutManager(llm);
        this.mList = new ArrayList<Servico>();
        this.mListKeyIdServicos = new ArrayList<String>();
        RecyclerAdapterServicos recyclerAdapter = new RecyclerAdapterServicos(this.mList,getContext());
        recyclerAdapter.setRecyclerViewOnClickListenerHack(this);
        this.mRecyclerView.setAdapter(recyclerAdapter);
    }

    private boolean preenchimentoIsValid(){
        if (this.nomeServico.getText().toString().isEmpty() || this.nomeServico.getText().toString().matches("[^\\S]+")){
            Toast.makeText(getActivity(),"Adicione um nome ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.spinnerIcones.getSelectedItemPosition() == 0){
            Toast.makeText(getActivity(),"Adicione um icone ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.precoServico.getText().toString().isEmpty() || this.precoServico.getText().toString().equals("R$0,00")){
            Toast.makeText(getActivity(),"Adicione um preço ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.spinnerHoras.getSelectedItemPosition() == 0 && spinnerMinutos.getSelectedItemPosition() == 0){
            Toast.makeText(getActivity(),"Adicione um tempo de duração ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }
        if (this.descricaoServico.getText().toString().isEmpty() || this.descricaoServico.getText().toString().matches("[^\\S]+")){
            Toast.makeText(getActivity(),"Adicione uma descrição ao serviço !",Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private int gerarDuracao(){
        int tempoMinutos = 0;
        String horas = (String) this.spinnerHoras.getSelectedItem();
        String horasConvertida = horas.replaceAll("[^0-9]*", "");
        String minutos = (String) this.spinnerMinutos.getSelectedItem();
        String minutosConvertido = minutos.replaceAll("[^0-9]*", "");

        tempoMinutos = ((Integer.valueOf(horasConvertida)) * 60) + Integer.valueOf(minutosConvertido);

        return tempoMinutos;
    }

    private String converterDuracao(int minutos){
        int horas = minutos / 60;
        int min = (minutos - (horas*60));
        if (horas > 0){
            return (horas + "h e " + min + "min");
        }else{
            if (min > 1){
                return (min + " minutos");
            }else{
                return (min + " minuto");
            }
        }
    }

    private Double gerarPrecoFloat(){
        String preco = this.precoServico.getText().toString();
        String precoConvertido = preco.replaceAll("[^0-9,]*", "");
        precoConvertido = precoConvertido.replace(",", ".");
        return Double.valueOf(precoConvertido);
    }

    public void limparCampos(){
        this.nomeServico.setText("");
        this.precoServico.setText("0");
        this.descricaoServico.setText("");
        this.spinnerIcones.setSelection(0);
        this.spinnerMinutos.setSelection(0);
        this.spinnerHoras.setSelection(0);
        this.nomeServico.requestFocus();
    }

    private void alertDialogBuilderMessage(AlertDialog.Builder builder, String nomeServico, String precoServico, int duracaoServico, String descricaoServico){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            builder.setMessage(Html.fromHtml("<p><b>" + getString(R.string.nome_servico_bold_html) +
                    "</b><br>" + nomeServico + "</p><p><b>" +
                    getString(R.string.preco_servico_bold_html) +
                    "</b><br>R$" + precoServico + "</p><p><b>" +
                    getString(R.string.duracao_servico_bold_html) +
                    "</b><br>" + converterDuracao(duracaoServico) + "</p><p><b>" +
                    getString(R.string.descricao_servico_bold_html) +
                    "</b><br>" + descricaoServico + "</p>", Html.FROM_HTML_MODE_LEGACY));
        } else {
            builder.setMessage(Html.fromHtml("<p><b>" + getString(R.string.nome_servico_bold_html) +
                    "</b><br>" + nomeServico + "</p><p><b>" +
                    getString(R.string.preco_servico_bold_html) +
                    "</b><br>R$" + precoServico + "</p><p><b>" +
                    getString(R.string.duracao_servico_bold_html) +
                    "</b><br>" + converterDuracao(duracaoServico) + "</p><p><b>" +
                    getString(R.string.descricao_servico_bold_html) +
                    "</b><br>" + descricaoServico + "</p>"));
        }
    }




    //GETTERS AND SETTERS
    public static String getTITULO() {
        return TITULO;
    }

    public static boolean isFragmentServicosSalaoAtivo() {
        return fragmentServicosSalaoAtivo;
    }


    //CLASSES
    private class MascaraMonetaria implements TextWatcher {
        EditText campo;

        public MascaraMonetaria(EditText campo) {
            super();
            this.campo = campo;
        }

        private boolean isUpdating = false;
        // Pega a formatacao do sistema, se for brasil R$ se EUA US$
        private NumberFormat nf = NumberFormat.getCurrencyInstance();

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
            boolean hasMask = ((str.indexOf("R$") > -1 || str.indexOf("$") > -1) && (str.indexOf(".") > -1 || str.indexOf(",") > -1));
            // Verificamos se existe máscara
            if (hasMask) {
                // Retiramos a máscara.
                str = str.replaceAll("[R$]", "").replaceAll("[,]", "").replaceAll("[.]", "");
            }
            try {
                // Transformamos o número que está escrito no EditText em
                // monetário.
                str = nf.format(Double.parseDouble(str) / 100);
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


    /*private void sincronizarRecyclerView(){
        if (this.runnableIniciarFormulario == null){
            this.runnableIniciarFormulario = new Runnable() {
                @Override
                public void run() {
                    if (servicosSalao != null && servicosSalao.getServicosSalao() != null && servicosSalao.getServicosSalao().size() > 0){
                        labelSemServicos.setVisibility(View.INVISIBLE);
                        for (String key : servicosSalao.getServicosSalao().keySet()){
                            if(mListKeyIdServicos.contains(key)){
                                //REMOVE
                                mListKeyIdServicos.remove(key);
                                int position = mList.indexOf(servicosSalao.getServicosSalao().get(key));
                                mList.remove(servicosSalao.getServicosSalao().get(key));
                                ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                                //INSERT
                                mListKeyIdServicos.add(key);
                                mList.add(servicosSalao.getServicosSalao().get(key));
                                int newPosition = mList.indexOf(servicosSalao.getServicosSalao().get(key));
                                ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(newPosition);
                            }else {
                                mListKeyIdServicos.add(key);
                                mList.add(servicosSalao.getServicosSalao().get(key));
                                int position = mList.indexOf(servicosSalao.getServicosSalao().get(key));
                                ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position);
                            }
                        }
                    }else{
                        labelSemServicos.setVisibility(View.VISIBLE);
                    }
                    liberarRecyclerView();
                    if (refServicosSalao != null){
                        refServicosSalao.addChildEventListener(childEventListenerServicosSalao);
                    }
                }
            };
        }
        this.handler.post(this.runnableIniciarFormulario);
    }*/

    /*private void adicionarServicoValidado(Servico servico){
        servicosSalao.addServico(servico);
        if(mListKeyIdServicos.contains(servico.getIdServico())){
            //REMOVE
            mListKeyIdServicos.remove(servico.getIdServico());
            int position = mList.indexOf(servicosSalao.getServicosSalao().get(servico.getIdServico()));
            mList.remove(servicosSalao.getServicosSalao().get(servico.getIdServico()));
            ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
            //INSERT
            mListKeyIdServicos.add(servico.getIdServico());
            mList.add(servicosSalao.getServicosSalao().get(servico.getIdServico()));
            int newPosition = mList.indexOf(servicosSalao.getServicosSalao().get(servico.getIdServico()));
            ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(newPosition);
        }else {
            mListKeyIdServicos.add(servico.getIdServico());
            mList.add(servicosSalao.getServicosSalao().get(servico.getIdServico()));
            int position = mList.indexOf(servicosSalao.getServicosSalao().get(servico.getIdServico()));
            ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position);
        }
    }

    public void servicoAdicionado(final String idServico){
        if(this.handler != null){
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().containsKey(idServico)){
                        if (mListKeyIdServicos.contains(idServico)){
                            for (Servico servico : mList) {
                                if (servico.getIdServico().equals(idServico)){
                                    if (!Servico.verificarServicosSaoIguais(servico,ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(idServico))){
                                        int position = mList.indexOf(servico);
                                        mList.remove(position);
                                        mListKeyIdServicos.remove(idServico);
                                        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                                        if (!mListKeyIdServicos.contains(idServico)){
                                            mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(idServico));
                                            mListKeyIdServicos.add(idServico);
                                            int position2 = mList.indexOf(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(idServico));
                                            ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position2);
                                        }
                                    }
                                    break;
                                }
                            }
                        }else{
                            mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(idServico));
                            mListKeyIdServicos.add(idServico);
                            int position2 = mList.indexOf(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(idServico));
                            ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position2);
                        }

                    }
                    liberarFab();
                }
            });
        }
    }*/

    /*public void servicoRemovido(final String idServico){
        if(this.handler != null){
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    for (Servico servico : mList) {
                        if (servico.getIdServico().equals(idServico)){
                            int position = mList.indexOf(servico);
                            mList.remove(position);
                            mListKeyIdServicos.remove(idServico);
                            ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                            break;
                        }
                    }
                    liberarFab();
                }
            });
        }
    }

    public void servicoAlterado(final String idServico){
        if(this.handler != null){
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (mListKeyIdServicos.contains(idServico)){
                        for (Servico servico : mList) {
                            if (servico.getIdServico().equals(idServico)){
                                int position = mList.indexOf(servico);
                                mList.remove(position);
                                mListKeyIdServicos.remove(idServico);
                                ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                                break;
                            }
                        }
                        if (ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().containsKey(idServico)){
                            if (!mListKeyIdServicos.contains(idServico)){
                                mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(idServico));
                                int position = mList.indexOf(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(idServico));
                                mListKeyIdServicos.add(idServico);
                                ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position);
                            }
                        }
                    }
                    liberarFab();
                }
            });
        }
    }*/

    /*public void liberarFormulario(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                liberarFab();
                buttonAddServico.setClickable(true);
                buttonAddServico.setVisibility(View.VISIBLE);
                progressServicos.setVisibility(View.INVISIBLE);
            }
        });
    }*/

    /*private void liberarFab(){
        if (ConfiguracaoInicialActivity.getServicosSalao()!= null && ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao()!= null && ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().size() > 0){
            this.fabServicos.setClickable(true);
            this.fabServicos.setVisibility(View.VISIBLE);
        }else{
            this.fabServicos.setClickable(false);
            this.fabServicos.setVisibility(View.INVISIBLE);
        }
    }*/







    private void aguardarDados(){
        this.formServicos.setClickable(false);
        this.formServicos.setVisibility(View.INVISIBLE);
        this.progressServicos.setVisibility(View.VISIBLE);
        this.fabServicos.setVisibility(View.INVISIBLE);
        this.fabServicos.setClickable(false);
    }



    /*public void criarServico2(){
        if (preenchimentoIsValid()){
            if (!criandoServico){
                criandoServico = true;
                Servico servico = new Servico();
                servico.setNome(this.nomeServico.getText().toString());
                //servico.setIcone((Integer) this.adapter.getItem(this.spinnerIcones.getSelectedItemPosition()));
                servico.setPreco(gerarPrecoFloat());
                servico.setDuracao(gerarDuracao());
                servico.setDescricao(this.descricaoServico.getText().toString());
                servico.setIdServico(((ConfiguracaoInicialActivity)getActivity()).getRefServicosSalao().push().getKey());
                ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().put(servico.getIdServico(),servico);
                this.mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(servico.getIdServico()));
                ((RecyclerAdapterServicos) this.mRecyclerView.getAdapter()).addItemList(this.mList.size()-1);
                ((ConfiguracaoInicialActivity)getActivity()).adicionarServicoFirebase(servico.getIdServico());
                limparCampos();
                criandoServico = false;
            }
        }
    }*/

    /*private void atualizarFormulario(){
        for (Iterator<Servico> iterator = this.mList.iterator(); iterator.hasNext();){
            if (!ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().containsKey(iterator.next().getIdServico())){
                int position = this.mList.indexOf(iterator.next());
                this.mList.remove(iterator.next());
                ((RecyclerAdapterServicos)this.mRecyclerView.getAdapter()).removeItemList(position);
            }
        }
        for (String key : ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().keySet()){
            if (!this.mList.contains(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(key))){
                this.mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(key));
                ((RecyclerAdapterServicos)this.mRecyclerView.getAdapter()).addItemList(this.mList.size()-1);
            }
        }
    }*/


    public void addServico(Servico servico) {
       /* if (this.servicoList == null) {
            this.servicoList = new ArrayList<Servico>();
        }
        this.servicoList.add(servico);
        this.nomesServicos.add(servico.getNome());
        RecyclerAdapterServicos recyclerAdapter = (RecyclerAdapterServicos) this.mRecyclerView.getAdapter();
        recyclerAdapter.addItemList(this.servicoList.size());
        limparCampos();*/
    }

    public void removerServico(Servico servico){
       /* RecyclerAdapterServicos recyclerAdapter = (RecyclerAdapterServicos) this.mRecyclerView.getAdapter();
        int position = this.servicoList.indexOf(servico);
        nomesServicos.remove(servico.getNome());
        servicoList.remove(servico);
        recyclerAdapter.removeItemList(position);*/
    }


    public void removeList(){
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        Log.i("testeteste","before clear size = " + list.size());
        list.clear();
        Log.i("testeteste","after clear size = " + list.size());
        list.add("3");
        list.add("4");
        Log.i("testeteste","index = " + list.indexOf("4"));
    }

}

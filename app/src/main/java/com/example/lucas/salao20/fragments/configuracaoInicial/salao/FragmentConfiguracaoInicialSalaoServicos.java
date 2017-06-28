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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.activitys.ConfiguracaoInicialActivity;
import com.example.lucas.salao20.adapters.AdapterSpinnerIcones;
import com.example.lucas.salao20.adapters.RecyclerAdapterServicos;
import com.example.lucas.salao20.geral.geral.Servico;
import com.example.lucas.salao20.interfaces.RecyclerViewOnClickListenerHack;

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

    private Handler handler;

    private ProgressBar progressServicos;
    private FloatingActionButton fabServicos;

    private AdapterSpinnerIcones adapter;
    private RecyclerView mRecyclerView;

    private AutoCompleteTextView nomeServico;
    private EditText precoServico;
    private Spinner spinnerIcones;
    private Spinner spinnerHoras;
    private Spinner spinnerMinutos;
    private AutoCompleteTextView descricaoServico;
    private Button buttonAddServico;

    //CONTROLES
    private static boolean fragmentServicosSalaoAtivo;
    private boolean criandoServico;

    //ARRAYS
    private List<Servico> mList;
    private List<String> mListKeyIdServicos;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_configuracao_inicial_salao_servicos,container,false);
        this.handler = new Handler();
        initControles();
        initViews(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentServicosSalaoAtivo = true;
        criandoServico = false;
        Log.i("testeteste","FragmentConfiguracaoInicialSalaoServicos onStart");
        iniciarFormulario();
    }

    @Override
    public void onStop() {
        super.onStop();
        fragmentServicosSalaoAtivo = false;
        Log.i("testeteste","FragmentConfiguracaoInicialSalaoServicos onStop");
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
                        ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().remove(idServico);
                        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                        ((ConfiguracaoInicialActivity)getActivity()).getRefServicosSalao().child(idServico).removeValue();

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
            builder.setIcon(mList.get(position).getIcone());
        }
        builder.setCancelable(true);
        alertDialogBuilderMessage(builder, mList.get(position).getNome(), this.mList.get(position).getPreco().toString(), this.mList.get(position).getDuracao(), this.mList.get(position).getDescricao());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initViews(View view){
        this.fabServicos = (FloatingActionButton) view.findViewById(R.id.fab_fragment_servicos);
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

        createSpinnerIcones();
        createRecyclerViewServicosAdicionados(view);
    }

    private void initControles(){
        fragmentServicosSalaoAtivo = false;
        this.criandoServico = false;
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

    private void iniciarFormulario(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (ConfiguracaoInicialActivity.getServicosSalao() != null && ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao() != null){
                    Log.i("testeteste","iniciarFormulario servicosSalao != null");
                    if (mList.size() > 0){
                        do{
                            int position = mList.size()-1;
                            mListKeyIdServicos.remove(mList.get(position).getIdServico());
                            mList.remove(position);
                            ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).removeItemList(position);
                        }while(mList.size() > 0);
                    }
                    for (String key : ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().keySet()){
                        mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(key));
                        int position = mList.indexOf(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(key));
                        mListKeyIdServicos.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(key).getIdServico());
                        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position);
                    }
                    liberarFormulario();
                }else{
                    Log.i("testeteste","iniciarFormulario servicosSalao == null");
                }
            }
        });
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
    }

    public void servicoRemovido(final String idServico){
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
    }

    public void liberarFormulario(){
        this.handler.post(new Runnable() {
            @Override
            public void run() {
                liberarFab();
                buttonAddServico.setClickable(true);
                buttonAddServico.setVisibility(View.VISIBLE);
                progressServicos.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void liberarFab(){
        if (ConfiguracaoInicialActivity.getServicosSalao()!= null && ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao()!= null && ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().size() > 0){
            this.fabServicos.setClickable(true);
            this.fabServicos.setVisibility(View.VISIBLE);
        }else{
            this.fabServicos.setClickable(false);
            this.fabServicos.setVisibility(View.INVISIBLE);
        }
    }

    public void criarServico(){
        if (preenchimentoIsValid()){
            if (!criandoServico){
                criandoServico = true;
                this.handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Servico servico = new Servico();
                        servico.setNome(nomeServico.getText().toString());
                        servico.setIcone(adapter.getItem(spinnerIcones.getSelectedItemPosition()));
                        servico.setPreco(gerarPrecoFloat());
                        servico.setDuracao(gerarDuracao());
                        servico.setDescricao(descricaoServico.getText().toString());
                        servico.setIdServico(((ConfiguracaoInicialActivity)getActivity()).getRefServicosSalao().push().getKey());
                        ConfiguracaoInicialActivity.getServicosSalao().addServico(servico);
                        mList.add(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(servico.getIdServico()));
                        int position = mList.indexOf(ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(servico.getIdServico()));
                        mListKeyIdServicos.add(servico.getIdServico());
                        ((RecyclerAdapterServicos) mRecyclerView.getAdapter()).addItemList(position);
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put(servico.getIdServico(), ConfiguracaoInicialActivity.getServicosSalao().getServicosSalao().get(servico.getIdServico()).toMap());
                        ((ConfiguracaoInicialActivity)getActivity()).getRefServicosSalao().updateChildren(childUpdates);
                        limparCampos();
                        criandoServico = false;
                    }
                });
            }
        }
    }

    //AUXILIARES
    private ArrayList<Integer> createArrayListIcones(){
        ArrayList<Integer> icones = new ArrayList<Integer>();
        icones.add(R.mipmap.ic_launcher);
        icones.add(R.mipmap.ic_launcher);
        icones.add(R.mipmap.ic_launcher);

        return icones;
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

    private void limparCampos(){
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



    public void criarServico2(){
        if (preenchimentoIsValid()){
            if (!criandoServico){
                criandoServico = true;
                Servico servico = new Servico();
                servico.setNome(this.nomeServico.getText().toString());
                servico.setIcone((Integer) this.adapter.getItem(this.spinnerIcones.getSelectedItemPosition()));
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
    }

    private void atualizarFormulario(){
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
    }


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

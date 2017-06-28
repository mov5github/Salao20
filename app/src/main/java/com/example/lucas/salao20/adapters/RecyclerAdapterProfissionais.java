package com.example.lucas.salao20.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lucas.salao20.R;
import com.example.lucas.salao20.geral.geral.Profissional;
import com.example.lucas.salao20.geral.geral.Servico;
import com.example.lucas.salao20.interfaces.RecyclerViewOnClickListenerHack;

import java.util.Iterator;
import java.util.List;

/**
 * Created by Lucas on 21/03/2017.
 */

public class RecyclerAdapterProfissionais extends RecyclerView.Adapter<RecyclerAdapterProfissionais.MyViewHolder>{
    private List<Profissional> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mRecyclerViewOnClickListenerHack;

    public RecyclerAdapterProfissionais(List<Profissional> mList, Context context) {
        this.mList = mList;
        this.mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = this.mLayoutInflater.inflate(R.layout.recycleview_profissionais_item, viewGroup, false);
        MyViewHolder mvh = new MyViewHolder(v);
        return mvh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder myViewHolder, int position) {
        myViewHolder.ivFoto.setImageResource(mList.get(position).getCadastroComplementar().getFoto());
        myViewHolder.tvNome.setText("NOME PROFISSIONAL");
        myViewHolder.tvCodUnico.setText("#000000");
    }

    @Override
    public int getItemCount() {
        return this.mList.size();
    }

    public void setRecyclerViewOnClickListenerHack(RecyclerViewOnClickListenerHack r){
        this.mRecyclerViewOnClickListenerHack = r;
    }

    public void addItemList(int position){
        notifyItemInserted(position);
    }

    public void removeItemList(int position){
        notifyItemRemoved(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView ivFoto;
        public TextView tvNome;
        public TextView tvCodUnico;
        public MyViewHolder(View itemView){
            super(itemView);

            ivFoto = (ImageView) itemView.findViewById(R.id.icone_profissional_item);
            tvNome = (TextView) itemView.findViewById(R.id.nome_profissional_item);
            tvCodUnico = (TextView) itemView.findViewById(R.id.codigo_unico_profissional_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mRecyclerViewOnClickListenerHack != null){
                mRecyclerViewOnClickListenerHack.onClickListener(v, getAdapterPosition());
            }
        }
    }

    /*public void exibirList(){
        for (Iterator<Servico> iterator = this.mList.iterator(); iterator.hasNext();){
            int position = this.mList.indexOf(iterator.next());
            Log.i("testeteste","mList recycler position = " + position);
        }
    }*/
}

package com.example.lucas.salao20.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

import com.example.lucas.salao20.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 21/03/2017.
 */

public class AdapterSpinnerIcones extends BaseAdapter{
    private Context context;
    private ArrayList<Integer> icones;
    private LayoutInflater inflater;

    public AdapterSpinnerIcones(Context context, ArrayList<Integer> icones) {
        this.context = context;
        this.icones = icones;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return this.icones.size();
    }

    @Override
    public Integer getItem(int position) {
        return this.icones.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.spinner_icones_item,null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview_servico_item);
        imageView.setImageResource(this.icones.get(position));

        return convertView;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null){
            convertView = inflater.inflate(R.layout.spinner_icones_item,null);
        }
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview_servico_item);
        imageView.setImageResource(this.icones.get(position));

        return convertView;
    }
}

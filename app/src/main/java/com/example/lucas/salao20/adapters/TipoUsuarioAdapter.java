package com.example.lucas.salao20.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.example.lucas.salao20.fragments.tipoUsuario.FragmentTipoUsuario;

/**
 * Created by Lucas on 21/03/2017.
 */

public class TipoUsuarioAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles;
    private Fragment[] mFragments;

    public TipoUsuarioAdapter(FragmentManager fm, Context ctx, String[] titulos){
        super(fm);
        mContext = ctx;
        titles =  titulos;
        mFragments = new Fragment[1];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        switch (position){
            case 0:
                frag = new FragmentTipoUsuario();
                break;
            default:
                break;
        }

        if (frag != null){
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            frag.setArguments(bundle);
        }

        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (titles[position]);
    }

    public Fragment getFragment(int position){
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = mFragments[position];
                break;
            default:
                break;
        }
        return fragment;
    }
}

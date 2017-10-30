package com.example.lucas.salao20.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.home.cliente.FragmentHomeClienteAgenda;
import com.example.lucas.salao20.fragments.home.cliente.FragmentHomeClientePromocoes;
import com.example.lucas.salao20.fragments.home.cliente.FragmentHomeClienteSaloes;
import com.example.lucas.salao20.fragments.home.profissional.FragmentHomeProfissionalAgendas;
import com.example.lucas.salao20.fragments.home.profissional.FragmentHomeProfissionalCadeiras;
import com.example.lucas.salao20.fragments.home.profissional.FragmentHomeProfissionalDados;
import com.example.lucas.salao20.fragments.home.salao.FragmentHomeSalaoAgendas;
import com.example.lucas.salao20.fragments.home.salao.FragmentHomeSalaoDados;
import com.example.lucas.salao20.fragments.home.salao.FragmentHomeSalaoPromocoes;

/**
 * Created by Lucas on 21/03/2017.
 */

public class HomeSalaoAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles;
    private Fragment[] mFragments;

    public HomeSalaoAdapter(FragmentManager fm, Context ctx, String[] titulos){
        super(fm);
        mContext = ctx;
        titles =  titulos;
        mFragments = new Fragment[3];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (position) {
            case 0:
                frag = new FragmentHomeSalaoDados();
                mFragments[position] = frag;
                break;
            case 1:
                frag = new FragmentHomeSalaoAgendas();
                mFragments[position] = frag;
                break;
            case 2:
                frag = new FragmentHomeSalaoPromocoes();
                mFragments[position] = frag;
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

    public Fragment getCurrentFragment(int position){
        Fragment fragment = null;
        switch (position){
            case 0:
                fragment = mFragments[position];
                break;
            case 1:
                fragment = mFragments[position];
                break;
            case 2:
                fragment = mFragments[position];
                break;
            default:
                break;
        }
        return fragment;
    }
}

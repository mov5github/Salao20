package com.example.lucas.salao20.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCabeleireiro;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCliente;
import com.example.lucas.salao20.fragments.home.FragmentHomeSalaoAgendas;
import com.example.lucas.salao20.fragments.home.FragmentHomeSalaoDados;
import com.example.lucas.salao20.fragments.home.FragmentHomeSalaoPromocoes;

/**
 * Created by Lucas on 21/03/2017.
 */

public class HomeAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles;
    private String tipoUsuario;
    private Fragment[] mFragments;

    public HomeAdapter(FragmentManager fm, Context ctx, String[] titulos, String tipoUsuario){
        super(fm);
        mContext = ctx;
        titles =  titulos;
        mFragments = new Fragment[3];
        this.tipoUsuario = tipoUsuario;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        switch (tipoUsuario) {
            case TipoUsuarioENUM.SALAO:
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
                break;
            case TipoUsuarioENUM.CLIENTE:
                switch (position) {
                    case 0:
                        frag = new FragmentBasicoCliente();
                        break;
                    default:
                        break;
                }
                break;
            case TipoUsuarioENUM.CABELEIREIRO:
                switch (position) {
                    case 0:
                        frag = new FragmentBasicoCabeleireiro();
                        break;
                    default:
                        break;
                }
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

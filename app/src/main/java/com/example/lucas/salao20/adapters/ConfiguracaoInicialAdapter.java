package com.example.lucas.salao20.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCabeleireiro;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentBasicoCliente;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentProfissionais;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentServicos;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentTipoCadastro;

/**
 * Created by Lucas on 21/03/2017.
 */

public class ConfiguracaoInicialAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private String[] titles;
    private String tipoUsuario;
    private Fragment[] mFragments;

    public ConfiguracaoInicialAdapter(FragmentManager fm, Context ctx, String[] titulos, String tipoUsuario){
        super(fm);
        mContext = ctx;
        titles =  titulos;
        if (tipoUsuario == null){
            this.tipoUsuario = null;
            mFragments = new Fragment[1];
        }else {
            switch (tipoUsuario){
                case TipoUsuarioENUM.SALAO:
                    mFragments = new Fragment[3];
                    break;
                case TipoUsuarioENUM.CABELEIREIRO:
                    mFragments = new Fragment[3];
                    break;
                case TipoUsuarioENUM.CLIENTE:
                    mFragments = new Fragment[3];
                    break;
                default:
                    break;
            }
            this.tipoUsuario = tipoUsuario;
        }
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if (this.tipoUsuario == null){
            switch (position){
                case 0:
                    frag = new FragmentTipoCadastro();
                    break;
                default:
                    break;
            }
        }else if (tipoUsuario.equals(TipoUsuarioENUM.SALAO)){
            switch (position){
                case 0:
                    frag = new FragmentFuncionamento();
                    mFragments[position] = frag;
                    break;
                case 1:
                    frag = new FragmentServicos();
                    mFragments[position] = frag;
                    break;
                case 2:
                    frag = new FragmentProfissionais();
                    mFragments[position] = frag;
                    break;
                default:
                    break;
            }
        }else if (tipoUsuario.equals(TipoUsuarioENUM.CLIENTE)){
            switch (position){
                case 0:
                    frag = new FragmentBasicoCliente();
                    break;
                default:
                    break;
            }
        }
        else if (tipoUsuario.equals(TipoUsuarioENUM.CABELEIREIRO)){
            switch (position){
                case 0:
                    frag = new FragmentBasicoCabeleireiro();
                    break;
                default:
                    break;
            }
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

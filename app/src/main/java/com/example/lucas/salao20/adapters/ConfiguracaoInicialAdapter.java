package com.example.lucas.salao20.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.lucas.salao20.enumeradores.TipoUsuarioENUM;
import com.example.lucas.salao20.fragments.configuracaoInicial.profissional.FragmentConfiguracaoInicialProfissionalBasico;
import com.example.lucas.salao20.fragments.configuracaoInicial.cliente.FragmentConfiguracaoInicialClienteBasico;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoProfissionais;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoFuncionamento;
import com.example.lucas.salao20.fragments.configuracaoInicial.salao.FragmentConfiguracaoInicialSalaoServicos;
import com.example.lucas.salao20.fragments.configuracaoInicial.FragmentConfiguracaoInicialTipoCadastro;

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
                case TipoUsuarioENUM.PROFISSIONAl:
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
                    frag = new FragmentConfiguracaoInicialTipoCadastro();
                    break;
                default:
                    break;
            }
        }else if (tipoUsuario.equals(TipoUsuarioENUM.SALAO)){
            switch (position){
                case 0:
                    frag = new FragmentConfiguracaoInicialSalaoFuncionamento();
                    mFragments[position] = frag;
                    break;
                case 1:
                    frag = new FragmentConfiguracaoInicialSalaoServicos();
                    mFragments[position] = frag;
                    break;
                case 2:
                    frag = new FragmentConfiguracaoInicialSalaoProfissionais();
                    mFragments[position] = frag;
                    break;
                default:
                    break;
            }
        }else if (tipoUsuario.equals(TipoUsuarioENUM.CLIENTE)){
            switch (position){
                case 0:
                    frag = new FragmentConfiguracaoInicialClienteBasico();
                    break;
                default:
                    break;
            }
        }
        else if (tipoUsuario.equals(TipoUsuarioENUM.PROFISSIONAl)){
            switch (position){
                case 0:
                    frag = new FragmentConfiguracaoInicialProfissionalBasico();
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

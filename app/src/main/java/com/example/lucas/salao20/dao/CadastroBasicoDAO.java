package com.example.lucas.salao20.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.salao20.dao.model.CadastroBasico;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Lucas on 17/03/2017.
 */

public class CadastroBasicoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CadastroBasicoDAO(Context context){
        this.databaseHelper = new DatabaseHelper(context);
    }

    //ACESSOS
    public List<CadastroBasico> listarCadastrosBasicos(){
        List<CadastroBasico> cadastroBasicos = new ArrayList<CadastroBasico>();
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroBasico.TABELA,
                DatabaseHelper.CadastroBasico.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            CadastroBasico model = criarCadastroBasico(cursor);
            cadastroBasicos.add(model);
        }
        cursor.close();
        return cadastroBasicos;
    }
    public List<CadastroBasico> listarCadastrosBasicosCloud(){
        List<CadastroBasico> cadastrosBasicos = new ArrayList<CadastroBasico>();
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroBasico.TABELA_CLOUD,
                DatabaseHelper.CadastroBasico.COLUNAS, null, null, null, null, null);

        while (cursor.moveToNext()){
            CadastroBasico model = criarCadastroBasico(cursor);
            cadastrosBasicos.add(model);
        }
        cursor.close();
        return cadastrosBasicos;
    }

    public long salvarCadastroBasico(CadastroBasico cadastroBasico){
        //TODO
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroBasico.NIVEL_USUARIO, cadastroBasico.getNivelUsuario());
        if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
            values.put(DatabaseHelper.CadastroBasico.TIPO_USUARIO, cadastroBasico.getTipoUsuario());
        }

        if(cadastroBasico.get_uid() != null){
            long retorno = this.database.update(DatabaseHelper.CadastroBasico.TABELA, values,
                    "uid = ?", new String[]{cadastroBasico.get_uid()});
            if (retorno < 1){
                return -1;
            }else {
                return retorno;
            }
        }else {
            return getDatabase().insert(DatabaseHelper.CadastroBasico.TABELA, null, values);
        }
    }
    public long salvarCadastroBasicoCloud(CadastroBasico cadastroBasico){
        //TODO
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroBasico.NIVEL_USUARIO, cadastroBasico.getNivelUsuario());
        if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
            values.put(DatabaseHelper.CadastroBasico.TIPO_USUARIO, cadastroBasico.getTipoUsuario());
        }

        if(cadastroBasico.get_uid() != null){
            long retorno = this.database.update(DatabaseHelper.CadastroBasico.TABELA_CLOUD, values,
                    "uid = ?", new String[]{cadastroBasico.get_uid()});
            if (retorno < 1){
                return -1;
            }else {
                return retorno;
            }
        }else {
            return getDatabase().insert(DatabaseHelper.CadastroBasico.TABELA_CLOUD, null, values);
        }
    }

    public long atualizarCadastroBasico(CadastroBasico cadastroBasico){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroBasico.NIVEL_USUARIO, cadastroBasico.getNivelUsuario());
        if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
            values.put(DatabaseHelper.CadastroBasico.TIPO_USUARIO, cadastroBasico.getTipoUsuario());
        }

        return this.database.update(DatabaseHelper.CadastroBasico.TABELA, values,
                "uid = ?", new String[]{cadastroBasico.get_uid()});
    }
    public long atualizarCadastroBasicoCloud(CadastroBasico cadastroBasico){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroBasico.NIVEL_USUARIO, cadastroBasico.getNivelUsuario());
        if (cadastroBasico.getTipoUsuario() != null && !cadastroBasico.getTipoUsuario().isEmpty()){
            values.put(DatabaseHelper.CadastroBasico.TIPO_USUARIO, cadastroBasico.getTipoUsuario());
        }

        return this.database.update(DatabaseHelper.CadastroBasico.TABELA_CLOUD, values,
                "uid = ?", new String[]{cadastroBasico.get_uid()});
    }

    public CadastroBasico buscarCadastroBasicoPorUID(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroBasico.TABELA,
                DatabaseHelper.CadastroBasico.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            CadastroBasico model = criarCadastroBasico(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public CadastroBasico buscarCadastroBasicoPorUIDCloud(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroBasico.TABELA_CLOUD,
                DatabaseHelper.CadastroBasico.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            CadastroBasico model = criarCadastroBasico(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }



    //AUXILIARES
    private SQLiteDatabase getDatabase(){
        if (this.database == null){
            this.database = this.databaseHelper.getWritableDatabase();
        }
        return this.database;
    }

    public void fechar(){
        this.databaseHelper.close();
        this.database = null;
    }

    private CadastroBasico criarCadastroBasico(Cursor cursor){
        CadastroBasico model = new CadastroBasico(
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroBasico.UID)),
                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.CadastroBasico.NIVEL_USUARIO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroBasico.TIPO_USUARIO))
        );
        return model;
    }

}

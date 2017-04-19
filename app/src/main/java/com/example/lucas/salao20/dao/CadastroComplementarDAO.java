package com.example.lucas.salao20.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.salao20.dao.model.CadastroComplementar;

/**
 * Created by Lucas on 14/04/2017.
 */

public class CadastroComplementarDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CadastroComplementarDAO(Context context){
        this.databaseHelper = new DatabaseHelper(context);
    }

    //ACESSOS
    public long salvarCadastroComplementar(CadastroComplementar cadastroComplementar){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroComplementar.UID, cadastroComplementar.getUid());
        if (cadastroComplementar.getNome() != null && !cadastroComplementar.getNome().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.NOME, cadastroComplementar.getNome());
        }
        if (cadastroComplementar.getEndereco() != null && !cadastroComplementar.getEndereco().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.ENDERECO, cadastroComplementar.getEndereco());
        }
        if (cadastroComplementar.getNumeroEndereco() != null){
            values.put(DatabaseHelper.CadastroComplementar.NUMERO_ENDERECO, cadastroComplementar.getNumeroEndereco());
        }
        if (cadastroComplementar.getComplementoEndereco() != null && !cadastroComplementar.getComplementoEndereco().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.COMPLEMENTO_ENDERECO, cadastroComplementar.getComplementoEndereco());
        }
        if (cadastroComplementar.getCep() != null){
            values.put(DatabaseHelper.CadastroComplementar.CEP, cadastroComplementar.getCep());
        }
        if (cadastroComplementar.getTelefoneFixo1() != null){
            values.put(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_1, cadastroComplementar.getTelefoneFixo1());
        }
        if (cadastroComplementar.getTelefoneFixo2() != null){
            values.put(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_2, cadastroComplementar.getTelefoneFixo2());
        }
        if (cadastroComplementar.getWhatsapp() != null){
            values.put(DatabaseHelper.CadastroComplementar.WHATSAPP, cadastroComplementar.getWhatsapp());
        }
        if (cadastroComplementar.getCelular1() != null){
            values.put(DatabaseHelper.CadastroComplementar.CELULAR_1, cadastroComplementar.getCelular1());
        }
        if (cadastroComplementar.getCelular2() != null){
            values.put(DatabaseHelper.CadastroComplementar.CELULAR_2, cadastroComplementar.getCelular2());
        }
        if (cadastroComplementar.getFacebook() != null && !cadastroComplementar.getFacebook().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.FACEBOOK, cadastroComplementar.getFacebook());
        }
        if (cadastroComplementar.getLogo() != null){
            values.put(DatabaseHelper.CadastroComplementar.LOGO, cadastroComplementar.getLogo());
        }

        return getDatabase().insert(DatabaseHelper.CadastroComplementar.TABELA, null, values);
    }
    public long salvarCadastroComplementarCloud(CadastroComplementar cadastroComplementar){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.CadastroComplementar.UID, cadastroComplementar.getUid());
        if (cadastroComplementar.getNome() != null && !cadastroComplementar.getNome().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.NOME, cadastroComplementar.getNome());
        }
        if (cadastroComplementar.getEndereco() != null && !cadastroComplementar.getEndereco().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.ENDERECO, cadastroComplementar.getEndereco());
        }
        if (cadastroComplementar.getNumeroEndereco() != null){
            values.put(DatabaseHelper.CadastroComplementar.NUMERO_ENDERECO, cadastroComplementar.getNumeroEndereco());
        }
        if (cadastroComplementar.getComplementoEndereco() != null && !cadastroComplementar.getComplementoEndereco().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.COMPLEMENTO_ENDERECO, cadastroComplementar.getComplementoEndereco());
        }
        if (cadastroComplementar.getCep() != null){
            values.put(DatabaseHelper.CadastroComplementar.CEP, cadastroComplementar.getCep());
        }
        if (cadastroComplementar.getTelefoneFixo1() != null){
            values.put(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_1, cadastroComplementar.getTelefoneFixo1());
        }
        if (cadastroComplementar.getTelefoneFixo2() != null){
            values.put(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_2, cadastroComplementar.getTelefoneFixo2());
        }
        if (cadastroComplementar.getWhatsapp() != null){
            values.put(DatabaseHelper.CadastroComplementar.WHATSAPP, cadastroComplementar.getWhatsapp());
        }
        if (cadastroComplementar.getCelular1() != null){
            values.put(DatabaseHelper.CadastroComplementar.CELULAR_1, cadastroComplementar.getCelular1());
        }
        if (cadastroComplementar.getCelular2() != null){
            values.put(DatabaseHelper.CadastroComplementar.CELULAR_2, cadastroComplementar.getCelular2());
        }
        if (cadastroComplementar.getFacebook() != null && !cadastroComplementar.getFacebook().isEmpty()){
            values.put(DatabaseHelper.CadastroComplementar.FACEBOOK, cadastroComplementar.getFacebook());
        }
        if (cadastroComplementar.getLogo() != null){
            values.put(DatabaseHelper.CadastroComplementar.LOGO, cadastroComplementar.getLogo());
        }

        return getDatabase().insert(DatabaseHelper.CadastroComplementar.TABELA_CLOUD, null, values);
    }

    public CadastroComplementar buscarCadastroComplementarPorUID(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroComplementar.TABELA,
                DatabaseHelper.CadastroComplementar.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            CadastroComplementar model = criarCadastroComplementar(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public CadastroComplementar buscarCadastroComplementarPorUIDCloud(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.CadastroComplementar.TABELA_CLOUD,
                DatabaseHelper.CadastroComplementar.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            CadastroComplementar model = criarCadastroComplementar(cursor);
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

    private CadastroComplementar criarCadastroComplementar(Cursor cursor){
        CadastroComplementar model = new CadastroComplementar(
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.UID)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.NOME)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.ENDERECO)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.NUMERO_ENDERECO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.COMPLEMENTO_ENDERECO)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.CEP)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_1)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.TELEFONE_FIXO_2)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.WHATSAPP)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.CELULAR_1)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.CELULAR_2)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.FACEBOOK)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementar.LOGO))
        );
        return model;
    }
}

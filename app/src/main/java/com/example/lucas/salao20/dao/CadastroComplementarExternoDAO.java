package com.example.lucas.salao20.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.salao20.dao.model.CadastroComplementarExterno;

/**
 * Created by Lucas on 10/04/2017.
 */

public class CadastroComplementarExternoDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public CadastroComplementarExternoDAO(Context context){
        this.databaseHelper = new DatabaseHelper(context);
    }

    //ACESSOS


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

    private CadastroComplementarExterno criarCadastroComplementar(Cursor cursor){
        CadastroComplementarExterno model = new CadastroComplementarExterno(
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno._ID)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.VERSAO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.DATA_MODIFICACAO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.NOME)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.ENDERECO)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.NUMERO_ENDERECO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.COMPLEMENTO_ENDERECO)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.CEP)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.TELEFONE_FIXO_1)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.TELEFONE_FIXO_2)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.WHATSAPP)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.CELULAR_1)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.CELULAR_2)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.FACEBOOK)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.LOGO)),
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.CRIP_UID_CLIENTE_CLIENTES)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.COD_UNICO_SALAO_PROFISSIONAL)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.COD_UNICO_CABELEIREIRO_PROFISSIONAL)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.CadastroComplementarExterno.COD_UNICO_SALAO_SALOES_ESCOLHIDOS))
        );
        return model;
    }
}

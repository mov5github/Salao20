package com.example.lucas.salao20.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lucas.salao20.dao.model.User;

/**
 * Created by Lucas on 10/04/2017.
 */

public class UserDAO {
    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public UserDAO(Context context) {
        this.databaseHelper = new DatabaseHelper(context);
    }

    //ACESSOS
    public User buscarUserPorUID(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.User.TABELA,
                DatabaseHelper.User.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            User model = criarUser(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }
    public User buscarUserUIDCloud(String uid){
        Cursor cursor = getDatabase().query(DatabaseHelper.User.TABELA_CLOUD,
                DatabaseHelper.User.COLUNAS, "uid = ?", new String[]{uid}, null, null, null);

        if (cursor.moveToNext()){
            User model = criarUser(cursor);
            cursor.close();
            return model;
        }else {
            return null;
        }
    }

    public long salvarUser(User user){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.User.UID, user.getUid());
        if (user.getCodUnicoUserSalao() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_SALAO, user.getCodUnicoUserSalao());
        }
        if (user.getCodUnicoUserCabeleireiro() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_CABELEIREIRO, user.getCodUnicoUserCabeleireiro());
        }

        return getDatabase().insert(DatabaseHelper.User.TABELA, null, values);
    }
    public long salvarUserCloud(User user){
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.User.UID, user.getUid());
        if (user.getCodUnicoUserSalao() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_SALAO, user.getCodUnicoUserSalao());
        }
        if (user.getCodUnicoUserCabeleireiro() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_CABELEIREIRO, user.getCodUnicoUserCabeleireiro());
        }

        return getDatabase().insert(DatabaseHelper.User.TABELA_CLOUD, null, values);
    }

    public long atualizarUser(User user){
        ContentValues values = new ContentValues();
        if (user.getCodUnicoUserSalao() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_SALAO, user.getCodUnicoUserSalao());
        }
        if (user.getCodUnicoUserCabeleireiro() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_CABELEIREIRO, user.getCodUnicoUserCabeleireiro());
        }

        return this.database.update(DatabaseHelper.User.TABELA, values,
                "uid = ?", new String[]{user.getUid()});
    }
    public long atualizarUserCloud(User user){
        ContentValues values = new ContentValues();
        if (user.getCodUnicoUserSalao() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_SALAO, user.getCodUnicoUserSalao());
        }
        if (user.getCodUnicoUserCabeleireiro() != null){
            values.put(DatabaseHelper.User.COD_UNICO_USER_CABELEIREIRO, user.getCodUnicoUserCabeleireiro());
        }

        return this.database.update(DatabaseHelper.User.TABELA_CLOUD, values,
                "uid = ?", new String[]{user.getUid()});
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

    private User criarUser(Cursor cursor){
        User model = new User(
                cursor.getString(cursor.getColumnIndex(DatabaseHelper.User.UID)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.User.COD_UNICO_USER_SALAO)),
                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.User.COD_UNICO_USER_CABELEIREIRO))
        );
        return model;
    }
}

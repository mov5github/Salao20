package com.example.lucas.salao20.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lucas on 17/03/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final String NOME_BD = "principal";
    private static final int VERSAO_BD = 1;

    public DatabaseHelper(Context context){
        super(context,NOME_BD,null,VERSAO_BD);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //TABELA DE VERSOES
        db.execSQL("create table versoes(_id integer primary key autoincrement, "
                +"identificacaotabela text not null, versao integer not null, datamodificacao text not null)");

        //TABELA DE VERSOES CLOUD
        db.execSQL("create table versoescloud(_id integer primary key autoincrement, "
                +"identificacaotabela text not null, versao integer not null, datamodificacao text not null)");

        //TABELA DE USER
        db.execSQL("create table user(uid text primary key, "
                +"codunicousersalao integer, codunicousercabeleireiro integer)");

        //TABELA DE USER CLOUD
        db.execSQL("create table usercloud(uid text primary key, "
                +"codunicousersalao integer, codunicousercabeleireiro integer)");

        //TABELA DE CADASTRO BASICO
        db.execSQL("create table cadastrobasico(uid text primary key, "
                +"nivelusuario real not null, tipousuario text)");

        //TABELA DE CADASTRO BASICO CLOUD
        db.execSQL("create table cadastrobasicocloud(uid text primary key, "
                +"nivelusuario real not null, tipousuario text)");

        //TABELA DE CADASTRO COMPLEMENTAR
        db.execSQL("create table cadastrocomplementar(uid text primary key, "
                +"nome text not null, endereco text not null, numendereco integer not null, " +
                "complementoendereco text, cep integer, telefonefixo1 integer, telefonefixo1 integer, " +
                "whatsapp integer, celular1 integer, celular2 integer, facebook text, logo integer)");

        //TABELA DE CADASTRO COMPLEMENTAR CLOUD
        db.execSQL("create table cadastrocomplementarcloud(uid text primary key, "
                +"nome text not null, endereco text not null, numeroendereco integer not null, " +
                "complementoendereco text, cep integer, telefonefixo1 integer, telefonefixo2 integer, " +
                "whatsapp integer, celular1 integer, celular2 integer, facebook text, logo integer)");

        //TABELA DE CADASTRO COMPLEMENTAR EXTERNO
        db.execSQL("create table cadastrocomplementarexterno(_id integer primary key not null, "
                +"nome text not null, endereco text not null, numendereco integer not null, complementoendereco text, versao integer, datamodificacao text, " +
                "cep integer, telefonefixo1 integer, telefonefixo1 integer, whatsapp integer, celular1 integer, celular2 integer, " +
                "facebook text, logo integer, cripuidclienteclientes text, codunicosalaoprofissional integer, codunicocabeleireiroprofissional integer, codunicosalaosaloesescolhidos integer)");

        //TABELA DE CADASTRO COMPLEMENTAR EXTERNO CLOUD
        db.execSQL("create table cadastrocomplementarexternocloud(_id integer primary key not null, "
                +"nome text not null, endereco text not null, numeroendereco integer not null, complementoendereco text, versao integer, datamodificacao text, " +
                "cep integer, telefonefixo1 integer, telefonefixo2 integer, whatsapp integer, celular1 integer, celular2 integer, " +
                "facebook text, logo integer, cripuidclienteclientes text, codunicosalaoprofissional integer, codunicocabeleireiroprofissional integer, codunicosalaosaloesescolhidos integer)");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static class Versoes{
        public static  final String TABELA = "versoes";
        public static  final String TABELA_CLOUD = "versoescloud";
        public static  final String _ID = "_id";
        public static  final String IDENTIFICACAO_TABELA = "identificacaotabela";
        public static  final String VERSAO = "versao";
        public static  final String DATA_MODIFICACAO = "datamodificacao";

        public static  final String[] COLUNAS = new String[]{_ID, IDENTIFICACAO_TABELA, VERSAO, DATA_MODIFICACAO};
    }

    public static class User{
        public static  final String TABELA = "user";
        public static  final String TABELA_CLOUD = "usercloud";
        public static  final String UID = "uid";
        public static  final String COD_UNICO_USER_SALAO = "codunicousersalao";
        public static  final String COD_UNICO_USER_CABELEIREIRO = "codunicousercabeleireiro";

        public static  final String[] COLUNAS = new String[]{UID, COD_UNICO_USER_SALAO, COD_UNICO_USER_CABELEIREIRO};
    }

    public static class CadastroBasico{
        public static  final String TABELA = "cadastrobasico";
        public static  final String TABELA_CLOUD = "cadastrobasicocloud";
        public static  final String UID = "uid";
        public static  final String NIVEL_USUARIO = "nivelusuario";
        public static  final String TIPO_USUARIO = "tipousuario";

        public static  final String[] COLUNAS = new String[]{UID, NIVEL_USUARIO, TIPO_USUARIO};
    }

    public static class CadastroComplementar{
        public static  final String TABELA = "cadastrocomplementar";
        public static  final String TABELA_CLOUD = "cadastrocomplementarcloud";
        public static  final String UID = "uid";
        public static  final String NOME = "nome";
        public static  final String ENDERECO = "endereco";
        public static  final String NUMERO_ENDERECO = "numeroendereco";
        public static  final String COMPLEMENTO_ENDERECO = "complementoendereco";
        public static  final String CEP = "cep";
        public static  final String TELEFONE_FIXO_1 = "telefonefixo1";
        public static  final String TELEFONE_FIXO_2 = "telefonefixo2";
        public static  final String WHATSAPP = "whatsapp";
        public static  final String CELULAR_1 = "celular1";
        public static  final String CELULAR_2 = "celular2";
        public static  final String FACEBOOK = "facebook";
        public static  final String LOGO = "logo";

        public static  final String[] COLUNAS = new String[]{UID, NOME, ENDERECO, NUMERO_ENDERECO, COMPLEMENTO_ENDERECO, CEP, TELEFONE_FIXO_1, TELEFONE_FIXO_2, WHATSAPP, CELULAR_1, CELULAR_2, FACEBOOK, LOGO};
    }

    public static class CadastroComplementarExterno{
        public static  final String TABELA = "cadastrocomplementarexterno";
        public static  final String TABELA_CLOUD = "cadastrocomplementarexternocloud";
        public static  final String _ID = "_id";
        public static  final String VERSAO = "versao";
        public static  final String DATA_MODIFICACAO = "datamodificacao";
        public static  final String NOME = "nome";
        public static  final String ENDERECO = "endereco";
        public static  final String NUMERO_ENDERECO = "numeroendereco";
        public static  final String COMPLEMENTO_ENDERECO = "complementoendereco";
        public static  final String CEP = "cep";
        public static  final String TELEFONE_FIXO_1 = "telefonefixo1";
        public static  final String TELEFONE_FIXO_2 = "telefonefixo2";
        public static  final String WHATSAPP = "whatsapp";
        public static  final String CELULAR_1 = "celular1";
        public static  final String CELULAR_2 = "celular2";
        public static  final String FACEBOOK = "facebook";
        public static  final String LOGO = "logo";
        public static  final String CRIP_UID_CLIENTE_CLIENTES = "cripuidclienteclientes";
        public static  final String COD_UNICO_SALAO_PROFISSIONAL = "codunicosalaoprofissional";
        public static  final String COD_UNICO_CABELEIREIRO_PROFISSIONAL = "codunicocabeleireiroprofissional";
        public static  final String COD_UNICO_SALAO_SALOES_ESCOLHIDOS = "codunicosalaosaloesescolhidos";

        public static  final String[] COLUNAS = new String[]{_ID, VERSAO, DATA_MODIFICACAO, NOME, ENDERECO, NUMERO_ENDERECO, COMPLEMENTO_ENDERECO, CEP, TELEFONE_FIXO_1, TELEFONE_FIXO_2, WHATSAPP, CELULAR_1, CELULAR_2, FACEBOOK, LOGO, CRIP_UID_CLIENTE_CLIENTES, COD_UNICO_SALAO_PROFISSIONAL, COD_UNICO_CABELEIREIRO_PROFISSIONAL, COD_UNICO_SALAO_SALOES_ESCOLHIDOS};
    }



    public static class Funcionamento{
        public static  final String TABELA = "funcionamento";
        public static  final String TABELA_CLOUD = "funcionamentocloud";
        public static  final String _ID = "_id";
        public static  final String DIA = "dia";
        public static  final String ABRE = "abre";
        public static  final String FECHA = "fecha";

        public static  final String[] COLUNAS = new String[]{_ID, DIA, ABRE, FECHA};

    }

    public static class Servico{
        public static  final String TABELA = "servico";
        public static  final String TABELA_CLOUD = "servicocloud";
        public static  final String _ID = "_id";
        public static  final String NOME = "nome";
        public static  final String ICONE = "icone";
        public static  final String DURACAO = "duracao";
        public static  final String PRECO = "preco";
        public static  final String DESCRICAO = "descricao";

        public static  final String[] COLUNAS = new String[]{_ID, NOME, ICONE, DURACAO, PRECO, DESCRICAO};
    }

    public static class Cabeleireiro{
        public static  final String TABELA = "cabeleireiro";
        public static  final String TABELA_CLOUD = "cabeleireirocloud";
        public static  final String _ID = "_id";
        public static  final String NOME = "nome";
        public static  final String FOTO = "foto";
        public static  final String CODIGO_UNICO = "codigounico";

        public static  final String[] COLUNAS = new String[]{_ID, NOME, FOTO, CODIGO_UNICO};
    }


}

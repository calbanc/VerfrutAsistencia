package com.verfrut.grupoverfrut_asistencia;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Perfil extends SQLiteOpenHelper {


    String tabla="Perfil";
    String idmenu="IdMenu";
    String idaplicacion="IdAplicacion";
    String idusuario="IdUsuario";
    String idempresa="IdEmpresa";
    String idzona="IdZona";
    String clave="CLAVE";
    String cod_emp="COD_EMP";
    String nom_emp="NOM_EMP";
    String cod_tem_default="COD_TEM_DEFAULT";
    String id_empresa_rem="ID_EMPRESA_REM";
    String precioestimado="PRECIO_ESTIMADO";
    private SQLiteDatabase db;

    final String creartablaperfil="CREATE TABLE "+tabla+"("
            +cod_emp+" TEXT NOT NULL,"
            +nom_emp+" TEXT NOT NULL,"
            +cod_tem_default+" TEXT NOT NULL,"
            +id_empresa_rem+" TEXT NOT NULL,"
            +idmenu+" TEXT NOT NULL,"
            +idaplicacion+" TEXT NOT NULL,"
            +idusuario+" TEXT NOT NULL,"
            +idempresa+" TEXT NOT NULL,"
            +idzona+" TEXT,"
            +clave+" TEXT,"
            +precioestimado+" text)";





    public Perfil(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        db.execSQL(creartablaperfil);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        db.execSQL("DROP TABLE Perfil");
        onCreate(db);
    }
}

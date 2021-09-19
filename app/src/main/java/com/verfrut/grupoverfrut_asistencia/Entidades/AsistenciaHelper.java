package com.verfrut.grupoverfrut_asistencia.Entidades;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class AsistenciaHelper  extends SQLiteOpenHelper {
    String id="ID";
    String fecha="FECHA";
    String hora="HORA";
    String latitud="LATITUD";
    String longitud="LONGITUD";
    String velocidad="VELOCIDAD";
    String estacion="ESTACION";
    String versionapp="VERSIONAPP";
    String sw_enviado="SW_ENVIADO";
    String DNI="DNI";
    String tipo="TIPO";
    String idmarcacion="IDMARCACION";

    final String creartablaposiciones=" CREATE TABLE POSICIONES("
            +id+" TEXT NOT NULL PRIMARY KEY,"
            +fecha+" TEXT ,"
            +hora+" TEXT ,"
            +latitud+" TEXT ,"
            +longitud+" TEXT ,"
            +velocidad+" TEXT ,"
            +estacion+" TEXT ,"
            +versionapp+" TEXT ,"
            +sw_enviado+" TEXT )";


    final String creartablaestacion=" CREATE TABLE ESTACION("
            +id+" TEXT NOT NULL,"
            +tipo+" TEXT NOT NULL,"
            +estacion+" TEXT NOT NULL)";

    final String creartablamarcaciones=" CREATE TABLE MARCACIONES("

            +DNI+ " TEXT ,"
            +fecha+ " TEXT ,"
            +hora+" TEXT ,"
            +estacion+" TEXT ,"
            +latitud+" TEXT ,"
            +longitud+" TEXT ,"
            +idmarcacion+" TEXT NOT NULL PRIMARY KEY ,"
            +versionapp+" TEXT ,"
            +sw_enviado+ " TEXT )";

    public AsistenciaHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(creartablaestacion);
        sqLiteDatabase.execSQL(creartablamarcaciones);
        sqLiteDatabase.execSQL(creartablaposiciones);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE MARCACIONES");
        sqLiteDatabase.execSQL("DROP TABLE ESTACION");
        sqLiteDatabase.execSQL("DROP TABLE POSICIONES");

        onCreate(sqLiteDatabase);

    }
}

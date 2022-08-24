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
    String horatermino="HORATERMINO";
    String fechatermino="FECHATERMINO";
    String longitudtermino="LONGITUDTERMINO";
    String latitudtermino="LATITUDTERMINO";
    String idviajentrada="IDVIAJEINGRESO";
    String dniconductorretorno="DNIRETORNO";
    String fechainicioretorno="FECHAINICIORETORNO";
    String horainicioretorno="HORAINICIORETORNO";
    String latitudinicioretorno="LATITUDINICIORETORNO";
    String longitudinicioretorno="LONGITUDINICIORETORNO";
    String fechaterminoretorno="FECHATERMINORETORNO";
    String horaterminoretorno="HORATERMINORETORNO";
    String latitudterminoretorno="LATITUDTERMINORETORNO";
    String longitudterminoretorno="LONGITUDTERMINORETORNO";
    String swrestriccion="SW_RESTRICCION";






    final String creartablarestricciones="CREATE TABLE PERSONAL_RESTRICCIONES("
            +DNI+" TEXT NOT NULL,"
            +fecha+" TEXT)";


    final String creartablaviajes="CREATE TABLE VIAJES("
            +id+" TEXT NOT NULL PRIMARY KEY,"
            +DNI+" TEXT NOT NULL,"
            +fecha+" TEXT,"
            +hora+" TEXT,"
            +latitud+" TEXT,"
            +longitud+" TEXT,"
            +estacion+" TEXT,"
            +horatermino+" TEXT,"
            +fechatermino+" TEXT,"
            +latitudtermino+" TEXT,"
            +longitudtermino+" TEXT,"
            +dniconductorretorno+" TEXT,"
            +fechainicioretorno+" TEXT,"
            +horainicioretorno+" TEXT,"
            +latitudinicioretorno+" TEXT,"
            +longitudinicioretorno+" TEXT,"
            +fechaterminoretorno+" TEXT,"
            +horaterminoretorno+" TEXT,"
            +latitudterminoretorno+" TEXT,"
            +longitudterminoretorno+" TEXT,"
            +sw_enviado+" TEXT NOT NULL)";


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
            +swrestriccion+" TEXT,"
            +sw_enviado+ " TEXT )";

    public AsistenciaHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(creartablaestacion);
        sqLiteDatabase.execSQL(creartablamarcaciones);
        sqLiteDatabase.execSQL(creartablaposiciones);
        sqLiteDatabase.execSQL(creartablaviajes);
        sqLiteDatabase.execSQL(creartablarestricciones);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE MARCACIONES");
        sqLiteDatabase.execSQL("DROP TABLE ESTACION");
        sqLiteDatabase.execSQL("DROP TABLE POSICIONES");
        sqLiteDatabase.execSQL("DROP TABLE VIAJES");
        sqLiteDatabase.execSQL("DROP TABLE PERSONAL_RESTRICCIONES");

        onCreate(sqLiteDatabase);

    }
}

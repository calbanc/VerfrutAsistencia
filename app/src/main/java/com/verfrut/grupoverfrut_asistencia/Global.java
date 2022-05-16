package com.verfrut.grupoverfrut_asistencia;

public class Global {

    public static String usuario;
    public static String clave;
    public static String EmailEnv;
    public static String PassEnv;
    public static String combustible;
    public static String empresa;
    public static String idempresa;
    public static String idtemporada;
    public static String CorreoEnvio;
    public static String CorreoEnvioCC;
    public static String codigochip;
    public static String SmtpEnv;
    public static String temporada;
    public static String rondin;
    public static String cod_packing;
    public static String cod_fri;
    public static String zona;
    public static String nombrecompleto;
    public static String codigotrabajador;
    public static String cuadrilla;
    public static String subcentrocosto;
    public static String codlinea;
    public static String nom_pack;
    public static String nom_fri;
    public static String url2="https://app.verfrut.cl/app/";
    public static  String url="http://app.verfrut.pe/";

    public static String leecodigo(String texto, String caracter){
        String[]asc=texto.split(caracter,0);
        String id=asc[0];
        return id;
    }
    public static String[] separatexto(String texto,String caracter){
        String[]asc=texto.split(caracter,0);

        return asc;
    }


}

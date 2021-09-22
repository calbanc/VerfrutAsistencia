package com.verfrut.grupoverfrut_asistencia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.UnicodeSetSpanner;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.UrlQuerySanitizer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpClientConnection;

public class ASISTENCIA extends AppCompatActivity {
    Button btncerrar;
    TextView txtmensaje,txtlatitud,txtlongitud,txtvelocidad,txtidequipo,txtplaca,txtcantidad;
    SurfaceView svqr;
    private AsyncHttpClient cliente;
    final Handler handler = new Handler();
    MediaPlayer mp;
    ImageView imgsalir;
    ProgressDialog progreso;
    public static final String prefencia="prefencia";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asistencia);
        btncerrar = findViewById(R.id.btncerrar);
        txtlatitud=findViewById(R.id.txtlatitud);
        txtlongitud=findViewById(R.id.txtlongitud);
        txtvelocidad=findViewById(R.id.txtvelocidad);
        svqr = findViewById(R.id.svqr);
        txtmensaje = findViewById(R.id.txtmensaje);
        txtidequipo=findViewById(R.id.txtidequipo);
        txtplaca=findViewById(R.id.txtplaca);
        txtcantidad=findViewById(R.id.txtcantidad);
        imgsalir=findViewById(R.id.imgsalir);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mp=MediaPlayer.create(ASISTENCIA.this,R.raw.beep);
        cliente=new AsyncHttpClient();
        cliente.setTimeout(100000);



        try{
            cargarequipoexistente();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
            String fecha = sdf.format(new Date());

            mostrarcantidaddemarcacion(fecha);
        }catch (Exception e){
            Toast.makeText(ASISTENCIA.this,"ERROR EN CARGAR EQUIPO EXISTENTE",Toast.LENGTH_SHORT).show();
        }


        btncerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String placa=txtplaca.getText().toString();
                if(placa.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                    builder.setTitle("CIERRE DEL DIA")
                            .setMessage("ESTIMADO USUARIO DEBE ESTAR REGISTRADO")
                            .setPositiveButton("ACEPTAR ", null);
                    builder.create().show();
                }else{
                    mostrarformulario();
                }
            }
        });


        imgsalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                builder.setTitle("CERRAR SESION")
                        .setMessage("ESTA SEGURO DE CERRAR SESION")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final AlertDialog dialogBuilder=new AlertDialog.Builder(ASISTENCIA.this).create();
                                LayoutInflater inflater=ASISTENCIA.this.getLayoutInflater();
                                View dialogview=inflater.inflate(R.layout.dialogclave,null);

                                final TextInputEditText txtclave =dialogview.findViewById(R.id.txtclave);

                                Button btncancelar=dialogview.findViewById(R.id.btncancelar);
                                Button btnsalir=dialogview.findViewById(R.id.btnsalir);

                                btncancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        dialogBuilder.dismiss();
                                    }
                                });

                                btnsalir.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String pass="Rapel.2020";

                                        if(txtclave.getText().toString().equals(pass)){
                                            dialogBuilder.dismiss();
                                            SharedPreferences preferences=getSharedPreferences(prefencia,MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.clear().apply();
                                            Intent miintent=new Intent(ASISTENCIA.this,MainActivity.class);
                                            startActivity(miintent);
                                            finish();
                                        }else{
                                            Toast.makeText(ASISTENCIA.this,"CLAVE DE ADMINISTRADOR INCORRECTA",Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                                dialogBuilder.setView(dialogview);
                                dialogBuilder.show();
                            }
                        });
                builder.create().show();



            }
        });

        try{
            configuracioncamara();
        }catch (Exception e){
            Toast.makeText(ASISTENCIA.this,"ERROR EN CONFIGURACION DE CAMARA",Toast.LENGTH_SHORT).show();
        }


        if (ActivityCompat.checkSelfPermission(ASISTENCIA.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ASISTENCIA.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ASISTENCIA.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {

            try{
                locationStart();
            }catch (Exception e){
                Toast.makeText(ASISTENCIA.this,"ERROR EN LOCATIONSTART",Toast.LENGTH_SHORT).show();
            }


        }




    //tiempo de 3 minutos para envio de datos
        try{
            handler.postDelayed(new Runnable() {
                public void run() {



                    if(isNetDisponible()) {
                        enviardatosaservidor();


                    }

                    handler.postDelayed(this, 30000);
                }

            }, 30000);



            handler.postDelayed(new Runnable() {
                public void run() {



                    if(isNetDisponible()) {
                        enviarposicionesservidor();


                    }

                    handler.postDelayed(this, 40000);
                }

            }, 40000);



            handler.postDelayed(new Runnable() {
                public void run() {
                   txtmensaje.setText("");
                    handler.postDelayed(this, 2000);
                }

            }, 2000);

            handler.postDelayed(new Runnable() {
                public void run() {
                    guardaposicion();
                    handler.postDelayed(this, 20000);
                }

            }, 2000);






        }catch (Exception e){
//            Toast.makeText(ASISTENCIA.this,"ERROR EN TRY 1"+e.getMessage(),Toast.LENGTH_SHORT).show();

        }




    }

    private void mostrarformulario() {
        final AlertDialog dialogBuilder=new AlertDialog.Builder(ASISTENCIA.this).create();
        LayoutInflater inflater=ASISTENCIA.this.getLayoutInflater();
        View dialogview=inflater.inflate(R.layout.dialogreportepasajeros,null);

        final TextInputEditText txtdialogpasajeros =dialogview.findViewById(R.id.txtdialogpasajeros);
        final TextInputEditText txtdialogepp=dialogview.findViewById(R.id.txtdialogepp);
        Button btndialogpasajerosenviar=dialogview.findViewById(R.id.btndialogpasajerosenviar);

        btndialogpasajerosenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                   final int cantidad= Integer.parseInt(txtdialogpasajeros.getText().toString());
                   final int cantidadepp=Integer.parseInt(txtdialogepp.getText().toString());
                    if(cantidad>0&&cantidad<=65&&cantidadepp<=65){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                        builder.setTitle("CIERRE DEL DIA")
                                .setMessage("ESTA SEGURO DE REPORTAR "+cantidad+ " PASAJEROS")
                                .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        txtdialogpasajeros.setText("");
                                    }
                                })
                                .setPositiveButton("ACEPTAR ", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String idequipo=txtidequipo.getText().toString();
                                        enviarreporte(idequipo,cantidad,cantidadepp);
                                        dialogBuilder.dismiss();
                                    }
                                });
                        builder.create().show();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                        builder.setTitle("CIERRE DEL DIA")
                                .setMessage("CANTIDAD DE TRABJADORES INGRESADA ES INCORRECTA")
                                .setPositiveButton("ACEPTAR ", null);
                        builder.create().show();
                        txtdialogpasajeros.setText("");
                    }



                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                    builder.setTitle("CIERRE DEL DIA")
                            .setMessage("ERROR INGRESANDO CANTIDAD" + e.getMessage())
                            .setPositiveButton("ACEPTAR ", null);
                    builder.create().show();
                    txtdialogpasajeros.setText("");
                }



            }
        });

        dialogBuilder.setView(dialogview);
        dialogBuilder.show();
    }

    private void enviarreporte(String idequipo, int cantidad,int cantidadepp) {

        progreso = new ProgressDialog(ASISTENCIA.this);
        progreso.setMessage("Sincronizando Trabajadores...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
        String fecha = sdf.format(new Date());
        SimpleDateFormat hdf=new SimpleDateFormat("HH:mm:ss");
        String hora=hdf.format(new Date());



        String url=Global.url+"swinsertadotacionpasajeros.php?Pasajeros="+cantidad+"&Fecha="+fecha+"&Hora="+hora+"&IdEstacion="+idequipo+"&Epp="+cantidadepp;

        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    progreso.hide();
                    String enviar=new String(responseBody);
                    try{
                        JSONArray jsonArray=new JSONArray(enviar);
                        String respuesta=jsonArray.getJSONObject(0).getString("id");
                        System.out.println(respuesta+" RESPUESTA DEL SERVIDOR");
                        if(respuesta.equals("REGISTRA")){

                            AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                            builder.setTitle("CIERRE DEL DIA")
                                    .setMessage("REPORTE GENERADO CORRECTAMENTE GRACIAS")
                                    .setPositiveButton("ACEPTAR ", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.exit(0);
                                        }
                                    });
                            builder.create().show();


                        }

                    }catch (Exception e){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                        builder.setTitle("CIERRE DEL DIA")
                                .setMessage("ERROR EN RESPUESTA DE SERVIDOR "+e.getMessage())
                                .setPositiveButton("ACEPTAR ", null);
                        builder.create().show();

                    }

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                AlertDialog.Builder builder = new AlertDialog.Builder(ASISTENCIA.this);
                builder.setTitle("CIERRE DEL DIA")
                        .setMessage("SERVIDOR NO DISPONIBLE INTENTE NUEVAMENTE EN UNOS MOMENTOS")
                        .setPositiveButton("ACEPTAR ", null);
                builder.create().show();
            }
        });






    }


    private void enviarposicionesservidorlocal() {
        AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
        final SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT VELOCIDAD,FECHA,HORA,ESTACION,LATITUD,LONGITUD,ID,VERSIONAPP FROM POSICIONES WHERE SW_ENVIADO='0' LIMIT 0,10 ";

        try{
            Cursor cr=db.rawQuery(consulta,null);
            if(cr.moveToFirst()) {
                while(cr.isAfterLast() == false) {
                    String velocidad=cr.getString(0).replaceAll(" ","%20");
                    String fecha=cr.getString(1).replaceAll(" ","%20");
                    String hora=cr.getString(2).replaceAll(" ","%20");
                    String estacion=cr.getString(3).replaceAll(" ","%20");
                    String latitud=cr.getString(4).replaceAll(" ","%20");
                    String longitud=cr.getString(5).replaceAll(" ","%20");
                    final String idmarcacion=cr.getString(6).replaceAll(" ","%20");
                    String versionapp=cr.getString(7).replaceAll(" ","%20");

                    String url=Global.url+"wsinsertaposicion.php?Velocidad="+velocidad+"&&Fecha="+fecha+"&&Hora="+hora+"&&Latitud="+latitud+"&&Longitud="+longitud+"&&IdEstacion="+estacion+"&&Id="+idmarcacion+"&&VersionApp="+versionapp;

                    cliente.post(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200){
                                String enviar=new String(responseBody);
                                try{
                                    JSONArray jsonArray=new JSONArray(enviar);
                                    String respuesta=jsonArray.getJSONObject(0).getString("id");

                                    if(respuesta.equals("REGISTRA")){
                                        String actualizadaestado="UPDATE POSICIONES SET SW_ENVIADO='1' WHERE ID='"+idmarcacion+"'";

                                        db.execSQL(actualizadaestado);
                                    }else{
                                    }

                                }catch (Exception e){
                                    // Toast.makeText(ASISTENCIA.this,"NO ENVIADO"+e.getMessage(),Toast.LENGTH_LONG).show();

                                }

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            //    Toast.makeText(ASISTENCIA.this,"SIN CONEXION",Toast.LENGTH_SHORT).show();
                        }
                    });

                    cr.moveToNext();
                }
            }else{

            }

        }catch (Exception e){
            //Toast.makeText(ASISTENCIA.this,"Eror enviando posiciones a servidor"+e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    private void enviardatosaservidorlocal() {

        System.out.println("METODO DE ENVIAR A SISTEMA");
        AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
        final  SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT DNI,FECHA,HORA,ESTACION,LATITUD,LONGITUD,IDMARCACION,VERSIONAPP FROM MARCACIONES WHERE SW_ENVIADO='0' LIMIT 0,10  ";
        try{
            Cursor cr=db.rawQuery(consulta,null);
            if(cr.moveToFirst()) {
                while(cr.isAfterLast() == false) {
                    String dni=cr.getString(0).replaceAll(" ","%20");
                    String fecha=cr.getString(1).replaceAll(" ","%20");
                    String hora=cr.getString(2).replaceAll(" ","%20");
                    String estacion=cr.getString(3).replaceAll(" ","%20");
                    String latitud=cr.getString(4).replaceAll(" ","%20");
                    String longitud=cr.getString(5).replaceAll(" ","%20");
                    final String idmarcacion=cr.getString(6).replaceAll(" ","%20");
                    String versionapp=cr.getString(7).replaceAll(" ","%20");


                    Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 192.168.60.8");

                    int val           = p.waitFor();
                    boolean reachable = (val == 0);

                    if(reachable=true){
                        System.out.println("RED LOCAL");
                    }else{
                        System.out.println("RED EXTERNA");
                    }



                    String url=Global.url+"wsinsertamarcacionprueba.php?RutTrabajador="+dni+"&&Fecha="+fecha+"&&Hora="+hora+"&&Latitud="+latitud+"&&Longitud="+longitud+"&&IdEstacion="+estacion+"&&Id="+idmarcacion+"&&VersionApp="+versionapp;


                    cliente.post(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200){
                                String enviar=new String(responseBody);
                                try{
                                    JSONArray jsonArray=new JSONArray(enviar);
                                    String respuesta=jsonArray.getJSONObject(0).getString("id");
                                    System.out.println(respuesta+" RESPUESTA DEL SERVIDOR");
                                    if(respuesta.equals("REGISTRA")){
                                        String actualizadaestado="UPDATE MARCACIONES SET SW_ENVIADO='1' WHERE IDMARCACION='"+idmarcacion+"'";
                                        System.out.println("CONSULTA DE ACTUALIZAR MARCACION ="+actualizadaestado);
                                        db.execSQL(actualizadaestado);


                                    }else{


                                    }

                                }catch (Exception e){
                                    //   Toast.makeText(ASISTENCIA.this,"MARCACION NO ENVIADA"+e.getMessage(),Toast.LENGTH_LONG).show();

                                }

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            //        Toast.makeText(ASISTENCIA.this,"SIN CONEXION MARCACION",Toast.LENGTH_SHORT).show();
                        }
                    });

                    cr.moveToNext();
                }
            }else{

            }

        }catch (Exception e){
            //  Toast.makeText(ASISTENCIA.this,"Eror enviando datos a servidor"+e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 http://app.verfrut.pe");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }




    private void cargarequipoexistente() {
        AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT * FROM ESTACION";
        try{
            Cursor cr=db.rawQuery(consulta,null);
            if(cr.moveToNext()){
                txtidequipo.setText(cr.getString(0));
                txtplaca.setText("Placa : "+ cr.getString(1));
            }else{
                Toast.makeText(ASISTENCIA.this, "NO HA REGISTRADO UNA ESTACION", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            Toast.makeText(ASISTENCIA.this, "NO HA REGISTRADO UNA ESTACION", Toast.LENGTH_SHORT).show();
        }

    }
    private void enviarposicionesservidor() {

        AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
       final SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT VELOCIDAD,FECHA,HORA,ESTACION,LATITUD,LONGITUD,ID,VERSIONAPP FROM POSICIONES WHERE SW_ENVIADO='0' LIMIT 0,10 ";



        try{
            Cursor cr=db.rawQuery(consulta,null);
            if(cr.moveToFirst()) {
                while(cr.isAfterLast() == false) {
                    String velocidad=cr.getString(0).replaceAll(" ","%20");
                    String fecha=cr.getString(1).replaceAll(" ","%20");
                    String hora=cr.getString(2).replaceAll(" ","%20");
                    String estacion=cr.getString(3).replaceAll(" ","%20");
                    String latitud=cr.getString(4).replaceAll(" ","%20");
                    String longitud=cr.getString(5).replaceAll(" ","%20");
                   final String idmarcacion=cr.getString(6).replaceAll(" ","%20");
                    String versionapp=cr.getString(7).replaceAll(" ","%20");

                    String url=Global.url+"wsinsertaposicion.php?Velocidad="+velocidad+"&&Fecha="+fecha+"&&Hora="+hora+"&&Latitud="+latitud+"&&Longitud="+longitud+"&&IdEstacion="+estacion+"&&Id="+idmarcacion+"&&VersionApp="+versionapp;
                    cliente.post(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200){
                                String enviar=new String(responseBody);
                                try{
                                    JSONArray jsonArray=new JSONArray(enviar);
                                    String respuesta=jsonArray.getJSONObject(0).getString("id");

                                    if(respuesta.equals("REGISTRA")){
                                        String actualizadaestado="UPDATE POSICIONES SET SW_ENVIADO='1' WHERE ID='"+idmarcacion+"'";

                                        db.execSQL(actualizadaestado);
                                    }else{
                                    }

                                }catch (Exception e){
                                   // Toast.makeText(ASISTENCIA.this,"NO ENVIADO"+e.getMessage(),Toast.LENGTH_LONG).show();

                                }

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        //    Toast.makeText(ASISTENCIA.this,"SIN CONEXION",Toast.LENGTH_SHORT).show();
                        }
                    });

                    cr.moveToNext();
                }
            }else{

            }

        }catch (Exception e){
            //Toast.makeText(ASISTENCIA.this,"Eror enviando posiciones a servidor"+e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    private void guardaposicion() {

        try{
            AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
            SQLiteDatabase db=cn.getWritableDatabase();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
            String fecha = sdf.format(new Date());
            SimpleDateFormat hdf=new SimpleDateFormat("HH:mm:ss");
            String hora=hdf.format(new Date());
            String idequipo=txtidequipo.getText().toString();
            String idmarcacion=idequipo+fecha.replaceAll("-","")+hora.replaceAll(":","");
            String latitud=txtlatitud.getText().toString();
            String longitud=txtlongitud.getText().toString();
            String velocidad=txtvelocidad.getText().toString();
            String versionapp="Posiciones";
            String inserta="INSERT INTO POSICIONES(ID,FECHA,HORA,LATITUD,LONGITUD,VELOCIDAD,ESTACION,VERSIONAPP,SW_ENVIADO) VALUES ('"+idmarcacion+"','"+fecha+"','"+hora+"','"+latitud+"','"+longitud+"','"+velocidad+"','"+idequipo+"','"+versionapp+"','0')";
            db.execSQL(inserta);

        }catch (Exception e){
            //Toast.makeText(ASISTENCIA.this,"ERROR GUARDANDO POSICION"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

   }
    private void enviardatosaservidor() {

        AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
        final  SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT DNI,FECHA,HORA,ESTACION,LATITUD,LONGITUD,IDMARCACION,VERSIONAPP FROM MARCACIONES WHERE SW_ENVIADO='0'  LIMIT 0,50 ";

        try{
            Cursor cr=db.rawQuery(consulta,null);
            if(cr.moveToFirst()) {
                while(cr.isAfterLast() == false) {
                    String dni=cr.getString(0).replaceAll(" ","%20");
                    String fecha=cr.getString(1).replaceAll(" ","%20");
                    String hora=cr.getString(2).replaceAll(" ","%20");
                    String estacion=cr.getString(3).replaceAll(" ","%20");
                    String latitud=cr.getString(4).replaceAll(" ","%20");
                    String longitud=cr.getString(5).replaceAll(" ","%20");
                   final String idmarcacion=cr.getString(6).replaceAll(" ","%20");
                    String versionapp=cr.getString(7).replaceAll(" ","%20");

                    String url=Global.url+"wsinsertamarcacion.php?RutTrabajador="+dni+"&&Fecha="+fecha+"&&Hora="+hora+"&&Latitud="+latitud+"&&Longitud="+longitud+"&&IdEstacion="+estacion+"&&Id="+idmarcacion+"&&VersionApp="+versionapp;

                    cliente.post(url, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if(statusCode==200){
                                String enviar=new String(responseBody);
                                try{
                                    JSONArray jsonArray=new JSONArray(enviar);
                                    String respuesta=jsonArray.getJSONObject(0).getString("id");
                                    System.out.println(respuesta+" RESPUESTA DEL SERVIDOR");
                                    if(respuesta.equals("REGISTRA")){
                                        String actualizadaestado="UPDATE MARCACIONES SET SW_ENVIADO='1' WHERE IDMARCACION='"+idmarcacion+"'";

                                        db.execSQL(actualizadaestado);


                                    }else{


                                    }

                                }catch (Exception e){
                                 //   Toast.makeText(ASISTENCIA.this,"MARCACION NO ENVIADA"+e.getMessage(),Toast.LENGTH_LONG).show();

                                }

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        //        Toast.makeText(ASISTENCIA.this,"SIN CONEXION MARCACION",Toast.LENGTH_SHORT).show();
                        }
                    });

                    cr.moveToNext();
                }
            }else{

            }

        }catch (Exception e){
          //  Toast.makeText(ASISTENCIA.this,"Eror enviando datos a servidor"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    public static boolean compruebaConexion(Context context) {

        boolean connected = false;

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Recupera todas las redes (tanto móviles como wifi)
        NetworkInfo[] redes = connec.getAllNetworkInfo();

        for (int i = 0; i < redes.length; i++) {
            // Si alguna red tiene conexión, se devuelve true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                connected = true;
            }
        }
        return connected;
    }
    private void locationStart() {
        try{
            LocationManager mlocManager = (LocationManager) ASISTENCIA.this.getSystemService(Context.LOCATION_SERVICE);
            ASISTENCIA.Localizacion Local = new ASISTENCIA.Localizacion();

            Local.setMainActivity(ASISTENCIA.this);
            final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gpsEnabled) {
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  // Abre pantalla para activar GPS cuando esta apagado
                startActivity(settingsIntent);
            }
            if (ActivityCompat.checkSelfPermission(ASISTENCIA.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(ASISTENCIA.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ASISTENCIA.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                return;
            }
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        }catch (Exception e){
            Toast.makeText(ASISTENCIA.this,"ERROR DENTRO DE LOCATIONSTAR"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }
    public class Localizacion implements LocationListener {
        ASISTENCIA mainActivity;
        public ASISTENCIA getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(ASISTENCIA mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            try{
                loc.getLatitude();
                loc.getLongitude();
                loc.getSpeed();
                DecimalFormat df = new DecimalFormat("#.00");
                String sLatitud = String.valueOf(loc.getLatitude());
                String sLongitud = String.valueOf(loc.getLongitude());
                final  String sVelocidad = String.valueOf(df.format(loc.getSpeed()*3.6));

                if(sVelocidad.equals(",00")){
                    txtvelocidad.setText("0.00");
                }else{
                    txtvelocidad.setText(sVelocidad);
                }
                //Toast.makeText(getApplicationContext(),"latitud"+sLatitud,Toast.LENGTH_LONG).show();
                txtlatitud.setText(sLatitud);
                txtlongitud.setText(sLongitud);

                //llamamos a la clase para obtener la direccion
                //this.mainActivity.setLocation(loc);
            }catch (Exception e){
                Toast.makeText(ASISTENCIA.this,"on error in location change"+e.getMessage(),Toast.LENGTH_SHORT).show();
            }


        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(ASISTENCIA.this,"GPS DESACTIVADO",Toast.LENGTH_LONG).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(ASISTENCIA.this,"GPS ACTIVO",Toast.LENGTH_LONG).show();
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }


    private void insertardatolocal(String dni) {

        try{
            AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
            SQLiteDatabase db=cn.getWritableDatabase();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
            String fecha = sdf.format(new Date());
            SimpleDateFormat hdf=new SimpleDateFormat("HH:mm:ss");
            String hora=hdf.format(new Date());
            String idequipo=txtidequipo.getText().toString();
            String idmarcacion=idequipo+fecha.replaceAll("-","")+hora.replaceAll(":","");
            String latitud=txtlatitud.getText().toString();
            String longitud=txtlongitud.getText().toString();
            String swenviado="0";
            String versionapp="AppRemu2.2";


            String consultaantes="SELECT * FROM MARCACIONES WHERE DNI='"+dni+"' and FECHA='"+fecha+"'  and HORA BETWEEN TIME('"+hora+"','-120 seconds')  AND '"+hora+"' ";
            Cursor cr1=db.rawQuery(consultaantes,null);
            if(cr1.moveToFirst()){
                mp.start();
                txtmensaje.setText("MARCACION YA REGISTRADA");
            }else{
                String insertaregistro="INSERT INTO MARCACIONES (DNI,FECHA,HORA,ESTACION,LATITUD,LONGITUD,IDMARCACION,VERSIONAPP,SW_ENVIADO)" +
                        " VALUES ('"+dni+"','"+fecha+"','"+hora+"','"+idequipo+"','"+latitud+"','"+longitud+"','"+idmarcacion+"','"+versionapp+"','"+swenviado+"') ";

                db.execSQL(insertaregistro);
                mp.start();
                txtmensaje.setText("MARCACION REGISTRADA");

                String consulta="SELECT COUNT(DISTINCT(IDMARCACION)) FROM MARCACIONES WHERE FECHA='"+fecha+"'";
                System.out.println(consulta);
                Cursor cr=db.rawQuery(consulta,null);
                if(cr.moveToNext()){
                    txtcantidad.setText(cr.getString(0));

                }
            }
        }catch (Exception e){

        }
    }

    private void mostrarcantidaddemarcacion(String fecha) {
        AsistenciaHelper cn=new AsistenciaHelper(ASISTENCIA.this,"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT COUNT(DISTINCT(IDMARCACION)) FROM MARCACIONES WHERE FECHA='"+fecha+"'";
        System.out.println(consulta);
        Cursor cr=db.rawQuery(consulta,null);
        if(cr.moveToNext()){
            txtcantidad.setText(cr.getString(0));
            System.out.println(cr.getString(0));
        }


    }


    private void configuracioncamara() {
        BarcodeDetector detector = new BarcodeDetector.Builder(this).build();

        final CameraSource camara = new CameraSource.Builder(this, detector)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .build();
        svqr.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ASISTENCIA.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    camara.start(svqr.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                camara.stop();
            }
        });


        detector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                try{
                    final SparseArray<Barcode> barcodes=detections.getDetectedItems();
                    if(barcodes.size()>0){

                        Barcode resul=barcodes.valueAt(0);


                        String dni=resul.rawValue;

                        if(dni.length()>7 && dni.length()<=9){
                            insertardatolocal(dni);

                        }else{
                           // txtmensaje.setText("CODIGO ESCANEADO NO ES VALIDO");
                        }



                    }
                }catch (Exception e){
                  //  Toast.makeText(ASISTENCIA.this,"ERROR DETECTADO"+e.getMessage(),Toast.LENGTH_SHORT).show();
                }




            }
        });

    }
}
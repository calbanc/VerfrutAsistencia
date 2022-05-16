package com.verfrut.grupoverfrut_asistencia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ADMINISTRADOR extends AppCompatActivity {
    Button btnaceptar,btnnuevo,btnretirar,btncerrarsesion;
    TextInputEditText txtnombre_estacion;
    TextView txtestacionguardada,txttipoestacion,idempresa,txtid;
    RadioButton rbfija,rbmovil,rbbus;
    private AsyncHttpClient cliente;
    ProgressDialog progreso;
    public static final String prefencia="prefencia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador);
        btnaceptar=findViewById(R.id.btnaceptar);
        txtnombre_estacion=findViewById(R.id.txtnombre_estacion);

        rbfija=findViewById(R.id.rbfija);
        rbmovil=findViewById(R.id.rbmovil);
        rbbus=findViewById(R.id.rbbus);
        txtestacionguardada=findViewById(R.id.txtestacionguardada);
        txttipoestacion=findViewById(R.id.txttipoestacion);
        idempresa=findViewById(R.id.idempresa);
        txtid=findViewById(R.id.txtid);
        btnretirar=findViewById(R.id.btnretirar);
        btncerrarsesion=findViewById(R.id.btncerrarsesion);


        cliente=new AsyncHttpClient();



        btncerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ADMINISTRADOR.this);
                builder.setTitle("CERRAR SESION")
                        .setMessage("ESTA SEGURO DE CERRAR SESION")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final AlertDialog dialogBuilder=new AlertDialog.Builder(ADMINISTRADOR.this).create();
                                LayoutInflater inflater=ADMINISTRADOR.this.getLayoutInflater();
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
                                                Intent miintent=new Intent(ADMINISTRADOR.this,MainActivity.class);
                                                startActivity(miintent);
                                                finish();
                                            }else{
                                                Toast.makeText(ADMINISTRADOR.this,"CLAVE DE ADMINISTRADOR INCORRECTA",Toast.LENGTH_SHORT).show();
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



        AsistenciaHelper cn=new AsistenciaHelper(ADMINISTRADOR.this,"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();

        try{
            mostrarnombreestacion();
        }catch (Exception e){

        }



        btnretirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retirar();
            }
        });

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtnombre_estacion.getText().toString().isEmpty()){
                    Toast.makeText(ADMINISTRADOR.this,"DEBE INGRESAR UN NOMBRE PARA LA ESTACION",Toast.LENGTH_LONG).show();
                }else{
                    if(rbbus.isChecked()||rbmovil.isChecked()||rbfija.isChecked()){

                        if(isNetDisponible()) {

                            if (isOnlineNet()) {
                                System.out.println("REGISTRO LOCAL");
                               // registrarinstalacionlocal();

                            }else{
                                System.out.println("REGISTRO REMOTO");
                                //regitrarestacion();
                            }

                        }


                    }else{
                        Toast.makeText(ADMINISTRADOR.this,"DEBE SELECCIONAR UN TIPO DE ESTACION",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }

    private void registrarinstalacionlocal() {
        progreso = new ProgressDialog(ADMINISTRADOR.this);
        progreso.setMessage("Registrando instalacion de equipo...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();
        String nombre=txtnombre_estacion.getText().toString().replaceAll(" ","%20").toUpperCase();
        final String tipoestacion;
        if(rbfija.isChecked()){
            tipoestacion="FIJA";
        }else{
            if(rbmovil.isChecked()){
                tipoestacion="MOVIL";
            }else{
                tipoestacion="BUS";
            }
        }

        String idempresas="9";
        String temp="20";
        String tempo[]=temp.split("   ",0);
        String temporada=tempo[0].toString();
        Date d = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String fecha=dateFormat.format(d).replaceAll(" ","%20");


        String url="http://192.168.60.8/wsinsertaestacionmarcacion.php?idempresa="+idempresas+"&&Temporada="+temporada+"&&TIPO_ESTACION="+tipoestacion+
                "&&NOMBRE_ESTACION="+nombre+"&&FECHA="+fecha+"&&TIPO=INSTALACION";
        System.out.println(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progreso.hide();
                    String respuesta = new String(responseBody);
                    try {
                        JSONArray jsonarreglo = new JSONArray(respuesta);
                        String id=jsonarreglo.getJSONObject(0).getString("ID");
                        AsistenciaHelper cn=new AsistenciaHelper(ADMINISTRADOR.this,"RRHH",null,1);
                        SQLiteDatabase db=cn.getWritableDatabase();
                        db.isOpen();

                        String llenartabla="INSERT INTO ESTACION(ID,TIPO,ESTACION)VALUES('"+id+"','"+txtnombre_estacion.getText().toString()+"','"+tipoestacion+"')";

                        db.execSQL(llenartabla);

                        db.close();
                        mostrarnombreestacion();
                    } catch (Exception e) {
                        progreso.hide();
                        Toast.makeText(ADMINISTRADOR.this,"ERROR INSERTANDO"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(ADMINISTRADOR.this,"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });



    }

    private boolean isNetDisponible() {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo actNetInfo = connectivityManager.getActiveNetworkInfo();

        return (actNetInfo != null && actNetInfo.isConnected());
    }
    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 192.168.60.8");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private void regitrarestacion() {

        progreso = new ProgressDialog(ADMINISTRADOR.this);
        progreso.setMessage("Registrando instalacion de equipo...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();
        String nombre=txtnombre_estacion.getText().toString().replaceAll(" ","%20").toUpperCase();
        final String tipoestacion;
        if(rbfija.isChecked()){
            tipoestacion="FIJA";
        }else{
            if(rbmovil.isChecked()){
                tipoestacion="MOVIL";
            }else{
                tipoestacion="BUS";
            }
        }

        String idempresas="9";
        String temp="20";
        String tempo[]=temp.split("   ",0);
        String temporada=tempo[0].toString();
        Date d = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
        String fecha=dateFormat.format(d).replaceAll(" ","%20");


        String url="http://190.108.85.35:8080/wsinsertaestacionmarcacion.php?idempresa="+idempresas+"&&Temporada="+temporada+"&&TIPO_ESTACION="+tipoestacion+
                "&&NOMBRE_ESTACION="+nombre+"&&FECHA="+fecha+"&&TIPO=INSTALACION";
        System.out.println(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progreso.hide();
                    String respuesta = new String(responseBody);
                    try {
                        JSONArray jsonarreglo = new JSONArray(respuesta);
                        String id=jsonarreglo.getJSONObject(0).getString("ID");
                        AsistenciaHelper cn=new AsistenciaHelper(ADMINISTRADOR.this,"RRHH",null,1);
                        SQLiteDatabase db=cn.getWritableDatabase();
                        db.isOpen();

                        String llenartabla="INSERT INTO ESTACION(ID,TIPO,ESTACION)VALUES('"+id+"','"+txtnombre_estacion.getText().toString()+"','"+tipoestacion+"')";

                        db.execSQL(llenartabla);

                        db.close();
                        mostrarnombreestacion();
                    } catch (Exception e) {
                        progreso.hide();
                        Toast.makeText(ADMINISTRADOR.this,"ERROR INSERTANDO"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(ADMINISTRADOR.this,"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });

    }

    private void retirar() {
        String nombre=txtestacionguardada.getText().toString().replaceAll(" ","%20").toUpperCase();
        String tipoestacion=txttipoestacion.getText().toString();


        String idempresas="9";
        String temp="20";
        String tempo[]=temp.split("   ",0);
        String temporada=tempo[0].toString();
        Date d = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fecha=dateFormat.format(d).replaceAll(" ","%20");


        String url="http://190.108.85.35:8080/wsinsertaestacionmarcacion.php?idempresa="+idempresas+"&&Temporada="+temporada+"&&TIPO_ESTACION="+tipoestacion+
                "&&NOMBRE_ESTACION="+nombre+"&&FECHA="+fecha+"&&TIPO=RETIRO";
        System.out.println(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    nuevo();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(ADMINISTRADOR.this,"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void nuevo() {

        txtnombre_estacion.setText("");
        txtid.setText("");
        txtestacionguardada.setText("");
        txttipoestacion.setText("");
        AsistenciaHelper cn=new AsistenciaHelper(ADMINISTRADOR.this,"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();

        String llenartabla="DELETE FROM ESTACION";
        db.execSQL(llenartabla);
        db.close();
        Toast.makeText(ADMINISTRADOR.this,"EQUIPO RETIRADO",Toast.LENGTH_LONG).show();
    }

    private void mostrarnombreestacion() {
        AsistenciaHelper cn=new AsistenciaHelper(ADMINISTRADOR.this,"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT * FROM ESTACION";
        Cursor cr=db.rawQuery(consulta,null);

        if(cr.moveToNext()){
            txtestacionguardada.setText(cr.getString(1));
            txttipoestacion.setText(cr.getString(2));
            txtid.setText(cr.getString(0));
        }
    }

}
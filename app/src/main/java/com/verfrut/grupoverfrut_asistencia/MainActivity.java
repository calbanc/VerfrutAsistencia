package com.verfrut.grupoverfrut_asistencia;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.verfrut.grupoverfrut_asistencia.Entidades.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;


import org.json.JSONArray;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


public class MainActivity extends AppCompatActivity {

    TextInputEditText txtusuario,txtclave;
    Button btningresar;
    ProgressDialog progreso;
    TelephonyManager tm;
    String imei;
    private AsyncHttpClient cliente;
    public static final String prefencia="prefencia";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtusuario=findViewById(R.id.txtusuario);
        txtclave=findViewById(R.id.txtclave);
        btningresar=findViewById(R.id.btningresar);
        cliente=new AsyncHttpClient();
        if(validaPermisos()){
            btningresar.setEnabled(true);
        }else{
            btningresar.setEnabled(false);
        }

        btningresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtusuario.getText().toString().isEmpty()||txtclave.getText().toString().isEmpty()){
                    Toast.makeText(MainActivity.this,"DEBE INGRESAR UN USUARIO Y UNA CLAVE",Toast.LENGTH_SHORT).show();
                }else{
                    String usuario=txtusuario.getText().toString();
                    String clave=txtclave.getText().toString();
                    consultarperfil(usuario,clave);
                }
            }
        });


        consultarestado();
    }

    private void consultarestado() {

        SharedPreferences preferences=getSharedPreferences(prefencia,MODE_PRIVATE);
        String acces= preferences.getString("acceso","Invitado");
        if(acces.equals("Invitado")) {

        }else {
            if (acces.equals("admasis")) {
                Intent midintent = new Intent(MainActivity.this, Admin.class);
                Bundle mibundle = new Bundle();
                mibundle.putString("usuario",acces);
                midintent.putExtras(mibundle);
                startActivity(midintent);
                super.finish();
            }else{
                Intent midintent = new Intent(MainActivity.this, ASISTENCIA.class);
                Bundle mibundle = new Bundle();


                mibundle.putString("usuario",acces);


                midintent.putExtras(mibundle);
                startActivity(midintent);
                super.finish();
            }
        }

    }

    private void consultarperfil(String usuario, String clave) {

        progreso = new ProgressDialog(this);
        progreso.setMessage("Ingresando ...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();
        String url=Global.url2+"wsconsultausuariologinmarcaciones.php?IDUSUARIO="+txtusuario.getText().toString();

        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    progreso.hide();
                    cargardatosusuario(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(),"VERIFICAR CONEXION A INTERNET",Toast.LENGTH_LONG).show();
                progreso.hide();
            }
        });
    }

    private void cargardatosusuario(String s) {
        String clave;
        Usuario user=new Usuario();
        try{
            JSONArray jsonarreglo=new JSONArray(s);

            for(int i=0;i<jsonarreglo.length();i++){
                user.setClave(jsonarreglo.getJSONObject(i).getString("CLAVE"));
                user.setUsuario(jsonarreglo.getJSONObject(i).getString("IDUSUARIO"));
                System.out.println("USUARIO"+user.getUsuario()+ "\nCLAVE="+user.getClave());
            }
            clave=user.getClave();
            String usuario=user.getUsuario();
            String claveingresada=txtclave.getText().toString();



            if(claveingresada.equals(clave)){

                if (usuario.equals("admasis")) {

                    System.out.println("usuario admasis");

                    Intent midintent = new Intent(MainActivity.this, Admin.class);
                    Bundle mibundle = new Bundle();

                    SharedPreferences preferences = getSharedPreferences(prefencia, MODE_PRIVATE);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("acceso", usuario);
                    editor.putString("clave", clave);
                    mibundle.putString("usuario", usuario);
                    editor.commit();
                    midintent.putExtras(mibundle);
                    startActivity(midintent);
                    super.finish();
                }else{
                    System.out.println("usuario admasis");

                    Intent midintent = new Intent(MainActivity.this, ASISTENCIA.class);
                    Bundle mibundle = new Bundle();

                    SharedPreferences preferences = getSharedPreferences(prefencia, MODE_PRIVATE);

                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("acceso", usuario);
                    editor.putString("clave", clave);
                    mibundle.putString("usuario", usuario);
                    editor.commit();
                    midintent.putExtras(mibundle);
                    startActivity(midintent);
                    super.finish();
                }



                //abrir el activity de marcacion
            }else{


                    Toast.makeText(MainActivity.this,"USUARIO O CLAVE INCORRECTA",Toast.LENGTH_SHORT).show();




            }
        }catch (Exception e){
            Toast.makeText(getApplicationContext(),"ERROR CARGANDO CLAVE USUARIO"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    private boolean validaPermisos() {

        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }

        if((checkSelfPermission(CAMERA)== PackageManager.PERMISSION_GRANTED)&&
                (checkSelfPermission(WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)&& (checkSelfPermission(ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }

        if((shouldShowRequestPermissionRationale(CAMERA)) ||
                (shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))||(shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION))){
            cargarDialogoRecomendacion();
        }else{
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_FINE_LOCATION},100);
        }

        return false;
    }

    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo=new AlertDialog.Builder(MainActivity.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,CAMERA,ACCESS_FINE_LOCATION},100);
            }
        });
        dialogo.show();
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==100){
            if(grantResults.length==3 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    && grantResults[1]==PackageManager.PERMISSION_GRANTED && grantResults[2]==PackageManager.PERMISSION_GRANTED){
                btningresar.setEnabled(true);
            }else{
                solicitarPermisosManual();
            }
        }

    }
    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(MainActivity.this);
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getApplicationContext(),"Los permisos no fueron aceptados", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });
        alertOpciones.show();
    }



}
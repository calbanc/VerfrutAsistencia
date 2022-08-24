package com.verfrut.grupoverfrut_asistencia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.verfrut.grupoverfrut_asistencia.Adaptador.ListadoViajesAdapter;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.verfrut.grupoverfrut_asistencia.Entidades.Viajes;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PrincipalViajes extends AppCompatActivity {
    Button btntermino,btninicio;
    TextView txtidequipo,txtidtipoestacion,txtlatitud,txtlongitud;
    RecyclerView rvlistado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_viajes);
        btntermino=findViewById(R.id.btntermino);
        btninicio=findViewById(R.id.btninicio);
        txtidequipo=findViewById(R.id.txtidequipo);
        txtidtipoestacion=findViewById(R.id.txtidtipoestacion);
        txtlatitud=findViewById(R.id.txtlatitud);
        txtlongitud=findViewById(R.id.txtlongitud);
        rvlistado=findViewById(R.id.rvlistado);
        rvlistado.setLayoutManager(new LinearLayoutManager(PrincipalViajes.this));
        rvlistado.setHasFixedSize(true);




            if (ActivityCompat.checkSelfPermission(PrincipalViajes.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PrincipalViajes.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrincipalViajes.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);


            }else {
                try {
                    AsistenciaHelper cn = new AsistenciaHelper(PrincipalViajes.this, "RRHH", null, 1);
                    SQLiteDatabase db = cn.getWritableDatabase();
                   // db.execSQL("DELETE FROM VIAJES");

                    db.isOpen();
                    String sql = "SELECT ID,ESTACION FROM ESTACION ";
                    Cursor cr = db.rawQuery(sql, null);
                    if (cr.moveToFirst()) {
                        String tipo = cr.getString(1).trim();
                        if (tipo.equals("BUS")) {
                            txtidequipo.setText(cr.getString(0));
                            txtidtipoestacion.setText(cr.getString(1));
                            locationStart();
                            consultarultimoincio();
                            cargarviajes();
                        }else{
                            Intent midintent = new Intent(PrincipalViajes.this, ASISTENCIA.class);
                            startActivity(midintent);
                        }
                    }

                } catch (Exception e) {
                    Toast.makeText(PrincipalViajes.this, "NO TIENE ESTACION REGISTRADA", Toast.LENGTH_SHORT).show();
                }
            }


            btninicio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lectordebarras();
                }
            });

            btntermino.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    lectordebarrastermino();
                }
            });


    }

    private void cargarviajes() {
        AsistenciaHelper cn = new AsistenciaHelper(PrincipalViajes.this, "RRHH", null, 1);
        SQLiteDatabase db = cn.getWritableDatabase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
        String fecha = sdf.format(new Date());
        String select="SELECT * FROM VIAJES ORDER BY FECHA DESC,HORA ASC";
        Cursor cr=db.rawQuery(select,null);
        ArrayList<Viajes>listaviajes=new ArrayList<Viajes>();
        if(cr.moveToFirst()){

            while(cr.isAfterLast()==false){
                Viajes viaje=new Viajes();
                viaje.setIdviaje(cr.getString(0));
                viaje.setDniconductor(cr.getString(1));
                viaje.setFechaincioentrada(cr.getString(2));
                viaje.setHorainicioentrada(cr.getString(3));
                viaje.setFechaterminoentrada(cr.getString(8));
                viaje.setHoraterminoentrada(cr.getString(7));
                viaje.setFecharetornoentrada(cr.getString(12));
                viaje.setHoraretornoentrada(cr.getString(13));
                viaje.setFecharetornosalida(cr.getString(16));
                viaje.setHoraretornosalida(cr.getString(17));
                listaviajes.add(viaje);
                cr.moveToNext();
            }
        }
        ListadoViajesAdapter adapter=new ListadoViajesAdapter(listaviajes);
        rvlistado.setAdapter(adapter);
    }

    private void consultarultimoincio() {
        AsistenciaHelper cn = new AsistenciaHelper(PrincipalViajes.this, "RRHH", null, 1);
        SQLiteDatabase db = cn.getWritableDatabase();

        String select="SELECT * FROM VIAJES WHERE HORATERMINO IS NULL  OR (FECHAINICIORETORNO IS NOT NULL AND   FECHATERMINORETORNO IS NULL ) ORDER BY FECHA DESC, HORA ASC  ";
        Cursor cr=db.rawQuery(select,null);
        ArrayList<Viajes>listaviajes=new ArrayList<Viajes>();
        if(cr.moveToFirst()){
            Intent midintent = new Intent(PrincipalViajes.this, ASISTENCIA.class);

            String idviaje=cr.getString(0);
            Bundle mibundle = new Bundle();
            mibundle.putString("idviaje", idviaje);
            midintent.putExtras(mibundle);
            startActivity(midintent);
            finish();
        }

    }

    private void lectordebarrastermino() {
        Intent intent=new Intent(PrincipalViajes.this, CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        startActivityForResult(intent,2);
    }

    private void lectordebarras() {
        Intent intent=new Intent(PrincipalViajes.this, CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        startActivityForResult(intent,1);
    }


    private void locationStart() {
        try{
            LocationManager mlocManager = (LocationManager) PrincipalViajes.this.getSystemService(Context.LOCATION_SERVICE);
            PrincipalViajes.Localizacion Local = new PrincipalViajes.Localizacion();

            Local.setMainActivity(PrincipalViajes.this);
            final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gpsEnabled) {
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  // Abre pantalla para activar GPS cuando esta apagado
                startActivity(settingsIntent);
            }
            if (ActivityCompat.checkSelfPermission(PrincipalViajes.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(PrincipalViajes.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PrincipalViajes.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                return;
            }
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        }catch (Exception e){
            Toast.makeText(PrincipalViajes.this,"ERROR DENTRO DE LOCATIONSTAR"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public class Localizacion implements LocationListener {
        PrincipalViajes mainActivity;

        public PrincipalViajes getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(PrincipalViajes mainActivity) {
            this.mainActivity = mainActivity;
        }

        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            try {
                loc.getLatitude();
                loc.getLongitude();

                DecimalFormat df = new DecimalFormat("#.00");
                String sLatitud = String.valueOf(loc.getLatitude());
                String sLongitud = String.valueOf(loc.getLongitude());

                //Toast.makeText(getApplicationContext(),"latitud"+sLatitud,Toast.LENGTH_LONG).show();
                txtlatitud.setText(sLatitud);
                txtlongitud.setText(sLongitud);

                //llamamos a la clase para obtener la direccion
                //this.mainActivity.setLocation(loc);
            } catch (Exception e) {
                Toast.makeText(PrincipalViajes.this, "on error in location change" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(PrincipalViajes.this,"GPS DESACTIVADO",Toast.LENGTH_LONG).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(PrincipalViajes.this,"GPS ACTIVO",Toast.LENGTH_LONG).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                final String qr = data.getStringExtra("SCAN_RESULT");
                String idequipo=txtidequipo.getText().toString();
                String latitud=txtlatitud.getText().toString();
                String longitud=txtlongitud.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
                String fecha = sdf.format(new Date());
                SimpleDateFormat hdf=new SimpleDateFormat("HH:mm:ss");
                String hora=hdf.format(new Date());


                String dniconductor=qr.toString();
                String idviaje=idequipo+fecha.replaceAll("-","")+hora.replaceAll(":","");
                AsistenciaHelper cn = new AsistenciaHelper(PrincipalViajes.this, "RRHH", null, 1);
                SQLiteDatabase db = cn.getWritableDatabase();
                String insertinto="INSERT INTO VIAJES(ID,DNI,FECHA,HORA,LATITUD,LONGITUD,ESTACION,SW_ENVIADO) VALUES('"+idviaje+"','"+dniconductor+"','"+fecha+"','"+hora+"','"+latitud+"','"+longitud+"','"+idequipo+"','0')";
                db.execSQL(insertinto);



                Intent midintent = new Intent(PrincipalViajes.this, ASISTENCIA.class);
                Bundle mibundle = new Bundle();
                mibundle.putString("idviaje", idviaje);
                midintent.putExtras(mibundle);
                startActivity(midintent);
                finish();
              //  Intent intent=new Intent(getApplicationContext(),LocationService.class);
               // startService(intent);


            }
        }

      /*  if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                final String qr = data.getStringExtra("SCAN_RESULT");
                final String dniconductor=qr.trim();
                AsistenciaHelper cn = new AsistenciaHelper(PrincipalViajes.this, "RRHH", null, 1);
                SQLiteDatabase db = cn.getWritableDatabase();

                String sql="SELECT ID FROM VIAJES WHERE FECHA IS NULL AND HORA IS NULL ORDER BY FECHA,HORA DESC ";
                Cursor cr=db.rawQuery(sql,null);

                if(cr.moveToFirst()){

                    String idequipo=cr.getString(0);
                    String latitud=txtlatitud.getText().toString();
                    String longitud=txtlongitud.getText().toString();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
                    String fecha = sdf.format(new Date());
                    SimpleDateFormat hdf=new SimpleDateFormat("HH:mm:ss");
                    String hora=hdf.format(new Date());


                    String update="UPDATE VIAJES SET HORATERMINO='"+hora+"' ,FECHATERMINO='"+fecha+"',LATITUDTERMINO='"+latitud+"' .LONGITUDTERMINO='"+longitud+"' WHERE ID='"+idequipo+"' AND DNI='"+dniconductor+"'" ;
                    db.execSQL(update);

                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(PrincipalViajes.this);
                    builder.setTitle("Registro de termino de viaje")
                            .setMessage("Estimado usuario no tiene inicio de viaje registrado , desea registrar de igual forma")
                            .setPositiveButton("REGISTRAR ", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AsistenciaHelper cn = new AsistenciaHelper(PrincipalViajes.this, "RRHH", null, 1);
                                    SQLiteDatabase db = cn.getWritableDatabase();
                                    String idequipo=txtidequipo.getText().toString();
                                    String latitud=txtlatitud.getText().toString();
                                    String longitud=txtlongitud.getText().toString();
                                    String estacion=txtidtipoestacion.getText().toString();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
                                    String fecha = sdf.format(new Date());
                                    SimpleDateFormat hdf=new SimpleDateFormat("HH:mm:ss");
                                    String hora=hdf.format(new Date());

                                    String insertinto="INSERT INTO VIAJES(ID,DNI,FECHA,HORA,LATITUD,LONGITUD,ESTACION,SW_ENVIADO) VALUES('"+idequipo+"','"+dniconductor+"','"+fecha+"','"+hora+"','"+latitud+"','"+longitud+"','"+estacion+"','0')";
                                    db.execSQL(insertinto);
                                }
                            });
                    builder.create().show();
                }




            }
        }*/



    }
}
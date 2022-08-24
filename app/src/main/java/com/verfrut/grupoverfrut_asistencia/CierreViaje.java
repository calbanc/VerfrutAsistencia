package com.verfrut.grupoverfrut_asistencia;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.journeyapps.barcodescanner.CaptureActivity;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CierreViaje extends AppCompatActivity {
    String idviaje="";
    TextView txtlatitud,txtlongitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre_viaje);
        Bundle extras = getIntent().getExtras();
        txtlatitud=findViewById(R.id.txtlatitud);
        txtlongitud=findViewById(R.id.txtlongitud);
        idviaje = extras.getString("idviaje");

        if (ActivityCompat.checkSelfPermission(CierreViaje.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CierreViaje.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CierreViaje.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        }else {
            locationStart();
        }
        lectorbarras();
    }

    private void lectorbarras() {
        Intent intent = new Intent(CierreViaje.this, CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        startActivityForResult(intent, 1);
    }
    private void locationStart() {
        try{
            LocationManager mlocManager = (LocationManager) CierreViaje.this.getSystemService(Context.LOCATION_SERVICE);
            CierreViaje.Localizacion Local = new CierreViaje.Localizacion();

            Local.setMainActivity(CierreViaje.this);
            final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!gpsEnabled) {
                Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);  // Abre pantalla para activar GPS cuando esta apagado
                startActivity(settingsIntent);
            }
            if (ActivityCompat.checkSelfPermission(CierreViaje.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(CierreViaje.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(CierreViaje.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
                return;
            }
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        }catch (Exception e){
            Toast.makeText(CierreViaje.this,"ERROR DENTRO DE LOCATIONSTAR"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    public class Localizacion implements LocationListener {
        CierreViaje mainActivity;

        public CierreViaje getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(CierreViaje mainActivity) {
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
                Toast.makeText(CierreViaje.this, "on error in location change" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            Toast.makeText(CierreViaje.this,"GPS DESACTIVADO",Toast.LENGTH_LONG).show();
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            Toast.makeText(CierreViaje.this,"GPS ACTIVO",Toast.LENGTH_LONG).show();
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


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
                String fecha = sdf.format(new Date());
                SimpleDateFormat hdf = new SimpleDateFormat("HH:mm:ss");
                String hora = hdf.format(new Date());


                String dniconductor = qr.toString();
                String latitud=txtlatitud.getText().toString();
                String longitud=txtlongitud.getText().toString();

                AsistenciaHelper cn = new AsistenciaHelper(CierreViaje.this, "RRHH", null, 1);
                SQLiteDatabase db = cn.getWritableDatabase();

                String update = "UPDATE VIAJES SET DNIRETORNO='" + dniconductor + "' , FECHAINICIORETORNO='" + fecha + "' ,HORAINICIORETORNO='"+hora+"' , LATITUDINICIORETORNO='"+latitud+"' ,LONGITUDINICIORETORNO='"+longitud+"' where ID='"+idviaje+"'";
                db.execSQL(update);


                Intent midintent = new Intent(CierreViaje.this, ASISTENCIA.class);
                Bundle mibundle = new Bundle();
                mibundle.putString("idviaje", idviaje);
                midintent.putExtras(mibundle);
                startActivity(midintent);
                finish();


            }
        }
    }
}
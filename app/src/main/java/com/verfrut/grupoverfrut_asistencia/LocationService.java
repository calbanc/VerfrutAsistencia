package com.verfrut.grupoverfrut_asistencia;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class LocationService extends Service implements LocationListener {

    private Context context = this;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 20000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;

    @Override
    public void onCreate() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(),45,notify_interval);
        intent = new Intent(str_receiver);
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int idProcess) {
        onCreate();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }



    private void fn_getlocation() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnable && !isNetworkEnable) {

        } else {

            if (isNetworkEnable) {
                location = null;
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location!=null){

                        Log.e("latitudes",location.getLatitude()+"");
                        Log.e("longitudes",location.getLongitude()+"");

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }

            }


            if (isGPSEnable){
                location = null;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        Log.e("latitude",location.getLatitude()+"");
                        Log.e("longitude",location.getLongitude()+"");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                    }
                }
            }


        }

    }
    private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }
    private void fn_update(Location location){

        System.out.println("ingresa a metodo fnupdate");
        AsistenciaHelper cn = new AsistenciaHelper(context, "RRHH", null, 1);
        SQLiteDatabase db = cn.getWritableDatabase();
        intent.putExtra("latutide",location.getLatitude()+"");
        intent.putExtra("longitude",location.getLongitude()+"");
        location.getLatitude();
        location.getLongitude();
        location.getSpeed();
        DecimalFormat df = new DecimalFormat("#.00");
        String sLatitud = String.valueOf(location.getLatitude());
        String sLongitud = String.valueOf(location.getLongitude());

        String sVelocidad = String.valueOf(df.format(location.getSpeed() * 3.6));

        if (sVelocidad.equals(",00")) {

            sVelocidad = "0.00";
        }
        //Toast.makeText(getApplicationContext(),"latitud"+sLatitud,Toast.LENGTH_LONG).show();


        String consulta = "SELECT * FROM ESTACION";

        Cursor cr = db.rawQuery(consulta, null);
        if (cr.moveToFirst()) {
            String tipo=cr.getString(2).trim();
            if(tipo.equals("BUS")){
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
                String fecha = sdf.format(new Date());
                SimpleDateFormat hdf = new SimpleDateFormat("HH:mm:ss");
                String hora = hdf.format(new Date());
                String idequipo = cr.getString(0);
                String idmarcacion = idequipo + fecha.replaceAll("-", "") + hora.replaceAll(":", "");
                String latitud = sLatitud;
                String longitud = sLongitud;
                String velocidad = sVelocidad;
                String versionapp = "Seguimiento";
                String consultaS="SELECT ID FROM POSICIONES WHERE ID='"+idmarcacion+"' ";
                System.out.println(consultaS);
                Cursor cr1=db.rawQuery(consultaS,null);
                if(cr1.moveToFirst()){

                }else{
                    String inserta = "INSERT INTO POSICIONES(ID,FECHA,HORA,LATITUD,LONGITUD,VELOCIDAD,ESTACION,VERSIONAPP,SW_ENVIADO) VALUES ('" + idmarcacion + "','" + fecha + "','" + hora + "','" + latitud + "','" + longitud + "','" + velocidad + "','" + idequipo + "','" + versionapp + "','0')";
                    db.execSQL(inserta);
                    System.out.println(inserta);
                }
            }

        }
        sendBroadcast(intent);
    }
}

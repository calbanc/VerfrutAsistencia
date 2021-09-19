package com.verfrut.grupoverfrut_asistencia.ui.Consultas;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.verfrut.grupoverfrut_asistencia.ASISTENCIA;
import com.verfrut.grupoverfrut_asistencia.Adaptador.ListaMarcacionesAdapter;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.verfrut.grupoverfrut_asistencia.Entidades.Marcaciones;
import com.verfrut.grupoverfrut_asistencia.Global;
import com.verfrut.grupoverfrut_asistencia.R;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListarMarcaciones#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListarMarcaciones extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextInputEditText txtfecha;
    Button btnbuscar;
    Button btnenviar;
    RecyclerView rvlista;
    private AsyncHttpClient cliente;
    RadioButton rbnenviados,rbnnoenviados;
    Calendar calendario=Calendar.getInstance();
    public final Calendar c = Calendar.getInstance();
    public ListarMarcaciones() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListarMarcaciones.
     */
    // TODO: Rename and change types and number of parameters
    public static ListarMarcaciones newInstance(String param1, String param2) {
        ListarMarcaciones fragment = new ListarMarcaciones();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista= inflater.inflate(R.layout.fragment_listar_marcaciones, container, false);
        txtfecha=vista.findViewById(R.id.txtfecha);
        btnbuscar=vista.findViewById(R.id.btnbuscar);
        rbnnoenviados=vista.findViewById(R.id.rbnnoenviados);
        rbnenviados=vista.findViewById(R.id.rbnenviados);
        rvlista=vista.findViewById(R.id.rvlista);
        rvlista.setLayoutManager(new LinearLayoutManager(getContext()));
        rvlista.setHasFixedSize(true);
        btnenviar=vista.findViewById(R.id.btnenviar);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); //dd/MM/yyyy HH:mm:ss
        String fecha = sdf.format(new Date());
        cliente=new AsyncHttpClient();
        txtfecha.setText(fecha);

        btnenviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fechas=txtfecha.getText().toString();

                enviardatos(fechas);

               //
            }
        });


        txtfecha.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (txtfecha.getRight() - txtfecha.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        // your action here

                        new DatePickerDialog(getContext(), date, calendario
                                .get(Calendar.YEAR), calendario.get(Calendar.MONTH),
                                calendario.get(Calendar.DAY_OF_MONTH)).show();
                        return true;
                    }
                }
                return false;
            }
        });
        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtfecha.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"DEBE SELECCIONAR UNA FECHA",Toast.LENGTH_SHORT).show();
                }else{
                    String fecha=txtfecha.getText().toString();
                    consultarlista(fecha);
                }
            }
        });

        return vista;
    }

    private void enviardatos(String fechas) {
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        final  SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT DNI,FECHA,HORA,ESTACION,LATITUD,LONGITUD,IDMARCACION,VERSIONAPP FROM MARCACIONES WHERE SW_ENVIADO='0'  AND FECHA='"+fechas+"'  ";
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


                    String url= Global.url+"wsinsertamarcacion.php?RutTrabajador="+dni+"&&Fecha="+fecha+"&&Hora="+hora+"&&Latitud="+latitud+"&&Longitud="+longitud+"&&IdEstacion="+estacion+"&&Id="+idmarcacion+"&&VersionApp="+versionapp;

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
                                    Toast.makeText(getContext(),"MARCACION NO ENVIADA"+e.getMessage(),Toast.LENGTH_LONG).show();

                                }

                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getContext(),"SIN CONEXION MARCACION",Toast.LENGTH_SHORT).show();
                        }
                    });

                    cr.moveToNext();
                }
            }else{

            }

        }catch (Exception e){
            Toast.makeText(getContext(),"Eror enviando datos a servidor"+e.getMessage(),Toast.LENGTH_LONG).show();
        }



    }


    private void consultarlista(String fecha) {

        if(rbnenviados.isChecked()){
            AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
            SQLiteDatabase db=cn.getWritableDatabase();
            ArrayList<Marcaciones>listamarcaciones=new ArrayList<Marcaciones>();
            String consulta="SELECT DNI,HORA,IDMARCACION,SW_ENVIADO FROM MARCACIONES WHERE FECHA='"+fecha+"' AND SW_ENVIADO='1'  GROUP BY IDMARCACION ORDER BY HORA ASC";
            Cursor cr=db.rawQuery(consulta,null);
            if(cr.moveToFirst()){
                while(cr.isAfterLast()==false){
                    Marcaciones mismarcaciones=new Marcaciones();
                    mismarcaciones.setDni(cr.getString(0));
                    mismarcaciones.setHora(cr.getString(1));
                    mismarcaciones.setId(cr.getString(2));
                    mismarcaciones.setEnviado(cr.getString(3));
                    listamarcaciones.add(mismarcaciones);
                    cr.moveToNext();
                }
                ListaMarcacionesAdapter adapter=new ListaMarcacionesAdapter(listamarcaciones);
                rvlista.setAdapter(adapter);

            }else{
                Toast.makeText(getContext(),"SIN MARCACIONES",Toast.LENGTH_SHORT).show();
            }
        }else{
            AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
            SQLiteDatabase db=cn.getWritableDatabase();
            ArrayList<Marcaciones>listamarcaciones=new ArrayList<Marcaciones>();
            String consulta="SELECT DNI,HORA,IDMARCACION,SW_ENVIADO FROM MARCACIONES WHERE FECHA='"+fecha+"' AND SW_ENVIADO='0'  GROUP BY IDMARCACION ORDER BY HORA ASC";
            Cursor cr=db.rawQuery(consulta,null);
            if(cr.moveToFirst()){
                while(cr.isAfterLast()==false){
                    Marcaciones mismarcaciones=new Marcaciones();
                    mismarcaciones.setDni(cr.getString(0));
                    mismarcaciones.setHora(cr.getString(1));
                    mismarcaciones.setId(cr.getString(2));
                    mismarcaciones.setEnviado(cr.getString(3));
                    listamarcaciones.add(mismarcaciones);
                    cr.moveToNext();
                }
                ListaMarcacionesAdapter adapter=new ListaMarcacionesAdapter(listamarcaciones);
                rvlista.setAdapter(adapter);

            }else{
                Toast.makeText(getContext(),"SIN MARCACIONES",Toast.LENGTH_SHORT).show();
            }
        }





    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            calendario.set(Calendar.YEAR, year);
            calendario.set(Calendar.MONTH, monthOfYear);
            calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            actualizarInput();
        }

    };

    private void actualizarInput() {
        String formatoDeFecha = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(formatoDeFecha, Locale.US);

        txtfecha.setText(sdf.format(calendario.getTime()));

    }
}
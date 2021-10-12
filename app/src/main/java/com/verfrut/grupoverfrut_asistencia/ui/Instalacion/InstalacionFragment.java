package com.verfrut.grupoverfrut_asistencia.ui.Instalacion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.verfrut.grupoverfrut_asistencia.ADMINISTRADOR;
import com.verfrut.grupoverfrut_asistencia.Admin;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.verfrut.grupoverfrut_asistencia.Entidades.Estaciones;
import com.verfrut.grupoverfrut_asistencia.Global;
import com.verfrut.grupoverfrut_asistencia.MainActivity;
import com.verfrut.grupoverfrut_asistencia.R;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InstalacionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InstalacionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btnaceptar,btnretirar,btncerrarsesion;
    TextInputEditText txtnombre_estacion;
    TextView txtestacionguardada,txttipoestacion,idempresa,txtid;
    RadioButton rbfija,rbmovil,rbbus;
    private AsyncHttpClient cliente;
    Spinner spempresa,spzona,spcuartel,spcodbus;

    ProgressDialog progreso;
    public static final String prefencia="prefencia";
    public InstalacionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InstalacionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InstalacionFragment newInstance(String param1, String param2) {
        InstalacionFragment fragment = new InstalacionFragment();
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
        View vista=inflater.inflate(R.layout.fragment_instalacion, container, false);

        btnaceptar=vista.findViewById(R.id.btnaceptar);
        txtnombre_estacion=vista.findViewById(R.id.txtnombre_estacion);
        spempresa=vista.findViewById(R.id.spempresa);
        spzona=vista.findViewById(R.id.spzona);
        spcuartel=vista.findViewById(R.id.spcuartel);
        spcodbus=vista.findViewById(R.id.spcodbus);
        rbfija=vista.findViewById(R.id.rbfija);
        rbmovil=vista.findViewById(R.id.rbmovil);
        rbbus=vista.findViewById(R.id.rbbus);
        txtestacionguardada=vista.findViewById(R.id.txtestacionguardada);
        txttipoestacion=vista.findViewById(R.id.txttipoestacion);
        idempresa=vista.findViewById(R.id.idempresa);
        txtid=vista.findViewById(R.id.txtid);
        btnretirar=vista.findViewById(R.id.btnretirar);
        btncerrarsesion=vista.findViewById(R.id.btncerrarsesion);

        cliente=new AsyncHttpClient();
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();

        try{
            mostrarnombreestacion();
            cargarempresas();
        }catch (Exception e){
            System.out.println("MOSTRAR PERMISO"+e.getMessage());
            Toast.makeText(getContext(),"MOSTRAR PERMISO"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }



        spcuartel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spcuartel.getSelectedItem().toString().equals("Seleccione Cuartel")){

                }else{
                    try{
                        String nom=spcuartel.getSelectedItem().toString();
                        String[]nomb=nom.split("-",0);
                        String nombre=nomb[1]+"-"+nomb[2];
                        txtnombre_estacion.setText(nombre);
                    }catch (Exception e){
                        String nom=spcuartel.getSelectedItem().toString();
                        String[]nomb=nom.split("-",0);
                        String nombre=nomb[1];
                        txtnombre_estacion.setText(nombre);
                    }

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spzona.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spzona.getSelectedItem().toString().equals("Seleccione Zonas")){

                }else{
                    String codzon=spzona.getSelectedItem().toString();
                    String[]codzona=codzon.split("-",0);
                    String cod_zona=codzona[0];
                    String empresas=spempresa.getSelectedItem().toString();
                    String[]em=empresas.split("-",0);
                    String cod_emp=em[0];

                    cargarcuartel(cod_zona,cod_emp);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spempresa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if(spempresa.getSelectedItem().toString().equals("Seleccione Empresa")){
                    Toast.makeText(getContext(),"DEBE SELECCIONAR UNA EMPRESA",Toast.LENGTH_SHORT).show();
                }else{
                    String empresas=spempresa.getSelectedItem().toString();
                    String[]em=empresas.split("-",0);
                    String cod_emp=em[0];
                    if(rbbus.isChecked()){
                        cargarbuses(cod_emp);
                    }else{
                        if(rbfija.isChecked()||rbmovil.isChecked()){
                           cargarzonas(cod_emp);
                        }else{
                            Toast.makeText(getContext(),"DEBE SELECCIONAR UN TIPO DE EQUIPO",Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spcodbus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(spcodbus.getSelectedItem().toString().equals("Seleccione Bus")){

                }else{
                    String codbus=spcodbus.getSelectedItem().toString();
                    String[]cod=codbus.split("-",0);
                    String placa=cod[1]+"-"+cod[2];

                    txtnombre_estacion.setText(placa);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnretirar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtid.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"DEBE REGISTRAR UNA INTALACION ANTES",Toast.LENGTH_SHORT).show();

                }else{
                    String id=txtid.getText().toString();
                    Date d = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String fecha=dateFormat.format(d).replaceAll(" ","%20");



                    retirarlocal(id,fecha);

                }


            }
        });

        btnaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rbfija.isChecked()||rbmovil.isChecked()){
                    if(spempresa.getSelectedItem().toString().equals("Seleccione Empresa")
                            ||spzona.getSelectedItem().toString().equals("Seleccione Zonas")
                    ||spcuartel.getSelectedItem().toString().equals("Seleccione Cuartel")||txtnombre_estacion.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"DEBE COMPLETAR TODOS LOS DATOS",Toast.LENGTH_SHORT).show();
                    }else{

                            System.out.println("REGISTRO LOCAL");
                            String codzon=spzona.getSelectedItem().toString();
                            String[]codzona=codzon.split("-",0);
                            String cod_zona=codzona[0];
                            String empresas=spempresa.getSelectedItem().toString();
                            String[]em=empresas.split("-",0);
                            String cod_emp=em[0];
                            String cuartel=spcuartel.getSelectedItem().toString();
                            String[]idcu=cuartel.split("-",0);
                            String cod_cua=idcu[0];
                            Date d = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String fecha=dateFormat.format(d).replaceAll(" ","%20");
                            SimpleDateFormat dateFormats = new SimpleDateFormat("dd/MM/yyyy");
                            String dia=dateFormats.format(d).replaceAll("/","");
                            SimpleDateFormat datesFormats = new SimpleDateFormat("HH:mm:ss");
                            String hora=datesFormats.format(d).replaceAll(":","");


                            String nombre=txtnombre_estacion.getText().toString().replaceAll(" ","%20");
                            String id=cod_emp+cod_zona+cod_cua+dia+hora;



                                registrarinstalacionlocal(cod_zona,cod_emp,cod_cua,fecha,id,nombre);




                    }
                }else{
                    if(spempresa.getSelectedItem().toString().equals("Seleccione Empresa")||spcodbus.getSelectedItem().toString().equals("Seleccione Bus")||txtnombre_estacion.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"DEBE INGRESAR TODOS LOS DATOS",Toast.LENGTH_SHORT).show();
                    }else{

                            System.out.println("REGISTRO LOCAL");
                            String codbuss=spcodbus.getSelectedItem().toString();
                            String[]codzona=codbuss.split("-",0);
                            String codbus=codzona[0];
                            String empresas=spempresa.getSelectedItem().toString();
                            String[]em=empresas.split("-",0);
                            String cod_emp=em[0];

                            Date d = new Date();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            String fecha=dateFormat.format(d).replaceAll(" ","%20");
                            SimpleDateFormat dateFormats = new SimpleDateFormat("dd/MM/yyyy");
                            String dia=dateFormats.format(d).replaceAll("/","");
                            SimpleDateFormat datesFormats = new SimpleDateFormat("HH:mm:ss");
                            String hora=datesFormats.format(d).replaceAll(":","");


                            String nombre=txtnombre_estacion.getText().toString().replaceAll(" ","%20");
                            String id=cod_emp+codbus+dia+hora;



                                registrarbuslocal(codbus,cod_emp,fecha,id,nombre);



                    }

                }

            }
        });

        return vista;
    }



    private void registrarbusremoto(String codbus, String cod_emp, String fecha, String id, String nombre) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando instalacion de equipo...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();


        String url=Global.url+"swinstalbus.php?idempresa="+cod_emp+"&&ID="+id+"&&TIPO_ESTACION=BUS"+
                "&&NOMBRE_ESTACION="+nombre+"&&FECHA_INSTALACION="+fecha+"&&codbus="+codbus;
        System.out.println(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progreso.hide();

                    instalacion(new String(responseBody));

                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void registrarbuslocal(String codbus, String cod_emp, String fecha, String id, String nombre) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando instalacion de equipo...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();


        String url=Global.url+"swinstalbus.php?idempresa="+cod_emp+"&&ID="+id+"&&TIPO_ESTACION=BUS"+
                "&&NOMBRE_ESTACION="+nombre+"&&FECHA_INSTALACION="+fecha+"&&codbus="+codbus;
        System.out.println(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progreso.hide();

                    instalacion(new String(responseBody));

                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });


    }

    private void registrarinstalacionremoto(String cod_zona, String cod_emp, String cod_cua, String fecha, String id, String nombre) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando instalacion de equipo...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();


        String url=Global.url+"swinstalfija.php?idempresa="+cod_emp+"&&ID="+id+"&&TIPO_ESTACION=FIJA"+
                "&&NOMBRE_ESTACION="+nombre+"&&FECHA_INSTALACION="+fecha+"&&idzona="+cod_zona+"&&idcuartel="+cod_cua;
        System.out.println(url);
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progreso.hide();

                    instalacion(new String(responseBody));

                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });



    }

    private void cargarcuartel(String cod_zona, String cod_emp) {
        String url=Global.url+"wscuartel.php?IdEmpresa="+cod_emp+"&&IdZona="+cod_zona;


        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    cuartel(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }



    private void cuartel(String s) {
        ArrayList<Estaciones>listazonas=new ArrayList<Estaciones>();
        ArrayList<String>listazonastring=new ArrayList<String>();

        try{
            JSONArray json=new JSONArray(s);

            for(int i=0;i<json.length();i++){
                Estaciones empresa=new Estaciones();
                empresa.setEmpresa(json.getJSONObject(i).getString("IdCuartel"));
                empresa.setNombreempresa(json.getJSONObject(i).getString("Nombre"));

                listazonas.add(empresa);
            }
            listazonastring.add("Seleccione Cuartel");
            for(int i=0;i<listazonas.size();i++){
                listazonastring.add(listazonas.get(i).getEmpresa()+"-"+listazonas.get(i).getNombreempresa());
            }

            ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice
                    ,listazonastring);
            spcuartel.setAdapter(adapter);

        }catch (Exception e){

        }
    }

    private void cargarzonas(String cod_emp) {
        String url= Global.url+"wszona.php?IdEmpresa="+cod_emp;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    zonas(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });



    }



    private void zonas(String s) {
        ArrayList<Estaciones>listazonas=new ArrayList<Estaciones>();
        ArrayList<String>listazonastring=new ArrayList<String>();

        try{
            JSONArray json=new JSONArray(s);

            for(int i=0;i<json.length();i++){
                Estaciones empresa=new Estaciones();
                empresa.setEmpresa(json.getJSONObject(i).getString("IdZona"));
                empresa.setNombreempresa(json.getJSONObject(i).getString("Nombre"));

                listazonas.add(empresa);
            }
            listazonastring.add("Seleccione Zonas");
            for(int i=0;i<listazonas.size();i++){
                listazonastring.add(listazonas.get(i).getEmpresa()+"-"+listazonas.get(i).getNombreempresa());
            }

            ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice
                    ,listazonastring);
            spzona.setAdapter(adapter);

        }catch (Exception e){

        }





    }

    private void cargarbuses(String cod_emp) {
        String url=Global.url+"wsbuses.php?IDEMPRESA="+cod_emp;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    buses(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"ERROR EN CONEXION CON BUSES",Toast.LENGTH_SHORT).show();
            }
        });




    }


    private void buses(String s) {
        ArrayList<Estaciones>listabuses=new ArrayList<Estaciones>();
        ArrayList<String>listabusesestring=new ArrayList<String>();

        try{
            JSONArray json=new JSONArray(s);

            for(int i=0;i<json.length();i++){
                Estaciones empresa=new Estaciones();
                empresa.setEmpresa(json.getJSONObject(i).getString("COD_BUS"));
                empresa.setNombreempresa(json.getJSONObject(i).getString("PATENTE"));
                System.out.println("Empresa " +empresa.getEmpresa());
                listabuses.add(empresa);
            }
            listabusesestring.add("Seleccione Bus");
            for(int i=0;i<listabuses.size();i++){
                listabusesestring.add(listabuses.get(i).getEmpresa()+"-"+listabuses.get(i).getNombreempresa());
            }

            ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice
                    ,listabusesestring);
            spcodbus.setAdapter(adapter);

        }catch (Exception e){

        }



    }

    private void cargarempresas() {
        String url;
        url=Global.url+"wsempresa.php?";

            muestraempresa(url);

    }

    private void muestraempresa(String url) {

        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    empresas(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"ERRROR EN CONSULTA",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void empresas(String s) {
        ArrayList<Estaciones>listaempresa=new ArrayList<Estaciones>();
        ArrayList<String>listaempresastring=new ArrayList<String>();
        try{
            JSONArray json=new JSONArray(s);
            for(int i=0;i<json.length();i++){
                Estaciones empresa=new Estaciones();
                empresa.setEmpresa(json.getJSONObject(i).getString("IdEmpresa"));
                empresa.setNombreempresa(json.getJSONObject(i).getString("Nombre"));
                System.out.println("Empresa " +empresa.getEmpresa());
                listaempresa.add(empresa);
            }
            listaempresastring.add("Seleccione Empresa");
            for(int i=0;i<listaempresa.size();i++){
                listaempresastring.add(listaempresa.get(i).getEmpresa()+"-"+listaempresa.get(i).getNombreempresa());
            }
            ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice,listaempresastring);
            spempresa.setAdapter(adapter);
        }catch (Exception e){
        }
    }

    private void retirarlocal(String id,String fecha) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Retirando instalacion de equipo...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();
        String url=Global.url+"swretiraequipo.php?ID="+id+"&&FECHA_RETIRO="+fecha;
        System.out.println(url);

        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progreso.hide();
                    retirado(new String(responseBody));
                    nuevo();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void retirado(String s) {
        try{
            JSONArray json=new JSONArray(s);
            String sw_validado=json.getJSONObject(0).getString("sw_activo");
            if(sw_validado.equals("0")){
                nuevo();
            }else{
                Toast.makeText(getContext(),"NO SE LOGRO RETIRAR EL EQUIPO CORRECTAMENTE",Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
        }
    }


    private void registrarinstalacionlocal(String cod_zona,String cod_emp,String cod_cua,String fecha,String id,String nombre) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando instalacion de equipo...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();
        String url=Global.url+"swinstalfija.php?idempresa="+cod_emp+"&&ID="+id+"&&TIPO_ESTACION=FIJA"+
                "&&NOMBRE_ESTACION="+nombre+"&&FECHA_INSTALACION="+fecha+"&&idzona="+cod_zona+"&&idcuartel="+cod_cua;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    progreso.hide();
                    instalacion(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });



    }

    private void instalacion(String s) {

        try{
            JSONArray jsonarreglo = new JSONArray(s);


            String id=jsonarreglo.getJSONObject(0).getString("ID");
            String nombre=jsonarreglo.getJSONObject(0).getString("NOMBRE_ESTACION");
            String tipoestacion=jsonarreglo.getJSONObject(0).getString("TIPO_ESTACION");
            AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
            SQLiteDatabase db=cn.getWritableDatabase();
            db.isOpen();

            String llenartabla="INSERT INTO ESTACION(ID,TIPO,ESTACION)VALUES('"+id+"','"+nombre+"','"+tipoestacion+"')";

            db.execSQL(llenartabla);

            db.close();
            mostrarnombreestacion();
        }catch (Exception e){

        }

    }

    private void regitrarestacion() {

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando retiro de equipo...");
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fecha=dateFormat.format(d).replaceAll(" ","%20");


        String url=Global.url+"wsinsertaestacionmarcacion.php?idempresa="+idempresas+"&&Temporada="+temporada+"&&TIPO_ESTACION="+tipoestacion+
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
                        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
                        SQLiteDatabase db=cn.getWritableDatabase();
                        db.isOpen();

                        String llenartabla="INSERT INTO ESTACION(ID,TIPO,ESTACION)VALUES('"+id+"','"+txtnombre_estacion.getText().toString()+"','"+tipoestacion+"')";

                        db.execSQL(llenartabla);

                        db.close();
                        mostrarnombreestacion();
                    } catch (Exception e) {
                        progreso.hide();
                        Toast.makeText(getContext(),"ERROR INSERTANDO"+e.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });

    }
    private void nuevo() {

        txtnombre_estacion.setText("");
        txtid.setText("");
        txtestacionguardada.setText("");
        txttipoestacion.setText("");
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();

        String llenartabla="DELETE FROM ESTACION";
        db.execSQL(llenartabla);
        db.close();
        Toast.makeText(getContext(),"EQUIPO RETIRADO",Toast.LENGTH_LONG).show();
    }
    private void mostrarnombreestacion() {
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="SELECT * FROM ESTACION";
        Cursor cr=db.rawQuery(consulta,null);

        if(cr.moveToNext()){
            txtestacionguardada.setText(cr.getString(1));
            txttipoestacion.setText(cr.getString(2));
            txtid.setText(cr.getString(0));
        }
    }
    private void retirar(String id,String fecha) {

        String url=Global.url+"swretiraequipo.php?ID="+id+"&&FECHA_RETIRO="+fecha;


        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                   retirado(new String(responseBody));
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"SIN CONEXION",Toast.LENGTH_LONG).show();
            }
        });


    }
}
package com.verfrut.grupoverfrut_asistencia.ui.Instalacion;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
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
import org.json.JSONObject;

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
    Button btnaceptar,btnretirar;
    TextInputEditText txtnombre_estacion;
    TextView txtestacionguardada,txttipoestacion,idempresa,txtid;
    RadioButton rbfija,rbmovil,rbbus;
    private AsyncHttpClient cliente;

    TextInputEditText txtempresa,txttemporada,txtzona,txtcuartel,txttransportista,txtbus;
    Dialog dialog;
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
        txtempresa=vista.findViewById(R.id.txtempresa);
        txttemporada=vista.findViewById(R.id.txttemporada);
        txtzona=vista.findViewById(R.id.txtzona);
        txtcuartel=vista.findViewById(R.id.txtcuartel);
        txttransportista=vista.findViewById(R.id.txttransportista);
        txtbus=vista.findViewById(R.id.txtbus);


        btnaceptar=vista.findViewById(R.id.btnaceptar);
        txtnombre_estacion=vista.findViewById(R.id.txtnombre_estacion);

        rbfija=vista.findViewById(R.id.rbfija);
        rbmovil=vista.findViewById(R.id.rbmovil);
        rbbus=vista.findViewById(R.id.rbbus);
        txtestacionguardada=vista.findViewById(R.id.txtestacionguardada);
        txttipoestacion=vista.findViewById(R.id.txttipoestacion);
        idempresa=vista.findViewById(R.id.idempresa);
        txtid=vista.findViewById(R.id.txtid);
        btnretirar=vista.findViewById(R.id.btnretirar);

        btnaceptar.setEnabled(false);
        cliente=new AsyncHttpClient();
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();

        txtempresa.setFocusable(false);
        txtempresa.setClickable(true);

        txttemporada.setFocusable(false);
        txttemporada.setClickable(true);
        txtzona.setFocusable(false);
        txtzona.setClickable(true);
        txtcuartel.setFocusable(false);
        txtcuartel.setClickable(true);

        txttransportista.setFocusable(false);
        txttransportista.setClickable(true);

        txtbus.setFocusable(false);
        txtbus.setClickable(true);

        try{
            mostrarnombreestacion();

        }catch (Exception e){

            Toast.makeText(getContext(),"MOSTRAR PERMISO"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        rbbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtcuartel.setText("");
                txtzona.setText("");
                txtnombre_estacion.setText("");

            }
        });



        rbfija.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txttransportista.setText("");
                txtbus.setText("");
                txtnombre_estacion.setText("");
            }
        });


        txtbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rbbus.isChecked()){
                    if(txtempresa.getText().toString().isEmpty()||txttemporada.getText().toString().isEmpty()||txttransportista.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"Debe seleccionar una empresa , temporada y transportista",Toast.LENGTH_SHORT).show();
                    }else{
                        String cod_emp=Global.leecodigo(txtempresa.getText().toString()," - ");
                        String cod_tem=Global.leecodigo(txttemporada.getText().toString()," - ");
                        String cod_trp=Global.leecodigo(txttransportista.getText().toString()," - ");
                        cargarbuses(cod_emp,cod_tem,cod_trp);
                    }


                }else{
                    Toast.makeText(getContext(),"Opcion selecionada no compatible",Toast.LENGTH_SHORT).show();
                }



            }
        });



        txttransportista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbbus.isChecked()){
                    if(txtempresa.getText().toString().isEmpty()||txttemporada.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"Debe seleccionar una empresa y temporada",Toast.LENGTH_SHORT).show();
                    }else{
                        String empresa=Global.leecodigo(txtempresa.getText().toString()," - ");
                        String temporada=Global.leecodigo(txttemporada.getText().toString()," - ");
                        cargartransportistas(empresa,temporada);
                    }
                }else{
                    Toast.makeText(getContext(),"Opcion selecionada no compatible",Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtempresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rbfija.isChecked() || rbbus.isChecked() || rbmovil.isChecked()){
                    cargarempresas();
                }else{

                    Toast.makeText(getContext(),"Debe seleccionar un tipo de estacion a instalar",Toast.LENGTH_LONG).show();
                }

            }
        });
        txttemporada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtempresa.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"DEBE INGRESAR EMPERSA",Toast.LENGTH_LONG).show();
                }else{
                    String empresa=Global.leecodigo(txtempresa.getText().toString()," - ");
                    cargartemporada(empresa);
                }
            }
        });
        txtzona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(rbfija.isChecked()){
                    if(txtempresa.getText().toString().isEmpty()||txttemporada.getText().toString().isEmpty() ){
                        Toast.makeText(getContext(),"Debe selecionar una empresa y una temporada",Toast.LENGTH_SHORT).show();
                    }else{
                        String cod_emp= Global.leecodigo(txtempresa.getText().toString()," - ");
                        cargarzonas(cod_emp);
                    }


                }else{
                    Toast.makeText(getContext(),"Tipo de estacion seleccionado no compatible",Toast.LENGTH_LONG).show();
                }
            }
        });

        txtcuartel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(rbfija.isChecked()){
                    if(txtzona.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"DEBE SELECCIONAR UNA ZONA ",Toast.LENGTH_LONG).show();
                    }else{
                        String cod_emp=Global.leecodigo(txtempresa.getText().toString()," - ");
                        String idzona=Global.leecodigo(txtzona.getText().toString()," - ");
                        cargarcuartel(cod_emp,idzona);
                    }
                }else{
                    Toast.makeText(getContext(),"Tipo de estacion seleccionada no compatible",Toast.LENGTH_SHORT).show();
                }

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


                if(txtempresa.getText().toString().isEmpty()||txttemporada.getText().toString().isEmpty()){
                    Snackbar.make(getView(),"DEBE SELECCIONAR UNA EMPRESA Y TEMPORADA",Snackbar.LENGTH_SHORT).show();

                }else{
                    if(rbfija.isChecked()){
                        if(txtzona.getText().toString().isEmpty()||txtcuartel.getText().toString().isEmpty()||txtnombre_estacion.getText().toString().isEmpty()){
                            Snackbar.make(getView(),"DEBE INGRESAR ZONA Y CUARTEL ",Snackbar.LENGTH_SHORT).show();
                        }else{
                            registrar();
                        }
                    }else{
                         if(txttransportista.getText().toString().isEmpty()||txtbus.getText().toString().isEmpty()){
                             Snackbar.make(getView(),"DEBE INGRESAR TRANSPORTISTA Y BUS ",Snackbar.LENGTH_SHORT).show();
                         }else{
                             registrar();
                         }
                    }
                }





            }
        });

        return vista;
    }

    private void registrar() {

        String empresa=Global.leecodigo(txtempresa.getText().toString()," - ");
        String temporada=Global.leecodigo(txttemporada.getText().toString()," - ");
        String zona=Global.leecodigo(txtzona.getText().toString()," - ");
        String cuartel=Global.leecodigo(txtcuartel.getText().toString()," - ");
        String transportista=Global.leecodigo(txttransportista.getText().toString()," - ");
        String bus=Global.leecodigo(txtbus.getText().toString()," - ");
        String tipo="BUS";
        String nombreestacion=txtnombre_estacion.getText().toString().replaceAll(" ","%20");
        Date d = new Date();
        SimpleDateFormat dateFormats = new SimpleDateFormat("dd/MM/yyyy");
        String dia=dateFormats.format(d).replaceAll("/","");
        SimpleDateFormat datesFormats = new SimpleDateFormat("HH:mm:ss");
        String hora=datesFormats.format(d).replaceAll(":","");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fecha=dateFormat.format(d).replaceAll(" ","%20");

        String id=transportista+bus+dia+hora;

        if(rbfija.isChecked()){
            tipo="FIJA";
            id=cuartel+dia+hora;
        }

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Registrando Estacion de marcacion...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();

        String url=Global.url+"wsregistraestacion.php?ID="+id+"&IDEMPRESA="+empresa+"&IDTEMPORADA="+temporada+"&TIPO="+tipo+"&COD_TRP="+transportista+"&COD_BUS="+bus+"&IDZONA="+zona+"&IDCUARTEL="+cuartel+"&FECHA_INSTALACION="+fecha+"&NOMBRE_ESTACION="+nombreestacion;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progreso.hide();
                instalacion(new String(responseBody));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"ERROR EN SERVICIO DE INSTALACION DE MARCACION",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void cargartransportistas(String empresa, String temporada) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando transportistas...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();

        String url=Global.url+"wstransportistas.php?COD_EMP="+empresa+"&COD_TEM="+temporada+"&&IDUSUARIO=reporte&&clave=abc.123456";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progreso.hide();
                if(statusCode==200){
                    transportista(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"ERROR SERVIDOR CONSULTA TRANSPORTISTA",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void transportista(String s) {
        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        dialog.getWindow().setLayout(800,800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText=dialog.findViewById(R.id.edit_text);
        ListView listView=dialog.findViewById(R.id.list_view);

        // show dialog
        dialog.show();

        try{
            JSONArray jsonArray=new JSONArray(s);
            ArrayList<Estaciones>listaestaciones=new ArrayList<Estaciones>();
            for(int i=0;i<jsonArray.length();i++){
                Estaciones estacion=new Estaciones();
                estacion.setEmpresa(jsonArray.getJSONObject(i).getString("COD_TRP"));
                estacion.setNombreempresa(jsonArray.getJSONObject(i).getString("NOM_TRP"));
                listaestaciones.add(estacion);
            }

            ArrayList<String>listaestacionstring=new ArrayList<String>();
            for(int i=0;i<listaestaciones.size();i++){
                listaestacionstring.add(listaestaciones.get(i).getEmpresa()+" - "+listaestaciones.get(i).getNombreempresa());
            }

            final ArrayAdapter<String>adapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_activated_1,listaestacionstring);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    txttransportista.setText(adapter.getItem(position));



                    // Dismiss dialog
                    dialog.dismiss();
                }
            });




        }catch (Exception e){

        }

    }

    private void cargartemporada(String empresa) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando temporadas de la empresa...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();

        String url=Global.url2+"wscargartemporada.php?COD_EMP="+empresa.toString()+"&&IDUSUARIO=reporte&&clave=abc.123456";

        cliente.post(url , new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progreso.hide();
                if(statusCode==200){
                    cargartemporadas(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"ERROR EN SERVIDOR",Toast.LENGTH_LONG).show();

            }
        });




    }

    private void cargartemporadas(String s) {

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        dialog.getWindow().setLayout(800,800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText=dialog.findViewById(R.id.edit_text);
        ListView listView=dialog.findViewById(R.id.list_view);

        // show dialog
        dialog.show();


        ArrayList<Estaciones>listaempresa=new ArrayList<Estaciones>();
        ArrayList<String>listaempresastring=new ArrayList<String>();
        try{
            JSONArray jsonarreglo=new JSONArray(s);

            for(int i=0;i<jsonarreglo.length();i++){
                Estaciones miempresa=new Estaciones();
                miempresa.setEmpresa(jsonarreglo.getJSONObject(i).getString("COD_TEM")+" - "+jsonarreglo.getJSONObject(i).getString("DESCRIPCION"));


                listaempresa.add(miempresa);

            }
            for(int i=0;i<listaempresa.size();i++){
                listaempresastring.add(listaempresa.get(i).getEmpresa());
            }
            final ArrayAdapter<String>adapter=new ArrayAdapter(getContext(),android.R.layout.simple_list_item_activated_1,listaempresastring);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    txttemporada.setText(adapter.getItem(position));



                    // Dismiss dialog
                    dialog.dismiss();
                }
            });




        }catch (Exception e){
            Toast.makeText(getContext(),"ERROR CARGANDO CLAVE USUARIO"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }








    private void cargarcuartel(String cod_emp, String cod_zona) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando cuarteles...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();

        String url=Global.url+"wscuartel.php?IdEmpresa="+cod_emp+"&&IdZona="+cod_zona;


        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progreso.hide();
                if(statusCode==200){

                    cuartel(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"ERROR CARGANDO CUARTELES SERVIDOR",Toast.LENGTH_SHORT).show();
            }
        });

    }



    private void cuartel(String s) {

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        dialog.getWindow().setLayout(800,800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText=dialog.findViewById(R.id.edit_text);
        ListView listView=dialog.findViewById(R.id.list_view);

        // show dialog
        dialog.show();



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

            for(int i=0;i<listazonas.size();i++){
                listazonastring.add(listazonas.get(i).getEmpresa()+" - "+listazonas.get(i).getNombreempresa());
            }

            final ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice
                    ,listazonastring);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    txtcuartel.setText(adapter.getItem(position));

                    String[] nombre=Global.separatexto(txtcuartel.getText().toString()," - ");
                    txtnombre_estacion.setText(nombre[1]);

                    // Dismiss dialog
                    dialog.dismiss();
                    btnaceptar.setEnabled(true);
                }
            });


        }catch (Exception e){

        }
    }

    private void cargarzonas(String cod_emp) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando zonas...");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();


        String url= Global.url+"wszona.php?IdEmpresa="+cod_emp;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progreso.hide();
                if(statusCode==200){
                    zonas(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"ERROR EN SERVIDOR CARGANDO ZONAS ",Toast.LENGTH_LONG).show();
            }
        });



    }



    private void zonas(String s) {

        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        dialog.getWindow().setLayout(800,800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText=dialog.findViewById(R.id.edit_text);
        ListView listView=dialog.findViewById(R.id.list_view);

        // show dialog
        dialog.show();


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

            for(int i=0;i<listazonas.size();i++){
                listazonastring.add(listazonas.get(i).getEmpresa()+" - "+listazonas.get(i).getNombreempresa());
            }

           final ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice
                    ,listazonastring);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    txtzona.setText(adapter.getItem(position));



                    // Dismiss dialog
                    dialog.dismiss();
                }
            });


        }catch (Exception e){

        }





    }

    private void cargarbuses(String cod_emp,String cod_tem,String cod_trp) {
        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando buses..");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();


        String url=Global.url+"wsbuses.php?IDEMPRESA="+cod_emp+"&COD_TRP="+cod_trp;

        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progreso.hide();
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
        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        dialog.getWindow().setLayout(800,800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText=dialog.findViewById(R.id.edit_text);
        ListView listView=dialog.findViewById(R.id.list_view);

        // show dialog
        dialog.show();



        ArrayList<Estaciones>listabuses=new ArrayList<Estaciones>();
        ArrayList<String>listabusesestring=new ArrayList<String>();

        try{
            JSONArray json=new JSONArray(s);

            for(int i=0;i<json.length();i++){
                Estaciones empresa=new Estaciones();
                empresa.setEmpresa(json.getJSONObject(i).getString("COD_BUS"));
                empresa.setNombreempresa(json.getJSONObject(i).getString("PATENTE"));

                listabuses.add(empresa);
            }

            for(int i=0;i<listabuses.size();i++){
                listabusesestring.add(listabuses.get(i).getEmpresa()+" - "+listabuses.get(i).getNombreempresa());
            }

           final ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice
                    ,listabusesestring);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    txtbus.setText(adapter.getItem(position));

                    String[] nombre=Global.separatexto(txtbus.getText().toString()," - ");
                    txtnombre_estacion.setText(nombre[1]);

                    // Dismiss dialog
                    dialog.dismiss();
                    btnaceptar.setEnabled(true);
                }
            });


        }catch (Exception e){

        }



    }

    private void cargarempresas() {
        String url;
        url=Global.url+"wsempresa.php?";
        muestraempresa(url);

    }

    private void muestraempresa(String url) {

        progreso = new ProgressDialog(getContext());
        progreso.setMessage("Cargando empresas..");
        progreso.setCancelable(false);
        progreso.setCanceledOnTouchOutside(false);
        progreso.show();
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                progreso.hide();
                if(statusCode==200){
                    empresas(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progreso.hide();
                Toast.makeText(getContext(),"ERRROR EN CONSULTA",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void empresas(String s) {


        dialog=new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_searchable_spinner);

        dialog.getWindow().setLayout(800,800);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        EditText editText=dialog.findViewById(R.id.edit_text);
        ListView listView=dialog.findViewById(R.id.list_view);

        // show dialog
        dialog.show();

        try{
            ArrayList<Estaciones>listaempresa=new ArrayList<Estaciones>();
            ArrayList<String>listaempresastring=new ArrayList<String>();
            JSONArray json=new JSONArray(s);
            for(int i=0;i<json.length();i++){
                Estaciones empresa=new Estaciones();
                empresa.setEmpresa(json.getJSONObject(i).getString("IdEmpresa"));
                empresa.setNombreempresa(json.getJSONObject(i).getString("Nombre"));
                listaempresa.add(empresa);
            }

            for(int i=0;i<listaempresa.size();i++){
                listaempresastring.add(listaempresa.get(i).getEmpresa()+" - "+listaempresa.get(i).getNombreempresa());
            }
            final ArrayAdapter<String> adapter= new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_single_choice,listaempresastring);
            listView.setAdapter(adapter);

            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // when item selected from list
                    // set selected item on textView
                    txtempresa.setText(adapter.getItem(position));



                    // Dismiss dialog
                    dialog.dismiss();
                }
            });

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




    private void instalacion(String s) {

        try{
            //JSONArray jsonarreglo = new JSONArray(s);

            JSONObject objeto=new JSONObject(s);

                //String mensaje=jsonarreglo.getJSONObject(j).getString("message");

                String mensaje=objeto.getString("message");
                if(mensaje.equals("ANTES")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("YA REGISTRADO")
                            .setMessage("ESTACION YA CUENTA CON UNA INTALACION, DEBE RETIRAR LA INSTACION PARA CONTINUAR")
                            .setPositiveButton("ACEPTAR ", null);
                    builder.create().show();
                }else{

                    JSONObject datos=objeto.getJSONObject("data");
                    String id=datos.getString("ID");
                    String nombre=datos.getString("NOMBRE_ESTACION");
                    String tipoestacion=datos.getString("TIPO_ESTACION");
                    AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
                    SQLiteDatabase db=cn.getWritableDatabase();
                    db.isOpen();
                    String llenartabla="INSERT INTO ESTACION(ID,TIPO,ESTACION)VALUES('"+id+"','"+nombre+"','"+tipoestacion+"')";

                    db.execSQL(llenartabla);

                    db.close();
                    mostrarnombreestacion();


                }






        }catch (Exception e){
            Toast.makeText(getContext(),"error en"+e.getMessage(),Toast.LENGTH_SHORT).show();
            System.out.println("error en respuesta de instacion "+e.getMessage());
        }

    }


    private void nuevo() {

        txtnombre_estacion.setText("");
        txtid.setText("");
        txtestacionguardada.setText("");
        txttipoestacion.setText("");
        txtempresa.setText("");
        txttemporada.setText("");
        txtcuartel.setText("");
        txttransportista.setText("");
        txtbus.setText("");
        txtzona.setText("");
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
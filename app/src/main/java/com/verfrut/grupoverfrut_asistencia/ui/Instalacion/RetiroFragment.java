package com.verfrut.grupoverfrut_asistencia.ui.Instalacion;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.verfrut.grupoverfrut_asistencia.Adaptador.ListadoEstacionesAdapter;
import com.verfrut.grupoverfrut_asistencia.Entidades.Estaciones;
import com.verfrut.grupoverfrut_asistencia.Global;
import com.verfrut.grupoverfrut_asistencia.R;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RetiroFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RetiroFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextInputEditText txtnombreestacion;
    Button btnbuscar;
    RecyclerView rvlistado;
    String nombre;
    private AsyncHttpClient cliente;
    public RetiroFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RetiroFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RetiroFragment newInstance(String param1, String param2) {
        RetiroFragment fragment = new RetiroFragment();
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
        View vista=inflater.inflate(R.layout.fragment_retiro, container, false);
        txtnombreestacion=vista.findViewById(R.id.txtnombreestacion);
        btnbuscar=vista.findViewById(R.id.btnbuscar);
        rvlistado=vista.findViewById(R.id.rvlistado);
        rvlistado.setLayoutManager(new LinearLayoutManager(getContext()));
        rvlistado.setHasFixedSize(true);
        cliente=new AsyncHttpClient();



        try{
            nombre="TODOS";
            cargarlistado(nombre);
        }catch (Exception e){

        }


        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!txtnombreestacion.getText().toString().isEmpty()){
                  nombre=txtnombreestacion.getText().toString().replaceAll(" ","%20");
                  cargarlistado(nombre);
                }else{
                    nombre="TODOS";
                    cargarlistado(nombre);
                }
            }
        });


        return vista;
    }

    private void cargarlistado(String nombre) {

        String url= Global.url+"wslistadoestaciones.php?NOMBREESTACION="+nombre;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if(statusCode==200){
                    listadoestaciones(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(),"ERROR EN SERVIDOR , LISTADO DE ESTACIONES ACTIVAS",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void listadoestaciones(String s) {
        ArrayList<Estaciones>listaestaciones=new ArrayList<Estaciones>();
        ArrayList<String>listaestacionestring=new ArrayList<String>();
        try{
            JSONArray jsonArray=new JSONArray(s);
            for(int i=0;i<jsonArray.length();i++){
                Estaciones miestacion=new Estaciones();
                miestacion.setIdestacion(jsonArray.getJSONObject(i).getString("Id"));
                miestacion.setNombreestacion(jsonArray.getJSONObject(i).getString("NombreEstacion"));
                miestacion.setFechainstalacion(jsonArray.getJSONObject(i).getString("FechaInstalacion"));
                miestacion.setFecharetiro(jsonArray.getJSONObject(i).getString("FechaRetiro"));
                listaestaciones.add(miestacion);
            }
            ListadoEstacionesAdapter adapter=new ListadoEstacionesAdapter(listaestaciones);
            rvlistado.setAdapter(adapter);



        }catch (Exception e){
            System.out.println("error en listado de estaciones"+e.getMessage());
            Toast.makeText(getContext(), "Listado de estaciones" +e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
package com.verfrut.grupoverfrut_asistencia.ui.Consultas;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.verfrut.grupoverfrut_asistencia.Adaptador.ConsultaMarcacionesAdapter;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.verfrut.grupoverfrut_asistencia.Entidades.Marcaciones;
import com.verfrut.grupoverfrut_asistencia.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MarcacionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MarcacionFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btnbuscar,btneliminar;
    RadioButton rbndni,rbnid;
    EditText txtcampo;
    RecyclerView rvlista;

    public MarcacionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarcacionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarcacionFragment newInstance(String param1, String param2) {
        MarcacionFragment fragment = new MarcacionFragment();
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
        View vista=inflater.inflate(R.layout.fragment_marcacion, container, false);
        btnbuscar=vista.findViewById(R.id.btnbuscar);
        btneliminar=vista.findViewById(R.id.btneliminar);
        rbndni=vista.findViewById(R.id.rbndni);
        rbnid=vista.findViewById(R.id.rbnid);
        txtcampo=vista.findViewById(R.id.txtcampo);
        rvlista=vista.findViewById(R.id.rvlista);

        rvlista.setLayoutManager(new LinearLayoutManager(getContext()));
        rvlista.setHasFixedSize(true);

        btnbuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(txtcampo.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"DEBE INGRESAR UN ID O DNI A BUSCAR",Toast.LENGTH_SHORT).show();
                }else{

                    String campo=txtcampo.getText().toString();
                    consultarregistro(campo);
                }
            }
        });



        return vista;
    }

    private void consultarregistro(String campo) {


        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        ArrayList<Marcaciones>listamarcaciones=new ArrayList<Marcaciones>();
        if(rbndni.isChecked()){
            String consulta=" SELECT DNI,FECHA,HORA,IDMARCACION,SW_ENVIADO FROM  MARCACIONES WHERE DNI='"+campo+"' GROUP BY IDMARCACION ORDER BY FECHA,HORA ASC";
            Cursor cr=db.rawQuery(consulta,null);
            if (cr.moveToFirst()){
                while(cr.isAfterLast()==false){
                    Marcaciones mismarcaciones=new Marcaciones();
                    mismarcaciones.setDni(cr.getString(0));
                    mismarcaciones.setFecha(cr.getString(1));
                    mismarcaciones.setHora(cr.getString(2));
                    mismarcaciones.setId(cr.getString(3));
                    mismarcaciones.setEnviado(cr.getString(4));
                    listamarcaciones.add(mismarcaciones);
                    cr.moveToNext();
                }
                ConsultaMarcacionesAdapter adapter=new ConsultaMarcacionesAdapter(listamarcaciones);
                rvlista.setAdapter(adapter);

            }else{
                Toast.makeText(getContext(),"NO SE ENCONTRARON DATOS",Toast.LENGTH_SHORT).show();
            }


        }else{
            String consulta=" SELECT DNI,FECHA,HORA,IDMARCACION,SW_ENVIADO FROM  MARCACIONES WHERE IDMARCACION='"+campo+"' GROUP BY IDMARCACION ORDER BY FECHA,HORA ASC";

            Cursor cr=db.rawQuery(consulta,null);
            if (cr.moveToFirst()){
                while(cr.isAfterLast()==false){
                    Marcaciones mismarcaciones=new Marcaciones();
                    mismarcaciones.setDni(cr.getString(0));
                    mismarcaciones.setFecha(cr.getString(1));
                    mismarcaciones.setHora(cr.getString(2));
                    mismarcaciones.setId(cr.getString(3));
                    mismarcaciones.setEnviado(cr.getString(4));
                    listamarcaciones.add(mismarcaciones);
                    cr.moveToNext();
                }
                ConsultaMarcacionesAdapter adapter=new ConsultaMarcacionesAdapter(listamarcaciones);
                rvlista.setAdapter(adapter);

            }else{
                Toast.makeText(getContext(),"NO SE ENCONTRARON DATOS",Toast.LENGTH_SHORT).show();
            }

        }


    }
}
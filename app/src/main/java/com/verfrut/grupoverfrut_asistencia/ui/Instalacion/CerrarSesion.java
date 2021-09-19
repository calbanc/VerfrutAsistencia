package com.verfrut.grupoverfrut_asistencia.ui.Instalacion;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.verfrut.grupoverfrut_asistencia.ADMINISTRADOR;
import com.verfrut.grupoverfrut_asistencia.MainActivity;
import com.verfrut.grupoverfrut_asistencia.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CerrarSesion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CerrarSesion extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btncerrarsesion;
    public static final String prefencia="prefencia";
    public CerrarSesion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CerrarSesion.
     */
    // TODO: Rename and change types and number of parameters
    public static CerrarSesion newInstance(String param1, String param2) {
        CerrarSesion fragment = new CerrarSesion();
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
        View vista=inflater.inflate(R.layout.fragment_cerrar_sesion, container, false);
        btncerrarsesion=vista.findViewById(R.id.btncerrarsesion);

        btncerrarsesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("CERRAR SESION")
                        .setMessage("ESTA SEGURO DE CERRAR SESION")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("Salir", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                final AlertDialog dialogBuilder=new AlertDialog.Builder(getContext()).create();
                                LayoutInflater inflater=getActivity().getLayoutInflater();
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

                                            SharedPreferences preferences=getActivity().getSharedPreferences(prefencia, Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = preferences.edit();
                                            editor.clear().apply();
                                            Intent miintent=new Intent(getContext(), MainActivity.class);
                                            startActivity(miintent);
                                        }else{
                                            Toast.makeText(getContext(),"CLAVE DE ADMINISTRADOR INCORRECTA",Toast.LENGTH_SHORT).show();
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
        return vista;
    }
}
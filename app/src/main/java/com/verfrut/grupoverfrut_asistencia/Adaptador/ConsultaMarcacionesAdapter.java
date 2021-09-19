package com.verfrut.grupoverfrut_asistencia.Adaptador;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.verfrut.grupoverfrut_asistencia.Entidades.Marcaciones;
import com.verfrut.grupoverfrut_asistencia.R;

import java.util.ArrayList;

public class ConsultaMarcacionesAdapter extends RecyclerView.Adapter<ConsultaMarcacionesAdapter.ConsultaMarcacionesHolder> {
    ArrayList<Marcaciones>listamarcaciones;

    public ConsultaMarcacionesAdapter(ArrayList<Marcaciones> listamarcaciones) {
        this.listamarcaciones = listamarcaciones;
    }

    @NonNull
    @Override
    public ConsultaMarcacionesAdapter.ConsultaMarcacionesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.vistaconsultamarcacion, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ConsultaMarcacionesHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaMarcacionesAdapter.ConsultaMarcacionesHolder holder, int position) {
        holder.txtdni.setText(listamarcaciones.get(position).getDni());
        holder.txthora.setText(listamarcaciones.get(position).getHora());
        holder.txtid.setText(listamarcaciones.get(position).getId());
        holder.txtenviado.setText(listamarcaciones.get(position).getEnviado());
        holder.txtfecha.setText(listamarcaciones.get(position).getFecha());
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return listamarcaciones.size();
    }

    public class ConsultaMarcacionesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txtdni,txthora,txtid,txtenviado,txtfecha;
        Button btneliminar;
        Context context;
        public ConsultaMarcacionesHolder(@NonNull View itemView) {
            super(itemView);
            context=itemView.getContext();
            txtdni=itemView.findViewById(R.id.txtdni);
            txthora=itemView.findViewById(R.id.txthora);
            txtid=itemView.findViewById(R.id.txtid);
            txtenviado=itemView.findViewById(R.id.txtenviado);
            txtfecha=itemView.findViewById(R.id.txtfecha);
            btneliminar=itemView.findViewById(R.id.btneliminar);
        }
        void setOnClickListener(){
            btneliminar.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
            btneliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AsistenciaHelper cn=new AsistenciaHelper(context,"RRHH",null,1);
                    SQLiteDatabase db=cn.getWritableDatabase();
                    String id=txtid.getText().toString();
                    String elimina="DELETE FROM MARCACIONES WHERE IDMARCACIONES='"+id+"'";
                    db.execSQL(elimina);
                    for(int  i=0;i<listamarcaciones.size();i++){
                        if(listamarcaciones.get(i).getId().equals(id) ){
                            listamarcaciones.remove(i);
                        }
                    }
                    notifyDataSetChanged();
                }
            });
        }
    }
}

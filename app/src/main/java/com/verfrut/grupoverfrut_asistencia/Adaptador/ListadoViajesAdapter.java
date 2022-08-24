package com.verfrut.grupoverfrut_asistencia.Adaptador;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.verfrut.grupoverfrut_asistencia.ASISTENCIA;
import com.verfrut.grupoverfrut_asistencia.CierreViaje;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.verfrut.grupoverfrut_asistencia.Entidades.Viajes;
import com.verfrut.grupoverfrut_asistencia.PrincipalViajes;
import com.verfrut.grupoverfrut_asistencia.R;

import java.util.ArrayList;

public class ListadoViajesAdapter  extends RecyclerView.Adapter<ListadoViajesAdapter.ListadoViajesHolder>{
    ArrayList<Viajes>listaviajes=new ArrayList<Viajes>();

    public ListadoViajesAdapter(ArrayList<Viajes> listaviajes) {
        this.listaviajes = listaviajes;
    }

    @NonNull
    @Override
    public ListadoViajesAdapter.ListadoViajesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.vistaviajes, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ListadoViajesAdapter.ListadoViajesHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListadoViajesAdapter.ListadoViajesHolder holder, int position) {
        holder.txtdniconductor.setText("DNI CONDUCTOR: "+listaviajes.get(position).getDniconductor());
        holder.txtidviaje.setText(listaviajes.get(position).getIdviaje());
        holder.txtfechainicioentrada.setText(listaviajes.get(position).getFechaincioentrada());
        holder.txthorainicioentrada.setText(listaviajes.get(position).getHorainicioentrada());
        holder.txtfechaterminoentrada.setText(listaviajes.get(position).getFechaterminoentrada());
        holder.txthoraterminoentrada.setText(listaviajes.get(position).getHoraterminoentrada());

        holder.txtfechainiciosalida.setText(listaviajes.get(position).getFecharetornoentrada());
        holder.txthorainiciosalida.setText(listaviajes.get(position).getHoraretornoentrada());
        holder.txtfechaterminosalida.setText(listaviajes.get(position).getFecharetornosalida());
        holder.txthoraterminosalid.setText(listaviajes.get(position).getHoraretornosalida());
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return listaviajes.size();
    }

    public class ListadoViajesHolder extends RecyclerView.ViewHolder  implements View.OnClickListener  {
        TextView txtdniconductor,txtfechainicioentrada,txthorainicioentrada,txtfechaterminoentrada,txthoraterminoentrada,txtfechaterminosalida,txthoraterminosalid,txtidviaje;
        TextView txtfechainiciosalida,txthorainiciosalida;
        Button btnregistrarterminoinicio,btnregistrrarterminosalida;
        Context context;
        public ListadoViajesHolder(@NonNull View itemView) {
            super(itemView);
            txtdniconductor=itemView.findViewById(R.id.txtdniconductor);
            txtfechainicioentrada=itemView.findViewById(R.id.txtfechainicioentrada);
            txthorainicioentrada=itemView.findViewById(R.id.txthorainicioentrada);

            txtfechaterminoentrada=itemView.findViewById(R.id.txtfechaterminoentrada);
            txthoraterminoentrada=itemView.findViewById(R.id.txthoraterminoentrada);
            txtfechaterminosalida=itemView.findViewById(R.id.txtfechaterminosalida);
            txthoraterminosalid=itemView.findViewById(R.id.txthoraterminosalid);
            txtfechainiciosalida=itemView.findViewById(R.id.txtfechainiciosalida);
            txthorainiciosalida=itemView.findViewById(R.id.txthorainiciosalida);
            btnregistrarterminoinicio=itemView.findViewById(R.id.btnregistrarterminoinicio);
            btnregistrrarterminosalida=itemView.findViewById(R.id.btnregistrrarterminosalida);
            txtidviaje=itemView.findViewById(R.id.txtidviaje);
            context=itemView.getContext();



        }
        void setOnClickListener(){
            btnregistrarterminoinicio.setOnClickListener(this::onClick);
        }


        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnregistrarterminoinicio:
                    String idviaje=txtidviaje.getText().toString();
                    Intent midintent = new Intent(context, CierreViaje.class);
                    Bundle mibundle = new Bundle();
                    mibundle.putString("idviaje", idviaje);
                    midintent.putExtras(mibundle);

                    context.startActivity(midintent);




                    break;
            }
        }
    }
}

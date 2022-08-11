package com.verfrut.grupoverfrut_asistencia.Adaptador;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.tv.TvInputService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.verfrut.grupoverfrut_asistencia.Entidades.Estaciones;
import com.verfrut.grupoverfrut_asistencia.Entidades.Marcaciones;
import com.verfrut.grupoverfrut_asistencia.Global;
import com.verfrut.grupoverfrut_asistencia.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class ListadoEstacionesAdapter  extends RecyclerView.Adapter<ListadoEstacionesAdapter.ListadoEstacionesHolder>{

    ArrayList<Estaciones>listadomarcaciones=new ArrayList<Estaciones>();
    private AsyncHttpClient cliente;

    public ListadoEstacionesAdapter(ArrayList<Estaciones> listadomarcaciones) {
        this.listadomarcaciones = listadomarcaciones;
    }

    @NonNull
    @Override
    public ListadoEstacionesAdapter.ListadoEstacionesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.vistalistadoestaciones, parent, false);
        RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        vista.setLayoutParams(layoutParams);
        return new ListadoEstacionesAdapter.ListadoEstacionesHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ListadoEstacionesAdapter.ListadoEstacionesHolder holder, int position) {
        holder.txtid.setText(listadomarcaciones.get(position).getIdestacion());
        holder.txtnombreestacion.setText(listadomarcaciones.get(position).getNombreestacion());
        holder.txtfechainstalacion.setText(listadomarcaciones.get(position).getFechainstalacion());
        holder.txtfecharetiro.setText(listadomarcaciones.get(position).getFecharetiro());
        holder.setOnClickListener();
    }

    @Override
    public int getItemCount() {
        return listadomarcaciones.size();
    }

    public class ListadoEstacionesHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
        TextView txtid,txtnombreestacion,txtfechainstalacion,txtfecharetiro;
        Button btnretirar;
        Context context;
        public ListadoEstacionesHolder(@NonNull View itemView) {
            super(itemView);
            txtid=itemView.findViewById(R.id.txtid);
            txtnombreestacion=itemView.findViewById(R.id.txtnombreestacion);
            txtfechainstalacion=itemView.findViewById(R.id.txtfechainstalacion);
            txtfecharetiro=itemView.findViewById(R.id.txtfecharetiro);
            btnretirar=itemView.findViewById(R.id.btnretirar);
            context=itemView.getContext();
            cliente=new AsyncHttpClient();
        }

        @Override
        public void onClick(View v) {

            if(txtfecharetiro.getText().toString().equals("")){

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("RETIRAR INSTALACION")
                        .setMessage("PORFAVOR CONFIRMAR RETIRO DE INSTALACION")
                        .setPositiveButton("RETIRAR ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String id=txtid.getText().toString();
                                Date d = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                String fecha=dateFormat.format(d).replaceAll(" ","%20");
                                String url= Global.url+"swretiraequipo.php?ID="+id+"&&FECHA_RETIRO="+fecha;

                               cliente.post(url, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        if (statusCode == 200) {
                                            Toast.makeText(context,"RETIRADO CORRECTAMENTE ACTUALIZAR LISTA ",Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                        Toast.makeText(context,"SIN CONEXION",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        });
                builder.create().show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("RETIRAR INSTALACION")
                        .setMessage("EQUIPO YA RETIRADO")
                        .setPositiveButton("ACEPTAR ",null);
                builder.create().show();
            }



        }

         void setOnClickListener() {
            btnretirar.setOnClickListener(this);
        }
    }
}

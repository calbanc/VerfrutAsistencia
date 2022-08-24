package com.verfrut.grupoverfrut_asistencia.ui.Reset;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.verfrut.grupoverfrut_asistencia.Entidades.AsistenciaHelper;
import com.verfrut.grupoverfrut_asistencia.Global;
import com.verfrut.grupoverfrut_asistencia.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResetFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btnresetmarcaciones,btnresetposiciones,btnexportabd,btnexportarxlsmarcaciones,btnexportarxlsposiciones,btneliminar;
    Session session;
    public ResetFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResetFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResetFragment newInstance(String param1, String param2) {
        ResetFragment fragment = new ResetFragment();
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
        View vista=inflater.inflate(R.layout.fragment_reset, container, false);
        btnresetmarcaciones=vista.findViewById(R.id.btnresetmarcaciones);
        btnresetposiciones=vista.findViewById(R.id.btnresetposiciones);
        btnexportabd=vista.findViewById(R.id.btnexportabd);
        btnexportarxlsmarcaciones=vista.findViewById(R.id.btnexportarxlsmarcaciones);
        btnexportarxlsposiciones=vista.findViewById(R.id.btnexportarxlsposiciones);
        btneliminar=vista.findViewById(R.id.btneliminar);



        btnresetposiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("RESET TABLA POSICIONES")
                        .setMessage("ESTA SEGURO DE LIMPIAR TABLA POSICIONES, SE BORRARN TODOS LOS DATOS DE ESTA TABLA")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                resetposiciones();
                            }
                        });
                builder.create().show();
            }
        });
        btnresetmarcaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("RESET TABLA MARCACIONES")
                        .setMessage("ESTA SEGURO DE LIMPIAR TABLA MARCACIONES, SE BORRARN TODOS LOS DATOS DE ESTA TABLA")
                        .setNegativeButton("NO", null)
                        .setPositiveButton("RESET", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                resetmarcaciones();
                            }
                        });
                builder.create().show();
            }
        });
        btnexportarxlsmarcaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                guardarexcelmarcaciones();
            }
        });
        btnexportabd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("EXPORTAR BD");
                backupdDatabase();
            }
        });
        btneliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().deleteDatabase("AsistenciaHelper");
            }
        });

        return vista;
    }

    private void backupdDatabase() {
        String DatabaseName = "RRHH";
        File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
        String currentDBPath = "/data/"+ "com.verfrut.grupoverfrut_asistencia" +"/databases/"+DatabaseName ;
        String backupDBPath = "RRHH";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);

        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();

            final AlertDialog dialogBuilder=new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View dialogview=inflater.inflate(R.layout.dialogcorreo,null);

            final TextInputEditText txtcopiacc =dialogview.findViewById(R.id.txtcopiacc);
            final TextInputEditText txtcorreo=dialogview.findViewById(R.id.txtcorreo);

            Button btnenviarcorreo=dialogview.findViewById(R.id.btnenviarcorreo);
            Button btndiagcancelar=dialogview.findViewById(R.id.btndiagcancelar);



            btndiagcancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });

            btnenviarcorreo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txtcorreo.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"DEBE INGRESAR UN CORREO ELECTRONICO",Toast.LENGTH_SHORT).show();
                    }else{
                        String correo=txtcorreo.getText().toString();
                        String copia=txtcopiacc.getText().toString();
                        enviarcorreobd(correo,copia);
                        //EnviarEmailBD(correo,copia);
                        dialogBuilder.dismiss();
                    }
                }
            });

            dialogBuilder.setView(dialogview);
            dialogBuilder.show();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("error exportando bd"+e.getMessage());
        }



    }
    private void enviarcorreobd(String correo,String copia) {



        Global.EmailEnv="app@verfrut.cl";
        Global.PassEnv="Aplicaciones.8462";
        Global.CorreoEnvio=correo;
        Global.CorreoEnvioCC=copia;


        String xMensaje="", xAsunto="", xHora, databaseName, CorreoEnvio, CorreoEnvioCC;

        CorreoEnvio=Global.CorreoEnvio;
        CorreoEnvioCC=Global.CorreoEnvioCC;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        xHora=sdf.format(new Date());
        xMensaje="SE REALIZO EL ENVIO DE BASE DE DATOS DE CAMPO  DESDE EL APP GRUPO VERFRUT \n"
                +"Usuario: "+ Global.usuario + "\n "
                +"Hora de envio: "+ xHora;
        xAsunto="DATOS CAMPO "+Global.usuario;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();

        properties.put("mail.smtp.host", "mail.verfrut.cl");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.ssl.trust","mail.verfrut.cl");
        properties.put("mail.smtp.auth","true");
        //cargar correos y usuarios


        try {
            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Global.EmailEnv, Global.PassEnv);
                }
            });
            if (session != null) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Global.EmailEnv));
                message.setSubject(xAsunto);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(CorreoEnvio));
                message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(CorreoEnvioCC));
                message.setContent("Datos enviados desde el app ","text/html; charset=utf-8");


                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(xMensaje);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                String filename = (Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+ "/RRHH");

                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);

                Transport transport = session.getTransport("smtp");

                transport.connect("mail.verfrut.cl", 587, Global.EmailEnv, Global.PassEnv);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();
                Toast.makeText(getContext(),"BASE DE DATOS EXPORTADA",Toast.LENGTH_LONG).show();

            }

        }catch (Exception e){
            Toast.makeText(getContext(),"ERROR"+e.getMessage(),Toast.LENGTH_LONG).show();

            //Limpiar(true);
        }

    }

    private void guardarexcelmarcaciones() {
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        File sd = Environment.getExternalStorageDirectory();
        String csvFile = "/Marcaciones.xls";
        File directory = new File(String.valueOf(Environment.getExternalStorageDirectory())+ "/VerfrutBD");
        if (!directory.isDirectory()) {
            directory.mkdirs();
        }

        try{
            File file = new File(directory, csvFile);
            WorkbookSettings wbSettings = new WorkbookSettings();
            wbSettings.setLocale(new Locale("en", "EN"));
            WritableWorkbook workbook;
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("Listado", 0);
            sheet.addCell(new Label(0, 0, "DNI"));
            sheet.addCell(new Label(1, 0, "FECHA"));
            sheet.addCell(new Label(2, 0, "HORA"));
            sheet.addCell(new Label(3, 0, "ESTACION"));
            sheet.addCell(new Label(4, 0, "LATITUD"));
            sheet.addCell(new Label(5, 0, "LONGITUD"));
            sheet.addCell(new Label(6, 0, "IDMARCACION"));
            sheet.addCell(new Label(7, 0, "VERSIONAPP"));
            sheet.addCell(new Label(8, 0, "SW_ENVIADO"));

            Cursor cursor=db.rawQuery("SELECT * FROM MARCACIONES",null);

            if(cursor.moveToFirst()){
                do{
                    int i=(cursor.getPosition()+1)+1;
                    sheet.addCell(new Label(0,i,cursor.getString(0)));
                    sheet.addCell(new Label(1,i,cursor.getString(1)));
                    sheet.addCell(new Label(2,i,cursor.getString(2)));
                    sheet.addCell(new Label(3,i,cursor.getString(3)));
                    sheet.addCell(new Label(4,i,cursor.getString(4)));
                    sheet.addCell(new Label(5,i,cursor.getString(5)));
                    sheet.addCell(new Label(6,i,cursor.getString(6)));
                    sheet.addCell(new Label(7,i,cursor.getString(7)));
                    sheet.addCell(new Label(8,i,cursor.getString(8)));
                  //  sheet.addCell(new Label(9,i,cursor.getString(9)));

                }while (cursor.moveToNext());
            }

            cn.close();
            db.close();
            workbook.write();
            workbook.close();

            Toast.makeText(getContext(),"EXCEL CREADO CORRECTAMENTE",Toast.LENGTH_SHORT).show();

            final AlertDialog dialogBuilder=new AlertDialog.Builder(getActivity()).create();
            LayoutInflater inflater=getActivity().getLayoutInflater();
            View dialogview=inflater.inflate(R.layout.dialogcorreo,null);

            final TextInputEditText txtcopiacc =dialogview.findViewById(R.id.txtcopiacc);
            final TextInputEditText txtcorreo=dialogview.findViewById(R.id.txtcorreo);

            Button btnenviarcorreo=dialogview.findViewById(R.id.btnenviarcorreo);
            Button btndiagcancelar=dialogview.findViewById(R.id.btndiagcancelar);



            btndiagcancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogBuilder.dismiss();
                }
            });

            btnenviarcorreo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(txtcorreo.getText().toString().isEmpty()){
                        Toast.makeText(getContext(),"DEBE INGRESAR UN CORREO ELECTRONICO",Toast.LENGTH_SHORT).show();
                    }else{
                        String correo=txtcorreo.getText().toString();
                        String copia=txtcopiacc.getText().toString();
                        String archivo="/VerfrutBD/Marcaciones.xls";
                        enviarcorreo(correo,copia,archivo);
                        dialogBuilder.dismiss();
                    }
                }
            });

            dialogBuilder.setView(dialogview);
            dialogBuilder.show();


        }catch (Exception e){
            Toast.makeText(getContext(),"ERROR CREANDO EXCEL"+e.getMessage(),Toast.LENGTH_LONG).show();
            System.out.println("ERROR CREANDO EXCEL "+e.getMessage());

        }

    }

    private void enviarcorreo(String correo, String copia, String archivo) {
        Global.EmailEnv="app@verfrut.cl";
        Global.PassEnv="Aplicaciones.8462";

        Global.CorreoEnvio=correo;
        Global.CorreoEnvioCC=copia+",jalban@verfrut.pe";

        String xMensaje="", xAsunto="", xHora, databaseName, CorreoEnvio, CorreoEnvioCC;

        CorreoEnvio=Global.CorreoEnvio;
        CorreoEnvioCC=Global.CorreoEnvioCC;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        xHora=sdf.format(new Date());
        xMensaje="Se realizo el envio de datos de Aplicacion Marcaciones  \n"
                +"Hora de envio:     "+ xHora;
        xAsunto="Registro datos aplicacion de marcaciones ";
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "mail.verfrut.cl");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.ssl.trust","mail.verfrut.cl");
        properties.put("mail.smtp.auth","true");
        //cargar correos y usuarios


        try {


            session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(Global.EmailEnv, Global.PassEnv);
                }
            });
            if (session != null) {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(Global.EmailEnv));
                message.setSubject(xAsunto);
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(CorreoEnvio));
                message.setRecipients(Message.RecipientType.CC,InternetAddress.parse(CorreoEnvioCC));
                message.setContent("Datos enviados desde el app ","text/html; charset=utf-8");


                BodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setText(xMensaje);
                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                messageBodyPart = new MimeBodyPart();
                String filename = (Environment.getExternalStorageDirectory()+ archivo);

                DataSource source = new FileDataSource(filename);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
                message.setContent(multipart);

                Transport transport = session.getTransport("smtp");

                transport.connect("mail.verfrut.cl", 587, Global.EmailEnv, Global.PassEnv);
                transport.sendMessage(message, message.getAllRecipients());
                transport.close();

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("ENVIO DE REPORTE")
                        .setMessage("REPORTE ENVIADO CORRECTAMENTE")
                        .setPositiveButton("ACEPTAR ", null);
                builder.create().show();

            }

        }catch (Exception e){
            Toast.makeText(getContext(),"ERROR ENVIANDO EMAIL"+e.getMessage(),Toast.LENGTH_LONG).show();
            //Limpiar(true);
        }
    }

    private void exportarbd() {




        try {
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();


            if (sd.canWrite()) {
                String  currentDBPath= "//data//" + "com.verfrut.grupoverfrut_asistencia"
                        + "//databases//" + "RRHH";
                String backupDBPath  = "/COPIA/RRHH";

                File currentDB = new File(data, currentDBPath);

                File backupDB = new File(sd,backupDBPath );

                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();







            }else{
                System.out.println("sd.no write");
            }

        } catch (Exception e) {

            Toast.makeText(getContext(),"ERROR EN BD"+ e.toString(), Toast.LENGTH_LONG).show();
            //progreso.hide();

        }




        final AlertDialog dialogBuilder=new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View dialogview=inflater.inflate(R.layout.dialogcorreo,null);

        final TextInputEditText txtcopiacc =dialogview.findViewById(R.id.txtcopiacc);
        final TextInputEditText txtcorreo=dialogview.findViewById(R.id.txtcorreo);

        Button btnenviarcorreo=dialogview.findViewById(R.id.btnenviarcorreo);
        Button btndiagcancelar=dialogview.findViewById(R.id.btndiagcancelar);



        btndiagcancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });

        btnenviarcorreo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtcorreo.getText().toString().isEmpty()){
                    Toast.makeText(getContext(),"DEBE INGRESAR UN CORREO ELECTRONICO",Toast.LENGTH_SHORT).show();
                }else{
                    String correo=txtcorreo.getText().toString();
                    String copia=txtcopiacc.getText().toString();
                    String archivo="/COPIA/RRHH";
                    enviarcorreo(correo,copia,archivo);
                    dialogBuilder.dismiss();
                }
            }
        });

        dialogBuilder.setView(dialogview);
        dialogBuilder.show();




    }

    private void resetposiciones() {
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="DELETE FROM POSICIONES";
        db.execSQL(consulta);

        Toast.makeText(getContext(),"DATOS ELIMINADOS EXITOSAMENTE",Toast.LENGTH_SHORT).show();
    }

    private void resetmarcaciones() {
        AsistenciaHelper cn=new AsistenciaHelper(getContext(),"RRHH",null,1);
        SQLiteDatabase db=cn.getWritableDatabase();
        String consulta="DELETE FROM MARCACIONES";
        db.execSQL(consulta);

        Toast.makeText(getContext(),"DATOS ELIMINADOS EXITOSAMENTE",Toast.LENGTH_SHORT).show();

    }
}
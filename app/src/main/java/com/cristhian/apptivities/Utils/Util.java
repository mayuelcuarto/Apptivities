package com.cristhian.apptivities.Utils;

import com.cristhian.apptivities.R;

import android.content.Context;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cristhian on 06-01-2018.
 */

public class Util {
    private Context context;

    public Util(Context context){
        this.context = context;
    }

    public Date stringToDate(String fechaString, String format){
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat(format);
        Date fechaDate = null;
        try {
            fechaDate = formatoDelTexto.parse(fechaString);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return fechaDate;
    }

    public String dateToString(Date fecha, String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String fechaString = dateFormat.format(fecha);

        return fechaString;
    }

    public Date todayFiltro(int caso, Date fecha){
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatoFinal = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date fechaDate = null;
        String cadena = formatoDelTexto.format(fecha);
        String cadena2 = "";
        if(caso == 0){
            cadena2 = cadena + " 00:00:01";
        }else if(caso == 1) {
            cadena2 = cadena + " 24:00:00";
        }

        try {
            fechaDate = formatoFinal.parse(cadena2);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return fechaDate;
    }

    public Date stringSimpleToDate(int caso, String fechaString, String format){
        SimpleDateFormat formatoDelTexto = new SimpleDateFormat(format);
        Date fechaDate = null;
        String cadena2 = "";

        if(caso == 0){
            cadena2 = fechaString + " 00:00:01";
        }else if(caso == 1) {
            cadena2 = fechaString + " 24:00:00";
        }

        try {
            fechaDate = formatoDelTexto.parse(cadena2);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return fechaDate;
    }

    public String restarFechas(Date fechaIni, Date fechaFin){
        int diferencia = (int) (fechaIni.getTime() - fechaFin.getTime());
        String respuesta = "";

        int minutos = diferencia/(1000*60);
        int horas = diferencia/(1000*60*60);
        int dias = diferencia/(1000*60*60*24);

        if(minutos/60 >= 1){
            int restominutos = minutos%60;
            respuesta =  String.format("%02d",horas) + ":" + String.format("%02d",restominutos) ;

            if(horas/24 >= 1){
                int restohoras = horas%24;
                respuesta =  dias + " " + context.getString(R.string.util_restarFechas_days) + " " + String.format("%02d",restohoras) + ":" + String.format("%02d",restominutos);
            }

        }else{
            respuesta = minutos + " " + context.getString(R.string.util_restarFechas_minutes);
        }

        return respuesta;
    }
}

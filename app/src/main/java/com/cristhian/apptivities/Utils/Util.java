package com.cristhian.apptivities.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Cristhian on 06-01-2018.
 */

public class Util {
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
}

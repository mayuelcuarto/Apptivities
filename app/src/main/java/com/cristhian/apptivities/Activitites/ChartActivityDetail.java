package com.cristhian.apptivities.Activitites;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.Models.Categoria;
import com.cristhian.apptivities.R;
import com.cristhian.apptivities.Utils.ToastTipos;
import com.cristhian.apptivities.Utils.Util;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChartActivityDetail extends Activity {

    private Realm realm;
    private Util aux = new Util(this);
    private ToastTipos toastTipos = new ToastTipos(this);
    private static String formatoComplejo = "dd/MM/yyyy HH:mm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.activity_chart_title);

        //Recuperamos los parametros de las fechas de inicio y fin, posteriormente
        //los convertimos a date para poder trabajar con ellos
        String fechaIni = getIntent().getExtras().getString("fechaIni");
        String fechaFin = getIntent().getExtras().getString("fechaFin");
        final Date fechaIniDate = aux.stringToDate(fechaIni, formatoComplejo);
        final Date fechaFinDate = aux.stringToDate(fechaFin, formatoComplejo);

        //Iniciamos la instancia de realm
        realm = Realm.getDefaultInstance();

        //Recuperamos un realmResult de Actividad filtrado por fechas y distinct de Categoría (ordenado por Categoria)
        final RealmResults<Actividad> actividadesDistinct = actividadesXFechasDistinct(fechaIniDate, fechaFinDate);

        //Declaramos un ArrayList vacío para las entradas de la gráfica
        ArrayList<BarEntry> entradas = new ArrayList<>();

        //Declaramos un ArrayList temporal para almacenar y ordenar los datos antes de enviarlos a la gráfica
        final ArrayList<ActividadTemp> entradasTemp = new ArrayList<>();

        for(Actividad a : actividadesDistinct){
            float cantidad = cantidadactividadxcategoriaFechas(a.getCategoria(), fechaIniDate, fechaFinDate);
            //Añadiendo entradas al array temporal
            entradasTemp.add(new ActividadTemp(a.getCategoria(), cantidad));
        }

        //Ordenamos el array temporal con los filtros especificados en las clases de abajo
        Collections.sort(entradasTemp, new sortByCantidad());

        //La variable k la usaremos para el eje x de la gráfica, cantidad sera tomada como el eje y, categoria será la data
        int k = 0;
        for(ActividadTemp a : entradasTemp){
            //Añadiendo entradas al array final que se enviará a la gráfica
            entradas.add(new BarEntry(k, a.cantidad,  a.categoria));
            k++;
        }

        //Vinculamos las entradas al BarDataSet
        BarDataSet dataset = new BarDataSet(entradas,getString(R.string.activity_chart_dataset_label));
        dataset.setColors(ColorTemplate.MATERIAL_COLORS);

        setContentView(R.layout.activity_chart);
        //Esta es la variable general de Chart
        final BarChart grafica = (BarChart) findViewById(R.id.chart);

        //Vinculamos el BarDataSet al BarData
        BarData datos = new BarData(dataset);

        Description description = new Description();
        description.setText(getString(R.string.activity_chart_description));
        //Asignamos descripcion y datos a la variable chart más grande
        grafica.setDescription(description);
        grafica.setData(datos);
        grafica.animateY(5000);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // Mediante este método seteamos que valor se desplegará desde el eje x
                int valor = (int) value;
                return String.valueOf(entradasTemp.get(valor).categoria);
            }

        };

        XAxis xAxis = grafica.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        grafica.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Obtener la data de el elemento seleccionado
                long id = (long) e.getData();
                String categoria = categoriaxID((int)id);
                String tiempo = calcularTiempoxCategoriaFechas(id, fechaIniDate, fechaFinDate);
                int cantidad = (int) cantidadactividadxcategoriaFechas(id, fechaIniDate, fechaFinDate);

                toastTipos.toastMainShow(
                        categoria + "\n" +
                             getString(R.string.activity_chart_toast_accomplished) + " " + cantidad + " " + getString(R.string.activity_chart_toast_times) + "\n" +
                                getString(R.string.activity_chart_toast_time) + " " + tiempo,
                        Toast.LENGTH_LONG);
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private RealmResults<Actividad> actividadesXFechasDistinct(Date fechaIni, Date fechaFin){
        RealmResults<Actividad> actividades;
        actividades = realm
                .where(Actividad.class)
                .beginGroup()
                .between("fechaIni", fechaIni, fechaFin)
                .or()
                .between("fechaFin", fechaIni, fechaFin)
                .endGroup()
                .distinct("categoria")
                .sort("categoria");
        return actividades;
    }

    private float cantidadactividadxcategoriaFechas(long categoria, Date fechaIni, Date fechaFin){
        RealmResults<Actividad> actividades;
        actividades = realm
                .where(Actividad.class)
                .equalTo("categoria",categoria)
                .beginGroup()
                .between("fechaIni", fechaIni, fechaFin)
                .or()
                .between("fechaFin", fechaIni, fechaFin)
                .endGroup()
                .findAll();
        return actividades.size();
    }

    private String categoriaxID(int id){
        Categoria categorias2;
        categorias2 = realm
                .where(Categoria.class)
                .equalTo("id",id)
                .findFirst();
        return categorias2.getName();
    }

    private String calcularTiempoxCategoriaFechas(long categoria, Date fechaIniV, Date fechaFinV){
        RealmResults<Actividad> actividades;
        actividades = realm
                .where(Actividad.class)
                .equalTo("categoria",categoria)
                .beginGroup()
                .between("fechaIni", fechaIniV, fechaFinV)
                .or()
                .between("fechaFin", fechaIniV, fechaFinV)
                .endGroup()
                .findAll();

        long acumulado = 0;
        for (Actividad item : actividades) {
            Date fechaIni = item.getFechaIni();
            Date fechaFin = item.getFechaFin();
            int resta = aux.restarFechasNumero(fechaIni,fechaFin);
            acumulado += resta;
        }

        return aux.convertirTiempo(acumulado);
    }
}

class ActividadTemp
{
    float cantidad;
    long categoria;

    // Constructor
    public ActividadTemp(long categoria, float cantidad)
    {
        this.cantidad = cantidad;
        this.categoria = categoria;
    }
}

class sortByCantidad implements Comparator<ActividadTemp>
{
    // Used for sorting in ascending order of
    // roll number
    public int compare(ActividadTemp a, ActividadTemp b)
    {
        float resta = b.cantidad - a.cantidad;
        return (int) resta;
    }
}

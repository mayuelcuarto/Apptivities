package com.cristhian.apptivities.Activitites;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.Models.Categoria;
import com.cristhian.apptivities.R;
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
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class ChartActivity extends Activity {

    private Realm realm;
    private RealmResults<Categoria> categorias;
    private Util aux = new Util(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setTitle(R.string.activity_chart_title);
        realm = Realm.getDefaultInstance();

        categorias();
        ArrayList<BarEntry> entradas = new ArrayList<>();

        for(int i = 0; i<categorias.size();i++){
            float cantidad = cantidadactividadxcategoria(categorias.get(i).getId());
            entradas.add(new BarEntry(i,cantidad, categorias.get(i).getName()));
        }

        BarDataSet dataset = new BarDataSet(entradas,getString(R.string.activity_chart_dataset_label));

        dataset.setColors(ColorTemplate.COLORFUL_COLORS);

        setContentView(R.layout.activity_chart);
        final BarChart grafica = (BarChart) findViewById(R.id.chart);

        BarData datos = new BarData(dataset);

        Description description = new Description();
        description.setText(getString(R.string.activity_chart_description));
        grafica.setDescription(description);

        grafica.setData(datos);

        grafica.animateY(5000);

        IAxisValueFormatter formatter = new IAxisValueFormatter() {

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int valor = (int) value;
                return String.valueOf(categorias.get(valor).getId());
            }

        };

        XAxis xAxis = grafica.getXAxis();
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatter);

        grafica.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                int id = (int) e.getX() + 1;
                String categoria = categoriaxID(id);
                String tiempo = calcularTiempoxCategoria(id);
                int cantidad = (int) cantidadactividadxcategoria(id);
                Toast.makeText(getApplicationContext(),
                        categoria + "\n" +
                             getString(R.string.activity_chart_toast_accomplished) + " " + cantidad + " " + getString(R.string.activity_chart_toast_times) + "\n" +
                                getString(R.string.activity_chart_toast_time) + " " + tiempo,
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void categorias(){
        categorias = realm
                .where(Categoria.class)
                .findAll();
    }

    private float cantidadactividadxcategoria(long categoria){
        RealmResults<Actividad> actividades;
        actividades = realm
                .where(Actividad.class)
                .equalTo("categoria",categoria)
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

    private String calcularTiempoxCategoria(long categoria){
        RealmResults<Actividad> actividades;
        actividades = realm
                .where(Actividad.class)
                .equalTo("categoria",categoria)
                .findAll();

        int acumulado = 0;
        for (Actividad item : actividades) {
            Date fechaIni = item.getFechaIni();
            Date fechaFin = item.getFechaFin();
            int resta = aux.restarFechasNumero(fechaIni,fechaFin);
            acumulado += resta;
        }

        return aux.convertirTiempo(acumulado);
    }
}

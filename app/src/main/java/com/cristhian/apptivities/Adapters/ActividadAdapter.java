package com.cristhian.apptivities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.R;
import com.cristhian.apptivities.Utils.Util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;

/**
 * Created by Cristhian on 26-12-2017.
 */

public class ActividadAdapter extends BaseAdapter {
    private Context context;
    private List<Actividad> list;
    private int layout;
    private Util aux = new Util();

    public ActividadAdapter(Context context, List<Actividad> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    public int getCount() {
        return list.size();
    }

    @Override
    public Actividad getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder2 vh;

        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder2();
            vh.id = (TextView) convertView.findViewById(R.id.textViewActividadID);
            vh.descripcion = (TextView) convertView.findViewById(R.id.textViewActividadDescripcion);
            vh.fechaIni = (TextView) convertView.findViewById(R.id.textViewActividadFechaIni);
            vh.fechaFin = (TextView) convertView.findViewById(R.id.textViewActividadFechaFin);
            vh.imageView = (ImageView) convertView.findViewById(R.id.imageView);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder2) convertView.getTag();
        }

        final Actividad actividad = list.get(position);
        String id = String.valueOf(actividad.getId()) ;
        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaIni = dateFormat.format(actividad.getFechaIni());
        String fechaFin = dateFormat.format(actividad.getFechaFin());

        vh.id.setText(id);
        vh.descripcion.setText(actividad.getDescripcion());
        vh.fechaIni.setText(fechaIni);
        vh.fechaFin.setText(fechaFin);

        vh.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarFechaFinxActividad(actividad);
            }
        });

        return convertView;
    }

    private void actualizarFechaFinxActividad(Actividad actividad){
        String temp = aux.dateToString(new Date(),"dd/MM/yyyy HH:mm");
        Date fechaFin = aux.stringToDate(temp, "dd/MM/yyyy HH:mm");

        Realm realm;
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        actividad.setFechaFin(fechaFin);
        realm.copyToRealmOrUpdate(actividad);
        realm.commitTransaction();
        realm.close();
    }

    public class ViewHolder2{
        TextView id;
        TextView descripcion;
        TextView fechaIni;
        TextView fechaFin;
        ImageView imageView;
    }
}
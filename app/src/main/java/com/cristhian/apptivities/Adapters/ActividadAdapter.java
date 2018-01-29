package com.cristhian.apptivities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Cristhian on 26-12-2017.
 */

public class ActividadAdapter extends BaseAdapter {
    private Context context;
    private List<Actividad> list;
    private int layout;

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
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder2) convertView.getTag();
        }

        Actividad actividad = list.get(position);
        String id = String.valueOf(actividad.getId()) ;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaIni = dateFormat.format(actividad.getFechaIni());
        String fechaFin = dateFormat.format(actividad.getFechaFin());

        vh.id.setText(id);
        vh.descripcion.setText(actividad.getDescripcion());
        vh.fechaIni.setText(fechaIni);
        vh.fechaFin.setText(fechaFin);

        return convertView;
    }

    public class ViewHolder2{
        TextView id;
        TextView descripcion;
        TextView fechaIni;
        TextView fechaFin;
    }
}
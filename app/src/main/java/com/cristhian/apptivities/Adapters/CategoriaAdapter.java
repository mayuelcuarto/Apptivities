package com.cristhian.apptivities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.Models.Categoria;
import com.cristhian.apptivities.R;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Cristhian on 26-12-2017.
 */

public class CategoriaAdapter extends BaseAdapter {
    private Context context;
    private List<Categoria> list;
    private int layout;

    public CategoriaAdapter(Context context, List<Categoria> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    public int getCount() {
        return list.size();
    }

    @Override
    public Categoria getItem(int position) {
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
            vh.id = (TextView) convertView.findViewById(R.id.textViewCategoriaListID);
            vh.name = (TextView) convertView.findViewById(R.id.textViewCategoriaListName);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder2) convertView.getTag();
        }

        Categoria categoria = list.get(position);
        String id = String.valueOf(categoria.getId()) ;

        vh.id.setText(id);
        vh.name.setText(categoria.getName());


        return convertView;
    }

    public class ViewHolder2{
        TextView id;
        TextView name;
    }
}

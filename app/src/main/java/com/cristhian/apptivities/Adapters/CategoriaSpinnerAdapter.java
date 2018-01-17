package com.cristhian.apptivities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cristhian.apptivities.Models.Categoria;
import com.cristhian.apptivities.R;

import java.util.List;

/**
 * Created by Cristhian on 26-12-2017.
 */

public class CategoriaSpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<Categoria> list;
    private int layout;

    public CategoriaSpinnerAdapter(Context context, List<Categoria> list, int layout) {
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
        ViewHolder3 vh;

        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder3();
            vh.name = (TextView) convertView.findViewById(R.id.textViewCategoriaName);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder3) convertView.getTag();
        }

        Categoria categoria = list.get(position);
        vh.name.setText(categoria.getName());
        return convertView;
    }

    public class ViewHolder3{
        TextView name;
    }
}

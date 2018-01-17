package com.cristhian.apptivities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cristhian.apptivities.Models.Usuario;
import com.cristhian.apptivities.R;

import java.util.List;

/**
 * Created by Cristhian on 26-12-2017.
 */

public class UsuarioAdapter extends BaseAdapter {
    private Context context;
    private List<Usuario> list;
    private int layout;

    public UsuarioAdapter(Context context, List<Usuario> list, int layout) {
        this.context = context;
        this.list = list;
        this.layout = layout;
    }

    public int getCount() {
        return list.size();
    }

    @Override
    public Usuario getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh;

        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(layout, null);
            vh = new ViewHolder();
            vh.id = (TextView) convertView.findViewById(R.id.textViewUsuarioID);
            vh.apenom = (TextView) convertView.findViewById(R.id.textViewUsuarioApeNom);
            vh.cargo = (TextView) convertView.findViewById(R.id.textViewUsuarioCargo);
            convertView.setTag(vh);
        }else{
            vh = (ViewHolder) convertView.getTag();
        }

        Usuario usuario = list.get(position);
        String id = String.valueOf(usuario.getId()) ;
        vh.id.setText(id);
        vh.apenom.setText(usuario.getApellidos() + ", " + usuario.getNombres());
        vh.cargo.setText(usuario.getCargo());

        return convertView;
    }

    public class ViewHolder{
        TextView id;
        TextView apenom;
        TextView cargo;
    }
}

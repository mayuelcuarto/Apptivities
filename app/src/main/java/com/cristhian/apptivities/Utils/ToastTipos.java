package com.cristhian.apptivities.Utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cristhian.apptivities.R;

/**
 * Created by Cristhian on 14-03-2018.
 */

public class ToastTipos{
    private Context context;

    public ToastTipos(Context context) {
        this.context = context;
    }

    public void toastMainShow(String mensaje, int duracion){
        LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
        View layout = inflater.inflate(R.layout.toast_main, null);
        TextView text = (TextView) layout.findViewById(R.id.text);

        text.setText(mensaje);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(duracion);
        toast.setView(layout);
        toast.show();
    }
}

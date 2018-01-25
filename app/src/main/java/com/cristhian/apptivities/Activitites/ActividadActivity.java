package com.cristhian.apptivities.Activitites;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cristhian.apptivities.Adapters.ActividadAdapter;
import com.cristhian.apptivities.Adapters.CategoriaSpinnerAdapter;
import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.Models.Categoria;
import com.cristhian.apptivities.Utils.Util;
import com.cristhian.apptivities.R;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;


public class ActividadActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Actividad>>, AdapterView.OnItemClickListener{

    private Realm realm;
    private RealmResults<Actividad> actividades;
    private RealmResults<Categoria> categorias;
    private ListView listView;
    private ActividadAdapter adapter;
    private FloatingActionButton fab;
    private static String formatoSimple = "dd/MM/yyyy";
    private static String formatoComplejo = "dd/MM/yyyy HH:mm";
    private Util aux = new Util();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad);
        datosRealm();
        setTitle(getString(R.string.activity_activity_title) + ": " + aux.dateToString(new Date(), formatoSimple));
        Constructor();

    }

    private void Constructor(){
        actividades.addChangeListener(this);

        adapter = new ActividadAdapter(this,actividades,R.layout.list_view_actividad_item);
        listView = (ListView) findViewById(R.id.listViewActividad);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fabAddActividad);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingActividad(
                        getString(R.string.new_activity_dialog_title),
                        getString(R.string.new_activity_dialog_message));
            }
        });

        scrollMyListViewToBottom();
        registerForContextMenu(listView);
    }



    private void datosRealm(){
        realm = Realm.getDefaultInstance();

        actividades = realm
                .where(Actividad.class)
                .between("fechaIni", aux.todayFiltro(0, new Date()), aux.todayFiltro(1, new Date()))
                .or()
                .between("fechaFin", aux.todayFiltro(0, new Date()), aux.todayFiltro(1, new Date()))
                .findAll();
    }

    private void datosCategorias(){
        categorias = realm
                .where(Categoria.class)
                .findAll();
    }

    private Categoria datosCategoriaXID(int id){
        Categoria categoria = new Categoria();
        categoria = realm
                .where(Categoria.class)
                .equalTo("id", id)
                .findFirst();
        return categoria;
    }

    private void createNewActivity(String descripcion, String fechaIni, String fechaFin, long categoria) {
        realm.beginTransaction();
        Date fechaIni2 = aux.stringToDate(fechaIni, formatoComplejo);
        Date fechaFin2 = aux.stringToDate(fechaFin, formatoComplejo);
        Actividad actividad = new Actividad(descripcion, fechaIni2, fechaFin2, categoria);
        realm.copyToRealm(actividad);
        realm.commitTransaction();
        scrollMyListViewToBottom();
    }

    private void editActividad(String descripcion, String fechaIni, String fechaFin, long categoria, Actividad actividad){
        realm.beginTransaction();
        actividad.setDescripcion(descripcion);
        Date fechaIni2 = aux.stringToDate(fechaIni, formatoComplejo);
        Date fechaFin2 = aux.stringToDate(fechaFin, formatoComplejo);
        actividad.setFechaIni(fechaIni2);
        actividad.setFechaFin(fechaFin2);
        actividad.setCategoria(categoria);
        realm.copyToRealmOrUpdate(actividad);
        realm.commitTransaction();
    }

    private void deleteActividad(Actividad actividad){
        realm.beginTransaction();
        actividad.deleteFromRealm();
        realm.commitTransaction();
    }

    private void showCalendar(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.date_pick_calendar, null);
        builder.setView(viewInflated);

        final DatePicker calendar = (DatePicker) viewInflated.findViewById(R.id.datePicker);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String fechaCal =  String.valueOf(calendar.getDayOfMonth()) + "/" + String.valueOf(calendar.getMonth() + 1) + "/" + String.valueOf(calendar.getYear());

                actividades = realm
                        .where(Actividad.class)
                        .between("fechaIni", aux.stringSimpleToDate(0,fechaCal, formatoComplejo), aux.stringSimpleToDate(1,fechaCal, formatoComplejo))
                        .or()
                        .between("fechaFin", aux.stringSimpleToDate(0,fechaCal, formatoComplejo), aux.stringSimpleToDate(1,fechaCal, formatoComplejo))
                        .findAll();
                String fechaTitulo = aux.dateToString(aux.stringSimpleToDate(0,fechaCal,formatoSimple),formatoSimple);
                setTitle(getString(R.string.activity_activity_title) + ": " + fechaTitulo);
                Constructor();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForCreatingActividad(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_actividad, null);
        builder.setView(viewInflated);

        final EditText inputDescripcion = (EditText) viewInflated.findViewById(R.id.editTextNewActividadDescripcion);
        final EditText inputFechaIni = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaIni);
        inputFechaIni.setText(aux.dateToString(new Date(), formatoComplejo));
        final EditText inputFechaFin = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaFin);
        inputFechaFin.setText(aux.dateToString(new Date(), formatoComplejo));
        final Spinner inputCategoria = (Spinner) viewInflated.findViewById(R.id.spinner);
        datosCategorias();
        inputCategoria.setAdapter(new CategoriaSpinnerAdapter(this, categorias ,R.layout.spinner_categoria_item));

        builder.setPositiveButton(getString(R.string.new_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String actividadDescripcion = inputDescripcion.getText().toString().trim();
                String actividadFechaIni = inputFechaIni.getText().toString().trim();
                String actividadFechaFin = inputFechaFin.getText().toString().trim();

                if(actividadDescripcion.length()>0 && actividadFechaIni.length()>0 && actividadFechaFin.length()>0){
                    createNewActivity(actividadDescripcion,actividadFechaIni,actividadFechaFin,inputCategoria.getAdapter().getItemId(inputCategoria.getSelectedItemPosition())+1);
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.new_activity_dialog_empty_values_message),Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForEditingActividad(String title, String message, final Actividad actividad){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_actividad, null);
        builder.setView(viewInflated);

        final EditText inputActividadDescripcion = (EditText) viewInflated.findViewById(R.id.editTextNewActividadDescripcion);
        final EditText inputActividadFechaIni = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaIni);
        final EditText inputActividadFechaFin = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaFin);
        final Spinner inputCategoria = (Spinner) viewInflated.findViewById(R.id.spinner);

        inputActividadDescripcion.setText(actividad.getDescripcion());
        String fechaIniStr = aux.dateToString(actividad.getFechaIni(), formatoComplejo);
        inputActividadFechaIni.setText(fechaIniStr);
        String fechaFinStr = aux.dateToString(actividad.getFechaFin(), formatoComplejo);
        inputActividadFechaFin.setText(fechaFinStr);
        long categoriaID = actividad.getCategoria();

        datosCategorias();
        inputCategoria.setAdapter(new CategoriaSpinnerAdapter(this, categorias ,R.layout.spinner_categoria_item));
        Integer i = (int) (long) categoriaID - 1;
        //Toast.makeText(this,"Item seleccionado:" + i,Toast.LENGTH_LONG).show();
        inputCategoria.setSelection(i);

        builder.setPositiveButton(getString(R.string.edit_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String actividadDescripcion = inputActividadDescripcion.getText().toString().trim();
                String actividadFechaIni = inputActividadFechaIni.getText().toString().trim();
                String actividadFechaFin = inputActividadFechaFin.getText().toString().trim();
                if(actividadDescripcion.length()==0 || actividadFechaIni.length()==0 || actividadFechaFin.length()==0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.edit_activity_dialog_empty_values_message), Toast.LENGTH_LONG).show();
                }else{
                    editActividad(actividadDescripcion,actividadFechaIni,actividadFechaFin,inputCategoria.getAdapter().getItemId(inputCategoria.getSelectedItemPosition())+1,actividad);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForDeletingActividad(String title, String message, final Actividad actividad){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        builder.setPositiveButton(getString(R.string.delete_activity_dialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(getString(R.string.delete_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteActividad(actividad);
                    }
                }

        );

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /* Events */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actividad_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.verCalendario:
                showCalendar("", "");
                return true;
            case R.id.menuCategory:
                Intent intent = new Intent(ActividadActivity.this, CategoriaActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(actividades.get(info.position).getDescripcion());
        getMenuInflater().inflate(R.menu.context_menu_actividad_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete_actividad:
                showAlertForDeletingActividad(
                        getString(R.string.delete_activity_dialog_title),
                        getString(R.string.delete_activity_dialog_message),
                        actividades.get(info.position));
                return true;
            case R.id.edit_actividad:
                showAlertForEditingActividad(
                        getString(R.string.edit_activity_dialog_title),
                        getString(R.string.edit_activity_dialog_message),
                        actividades.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Actividad actividad = (Actividad) parent.getItemAtPosition(position);
        String categoriaNombre = getString(R.string.activity_activity_toast_category_empty);
        //Aquí se controla como el toast responderá en caso no haber seleccionado una categoría para la actividad seleccionada.
        if(actividad.getCategoria()>0) {
            Categoria categoria = datosCategoriaXID((int) (long) actividad.getCategoria());
            categoriaNombre = categoria.getName();
        }

        Toast.makeText(this, getString(R.string.activity_activity_toast_title) +"\n" +
                "ID: " + actividad.getId() + "\n" +
                getString(R.string.activity_activity_toast_description) + ": " + actividad.getDescripcion() + "\n" +
                getString(R.string.activity_activity_toast_fechaIni) + ": " + actividad.getFechaIni() + "\n" +
                getString(R.string.activity_activity_toast_fechaFin) + ": " + actividad.getFechaFin() + "\n" +
                getString(R.string.activity_activity_toast_category) + ": " + categoriaNombre
                , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChange(RealmResults<Actividad> element) {
        adapter.notifyDataSetChanged();
    }

    private void scrollMyListViewToBottom() {
        listView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listView.setSelection(listView.getCount() - 1);
            }
        });
    }
}

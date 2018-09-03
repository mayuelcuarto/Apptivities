package com.cristhian.apptivities.Activitites;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cristhian.apptivities.Adapters.ActividadAdapter;
import com.cristhian.apptivities.Adapters.CategoriaSpinnerAdapter;
import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.Models.Categoria;
import com.cristhian.apptivities.R;
import com.cristhian.apptivities.Utils.MaskWatcher;
import com.cristhian.apptivities.Utils.RealmBackupRestore;
import com.cristhian.apptivities.Utils.ToastTipos;
import com.cristhian.apptivities.Utils.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.util.Date;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ActividadActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Actividad>>, AdapterView.OnItemClickListener{

    private Realm realm;
    private RealmResults<Actividad> actividades;
    private RealmResults<Categoria> categorias;
    private ListView listView;
    private ActividadAdapter adapter;
    private CategoriaSpinnerAdapter adapter2;
    private CategoriaSpinnerAdapter adapter3;
    private FloatingActionButton fab;
    private FloatingActionButton sfab;
    private FloatingActionButton cfab;
    private FloatingActionButton gfab;
    private static String formatoHora = "HH";
    private static String formatoMinuto = "mm";
    private static String formatoSimple = "dd/MM/yyyy";
    private static String formatoComplejo = "dd/MM/yyyy HH:mm";
    private Util aux = new Util(this);
    private RealmBackupRestore rbur= new RealmBackupRestore(this);
    private ToastTipos toastTipos = new ToastTipos(this);
    private Date fechaSeleccionada = new Date();

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

        sfab = (FloatingActionButton) findViewById(R.id.fabSearchActividad);
        sfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForSearchActividad(
                        getString(R.string.search_activity_dialog_title),
                        getString(R.string.search_activity_dialog_message));
            }
        });

        gfab = (FloatingActionButton) findViewById(R.id.fabGraphic);
        gfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForGraphicActividad(
                        getString(R.string.graphic_activity_dialog_title),
                        getString(R.string.graphic_activity_dialog_message));
            }
        });

        cfab = (FloatingActionButton) findViewById(R.id.fabCalendar);
        cfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCalendar(getString(R.string.activity_activity_list_menu_calendar));
                //showDatePickerDialog();
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
                .findAllSorted("name");
    }

    private Categoria datosCategoriaXID(int id){
        Categoria categoria = realm
                .where(Categoria.class)
                .equalTo("id", id)
                .findFirst();
        return categoria;
    }

    private void actividadesXdescripcion(String descripcion, boolean filtroFechas, boolean filtroCategorias, Date FechaIni, Date FechaFin, int categoria_id){
        if(filtroFechas && filtroCategorias){
            actividades = realm
                    .where(Actividad.class)
                    .contains("descripcion", descripcion, Case.INSENSITIVE)
                    .equalTo("categoria", categoria_id)
                    .beginGroup()
                    .between("fechaIni", FechaIni, FechaFin)
                    .or()
                    .between("fechaFin", FechaIni, FechaFin)
                    .endGroup()
                    .findAll();
        }else if(filtroFechas){
            actividades = realm
                    .where(Actividad.class)
                    .contains("descripcion", descripcion, Case.INSENSITIVE)
                    .beginGroup()
                    .between("fechaIni", FechaIni, FechaFin)
                    .or()
                    .between("fechaFin", FechaIni, FechaFin)
                    .endGroup()
                    .findAll();
        }else if(filtroCategorias){
            actividades = realm
                    .where(Actividad.class)
                    .contains("descripcion", descripcion, Case.INSENSITIVE)
                    .equalTo("categoria", categoria_id)
                    .findAll();
        }else{
            actividades = realm
                    .where(Actividad.class)
                    .contains("descripcion", descripcion, Case.INSENSITIVE)
                    .findAll();
        }

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

    private void showCalendar(String title){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.date_pick_calendar, null);
        builder.setView(viewInflated);

        final DatePicker calendar = (DatePicker) viewInflated.findViewById(R.id.datePicker);
        calendar.getCalendarView().setDate(fechaSeleccionada.getTime());
        calendar.getCalendarView().setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String fechaCal =  String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);

                actividades = realm
                        .where(Actividad.class)
                        .between("fechaIni", aux.stringSimpleToDate(0, fechaCal, formatoComplejo), aux.stringSimpleToDate(1, fechaCal, formatoComplejo))
                        .or()
                        .between("fechaFin", aux.stringSimpleToDate(0, fechaCal, formatoComplejo), aux.stringSimpleToDate(1, fechaCal, formatoComplejo))
                        .findAll();
                String fechaTitulo = aux.dateToString(aux.stringSimpleToDate(0, fechaCal, formatoSimple), formatoSimple);
                setTitle(getString(R.string.activity_activity_title) + ": " + fechaTitulo);
                fechaSeleccionada = aux.stringToDate(fechaCal, formatoSimple);
                Constructor();
            }
        });
        Context mContext = builder.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View mView = mLayoutInflater.inflate(R.layout.dialog_calendar_title, null);
        if(title != null) {
            TextView mTextView = (TextView) mView.findViewById(R.id.textViewTitleCalendar);
            mTextView.setText(title);
            builder.setCustomTitle(mView);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForCreatingActividad(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_actividad, null);
        builder.setView(viewInflated);

        final EditText inputDescripcion = (EditText) viewInflated.findViewById(R.id.editTextNewActividadDescripcion);
        final EditText inputFechaIni = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaIni);
        inputFechaIni.setText(aux.dateToString(new Date(), formatoComplejo));
        inputFechaIni.addTextChangedListener(new MaskWatcher("##/##/####"));

        final EditText inputFechaFin = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaFin);
        inputFechaFin.setText(aux.dateToString(new Date(), formatoComplejo));
        inputFechaFin.addTextChangedListener(new MaskWatcher("##/##/####"));

        final TimePicker inputTimePickerActividadFechaIni = (TimePicker) viewInflated.findViewById(R.id.timePickerNewActividadFechaIni);
        inputTimePickerActividadFechaIni.setIs24HourView(true);

        final TimePicker inputTimePickerActividadFechaFin = (TimePicker) viewInflated.findViewById(R.id.timePickerNewActividadFechaFin);
        inputTimePickerActividadFechaFin.setIs24HourView(true);

        final Spinner inputCategoria = (Spinner) viewInflated.findViewById(R.id.spinner);
        datosCategorias();

        adapter2 = new CategoriaSpinnerAdapter(this, categorias, R.layout.spinner_categoria_item);
        inputCategoria.setAdapter(adapter2);

        builder.setPositiveButton(getString(R.string.new_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String actividadDescripcion = inputDescripcion.getText().toString().trim();
                String actividadFechaIni = inputFechaIni.getText().toString().trim() + " " + inputTimePickerActividadFechaIni.getCurrentHour() + ":" + inputTimePickerActividadFechaIni.getCurrentMinute();
                String actividadFechaFin = inputFechaFin.getText().toString().trim() + " " + inputTimePickerActividadFechaFin.getCurrentHour() + ":" + inputTimePickerActividadFechaFin.getCurrentMinute();

                Date fechaIniTemp = aux.stringToDate(actividadFechaIni, formatoComplejo);
                Date fechaFinTemp = aux.stringToDate(actividadFechaFin, formatoComplejo);

                Categoria categoria = (Categoria) inputCategoria.getSelectedItem();

                if(actividadDescripcion.length()==0 || actividadFechaIni.length()==0 || actividadFechaFin.length()==0){
                    toastTipos.toastMainShow(getString(R.string.new_activity_dialog_empty_values_message),Toast.LENGTH_LONG);
                }else if(aux.validarFecha(inputFechaIni.getText().toString().trim(),formatoSimple)==false){
                    toastTipos.toastMainShow(getString(R.string.new_activity_dialog_wrong_fechaIni_message), Toast.LENGTH_LONG);
                }else if(aux.validarFecha(inputFechaFin.getText().toString().trim(),formatoSimple)==false){
                    toastTipos.toastMainShow(getString(R.string.new_activity_dialog_wrong_fechaFin_message), Toast.LENGTH_LONG);
                }else if(fechaIniTemp.getTime() > fechaFinTemp.getTime()){
                    toastTipos.toastMainShow(getString(R.string.new_activity_dialog_date_calculate_error_message), Toast.LENGTH_LONG);
                }else{
                      createNewActivity(actividadDescripcion,actividadFechaIni,actividadFechaFin, categoria.getId());
                }
            }
        }
        );

        CustomTitleAndShow(builder,R.layout.dialog_create_activity_title,R.id.textViewTitleCreate,title,message);
    }

    private void showAlertForEditingActividad(String title, String message, final Actividad actividad){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_actividad, null);
        builder.setView(viewInflated);

        final EditText inputActividadDescripcion = (EditText) viewInflated.findViewById(R.id.editTextNewActividadDescripcion);
        final EditText inputActividadFechaIni = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaIni);
        inputActividadFechaIni.addTextChangedListener(new MaskWatcher("##/##/####"));

        final EditText inputActividadFechaFin = (EditText) viewInflated.findViewById(R.id.editTextNewActividadFechaFin);
        inputActividadFechaFin.addTextChangedListener(new MaskWatcher("##/##/####"));

        final Spinner inputCategoria = (Spinner) viewInflated.findViewById(R.id.spinner);

        final TimePicker inputTimePickerActividadFechaIni = (TimePicker) viewInflated.findViewById(R.id.timePickerNewActividadFechaIni);
        inputTimePickerActividadFechaIni.setIs24HourView(true);

        final TimePicker inputTimePickerActividadFechaFin = (TimePicker) viewInflated.findViewById(R.id.timePickerNewActividadFechaFin);
        inputTimePickerActividadFechaFin.setIs24HourView(true);

        inputActividadDescripcion.setText(actividad.getDescripcion());

        String fechaIniStr = aux.dateToString(actividad.getFechaIni(), formatoSimple);
        inputActividadFechaIni.setText(fechaIniStr);
        inputTimePickerActividadFechaIni.setCurrentHour(Integer.parseInt(aux.dateToString(actividad.getFechaIni(), formatoHora)));
        inputTimePickerActividadFechaIni.setCurrentMinute(Integer.parseInt(aux.dateToString(actividad.getFechaIni(), formatoMinuto)));

        String fechaFinStr = aux.dateToString(actividad.getFechaFin(), formatoSimple);
        inputActividadFechaFin.setText(fechaFinStr);
        inputTimePickerActividadFechaFin.setCurrentHour(Integer.parseInt(aux.dateToString(actividad.getFechaFin(), formatoHora)));
        inputTimePickerActividadFechaFin.setCurrentMinute(Integer.parseInt(aux.dateToString(actividad.getFechaFin(), formatoMinuto)));

        long categoriaID = actividad.getCategoria();
        Categoria categoriaRealm = datosCategoriaXID((int) (long) categoriaID);

        datosCategorias();
        adapter2 = new CategoriaSpinnerAdapter(this, categorias, R.layout.spinner_categoria_item);
        inputCategoria.setAdapter(adapter2);
        inputCategoria.setSelection(adapter2.getPosition(categoriaRealm));

        builder.setPositiveButton(getString(R.string.edit_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String actividadDescripcion = inputActividadDescripcion.getText().toString().trim();
                String actividadFechaIni = inputActividadFechaIni.getText().toString().trim() + " " + inputTimePickerActividadFechaIni.getCurrentHour() + ":" + inputTimePickerActividadFechaIni.getCurrentMinute();
                String actividadFechaFin = inputActividadFechaFin.getText().toString().trim() + " " + inputTimePickerActividadFechaFin.getCurrentHour() + ":" + inputTimePickerActividadFechaFin.getCurrentMinute();

                Date fechaIniTemp = aux.stringToDate(actividadFechaIni, formatoComplejo);
                Date fechaFinTemp = aux.stringToDate(actividadFechaFin, formatoComplejo);

                Categoria categoria = (Categoria) inputCategoria.getSelectedItem();

                if(actividadDescripcion.length()==0 || actividadFechaIni.length()==0 || actividadFechaFin.length()==0) {
                    toastTipos.toastMainShow(getString(R.string.edit_activity_dialog_empty_values_message), Toast.LENGTH_LONG);
                }else if(aux.validarFecha(inputActividadFechaIni.getText().toString().trim(),formatoSimple)==false){
                    toastTipos.toastMainShow(getString(R.string.new_activity_dialog_wrong_fechaIni_message), Toast.LENGTH_LONG);
                }else if(aux.validarFecha(inputActividadFechaFin.getText().toString().trim(),formatoSimple)==false){
                    toastTipos.toastMainShow(getString(R.string.new_activity_dialog_wrong_fechaFin_message), Toast.LENGTH_LONG);
                }else if(fechaIniTemp.getTime() > fechaFinTemp.getTime()){
                    toastTipos.toastMainShow(getString(R.string.new_activity_dialog_date_calculate_error_message), Toast.LENGTH_LONG);
                }else{
                    editActividad(actividadDescripcion,actividadFechaIni,actividadFechaFin,categoria.getId(),actividad);
                }
            }
        });
        CustomTitleAndShow(builder,R.layout.dialog_edit_activity_title,R.id.textViewTitleEdit,title,message);
    }

    private void showAlertForDeletingActividad(String title, String message, final Actividad actividad){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
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
        CustomTitleAndShow(builder,R.layout.dialog_delete_activity_title,R.id.textViewTitleDelete,title,message);
    }

    private void showAlertForSearchActividad(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_search_actividad, null);
        builder.setView(viewInflated);

        final EditText inputDescripcion = (EditText) viewInflated.findViewById(R.id.editTextSearchDescription);

        final Spinner inputCategoria = (Spinner) viewInflated.findViewById(R.id.spinnerSearchCategoria);
        inputCategoria.setEnabled(false);

        datosCategorias();
        adapter2 = new CategoriaSpinnerAdapter(this, categorias, R.layout.spinner_categoria_item_search);
        adapter3 = new CategoriaSpinnerAdapter(this, categorias, R.layout.spinner_categoria_item);
        inputCategoria.setAdapter(adapter2);

        final Switch switchCategorias = (Switch) viewInflated.findViewById(R.id.switchSearchCategoria);
        switchCategorias.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    inputCategoria.setAdapter(adapter3);
                    inputCategoria.setEnabled(true);

                    inputDescripcion.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputDescripcion.getWindowToken(), 0);
                    inputCategoria.requestFocus();
                }else{
                    inputCategoria.setAdapter(adapter2);
                    inputCategoria.setEnabled(false);

                    inputDescripcion.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputDescripcion, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        final DatePicker inputActividadFechaIni = (DatePicker) viewInflated.findViewById(R.id.datePickerSearchFechaIni);
        inputActividadFechaIni.setEnabled(false);

        final DatePicker inputActividadFechaFin = (DatePicker) viewInflated.findViewById(R.id.datePickerSearchFechaFin);
        inputActividadFechaFin.setEnabled(false);

        final Switch switchFechas = (Switch) viewInflated.findViewById(R.id.switchSearchFechas);
        switchFechas.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    inputActividadFechaIni.setEnabled(true);
                    inputActividadFechaFin.setEnabled(true);

                    inputDescripcion.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(inputDescripcion.getWindowToken(), 0);
                    inputActividadFechaIni.getChildAt(0).requestFocus();
                    imm.showSoftInput(inputActividadFechaIni.getChildAt(0), InputMethodManager.SHOW_IMPLICIT);
                }else{
                    inputActividadFechaIni.setEnabled(false);
                    inputActividadFechaFin.setEnabled(false);

                    inputDescripcion.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(inputDescripcion, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        builder.setPositiveButton(getString(R.string.search_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                 String actividadDescripcion = inputDescripcion.getText().toString().trim();
                 String actividadFechaIni =  inputActividadFechaIni.getDayOfMonth() + "/" + (inputActividadFechaIni.getMonth()+1) + "/" + inputActividadFechaIni.getYear() + " 00:00";
                 String actividadFechaFin = inputActividadFechaFin.getDayOfMonth() + "/" + (inputActividadFechaFin.getMonth()+1) + "/" + inputActividadFechaFin.getYear() + " 24:00";
                 Date fechaIniTemp = aux.stringToDate(actividadFechaIni, formatoComplejo);
                 Date fechaFinTemp = aux.stringToDate(actividadFechaFin, formatoComplejo);
                 if(actividadDescripcion.length()==0 && switchCategorias.isChecked() == false){
                     toastTipos.toastMainShow(getString(R.string.search_activity_dialog_error_message), Toast.LENGTH_SHORT);
                 }else{
                     int categoria_id = 0;
                     if(switchCategorias.isChecked()){
                         Categoria categoria = (Categoria) inputCategoria.getSelectedItem();
                         categoria_id = categoria.getId();
                     }
                     actividadesXdescripcion(actividadDescripcion,switchFechas.isChecked(),switchCategorias.isChecked(),fechaIniTemp,fechaFinTemp, categoria_id);
                     Constructor();
                     setTitle(getString(R.string.activity_activity_title) + ": " + getString(R.string.search_activity_title_mod));
                 }
                    }
                }
        );
        fechaSeleccionada = new Date();
        CustomTitleAndShow(builder,R.layout.dialog_search_actividad_title,R.id.textViewTitleSearch,title,message);
    }

    private void showAlertForGraphicActividad(String title, String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_graphic_actividad, null);
        builder.setView(viewInflated);

        final DatePicker inputActividadFechaIni = (DatePicker) viewInflated.findViewById(R.id.datePickerGraphicFechaIni);
        final DatePicker inputActividadFechaFin = (DatePicker) viewInflated.findViewById(R.id.datePickerGraphicFechaFin);

        builder.setPositiveButton(getString(R.string.graphic_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String actividadFechaIni =  inputActividadFechaIni.getDayOfMonth() + "/" + (inputActividadFechaIni.getMonth()+1) + "/" + inputActividadFechaIni.getYear() + " 00:00";
                        String actividadFechaFin = inputActividadFechaFin.getDayOfMonth() + "/" + (inputActividadFechaFin.getMonth()+1) + "/" + inputActividadFechaFin.getYear() + " 24:00";

                        Intent intent2 = new Intent(ActividadActivity.this, ChartActivityDetail.class);
                        intent2.putExtra("fechaIni", actividadFechaIni);
                        intent2.putExtra("fechaFin", actividadFechaFin);
                        startActivity(intent2);
                    }
                }
        );
        CustomTitleAndShow(builder,R.layout.dialog_graphic_actividad_title,R.id.textViewTitleGraphic,title,message);
    }

    private void showAlertForBackup(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setPositiveButton(getString(R.string.delete_activity_dialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton(getString(R.string.delete_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rbur.backup();
                    }
                }
        );
        CustomTitleAndShow(builder,R.layout.dialog_backup,R.id.textViewBackUpTitle,title,message);
    }

    private void showAlertForRestore(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        builder.setPositiveButton(getString(R.string.delete_activity_dialog_negative_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(getString(R.string.delete_activity_dialog_positive_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        rbur.restore();
                        realm.close();
                        datosRealm();
                        setTitle(getString(R.string.activity_activity_title) + ": " + aux.dateToString(new Date(), formatoSimple));
                        Constructor();
                    }
                }
        );

        CustomTitleAndShow(builder,R.layout.dialog_restore,R.id.textViewRestoreTitle,title,message);
    }

    private void CustomTitleAndShow(AlertDialog.Builder builder, int layout, int textView, String title, String message){
        Context mContext = builder.getContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View mView = mLayoutInflater.inflate(layout, null);
        if(title != null) {
            TextView mTextView = (TextView) mView.findViewById(textView);
            mTextView.setText(title);
            builder.setCustomTitle(mView);
        }
        if(message != null) builder.setMessage(message);
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
            case R.id.menuCategory:
                Intent intent = new Intent(ActividadActivity.this, CategoriaActivity.class);
                startActivity(intent);
                return true;
            case R.id.menuGrafica:
                Intent intent2 = new Intent(ActividadActivity.this, ChartActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menuRespaldo:
                showAlertForBackup(getString(R.string.activity_activity_dialog_backup_title), getString(R.string.activity_activity_dialog_backup_message));
                //grabar();
                return true;
            case R.id.menuRestaurar:
                showAlertForRestore(getString(R.string.activity_activity_dialog_restore_title), getString(R.string.activity_activity_dialog_restore_message));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Context mContext = getApplicationContext();
        LayoutInflater mLayoutInflater = LayoutInflater.from(mContext);
        View mView = mLayoutInflater.inflate(R.layout.context_menu_activity_title, null);
        TextView mTextView = (TextView) mView.findViewById(R.id.textViewTitleContext);
        mTextView.setText(actividades.get(info.position).getDescripcion());
        menu.setHeaderView(mView);
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

        String fechaIni = aux.dateToString(actividad.getFechaIni(), formatoComplejo);
        String fechaFin = aux.dateToString(actividad.getFechaFin(), formatoComplejo);
        String mensaje = aux.restarFechas(actividad.getFechaIni(), actividad.getFechaFin());

        toastTipos.toastMainShow(getString(R.string.activity_activity_toast_title) +"\n" +
                getString(R.string.activity_activity_toast_id) + ": " + actividad.getId() + "\n" +
                getString(R.string.activity_activity_toast_description) + ": " + actividad.getDescripcion() + "\n" +
                getString(R.string.activity_activity_toast_fechaIni) + ": " + fechaIni + "\n" +
                getString(R.string.activity_activity_toast_fechaFin) + ": " + fechaFin + "\n" +
                getString(R.string.activity_activity_toast_category) + ": " + categoriaNombre + "\n" +
                getString(R.string.activity_activity_toast_time) + ": " + mensaje,Toast.LENGTH_SHORT);
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

    public void grabar() {
        RealmResults<Actividad> actividadesBU;
        RealmResults<Categoria> categoriasBU;

        actividadesBU = realm
                .where(Actividad.class)
                .findAll();

        categoriasBU = realm
                .where(Categoria.class)
                .findAll();

        String fechaActual = aux.dateToString(new Date(), "ddMMyyyyHHmmss");
        try {
            File nuevaCarpeta = new File(Environment.getExternalStorageDirectory(), "Apptivities_Respaldo");
            if (!nuevaCarpeta.exists()) {
                nuevaCarpeta.mkdir();
            }
            try {
                File fileActividades = new File(nuevaCarpeta, "activitidades" + fechaActual + ".json");
                fileActividades.createNewFile();
                File fileCategorias = new File(nuevaCarpeta, "categorias" + fechaActual + ".json");
                fileCategorias.createNewFile();

                FileWriter fileWriter = new FileWriter(fileActividades);
                fileWriter.write("[");
                for(int i = 0; i<actividadesBU.size(); i++){
                    fileWriter.write("{");
                    fileWriter.write("\"id\": " + actividadesBU.get(i).getId() + ", ");
                    fileWriter.write("\"descripcion\": " + "\"" + actividadesBU.get(i).getDescripcion() + "\", ");
                    fileWriter.write("\"fechaIni\": " + "\"" + actividadesBU.get(i).getFechaIni() + "\", ");
                    fileWriter.write("\"fechaFin\": " + "\"" + actividadesBU.get(i).getFechaFin() + "\", ");
                    fileWriter.write("\"categoria\": " + actividadesBU.get(i).getCategoria());

                    if(i==actividadesBU.size()-1){
                        fileWriter.write("}");
                    }else{
                        fileWriter.write("},");
                    }
                }
                fileWriter.write("]");
                fileWriter.flush();
                fileWriter.close();

                FileWriter fileWriter2 = new FileWriter(fileCategorias);
                fileWriter2.write("[");
                for(int i = 0; i<categoriasBU.size(); i++){
                    fileWriter2.write("{");
                    fileWriter2.write("\"id\": " + categoriasBU.get(i).getId() + ", ");
                    fileWriter2.write("\"name\": " + "\"" + categoriasBU.get(i).getName() + "\", ");
                    fileWriter2.write("\"descripcion\": " + "\"" + categoriasBU.get(i).getDescripcion() + "\", ");
                    fileWriter2.write("\"createAt\": " + "\"" + categoriasBU.get(i).getCreateAt() + "\"");

                    if(i==categoriasBU.size()-1){
                        fileWriter2.write("}");
                    }else{
                        fileWriter2.write("},");
                    }
                }
                fileWriter2.write("]");
                fileWriter2.flush();
                fileWriter2.close();

                Toast.makeText(this,"Respaldo exitoso!",Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Log.e("Error", "ex: " + ex);
                Toast.makeText(this,"Error: " + ex,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("Error", "e: " + e);
            Toast.makeText(this,"Error: " + e,Toast.LENGTH_SHORT).show();
        }
    }
}
package com.cristhian.apptivities.Activitites;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.cristhian.apptivities.Adapters.ActividadAdapter;
import com.cristhian.apptivities.Adapters.CategoriaAdapter;
import com.cristhian.apptivities.Adapters.CategoriaSpinnerAdapter;
import com.cristhian.apptivities.Models.Actividad;
import com.cristhian.apptivities.Models.Categoria;
import com.cristhian.apptivities.R;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class CategoriaActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Categoria>>, AdapterView.OnItemClickListener {

    private Realm realm;
    private RealmResults<Categoria> categorias;
    private ListView listView;
    private CategoriaAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);
        datosRealm();
        setTitle(getString(R.string.activity_category_title));
        Constructor();
    }

    private void Constructor(){
        categorias.addChangeListener(this);

        adapter = new CategoriaAdapter(this,categorias,R.layout.list_view_categoria_item);
        listView = (ListView) findViewById(R.id.listViewCategoria);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fabAddCategoria);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingCategoria(
                        getString(R.string.new_category_dialog_title),
                        getString(R.string.new_category_dialog_message));
            }
        });

        scrollMyListViewToBottom();
        registerForContextMenu(listView);
    }

    private void datosRealm(){
        realm = Realm.getDefaultInstance();
        categorias = realm
                .where(Categoria.class)
                .findAll();
    }


    private void createNewCategory(String name, String descripcion) {
        realm.beginTransaction();
        Categoria categoria = new Categoria(name, descripcion);
        realm.copyToRealm(categoria);
        realm.commitTransaction();
        scrollMyListViewToBottom();
    }

    private void editCategory(String name, String descripcion, Categoria categoria){
        realm.beginTransaction();
        categoria.setName(name);
        categoria.setDescripcion(descripcion);
        realm.copyToRealmOrUpdate(categoria);
        realm.commitTransaction();
    }

    private void showAlertForCreatingCategoria(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_categoria, null);
        builder.setView(viewInflated);

        final EditText inputName = (EditText) viewInflated.findViewById(R.id.editTextNewCategoriaName);
        final EditText inputDescripcion = (EditText) viewInflated.findViewById(R.id.editTextNewCategoriaDescripcion);

        builder.setPositiveButton(getString(R.string.new_category_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoriaName = inputName.getText().toString().trim();
                String categoriaDescripcion = inputDescripcion.getText().toString().trim();

                if(categoriaName.length()>0){
                    createNewCategory(categoriaName, categoriaDescripcion);
                }else{
                    Toast.makeText(getApplicationContext(),getString(R.string.new_category_dialog_empty_values_message),Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForEditingCategoria(String title, String message, final Categoria categoria){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_categoria, null);
        builder.setView(viewInflated);

        final EditText inputCategoriaName = (EditText) viewInflated.findViewById(R.id.editTextNewCategoriaName);
        final EditText inputCategoriaDescripcion = (EditText) viewInflated.findViewById(R.id.editTextNewCategoriaDescripcion);

        inputCategoriaName.setText(categoria.getName());
        inputCategoriaDescripcion.setText(categoria.getDescripcion());

        builder.setPositiveButton(getString(R.string.edit_category_dialog_positive_button), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String categoriaName = inputCategoriaName.getText().toString().trim();
                String categoriaDescripcion = inputCategoriaDescripcion.getText().toString().trim();

                if(categoriaName.length()==0) {
                    Toast.makeText(getApplicationContext(), getString(R.string.edit_category_dialog_empty_values_message), Toast.LENGTH_LONG).show();
                }else{
                    editCategory(categoriaName,categoriaDescripcion,categoria);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_categoria_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(categorias.get(info.position).getName());
        getMenuInflater().inflate(R.menu.context_menu_categoria_activity, menu);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.edit_categoria:
                showAlertForEditingCategoria(
                        getString(R.string.edit_category_dialog_title),
                        getString(R.string.edit_category_dialog_message),
                        categorias.get(info.position));
                return true;
            default:
                // Prueba de codigo actualizado en temas 2
                return super.onContextItemSelected(item);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuActividad:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onChange(RealmResults<Categoria> element) {
        adapter.notifyDataSetChanged();
    }
}

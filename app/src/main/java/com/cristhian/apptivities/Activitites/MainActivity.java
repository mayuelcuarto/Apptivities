package com.cristhian.apptivities.Activitites;

import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cristhian.apptivities.Adapters.UsuarioAdapter;
import com.cristhian.apptivities.Models.Usuario;
import com.cristhian.apptivities.R;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Usuario>>, AdapterView.OnItemClickListener {

    private Realm realm;
    private RealmResults<Usuario> usuarios;
    private ListView listView;
    private UsuarioAdapter adapter;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Db realm
        realm = Realm.getDefaultInstance();
        usuarios = realm.where(Usuario.class).findAll();
        usuarios.addChangeListener(this);

        adapter = new UsuarioAdapter(this,usuarios,R.layout.list_view_usuario_item);
        listView = (ListView) findViewById(R.id.listViewUsuario);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        fab = (FloatingActionButton) findViewById(R.id.fabAddUsuario);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertForCreatingBoard("Add New User","Type a name, a last name and a role for your new user");
            }
        });

        registerForContextMenu(listView);
    }

    private void createNewUsuario(String nombres, String apellidos, String cargo) {
        realm.beginTransaction();
        Usuario usuario = new Usuario(nombres, apellidos, cargo);
        realm.copyToRealm(usuario);
        realm.commitTransaction();
    }

    private void editUsuario(String newNombres, String newApellidos, String newCargo, Usuario usuario){
        realm.beginTransaction();
        usuario.setNombres(newNombres);
        usuario.setApellidos(newApellidos);
        usuario.setCargo(newCargo);
        realm.copyToRealmOrUpdate(usuario);
        realm.commitTransaction();
    }

    private void deleteBoard(Usuario usuario){
        realm.beginTransaction();
        usuario.deleteFromRealm();
        realm.commitTransaction();
    }

    private void showAlertForCreatingBoard(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_usuario, null);
        builder.setView(viewInflated);

        final EditText inputNombres = (EditText) viewInflated.findViewById(R.id.editTextNewUsuarioNombres);
        final EditText inputApellidos = (EditText) viewInflated.findViewById(R.id.editTextNewUsuarioApellidos);
        final EditText inputCargo = (EditText) viewInflated.findViewById(R.id.editTextNewUsuarioCargo);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String usuarioNombres = inputNombres.getText().toString().trim();
                String usuarioApellidos = inputApellidos.getText().toString().trim();
                String usuarioCargo = inputCargo.getText().toString().trim();
                if(usuarioNombres.length()>0 && usuarioApellidos.length()>0 && usuarioCargo.length()>0){
                    createNewUsuario(usuarioNombres,usuarioApellidos, usuarioCargo);
                }else{
                    Toast.makeText(getApplicationContext(),"The name, last name and role is required to create a new User",Toast.LENGTH_LONG).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAlertForEditingBoard(String title, String message, final Usuario usuario){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if(title != null) builder.setTitle(title);
        if(message != null) builder.setMessage(message);

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_create_usuario, null);
        builder.setView(viewInflated);

        final EditText inputUsuarioNombres = (EditText) viewInflated.findViewById(R.id.editTextNewUsuarioNombres);
        final EditText inputUsuarioApellidos = (EditText) viewInflated.findViewById(R.id.editTextNewUsuarioApellidos);
        final EditText inputUsuarioCargo = (EditText) viewInflated.findViewById(R.id.editTextNewUsuarioCargo);
        inputUsuarioNombres.setText(usuario.getNombres());
        inputUsuarioApellidos.setText(usuario.getApellidos());
        inputUsuarioCargo.setText(usuario.getCargo());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String usuarioNombres = inputUsuarioNombres.getText().toString().trim();
                String usuarioApellidos = inputUsuarioApellidos.getText().toString().trim();
                String usuarioCargo = inputUsuarioCargo.getText().toString().trim();
                if(usuarioNombres.length()==0 || usuarioApellidos.length()==0 || usuarioCargo.length()==0){
                    Toast.makeText(getApplicationContext(),"The name, last name and role is required to edit the current Board",Toast.LENGTH_LONG).show();
                } else if(usuarioNombres.equals(usuario.getNombres()) && usuarioApellidos.equals(usuario.getApellidos()) && usuarioCargo.equals(usuario.getCargo())){
                    Toast.makeText(getApplicationContext(),"The data is the same than it was before",Toast.LENGTH_LONG).show();
                }  else{
                    editUsuario(usuarioNombres,usuarioApellidos,usuarioCargo,usuario);
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /* Events */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_usuario_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all:
                realm.beginTransaction();
                realm.deleteAll();
                realm.commitTransaction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        menu.setHeaderTitle(usuarios.get(info.position).getApellidos() + ", " + usuarios.get(info.position).getNombres());
        getMenuInflater().inflate(R.menu.context_menu_usuario_activity, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case R.id.delete_board:
                deleteBoard(usuarios.get(info.position));
                return true;
            case R.id.edit_board:
                showAlertForEditingBoard("Edit Board", "Change the name of the board", usuarios.get(info.position));
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Usuario usuario = (Usuario) parent.getItemAtPosition(position);
        Toast.makeText(this, "Datos de Usuario\n" +
                             "ID: " + usuario.getId() + "\n" +
                             "Nombres: " + usuario.getNombres() + "\n" +
                             "Apellidos: " + usuario.getApellidos() + "\n" +
                             "Cargo: " + usuario.getCargo(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onChange(RealmResults<Usuario> element) {
        adapter.notifyDataSetChanged();
    }
}
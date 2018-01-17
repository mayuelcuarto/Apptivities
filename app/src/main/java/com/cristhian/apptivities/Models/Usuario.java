package com.cristhian.apptivities.Models;

import com.cristhian.apptivities.Application.App;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Cristhian on 26-12-2017.
 */

public class Usuario extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String nombres;
    @Required
    private String apellidos;
    @Required
    private String cargo;

    public Usuario() {
    }

    public Usuario(String nombres, String apellidos, String cargo) {
        this.id = App.UsuarioID.incrementAndGet();
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.cargo = cargo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}

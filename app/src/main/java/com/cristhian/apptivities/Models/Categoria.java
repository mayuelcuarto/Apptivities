package com.cristhian.apptivities.Models;

import com.cristhian.apptivities.Application.App;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Cristhian on 05-01-2018.
 */

public class Categoria extends RealmObject{
    @PrimaryKey
    private int id;
    @Required
    private String name;
    private String descripcion;
    @Required
    private Date createAt;

    public Categoria(){

    }

    public Categoria(String name, String descripcion) {
        this.id = App.CategoriaID.incrementAndGet();
        this.name = name;
        this.descripcion = descripcion;
        this.createAt = new Date();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getCreateAt() {
        return createAt;
    }
}
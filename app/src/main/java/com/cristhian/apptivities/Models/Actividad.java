package com.cristhian.apptivities.Models;

import com.cristhian.apptivities.Application.App;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * Created by Cristhian on 26-12-2017.
 */

public class Actividad extends RealmObject {
    @PrimaryKey
    private int id;
    @Required
    private String descripcion;
    @Required
    private Date fechaIni;
    @Required
    private Date fechaFin;
    private long categoria;
    public Actividad() {
    }

    public Actividad(String descripcion, Date fechaIni, Date fechaFin, long categoria) {
        this.id = App.ActividadID.incrementAndGet();
        this.descripcion = descripcion;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public long getCategoria() {
        return categoria;
    }

    public void setCategoria(long categoria) {
        this.categoria = categoria;
    }
}

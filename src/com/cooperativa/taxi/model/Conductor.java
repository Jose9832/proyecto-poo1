package com.cooperativa.taxi.model;

import java.io.Serializable;

public class Conductor implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String nombre;
    private final Vehiculo vehiculo;
    private boolean disponible;

    public Conductor(String nombre, Vehiculo vehiculo) {
        this.nombre = nombre;
        this.vehiculo = vehiculo;
        this.disponible = true;
    }

    public String getNombre() {
        return nombre;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void asignar() {
        disponible = false;
    }

    public void liberar() {
        disponible = true;
    }
}

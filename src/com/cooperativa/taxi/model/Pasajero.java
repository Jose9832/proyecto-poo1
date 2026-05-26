package com.cooperativa.taxi.model;

import java.io.Serializable;

import com.cooperativa.taxi.exception.DatosInvalidosException;

public class Pasajero implements Serializable{
    private static final long serialVersionUID = 1L;
    private final String nombre;
    private final String direccion;
    private final String telefono;

    public Pasajero(String nombre, String direccion, String telefono) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException("El nombre del pasajero es obligatorio.");
        }
        if (direccion == null || direccion.isBlank()) {
            throw new DatosInvalidosException("La direccion del pasajero es obligatoria.");
        }
        this.nombre = nombre.trim();
        this.direccion = direccion.trim();
        this.telefono = telefono == null ? "" : telefono.trim();
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }
}

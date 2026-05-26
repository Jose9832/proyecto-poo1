package com.cooperativa.taxi.model;

import java.io.Serializable;

public class Vehiculo implements Serializable{
    private static final long serialVersionUID = 1L;

    private final String placa;
    private final String modelo;
    private final TipoServicio tipoSoportado;

    public Vehiculo(String placa, String modelo, TipoServicio tipoSoportado) {
        this.placa = placa;
        this.modelo = modelo;
        this.tipoSoportado = tipoSoportado;
    }

    public String getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    public TipoServicio getTipoSoportado() {
        return tipoSoportado;
    }
}

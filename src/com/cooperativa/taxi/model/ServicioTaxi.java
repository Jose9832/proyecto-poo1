package com.cooperativa.taxi.model;

import java.io.Serializable;

public abstract class ServicioTaxi implements Serializable{
    private static final long serialVersionUID = 1L;
    private final TipoServicio tipoServicio;

    protected ServicioTaxi(TipoServicio tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public TipoServicio getTipoServicio() {
        return tipoServicio;
    }

    public abstract double factorTarifa();

    public abstract String descripcion();
}

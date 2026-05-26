package com.cooperativa.taxi.model;

public abstract class ServicioTaxi {
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

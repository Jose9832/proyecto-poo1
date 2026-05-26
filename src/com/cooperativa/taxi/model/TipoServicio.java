package com.cooperativa.taxi.model;

public enum TipoServicio {
    ESTANDAR("Taxi estandar"),
    COMPARTIDO("Taxi compartido"),
    CARGA("Taxi de carga"),
    MASCOTAS("Taxi mascotas");


    private final String nombre;

    TipoServicio(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}

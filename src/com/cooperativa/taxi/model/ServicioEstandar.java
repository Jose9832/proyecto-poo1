package com.cooperativa.taxi.model;

public class ServicioEstandar extends ServicioTaxi {
    public ServicioEstandar() {
        super(TipoServicio.ESTANDAR);
    }

    @Override
    public double factorTarifa() {
        return 1.0;
    }

    @Override
    public String descripcion() {
        return "Servicio individual urbano para un pasajero o grupo pequeno.";
    }
}

package com.cooperativa.taxi.model;

public class ServicioCompartido extends ServicioTaxi {
    public ServicioCompartido() {
        super(TipoServicio.COMPARTIDO);
    }

    @Override
    public double factorTarifa() {
        return 0.75;
    }

    @Override
    public String descripcion() {
        return "Servicio de ruta compartida con descuento por ocupacion.";
    }
}

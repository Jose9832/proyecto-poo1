package com.cooperativa.taxi.model;

public class ServicioCarga extends ServicioTaxi {
    public ServicioCarga() {
        super(TipoServicio.CARGA);
    }

    @Override
    public double factorTarifa() {
        return 1.35;
    }

    @Override
    public String descripcion() {
        return "Servicio para transportar paquetes o carga ligera.";
    }
}

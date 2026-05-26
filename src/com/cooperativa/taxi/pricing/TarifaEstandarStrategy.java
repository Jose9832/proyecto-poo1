package com.cooperativa.taxi.pricing;

import com.cooperativa.taxi.exception.DatosInvalidosException;
import com.cooperativa.taxi.model.ServicioTaxi;

public class TarifaEstandarStrategy implements TarifaStrategy {
    private static final double TARIFA_BASE = 5000;
    private static final double COSTO_POR_KM = 2500;

    @Override
    public double calcular(ServicioTaxi servicio, double distanciaKm) {
        if (distanciaKm <= 0) {
            throw new DatosInvalidosException("La distancia debe ser mayor que cero.");
        }
        return (TARIFA_BASE + (COSTO_POR_KM * distanciaKm)) * servicio.factorTarifa();
    }
}

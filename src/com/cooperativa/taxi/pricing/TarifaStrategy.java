package com.cooperativa.taxi.pricing;

import com.cooperativa.taxi.model.ServicioTaxi;

public interface TarifaStrategy {
    double calcular(ServicioTaxi servicio, double distanciaKm);
}

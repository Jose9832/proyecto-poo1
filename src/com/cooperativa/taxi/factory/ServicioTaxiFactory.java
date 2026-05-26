package com.cooperativa.taxi.factory;

import com.cooperativa.taxi.model.ServicioCarga;
import com.cooperativa.taxi.model.ServicioEstandar;
import com.cooperativa.taxi.model.ServicioMascotas;
import com.cooperativa.taxi.model.ServicioTaxi;
import com.cooperativa.taxi.model.TipoServicio;

public class ServicioTaxiFactory {
    public ServicioTaxi crear(TipoServicio tipoServicio) {
        if (tipoServicio == null) {
            throw new IllegalArgumentException("TipoServicio no puede ser null");
        }

        return switch (tipoServicio) {
            case ESTANDAR -> new ServicioEstandar();
            case CARGA -> new ServicioCarga();
            case MASCOTAS -> new ServicioMascotas();
            default -> throw new IllegalArgumentException("Tipo de servicio no soportado: " + tipoServicio);
        };
    }
}

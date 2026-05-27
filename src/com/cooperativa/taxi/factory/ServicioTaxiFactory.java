package com.cooperativa.taxi.factory;

import com.cooperativa.taxi.model.ServicioCarga;
import com.cooperativa.taxi.model.ServicioEstandar;
import com.cooperativa.taxi.model.ServicioMascotas;
import com.cooperativa.taxi.model.ServicioTaxi;
import com.cooperativa.taxi.model.TipoServicio;

public class ServicioTaxiFactory {
    public ServicioTaxi crear(TipoServicio tipo) {
        if (tipo == null) {
            throw new IllegalArgumentException("TipoServicio no puede ser null");
        }

        switch (tipo) {
            case ESTANDAR:
                return new ServicioEstandar();
            case CARGA:
                return new ServicioCarga();
            case MASCOTAS:
                return new ServicioMascotas();
            default:
                throw new IllegalArgumentException("Tipo de servicio no soportado.");
        }
    }
}

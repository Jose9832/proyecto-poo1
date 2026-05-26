package com.cooperativa.taxi.factory;

import com.cooperativa.taxi.model.ServicioCarga;
import com.cooperativa.taxi.model.ServicioCompartido;
import com.cooperativa.taxi.model.ServicioEstandar;
import com.cooperativa.taxi.model.ServicioTaxi;
import com.cooperativa.taxi.model.TipoServicio;

public class ServicioTaxiFactory {
    public ServicioTaxi crear(TipoServicio tipoServicio) {
        return switch (tipoServicio) {
            case ESTANDAR -> new ServicioEstandar();
            case COMPARTIDO -> new ServicioCompartido();
            case CARGA -> new ServicioCarga();
        };
    }
}

package com.cooperativa.taxi.model;

import java.io.Serializable;

public class ServicioMascotas extends ServicioTaxi implements Serializable {
    private static final long serialVersionUID = 1L;

    public ServicioMascotas() {
        // Llama al constructor de la clase padre pasándole el Enum correspondiente
        super(TipoServicio.MASCOTAS); 
    }

    @Override
    public double factorTarifa() {
        return 1.25; // Incremento del 25% por ser pet-friendly
    }

    @Override
    public String descripcion() {
        return "Servicio Pet-Friendly para transporte seguro de mascotas";
    }
}
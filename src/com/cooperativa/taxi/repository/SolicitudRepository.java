package com.cooperativa.taxi.repository;

import com.cooperativa.taxi.model.Solicitud;

import java.util.List;

public interface SolicitudRepository {
    List<Solicitud> cargar();

    void guardar(List<Solicitud> solicitudes);
}

package com.cooperativa.taxi.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Solicitud {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int id;
    private final Pasajero pasajero;
    private final ServicioTaxi servicio;
    private final double distanciaKm;
    private final LocalDateTime fechaCreacion;
    private EstadoSolicitud estado;
    private Conductor conductor;
    private double costo;

    public Solicitud(int id, Pasajero pasajero, ServicioTaxi servicio, double distanciaKm) {
        this.id = id;
        this.pasajero = pasajero;
        this.servicio = servicio;
        this.distanciaKm = distanciaKm;
        this.fechaCreacion = LocalDateTime.now();
        this.estado = EstadoSolicitud.EN_ESPERA;
    }

    public Solicitud(int id, Pasajero pasajero, ServicioTaxi servicio, double distanciaKm,
                     LocalDateTime fechaCreacion, EstadoSolicitud estado, Conductor conductor, double costo) {
        this.id = id;
        this.pasajero = pasajero;
        this.servicio = servicio;
        this.distanciaKm = distanciaKm;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.conductor = conductor;
        this.costo = costo;
    }

    public int getId() {
        return id;
    }

    public Pasajero getPasajero() {
        return pasajero;
    }

    public ServicioTaxi getServicio() {
        return servicio;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoSolicitud getEstado() {
        return estado;
    }

    public Conductor getConductor() {
        return conductor;
    }

    public double getCosto() {
        return costo;
    }

    public void asignar(Conductor conductor, double costo) {
        this.conductor = conductor;
        this.costo = costo;
        this.estado = EstadoSolicitud.ASIGNADA;
    }

    public void reemplazarConductor(Conductor conductor) {
        this.conductor = conductor;
    }

    public void finalizar() {
        this.estado = EstadoSolicitud.FINALIZADA;
        if (conductor != null) {
            conductor.liberar();
        }
    }

    public void cancelar() {
        this.estado = EstadoSolicitud.CANCELADA;
    }

    public String resumen() {
        String conductorTexto = conductor == null ? "Sin asignar" : conductor.getNombre();
        String placa = conductor == null ? "-" : conductor.getVehiculo().getPlaca();
        return String.format("#%d | %s | %s | %.1f km | %s | Conductor: %s | Placa: %s | $%,.0f",
                id,
                pasajero.getNombre(),
                servicio.getTipoServicio().getNombre(),
                distanciaKm,
                estado,
                conductorTexto,
                placa,
                costo);
    }

    public String toCsv() {
        String conductorTexto = conductor == null ? "" : conductor.getNombre();
        String placa = conductor == null ? "" : conductor.getVehiculo().getPlaca();
        return String.join(";",
                String.valueOf(id),
                fechaCreacion.format(FORMATO),
                pasajero.getNombre(),
                pasajero.getDireccion(),
                pasajero.getTelefono(),
                servicio.getTipoServicio().name(),
                String.valueOf(distanciaKm),
                estado.name(),
                conductorTexto,
                placa,
                String.format("%.0f", costo));
    }
}

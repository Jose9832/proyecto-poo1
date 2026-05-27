package com.cooperativa.taxi.app;

import com.cooperativa.taxi.exception.ConductorNoDisponibleException;
import com.cooperativa.taxi.exception.RutaBloqueadaException;
import com.cooperativa.taxi.exception.SolicitudNoEncontradaException;
import com.cooperativa.taxi.factory.ServicioTaxiFactory;
import com.cooperativa.taxi.model.Conductor;
import com.cooperativa.taxi.model.EstadoSolicitud;
import com.cooperativa.taxi.model.Pasajero;
import com.cooperativa.taxi.model.ServicioTaxi;
import com.cooperativa.taxi.model.Solicitud;
import com.cooperativa.taxi.model.TipoServicio;
import com.cooperativa.taxi.model.Vehiculo;
import com.cooperativa.taxi.pricing.TarifaStrategy;
import com.cooperativa.taxi.repository.SolicitudRepository;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;

public class GestorSolicitudes {
    private final ServicioTaxiFactory factory;
    private final TarifaStrategy tarifaStrategy;
    private final SolicitudRepository repository;
    private final Queue<Solicitud> colaEspera;
    private final List<Solicitud> historial;
    private final List<Conductor> conductores;
    private int consecutivo;

    public GestorSolicitudes(ServicioTaxiFactory factory, TarifaStrategy tarifaStrategy, SolicitudRepository repository) {
        this.factory = factory;
        this.tarifaStrategy = tarifaStrategy;
        this.repository = repository;
        this.colaEspera = new ArrayDeque<>();
        this.historial = new ArrayList<>();
        this.conductores = new ArrayList<>();
        this.consecutivo = 1;
    }

    public void cargarDatosIniciales() {
        conductores.add(new Conductor("Laura Perez", new Vehiculo("TAX-101", "Kia Picanto", TipoServicio.ESTANDAR)));
        conductores.add(new Conductor("Carlos Ruiz", new Vehiculo("TAX-202", "Hyundai i10", TipoServicio.MASCOTAS)));
        conductores.add(new Conductor("Marta Diaz", new Vehiculo("CAR-303", "Renault Kangoo", TipoServicio.CARGA)));
        conductores.add(new Conductor("Goku fase 3", new Vehiculo("TAX-107", "Mazda 3", TipoServicio.MASCOTAS)));
        conductores.add(new Conductor("Andres Vega", new Vehiculo("TAX-404", "Chevrolet Spark", TipoServicio.ESTANDAR)));
        conductores.add(new Conductor("Fernando", new Vehiculo("TAX-987", "Camaro 2018", TipoServicio.CARGA)));
        conductores.add(new Conductor("Felipe ronaldo", new Vehiculo("TAX-745", "Toyota corolla", TipoServicio.CARGA)));
        List<Solicitud> solicitudesGuardadas = repository.cargar();
        historial.addAll(solicitudesGuardadas);
        solicitudesGuardadas.stream()
                .filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.EN_ESPERA)
                .forEach(colaEspera::add);
        solicitudesGuardadas.stream()
                .filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.ASIGNADA)
                .forEach(this::restaurarConductorAsignado);
        consecutivo = historial.stream()
                .map(Solicitud::getId)
                .max(Integer::compareTo)
                .orElse(0) + 1;
    }

    public Solicitud registrarSolicitud(String nombre, String direccion, String telefono, TipoServicio tipo, double distanciaKm) throws RutaBloqueadaException {
        
        // Simulación de cierre vial (requisito del proyecto)
        String direccionMinuscula = direccion.toLowerCase();
        if (direccionMinuscula.contains("calle 30")
                || direccionMinuscula.contains("centro")
                || direccionMinuscula.contains("libano")
                || direccionMinuscula.contains("gaira")
                || direccionMinuscula.contains("bahia concha")) {
            throw new RutaBloqueadaException("Servicio denegado: La ruta hacia/desde '" + direccion + "' se encuentra bloqueada.");
        }

        Pasajero pasajero = new Pasajero(nombre, direccion, telefono);
        ServicioTaxi servicio = factory.crear(tipo);
        Solicitud solicitud = new Solicitud(consecutivo++, pasajero, servicio, distanciaKm);
        colaEspera.add(solicitud);
        historial.add(solicitud);
        guardar();
        return solicitud;
    }

    public List<Solicitud> listarEnEspera() {
        return colaEspera.stream()
                .filter(solicitud -> solicitud.getEstado() == EstadoSolicitud.EN_ESPERA)
                .collect(Collectors.toList());
    }

    public List<Solicitud> listarHistorial() {
        return historial.stream()
                .sorted(Comparator.comparing(Solicitud::getId))
                .collect(Collectors.toList());
    }

    public Solicitud atenderSiguiente() {
        Solicitud solicitud = obtenerSiguientePendiente();
        Conductor conductor = buscarConductor(solicitud.getServicio().getTipoServicio())
                .orElseThrow(() -> new ConductorNoDisponibleException("No hay conductor disponible para "
                        + solicitud.getServicio().getTipoServicio().getNombre() + "."));
        conductor.asignar();
        double costo = tarifaStrategy.calcular(solicitud.getServicio(), solicitud.getDistanciaKm());
        solicitud.asignar(conductor, costo);
        guardar();
        return solicitud;
    }

    public void finalizarSolicitud(int id) {
        Solicitud solicitud = buscarPorId(id);
        if (solicitud.getEstado() != EstadoSolicitud.ASIGNADA) {
            throw new SolicitudNoEncontradaException("Solo se pueden finalizar solicitudes asignadas.");
        }
        solicitud.finalizar();
        guardar();
    }

    public void cancelarSolicitud(int id) {
        Solicitud solicitud = buscarPorId(id);
        if (solicitud.getEstado() != EstadoSolicitud.EN_ESPERA) {
            throw new SolicitudNoEncontradaException("Solo se pueden cancelar solicitudes en espera.");
        }
        solicitud.cancelar();
        colaEspera.removeIf(item -> item.getId() == id);
        guardar();
    }

    public List<Conductor> listarConductores() {
        return List.copyOf(conductores);
    }

    private Solicitud obtenerSiguientePendiente() {
        while (!colaEspera.isEmpty()) {
            Solicitud solicitud = colaEspera.poll();
            if (solicitud.getEstado() == EstadoSolicitud.EN_ESPERA) {
                return solicitud;
            }
        }
        throw new SolicitudNoEncontradaException("No hay solicitudes en espera.");
    }

    private Optional<Conductor> buscarConductor(TipoServicio tipo) {
        return conductores.stream()
                .filter(Conductor::isDisponible)
                .filter(conductor -> conductor.getVehiculo().getTipoSoportado() == tipo)
                .findFirst();
    }

    private void restaurarConductorAsignado(Solicitud solicitud) {
        if (solicitud.getConductor() == null) {
            return;
        }
        conductores.stream()
                .filter(conductor -> conductor.getVehiculo().getPlaca().equalsIgnoreCase(
                        solicitud.getConductor().getVehiculo().getPlaca()))
                .findFirst()
                .ifPresent(conductor -> {
                    conductor.asignar();
                    solicitud.reemplazarConductor(conductor);
                });
    }

    private Solicitud buscarPorId(int id) {
        return historial.stream()
                .filter(solicitud -> solicitud.getId() == id)
                .findFirst()
                .orElseThrow(() -> new SolicitudNoEncontradaException("No existe la solicitud #" + id + "."));
    }

    private void guardar() {
        repository.guardar(historial);
    }
}

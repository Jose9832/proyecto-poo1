package com.cooperativa.taxi.repository;

import com.cooperativa.taxi.factory.ServicioTaxiFactory;
import com.cooperativa.taxi.model.Conductor;
import com.cooperativa.taxi.model.EstadoSolicitud;
import com.cooperativa.taxi.model.Pasajero;
import com.cooperativa.taxi.model.ServicioTaxi;
import com.cooperativa.taxi.model.Solicitud;
import com.cooperativa.taxi.model.TipoServicio;
import com.cooperativa.taxi.model.Vehiculo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ArchivoSolicitudRepository implements SolicitudRepository {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Path archivo;
    private final ServicioTaxiFactory factory;

    public ArchivoSolicitudRepository(Path archivo) {
        this.archivo = archivo;
        this.factory = new ServicioTaxiFactory();
    }

    @Override
    public List<Solicitud> cargar() {
        if (!Files.exists(archivo)) {
            return List.of();
        }
        try {
            return Files.readAllLines(archivo).stream()
                    .skip(1)
                    .filter(linea -> !linea.isBlank())
                    .map(this::desdeCsv)
                    .toList();
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible cargar el historial: " + e.getMessage(), e);
        }
    }

    @Override
    public void guardar(List<Solicitud> solicitudes) {
        try {
            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }
            List<String> lineas = new ArrayList<>(solicitudes.stream()
                    .map(Solicitud::toCsv)
                    .toList());
            lineas.addFirst("id;fecha;pasajero;direccion;telefono;tipo;distanciaKm;estado;conductor;placa;costo");
            Files.write(archivo, lineas);
        } catch (IOException e) {
            throw new IllegalStateException("No fue posible guardar el historial: " + e.getMessage(), e);
        }
    }

    private Solicitud desdeCsv(String linea) {
        String[] campos = linea.split(";", -1);
        int id = Integer.parseInt(campos[0]);
        LocalDateTime fecha = LocalDateTime.parse(campos[1], FORMATO);
        Pasajero pasajero = new Pasajero(campos[2], campos[3], campos[4]);
        TipoServicio tipo = TipoServicio.valueOf(campos[5]);
        ServicioTaxi servicio = factory.crear(tipo);
        double distancia = Double.parseDouble(campos[6]);
        EstadoSolicitud estado = EstadoSolicitud.valueOf(campos[7]);
        Conductor conductor = campos[8].isBlank()
                ? null
                : new Conductor(campos[8], new Vehiculo(campos[9], "Recuperado del historial", tipo));
        double costo = campos[10].isBlank() ? 0 : Double.parseDouble(campos[10]);
        return new Solicitud(id, pasajero, servicio, distancia, fecha, estado, conductor, costo);
    }
}

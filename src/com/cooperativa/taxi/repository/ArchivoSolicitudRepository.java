package com.cooperativa.taxi.repository;

import com.cooperativa.taxi.factory.ServicioTaxiFactory;
import com.cooperativa.taxi.model.Conductor;
import com.cooperativa.taxi.model.EstadoSolicitud;
import com.cooperativa.taxi.model.Pasajero;
import com.cooperativa.taxi.model.ServicioTaxi;
import com.cooperativa.taxi.model.Solicitud;
import com.cooperativa.taxi.model.TipoServicio;
import com.cooperativa.taxi.model.Vehiculo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArchivoSolicitudRepository implements SolicitudRepository {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private final Path archivo;
    private final ServicioTaxiFactory factory;

    public ArchivoSolicitudRepository(Path archivo) {
        this.archivo = archivo;
        this.factory = new ServicioTaxiFactory();
    }

    // Cambiamos el .csv por .dat para indicar que son datos binarios serializados
    private static final String ARCHIVO = "data/historial_solicitudes.dat";

    public void guardarSolicitud(Solicitud solicitud) {
        List<Solicitud> historial = obtenerTodas();
        historial.add(solicitud);
        guardarTodo(historial);
    }

    private void guardarTodo(List<Solicitud> historial) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(historial);
        } catch (IOException e) {
            System.err.println("Error al serializar el historial: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public List<Solicitud> obtenerTodas() {
        File file = new File(ARCHIVO);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (List<Solicitud>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al deserializar el historial: " + e.getMessage());
            return new ArrayList<>();
        }
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
                    .collect(Collectors.toList());
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
            List<String> lineas = new ArrayList<>();
            lineas.add("id;fecha;pasajero;direccion;telefono;tipo;distanciaKm;estado;conductor;placa;costo");
            solicitudes.stream()
                    .map(Solicitud::toCsv)
                    .forEach(lineas::add);
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

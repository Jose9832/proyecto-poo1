package com.cooperativa.taxi.ui;

import com.cooperativa.taxi.app.GestorSolicitudes;
import com.cooperativa.taxi.exception.ConductorNoDisponibleException;
import com.cooperativa.taxi.exception.DatosInvalidosException;
import com.cooperativa.taxi.exception.RutaBloqueadaException;
import com.cooperativa.taxi.exception.SolicitudNoEncontradaException;
import com.cooperativa.taxi.model.Conductor;
import com.cooperativa.taxi.model.Solicitud;
import com.cooperativa.taxi.model.TipoServicio;

import java.util.Scanner;

public class Consola {
    private final GestorSolicitudes gestor;
    private final Scanner scanner;

    public Consola(GestorSolicitudes gestor) {
        this.gestor = gestor;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Seleccione una opcion: ");
            try {
                ejecutar(opcion);
            } catch (DatosInvalidosException | SolicitudNoEncontradaException | ConductorNoDisponibleException e) {
                System.out.println("Aviso: " + e.getMessage());
            } catch (RuntimeException e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarMenu() {
        System.out.println();
        System.out.println("=== Cooperativa de Taxis Multizona ===");
        System.out.println("1. Registrar solicitud");
        System.out.println("2. Ver solicitudes en espera");
        System.out.println("3. Atender siguiente solicitud");
        System.out.println("4. Finalizar solicitud");
        System.out.println("5. Cancelar solicitud en espera");
        System.out.println("6. Ver historial");
        System.out.println("7. Ver conductores");
        System.out.println("0. Salir");
    }

    private void ejecutar(int opcion) {
        switch (opcion) {
            case 1 -> registrarSolicitud();
            case 2 -> listarEspera();
            case 3 -> atenderSiguiente();
            case 4 -> finalizarSolicitud();
            case 5 -> cancelarSolicitud();
            case 6 -> listarHistorial();
            case 7 -> listarConductores();
            case 0 -> System.out.println("Hasta luego.");
            default -> System.out.println("Opcion no valida.");
        }
    }

    private void registrarSolicitud() {
        System.out.print("Nombre del pasajero: ");
        String nombre = scanner.nextLine();
        System.out.print("Direccion: ");
        String direccion = scanner.nextLine();
        System.out.print("Telefono: ");
        String telefono = scanner.nextLine();
        TipoServicio tipo = leerTipoServicio();
        double distancia = leerDouble("Distancia estimada en km: ");
        try {
            Solicitud solicitud = gestor.registrarSolicitud(nombre, direccion, telefono, tipo, distancia);
            System.out.println("Solicitud registrada: " + solicitud.resumen());
        } catch (DatosInvalidosException | ConductorNoDisponibleException | RutaBloqueadaException e) {
            System.out.println("Aviso: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarEspera() {
        var solicitudes = gestor.listarEnEspera();
        if (solicitudes.isEmpty()) {
            System.out.println("No hay solicitudes en espera.");
            return;
        }
        solicitudes.forEach(solicitud -> System.out.println(solicitud.resumen()));
    }

    private void atenderSiguiente() {
        try {
            Solicitud solicitud = gestor.atenderSiguiente();
            System.out.println("Solicitud asignada: " + solicitud.resumen());
        } catch (SolicitudNoEncontradaException | ConductorNoDisponibleException e) {
            System.out.println("Aviso: " + e.getMessage());
        }
    }

    private void finalizarSolicitud() {
        int id = leerEntero("Id de la solicitud a finalizar: ");
        try {
            gestor.finalizarSolicitud(id);
            System.out.println("Solicitud finalizada.");
        } catch (SolicitudNoEncontradaException e) {
            System.out.println("Aviso: " + e.getMessage());
        }
    }

    private void cancelarSolicitud() {
        int id = leerEntero("Id de la solicitud a cancelar: ");
        try {
            gestor.cancelarSolicitud(id);
            System.out.println("Solicitud cancelada.");
        } catch (SolicitudNoEncontradaException e) {
            System.out.println("Aviso: " + e.getMessage());
        }
    }

    private void listarHistorial() {
        var solicitudes = gestor.listarHistorial();
        if (solicitudes.isEmpty()) {
            System.out.println("No hay historial registrado.");
            return;
        }
        solicitudes.forEach(solicitud -> System.out.println(solicitud.resumen()));
    }

    private void listarConductores() {
        for (Conductor conductor : gestor.listarConductores()) {
            System.out.printf("%s | %s | %s | disponible: %s%n",
                    conductor.getNombre(),
                    conductor.getVehiculo().getPlaca(),
                    conductor.getVehiculo().getTipoSoportado().getNombre(),
                    conductor.isDisponible() ? "si" : "no");
        }
    }

    private TipoServicio leerTipoServicio() {
        System.out.println("Tipo de servicio:");
        System.out.println("1. Taxi estandar");
        System.out.println("2. Taxi de carga (Baul/Parrilla)");
        System.out.println("3. Taxi para mascotas"); // Se actualizó el menú
        int opcion = leerEntero("Seleccione tipo: ");
        return switch (opcion) {
            case 1 -> TipoServicio.ESTANDAR;
            case 2 -> TipoServicio.CARGA;
            case 3 -> TipoServicio.MASCOTAS; // Se actualizó el switch
            default -> throw new DatosInvalidosException("Tipo de servicio no valido.");
        };
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                double valor = Double.parseDouble(scanner.nextLine().replace(",", "."));
                if (valor <= 0) {
                    throw new DatosInvalidosException("La distancia debe ser mayor que cero.");
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero decimal valido.");
            }
        }
    }
}
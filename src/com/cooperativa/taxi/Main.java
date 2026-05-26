package com.cooperativa.taxi;

import com.cooperativa.taxi.app.GestorSolicitudes;
import com.cooperativa.taxi.factory.ServicioTaxiFactory;
import com.cooperativa.taxi.pricing.TarifaEstandarStrategy;
import com.cooperativa.taxi.repository.ArchivoSolicitudRepository;
import com.cooperativa.taxi.ui.Consola;
import com.cooperativa.taxi.ui.swing.VentanaPrincipal;

import javax.swing.SwingUtilities;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        Path archivoDatos = Path.of("data", "historial_solicitudes.csv");
        GestorSolicitudes gestor = new GestorSolicitudes(
                new ServicioTaxiFactory(),
                new TarifaEstandarStrategy(),
                new ArchivoSolicitudRepository(archivoDatos)
        );

        gestor.cargarDatosIniciales();
        if (args.length > 0 && "--console".equalsIgnoreCase(args[0])) {
            new Consola(gestor).iniciar();
            return;
        }

        SwingUtilities.invokeLater(() -> new VentanaPrincipal(gestor).setVisible(true));
    }
}

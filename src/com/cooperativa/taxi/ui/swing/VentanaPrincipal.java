package com.cooperativa.taxi.ui.swing;

import com.cooperativa.taxi.app.GestorSolicitudes;
import com.cooperativa.taxi.exception.ConductorNoDisponibleException;
import com.cooperativa.taxi.exception.DatosInvalidosException;
import com.cooperativa.taxi.exception.SolicitudNoEncontradaException;
import com.cooperativa.taxi.model.Conductor;
import com.cooperativa.taxi.model.Solicitud;
import com.cooperativa.taxi.model.TipoServicio;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.Locale;

public class VentanaPrincipal extends JFrame {
    private final GestorSolicitudes gestor;
    private final JTextField nombreField;
    private final JTextField direccionField;
    private final JTextField telefonoField;
    private final JTextField distanciaField;
    private final JComboBox<TipoServicio> tipoCombo;
    private final DefaultTableModel solicitudesModel;
    private final DefaultTableModel conductoresModel;
    private final JTable solicitudesTable;
    private final JLabel estadoLabel;
    private final NumberFormat monedaFormat;

    public VentanaPrincipal(GestorSolicitudes gestor) {
        this.gestor = gestor;
        this.nombreField = new JTextField();
        this.direccionField = new JTextField();
        this.telefonoField = new JTextField();
        this.distanciaField = new JTextField();
        this.tipoCombo = new JComboBox<>(TipoServicio.values());
        this.solicitudesModel = crearModelo("Id", "Pasajero", "Servicio", "Km", "Estado", "Conductor", "Placa", "Costo");
        this.conductoresModel = crearModelo("Conductor", "Placa", "Vehiculo", "Servicio", "Disponible");
        this.solicitudesTable = new JTable(solicitudesModel);
        this.estadoLabel = new JLabel("Sistema listo");
        this.monedaFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

        configurarVentana();
        refrescarTablas();
    }

    private void configurarVentana() {
        setTitle("Cooperativa de Taxis Multizona");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 660));
        setLocationRelativeTo(null);

        JPanel contenido = new JPanel(new BorderLayout(14, 14));
        contenido.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));
        contenido.setBackground(new Color(242, 245, 248));

        contenido.add(crearEncabezado(), BorderLayout.NORTH);
        contenido.add(crearCentro(), BorderLayout.CENTER);
        contenido.add(crearBarraEstado(), BorderLayout.SOUTH);

        setContentPane(contenido);
        pack();
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel titulo = new JLabel("Gestion de servicios de taxi");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titulo.setForeground(new Color(28, 41, 56));

        JLabel subtitulo = new JLabel("Registro, cola de espera, asignacion, cierre e historial");
        subtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitulo.setForeground(new Color(88, 101, 117));

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(subtitulo, BorderLayout.SOUTH);
        return panel;
    }

    private JSplitPane crearCentro() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, crearPanelRegistro(), crearPanelTablas());
        splitPane.setResizeWeight(0.28);
        splitPane.setBorder(null);
        splitPane.setOpaque(false);
        return splitPane;
    }

    private JPanel crearPanelRegistro() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(213, 220, 228)),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        agregarTituloSeccion(panel, gbc, "Nueva solicitud");
        agregarCampo(panel, gbc, "Nombre del pasajero", nombreField);
        agregarCampo(panel, gbc, "Direccion de recogida", direccionField);
        agregarCampo(panel, gbc, "Telefono", telefonoField);
        agregarCampo(panel, gbc, "Distancia estimada (km)", distanciaField);

        gbc.gridy++;
        panel.add(new JLabel("Tipo de servicio"), gbc);
        gbc.gridy++;
        tipoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(tipoCombo, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(16, 0, 6, 0);
        JButton registrarButton = crearBoton("Registrar solicitud");
        registrarButton.addActionListener(event -> registrarSolicitud());
        panel.add(registrarButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(6, 0, 6, 0);
        JButton limpiarButton = crearBotonSecundario("Limpiar formulario");
        limpiarButton.addActionListener(event -> limpiarFormulario());
        panel.add(limpiarButton, gbc);

        gbc.gridy++;
        gbc.weighty = 1;
        panel.add(new JLabel(), gbc);
        return panel;
    }

    private JPanel crearPanelTablas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel acciones = new JPanel(new GridBagLayout());
        acciones.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        JButton atenderButton = crearBoton("Atender siguiente");
        atenderButton.addActionListener(event -> atenderSiguiente());
        acciones.add(atenderButton, gbc);

        JButton finalizarButton = crearBotonSecundario("Finalizar seleccionada");
        finalizarButton.addActionListener(event -> finalizarSeleccionada());
        acciones.add(finalizarButton, gbc);

        JButton cancelarButton = crearBotonSecundario("Cancelar en espera");
        cancelarButton.addActionListener(event -> cancelarSeleccionada());
        acciones.add(cancelarButton, gbc);

        JButton refrescarButton = crearBotonSecundario("Refrescar");
        refrescarButton.addActionListener(event -> refrescarTablas());
        gbc.insets = new Insets(0, 0, 0, 0);
        acciones.add(refrescarButton, gbc);

        panel.add(acciones, BorderLayout.NORTH);
        panel.add(crearTablaSolicitudes(), BorderLayout.CENTER);
        panel.add(crearTablaConductores(), BorderLayout.SOUTH);
        return panel;
    }

    private JScrollPane crearTablaSolicitudes() {
        solicitudesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        solicitudesTable.setRowHeight(28);
        solicitudesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        solicitudesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(solicitudesTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Historial de solicitudes"));
        return scrollPane;
    }

    private JScrollPane crearTablaConductores() {
        JTable conductoresTable = new JTable(conductoresModel);
        conductoresTable.setRowHeight(26);
        conductoresTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        conductoresTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(conductoresTable);
        scrollPane.setPreferredSize(new Dimension(700, 170));
        scrollPane.setBorder(BorderFactory.createTitledBorder("Conductores"));
        return scrollPane;
    }

    private JPanel crearBarraEstado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 2, 0, 2));
        panel.setOpaque(false);
        estadoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        estadoLabel.setForeground(new Color(56, 68, 82));
        panel.add(estadoLabel, BorderLayout.CENTER);
        return panel;
    }

    private void agregarTituloSeccion(JPanel panel, GridBagConstraints gbc, String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Segoe UI", Font.BOLD, 18));
        label.setForeground(new Color(28, 41, 56));
        gbc.gridy++;
        panel.add(label, gbc);
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, String etiqueta, JTextField campo) {
        gbc.gridy++;
        JLabel label = new JLabel(etiqueta);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(label, gbc);

        gbc.gridy++;
        campo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campo.setMargin(new Insets(6, 8, 6, 8));
        panel.add(campo, gbc);
    }

    private JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(new Color(34, 111, 179));
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        return boton;
    }

    private JButton crearBotonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        boton.setFocusPainted(false);
        return boton;
    }

    private DefaultTableModel crearModelo(String... columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void registrarSolicitud() {
        try {
            double distancia = leerDistancia();
            Solicitud solicitud = gestor.registrarSolicitud(
                    nombreField.getText(),
                    direccionField.getText(),
                    telefonoField.getText(),
                    (TipoServicio) tipoCombo.getSelectedItem(),
                    distancia);
            limpiarFormulario();
            refrescarTablas();
            mostrarEstado("Solicitud #" + solicitud.getId() + " registrada en espera.");
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void atenderSiguiente() {
        try {
            Solicitud solicitud = gestor.atenderSiguiente();
            refrescarTablas();
            mostrarEstado("Solicitud #" + solicitud.getId() + " asignada a " + solicitud.getConductor().getNombre() + ".");
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void finalizarSeleccionada() {
        Integer id = obtenerIdSeleccionado();
        if (id == null) {
            return;
        }
        try {
            gestor.finalizarSolicitud(id);
            refrescarTablas();
            mostrarEstado("Solicitud #" + id + " finalizada.");
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void cancelarSeleccionada() {
        Integer id = obtenerIdSeleccionado();
        if (id == null) {
            return;
        }
        try {
            gestor.cancelarSolicitud(id);
            refrescarTablas();
            mostrarEstado("Solicitud #" + id + " cancelada.");
        } catch (RuntimeException e) {
            mostrarError(e);
        }
    }

    private void refrescarTablas() {
        solicitudesModel.setRowCount(0);
        for (Solicitud solicitud : gestor.listarHistorial()) {
            String conductor = solicitud.getConductor() == null ? "Sin asignar" : solicitud.getConductor().getNombre();
            String placa = solicitud.getConductor() == null ? "-" : solicitud.getConductor().getVehiculo().getPlaca();
            solicitudesModel.addRow(new Object[]{
                    solicitud.getId(),
                    solicitud.getPasajero().getNombre(),
                    solicitud.getServicio().getTipoServicio().getNombre(),
                    String.format(Locale.US, "%.1f", solicitud.getDistanciaKm()),
                    solicitud.getEstado(),
                    conductor,
                    placa,
                    monedaFormat.format(solicitud.getCosto())
            });
        }

        conductoresModel.setRowCount(0);
        for (Conductor conductor : gestor.listarConductores()) {
            conductoresModel.addRow(new Object[]{
                    conductor.getNombre(),
                    conductor.getVehiculo().getPlaca(),
                    conductor.getVehiculo().getModelo(),
                    conductor.getVehiculo().getTipoSoportado().getNombre(),
                    conductor.isDisponible() ? "Si" : "No"
            });
        }
    }

    private Integer obtenerIdSeleccionado() {
        int fila = solicitudesTable.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud de la tabla.", "Seleccion requerida",
                    JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        int modeloFila = solicitudesTable.convertRowIndexToModel(fila);
        return (Integer) solicitudesModel.getValueAt(modeloFila, 0);
    }

    private double leerDistancia() {
        String texto = distanciaField.getText().trim().replace(",", ".");
        if (texto.isEmpty()) {
            throw new DatosInvalidosException("La distancia es obligatoria.");
        }
        try {
            double distancia = Double.parseDouble(texto);
            if (distancia <= 0) {
                throw new DatosInvalidosException("La distancia debe ser mayor que cero.");
            }
            return distancia;
        } catch (NumberFormatException e) {
            throw new DatosInvalidosException("Ingrese una distancia valida. Ejemplo: 4.5");
        }
    }

    private void limpiarFormulario() {
        nombreField.setText("");
        direccionField.setText("");
        telefonoField.setText("");
        distanciaField.setText("");
        tipoCombo.setSelectedIndex(0);
        nombreField.requestFocus();
    }

    private void mostrarEstado(String mensaje) {
        estadoLabel.setText(mensaje);
    }

    private void mostrarError(RuntimeException e) {
        String mensaje = e.getMessage() == null ? "Operacion no completada." : e.getMessage();
        if (e instanceof DatosInvalidosException
                || e instanceof SolicitudNoEncontradaException
                || e instanceof ConductorNoDisponibleException) {
            JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        }
        mostrarEstado(mensaje);
    }
}

package proyecto.vista;

import proyecto.controlador.ControladorMedicamento;
import proyecto.modelo.Medicamento;
import proyecto.modelo.Insulina;
import proyecto.modelo.Paciente;
import proyecto.modelo.Recordatorio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GestionMedicamentosGUI extends JFrame {
    private final Paciente paciente;
    private final ControladorMedicamento controladorMed;
    private JComboBox<String> comboMedicamentos;
    private JTextArea medicamentosArea;
    private JTextArea historialArea;
    private JTextArea recordatoriosArea;
    private RecordatorioTimer recordatorioTimer;

    public GestionMedicamentosGUI(ControladorMedicamento controladorMed, Paciente paciente) {
        this.controladorMed = controladorMed;
        this.paciente = paciente;
        setTitle("Gestion de Medicamentos - " + paciente.getNombre());
        setSize(800, 600);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initComponents();
        actualizarMedicamentosArea();
        actualizarHistorialArea();
        actualizarRecordatoriosArea();

        recordatorioTimer = new RecordatorioTimer(paciente, this);
        recordatorioTimer.start();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });
        cargarComboMedicamentos();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JSplitPane splitNorte = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitNorte.setResizeWeight(0.5);
        splitNorte.setDividerSize(8);

        Font fuenteUI = new JLabel().getFont();

        medicamentosArea = new JTextArea(5, 40);
        medicamentosArea.setEditable(false);
        medicamentosArea.setFont(fuenteUI);
        JScrollPane scrollMeds = new JScrollPane(medicamentosArea);
        splitNorte.setTopComponent(scrollMeds);

        recordatoriosArea = new JTextArea(5, 40);
        recordatoriosArea.setEditable(false);
        recordatoriosArea.setFont(fuenteUI);
        JScrollPane scrollRecs = new JScrollPane(recordatoriosArea);
        splitNorte.setBottomComponent(scrollRecs);
        panel.add(splitNorte, BorderLayout.NORTH);

        historialArea = new JTextArea(10, 40);
        historialArea.setEditable(false);
        historialArea.setFont(fuenteUI);
        JScrollPane scrollHistorial = new JScrollPane(historialArea);
        scrollHistorial.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(scrollHistorial, BorderLayout.CENTER);

        JPanel panelSur = new JPanel();
        panelSur.setLayout(new BoxLayout(panelSur, BoxLayout.Y_AXIS));
        panelSur.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel panelFila1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelFila1.add(new JLabel("Seleccionar medicamento:"));
        comboMedicamentos = new JComboBox<>();
        panelFila1.add(comboMedicamentos);

        JButton btnTomar = new JButton("Tomar medicamento");
        panelFila1.add(btnTomar);

        JButton btnAgregar = new JButton("Agregar medicamento");
        panelFila1.add(btnAgregar);

        JPanel panelFila2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton btnRecordatorio = new JButton("Agregar recordatorio");
        panelFila2.add(btnRecordatorio);

        JButton btnVerificarRecs = new JButton("Verificar recordatorios");
        panelFila2.add(btnVerificarRecs);

        JButton btnRemover = new JButton("Remover medicamento");
        panelFila2.add(btnRemover);

        JButton btnCerrarSesion = new JButton("Cerrar Sesion");
        panelFila2.add(btnCerrarSesion);

        panelSur.add(panelFila1);
        panelSur.add(panelFila2);

        panel.add(panelSur, BorderLayout.SOUTH);
        add(panel);

        btnTomar.addActionListener(e -> tomarMedicamento());
        btnAgregar.addActionListener(e -> agregarMedicamentoDialog());
        btnRecordatorio.addActionListener(e -> agregarRecordatorioDialog());
        btnVerificarRecs.addActionListener(e -> verificarRecordatorios());
        btnRemover.addActionListener(e -> removerMedicamento());
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
    }

    private void cargarComboMedicamentos() {
        comboMedicamentos.removeAllItems();
        Object seleccionado = comboMedicamentos.getSelectedItem();
        //MEJORA: usar el metodo para que devuelta la lista directa, sin procesar texto
        List<String> nombres = controladorMed.listarNombresMedicamentos();
        if (nombres.isEmpty()) {
            comboMedicamentos.setEnabled(false);
            return;
        }
        comboMedicamentos.setEnabled(true);
        for (String nombre : nombres ) {
            comboMedicamentos.addItem(nombre);
        }
        if (seleccionado != null) {
            //se verifica si el medicamento seleccionado aun existe en la nueva lista
            if (nombres.contains(seleccionado)) {
                comboMedicamentos.setSelectedItem(seleccionado);
            }
        }
    }

    private void tomarMedicamento() {
        String nombreSeleccionado = (String) comboMedicamentos.getSelectedItem();
        if (nombreSeleccionado != null && !nombreSeleccionado.isEmpty()) {
            String msg = controladorMed.tomarMedicamento(nombreSeleccionado);
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            actualizarTodo();
            cargarComboMedicamentos();
        } else {
            JOptionPane.showMessageDialog(this, "Selecciona un medicamento", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void agregarMedicamentoDialog() {
        JPanel panelInput = new JPanel(new GridLayout(6, 2, 5, 5));
        panelInput.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField txtNombre = new JTextField();
        JTextField txtDosis = new JTextField();
        JTextField txtCantidad = new JTextField();
        JTextField txtFecha = new JTextField();
        JComboBox<String> comboTipo = new JComboBox<>(new String[]{"Medicamento", "Insulina"});
        JLabel lblGlucosa = new JLabel("Glucosa minima (solo para Insulina):");
        JTextField txtGlucosa = new JTextField("70.0");
        txtGlucosa.setEnabled(false);

        comboTipo.addActionListener(e -> txtGlucosa.setEnabled("Insulina".equals(comboTipo.getSelectedItem())));

        panelInput.add(new JLabel("Nombre:"));
        panelInput.add(txtNombre);
        panelInput.add(new JLabel("Dosis (mg/unidades):"));
        panelInput.add(txtDosis);
        panelInput.add(new JLabel("Cantidad disponible:"));
        panelInput.add(txtCantidad);
        panelInput.add(new JLabel("Fecha vencimiento (dd/MM/yyyy):"));
        panelInput.add(txtFecha);
        panelInput.add(new JLabel("Tipo:"));
        panelInput.add(comboTipo);
        panelInput.add(lblGlucosa);
        panelInput.add(txtGlucosa);

        int result = JOptionPane.showConfirmDialog(this, panelInput, "Agregar Medicamento", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            String nombre = txtNombre.getText().trim();
            if (nombre.isEmpty()) throw new IllegalArgumentException("Nombre requerido");

            int dosis = Integer.parseInt(txtDosis.getText().trim());
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            String fecha = txtFecha.getText().trim();
            if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
                throw new IllegalArgumentException("Formato de fecha invalido (dd/MM/yyyy)");
            }
            Medicamento m;
            if ("Insulina".equals(comboTipo.getSelectedItem())) {
                double glucMin = Double.parseDouble(txtGlucosa.getText().trim());
                m = new Insulina(nombre, dosis, cantidad, fecha, glucMin);
            } else {
                m = new Medicamento(nombre, dosis, cantidad, fecha);
            }
            String msg = controladorMed.agregarMedicamento(m);
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            actualizarTodo();
            cargarComboMedicamentos();
        } catch (Exception e) {
            System.err.println("Error en agregarMedicamentoDialog: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Datos invalidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarRecordatorioDialog() {
        String nombreMed = (String) comboMedicamentos.getSelectedItem();
        if (nombreMed == null || nombreMed.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primero selecciona un medicamento del ComboBox", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String horaStr = JOptionPane.showInputDialog(this, "Hora (HH:mm) para " + nombreMed + ":");
        if (horaStr == null || !horaStr.matches("\\d{2}:\\d{2}")) {
            if(horaStr != null)
                JOptionPane.showMessageDialog(this, "Formato de hora invalido (HH:mm)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalTime hora = LocalTime.parse(horaStr, DateTimeFormatter.ofPattern("HH:mm"));

        String freqStr = JOptionPane.showInputDialog(this, "Frecuencia en horas (ej 4):");
        int frecuencia;
        try {
            frecuencia = Integer.parseInt(freqStr);
            if (frecuencia <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Frecuencia invalida (numero >0)", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Medicamento med = null;
        for (Medicamento m : paciente.getListaMedicamentos()) {
            if (m.getNombre().equalsIgnoreCase(nombreMed.trim())) {
                med = m;
                break;
            }
        }
        if (med == null) {
            JOptionPane.showMessageDialog(this, "Medicamento '" + nombreMed + "' no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Recordatorio r = new Recordatorio(hora, frecuencia, med);
        String msg = controladorMed.agregarRecordatorio(r);
        JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
        actualizarRecordatoriosArea();
    }

    private void verificarRecordatorios() {
        List<String> activos = controladorMed.verificarRecordatoriosActivos();
        if (activos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay recordatorios activos en este momento", "Info", JOptionPane.INFORMATION_MESSAGE);
        } else {
            StringBuilder sb = new StringBuilder("Recordatorios activos:\n");
            for (String a : activos) {
                sb.append("- ").append(a).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Alertas Activas", JOptionPane.WARNING_MESSAGE);
        }
        actualizarRecordatoriosArea();
    }

    private void removerMedicamento() {
        String nombreSeleccionado = (String) comboMedicamentos.getSelectedItem();
        if (nombreSeleccionado == null || nombreSeleccionado.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Selecciona un medicamento para remover", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Estas seguro de que deseas eliminar '" + nombreSeleccionado + "'?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            String msg = controladorMed.removerMedicamento(nombreSeleccionado.trim());
            JOptionPane.showMessageDialog(this, msg, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            actualizarTodo();
            cargarComboMedicamentos();
        }
    }

    //CORRECCION: modificacion de nombre de metodos
    public void actualizarMedicamentosArea() {
        medicamentosArea.setText(controladorMed.generarReporteMedicamentos());
    }

    public void actualizarHistorialArea() {
        historialArea.setText(controladorMed.generarReporteHistorial());
    }

    public void actualizarRecordatoriosArea() {
        recordatoriosArea.setText(controladorMed.generarReporteRecordatorios());
    }

    public void actualizarTodo() {
        actualizarMedicamentosArea();
        actualizarHistorialArea();
        actualizarRecordatoriosArea();
    }

    public ControladorMedicamento getControladorMed() {
        return controladorMed;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    private void cerrarSesion() {
        if (recordatorioTimer != null) {
            recordatorioTimer.stop();
        }
        PantallaInicio inicio = new PantallaInicio();
        inicio.setVisible(true);
        dispose();
    }
}
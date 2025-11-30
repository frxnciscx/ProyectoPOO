package proyecto.vista;

import proyecto.controlador.ControladorPaciente;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RegistroPacienteGUI extends JFrame {
    private JTextField txtRut;
    private JTextField txtNombre;
    private JTextField txtEdad;
    private JButton btnRegistrar;
    private final ControladorPaciente controlador;

    public RegistroPacienteGUI(ControladorPaciente controlador) {
        this.controlador = controlador;

        setTitle("Registro de paciente");
        setSize(450, 350); // Tamaño ajustado
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new PantallaInicio().setVisible(true);
            }
        });
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitulo = new JLabel("Registro de nuevo paciente");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);

        JLabel lblRut = new JLabel("RUT:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblRut, gbc);

        txtRut = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtRut, gbc);

        JLabel lblNombre = new JLabel("Nombre:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblNombre, gbc);

        txtNombre = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(txtNombre, gbc);

        JLabel lblEdad = new JLabel("Edad:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblEdad, gbc);

        txtEdad = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 3;
        panel.add(txtEdad, gbc);

        btnRegistrar = new JButton("Registrar");
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.ipady = 10;
        panel.add(btnRegistrar, gbc);

        JButton btnVolver = new JButton("Volver al inicio");
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.ipady = 5;
        gbc.insets = new Insets(15, 8, 8, 8);
        panel.add(btnVolver, gbc);

        btnVolver.addActionListener(e -> {
            new PantallaInicio().setVisible(true);
            dispose();
        });

        add(panel);

        btnRegistrar.addActionListener(e -> registrarPaciente());
    }

    private void registrarPaciente() {
        String rut = txtRut.getText().trim();
        String nombre = txtNombre.getText().trim();
        String edadStr = txtEdad.getText().trim();

        if (rut.isEmpty() || nombre.isEmpty() || edadStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadStr);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Edad debe ser un numero valido", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String msg = controlador.registrarPaciente(rut, nombre, edad);

        if (msg.startsWith("EXITO")) {
            JOptionPane.showMessageDialog(this, msg, "Exito", JOptionPane.INFORMATION_MESSAGE);
            limpiarCampos();
            dispose();
            LoginPacienteGUI login = new LoginPacienteGUI(controlador);
            login.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCampos() {
        txtRut.setText("");
        txtNombre.setText("");
        txtEdad.setText("");
    }
}
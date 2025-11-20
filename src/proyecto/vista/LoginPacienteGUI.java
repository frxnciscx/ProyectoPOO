package proyecto.vista;

import proyecto.controlador.ControladorPaciente;
import proyecto.controlador.ControladorMedicamento;
import proyecto.modelo.Paciente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

public class LoginPacienteGUI extends JFrame {

    private final ControladorPaciente controlador;
    private JTextField txtRut;
    private JPasswordField txtClave;
    private JButton btnLogin;

    public LoginPacienteGUI(ControladorPaciente controlador) {
        this.controlador = controlador;

        setTitle("Login Paciente");
        setSize(400, 250);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new PantallaInicio().setVisible(true);
            }
        });

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(panel);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL; // Hacemos que los componentes se estiren horizontalmente

        JLabel lblTitulo = new JLabel("Iniciar Sesión");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 0.0; // El título no se estira
        panel.add(lblTitulo, gbc);

        JLabel lblRut = new JLabel("RUT:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        // --- INICIO DE MODIFICACIÓN ---
        gbc.weightx = 0.0; // La etiqueta (Col 0) no debe estirarse
        // --- FIN DE MODIFICACIÓN ---
        panel.add(lblRut, gbc);

        // --- INICIO DE MODIFICACIÓN ---
        txtRut = new JTextField(); // Quitamos el (15)
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // El campo de texto (Col 1) debe estirarse
        // --- FIN DE MODIFICACIÓN ---
        panel.add(txtRut, gbc);

        JLabel lblClave = new JLabel("Clave:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        // --- INICIO DE MODIFICACIÓN ---
        gbc.weightx = 0.0; // La etiqueta (Col 0) no debe estirarse
        // --- FIN DE MODIFICACIÓN ---
        panel.add(lblClave, gbc);

        // --- INICIO DE MODIFICACIÓN ---
        txtClave = new JPasswordField(); // Quitamos el (15)
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0; // El campo de texto (Col 1) debe estirarse
        // --- FIN DE MODIFICACIÓN ---
        panel.add(txtClave, gbc);

        btnLogin = new JButton("Entrar");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.ipady = 10;
        gbc.weightx = 0.0; // Reseteamos el weightx para el botón
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> procesarLogin());
    }

    private void procesarLogin() {
        String rut = txtRut.getText().trim();
        String clave = new String(txtClave.getPassword()).trim();

        if (rut.isEmpty() || clave.isEmpty()) {
            JOptionPane.showMessageDialog(this, "RUT y clave son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String msgLogin = controlador.procesarLogin(rut, clave);
        Optional<Paciente> optPaciente = controlador.loginPaciente(rut, clave);

        if (optPaciente.isPresent()) {
            JOptionPane.showMessageDialog(this, msgLogin, "Exito", JOptionPane.INFORMATION_MESSAGE);
            ControladorMedicamento ctrlMed = new ControladorMedicamento(optPaciente.get());
            GestionMedicamentosGUI gestion = new GestionMedicamentosGUI(ctrlMed, optPaciente.get());
            gestion.setVisible(true);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, msgLogin, "Error de Login", JOptionPane.ERROR_MESSAGE);
            txtClave.setText("");
        }
    }
}
package proyecto.vista;

import proyecto.controlador.ControladorPaciente;
import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;
import java.awt.*;

public class PantallaInicio extends JFrame {

    private final ControladorPaciente controlador;

    public PantallaInicio() {
        this.controlador = new ControladorPaciente();

        setTitle("Sistema de Gestion de Medicamentos");
        setSize(450, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titulo = new JLabel("Bienvenido al Sistema de Gestion");

        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titulo, gbc);

        JButton btnRegistrar = new JButton("Registrar Paciente");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.ipady = 10;
        panel.add(btnRegistrar, gbc);

        JButton btnLogin = new JButton("Iniciar Sesion");
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.ipady = 10;
        panel.add(btnLogin, gbc);

        add(panel);

        btnRegistrar.addActionListener(e -> {
            RegistroPacienteGUI registro = new RegistroPacienteGUI(controlador);
            registro.setVisible(true);
            dispose();
        });

        btnLogin.addActionListener(e -> {
            LoginPacienteGUI login = new LoginPacienteGUI(controlador);
            login.setVisible(true);
            dispose();
        });
    }

    public static void main(String[] args) {
        try {
            FlatDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Error al inicializar FlatLaf");
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        SwingUtilities.invokeLater(() -> {
            PantallaInicio inicio = new PantallaInicio();
            inicio.setVisible(true);
        });
    }
}
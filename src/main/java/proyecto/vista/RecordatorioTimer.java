package proyecto.vista;

import proyecto.modelo.Paciente;
import proyecto.modelo.Recordatorio;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class RecordatorioTimer {
    private final Paciente paciente;
    private final GestionMedicamentosGUI gui;
    private final javax.swing.Timer timer;
    private boolean running = true;

    public RecordatorioTimer(Paciente paciente, GestionMedicamentosGUI gui) {
        this.paciente = paciente;
        this.gui = gui;
        this.timer = new javax.swing.Timer(60000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) {
                    timer.stop();
                    return;
                }
                verificarYNotificarRecordatorios();
            }
        });
    }

    private void verificarYNotificarRecordatorios() {
        List<Recordatorio> recordatorios = paciente.getListaRecordatorios();
        for (Recordatorio r : recordatorios) {
            if (r.esHoraDeTomar()) {
                SwingUtilities.invokeLater(() -> {
                    String nombreMed = r.getMedicamentoAsociado().getNombre();
                    String msg = "¡ALERTA! Es hora de tomar " + nombreMed +
                            " (" + r.getHora() + "). ¿Confirmas la toma?";
                    int opcion = JOptionPane.showConfirmDialog(gui, msg, "Recordatorio de Medicamento",
                            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (opcion == JOptionPane.YES_OPTION) {
                        String resultado = gui.getControladorMed().tomarMedicamento(nombreMed);
                        if (resultado.startsWith("EXITO")) {
                            r.registrarTomado();
                            JOptionPane.showMessageDialog(gui, resultado + "\nRecordatorio marcado como tomado",
                                    "Toma Registrada", JOptionPane.INFORMATION_MESSAGE);
                            gui.actualizarTodo();
                        } else {
                            JOptionPane.showMessageDialog(gui, "Error al tomar: " + resultado, "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        gui.getControladorMed().getPaciente().getHistorial().agregarRegistro(
                                "Recordatorio ignorado para " + nombreMed + " a las " + r.getHora());
                        gui.actualizarHistorialArea();
                        JOptionPane.showMessageDialog(gui, "Recordatorio ignorado",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
            }
        }
    }

    public void start() {
        timer.start();
    }

    public void stop() {
        running = false;
        timer.stop();
    }
}
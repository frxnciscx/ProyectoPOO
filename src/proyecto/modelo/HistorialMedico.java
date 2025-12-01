package proyecto.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HistorialMedico {
    private final List<String> registros;

    public HistorialMedico() {
        this.registros = new ArrayList<>();
    }

    public void agregarRegistro(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje del registro no puede ser nulo o vacio");
        }
        String registroConTiempo = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + " - " + mensaje.trim();
        registros.add(registroConTiempo);
    }

    public void agregarRegistroDirecto(String registroCompleto) {
        if (registroCompleto == null || registroCompleto.trim().isEmpty()) {
            return;
        }
        registros.add(registroCompleto.trim());
    }

    public String toFormattedString() {
        if (registros.isEmpty()) {
            return "No hay registros en el historial";
        }
        StringBuilder sb = new StringBuilder("Historial Medico:\n");
        for (String reg : registros) {
            sb.append(reg).append("\n");
        }
        return sb.toString();
    }

    public List<String> getRegistros() {
        return Collections.unmodifiableList(new ArrayList<>(registros));
    }

    public void limpiar() {
        registros.clear();
    }

    //toString para depuracion

    public String toCSV() {
        StringBuilder sb = new StringBuilder();
        for (String reg : registros) {
            String msgEsc = reg.replace(";", "\\;"); // Simple escaping
            sb.append(msgEsc).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "HistorialMedico{" + "registros=" + registros.size() + " entradas}";
    }
}
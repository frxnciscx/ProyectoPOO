package proyecto.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Historial {
    private List<String> registros;

    //constructor
    public Historial() {
        this.registros = new ArrayList<>();
    }

    //agregar registro con fecha y hora automatica, con validacion
    public void agregarRegistro(String registro) {
        if (registro == null || registro.trim().isEmpty()) {
            throw new IllegalArgumentException("El registro no puede ser nulo o vacio");
        }
        String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        registros.add("[" + fechaHora + "] " + registro.trim());
    }

    //retorna el historial formateado como String (para MVC: pasa a vista)
    public String obtenerTextoFormateado() {
        if (registros.isEmpty()) {
            return "El historial esta vacio";
        }
        StringBuilder sb = new StringBuilder("----- HISTORIAL -----\n");
        for (String r : registros) {
            sb.append(r).append("\n");
        }
        return sb.toString();
    }

    //retorna la lista completa
    public List<String> obtenerRegistros() {
        return Collections.unmodifiableList(new ArrayList<>(registros));
    }

    //metodo para filtrar registros
    public List<String> filtrarRegistros(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return obtenerRegistros();
        }
        List<String> filtrados = new ArrayList<>();
        for (String r : registros) {
            if (r.toLowerCase().contains(keyword.toLowerCase())) {
                filtrados.add(r);
            }
        }
        return Collections.unmodifiableList(filtrados);
    }

    //una linea por registro, con timestamp incluido
    public String toCSV() {
        if (registros.isEmpty()) {
            return "";
        }
        StringBuilder csv = new StringBuilder();
        for (String r : registros) {
            String escaped = "\"" + r.replace("\"", "\"\"") + "\"";
            csv.append(escaped).append("\n");
        }
        return csv.toString();
    }

    public static Historial fromCSV(String contenidoCSV) {
        Historial historial = new Historial();
        if (contenidoCSV == null || contenidoCSV.trim().isEmpty()) {
            return historial;
        }
        String[] lineas = contenidoCSV.split("\n");
        for (String linea : lineas) {
            if (linea.trim().isEmpty()) continue;
            try {
                String registro = linea.trim().replaceAll("^\"|\"$", "");
                historial.agregarRegistro(registro);
            } catch (IllegalArgumentException e) {
                System.err.println("Linea CSV invalida ignorada: " + linea);
            }
        }
        return historial;
    }

    @Deprecated
    public List<String> getRegistros() {
        return obtenerRegistros();
    }
}

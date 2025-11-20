package proyecto.modelo;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RepositorioPaciente {

    private final String rutaArchivo = "pacientes.csv";
    private List<Paciente> pacientes;

    public static class PersistenciaException extends Exception {
        public PersistenciaException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public RepositorioPaciente() {
        this.pacientes = new ArrayList<>();
        try {
            cargarPacientes();
        } catch (PersistenciaException e) {
            System.err.println("Error inicial al cargar pacientes: " + e.getMessage());
        }
    }

    private void cargarPacientes() throws PersistenciaException {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean esHeader = true;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || (esHeader && linea.startsWith("rut"))) {
                    esHeader = false;
                    continue;
                }
                esHeader = false;
                try {
                    String[] datos = linea.split(";");
                    if (datos.length < 4) {
                        throw new IllegalArgumentException("Linea invalida (menos de 4 campos): " + linea);
                    }
                    String rut = datos[0].replaceAll("^\"|\"$", "").trim();
                    String nombre = datos[1].replaceAll("^\"|\"$", "").trim();
                    int edad = Integer.parseInt(datos[2].trim());
                    String clave = datos[3].replaceAll("^\"|\"$", "").trim();

                    Paciente p = new Paciente(rut, nombre, edad, clave);
                    pacientes.add(p);
                } catch (Exception e) {
                    System.err.println("Linea CSV ignorada: " + linea + " - " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al cargar pacientes desde " + rutaArchivo + ": " + e.getMessage(), e);
        }
    }

    private void guardarPacientes() throws PersistenciaException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo))) {
            bw.write("rut;nombre;edad;clave");
            bw.newLine();
            for (Paciente p : pacientes) {
                String rutEsc = "\"" + p.getRut().replace("\"", "\"\"") + "\"";
                String nombreEsc = "\"" + p.getNombre().replace("\"", "\"\"") + "\"";
                String claveEsc = "\"" + p.getClave().replace("\"", "\"\"") + "\"";
                String linea = rutEsc + ";" + nombreEsc + ";" + p.getEdad() + ";" + claveEsc;
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenciaException("Error al guardar pacientes en " + rutaArchivo + ": " + e.getMessage(), e);
        }
    }

    public String registrarPaciente(String rut, String nombre, int edad, String clave) {
        try {
            if (existePaciente(rut)) {
                return "ERROR: Paciente con RUT '" + rut + "' ya existe";
            }
            Paciente p = new Paciente(rut, nombre, edad, clave);
            pacientes.add(p);
            guardarPacientes();
            return "EXITO: Paciente '" + nombre + "' (RUT: " + rut + ") registrado correctamente";
        } catch (IllegalArgumentException e) {
            return "ERROR de validacion: " + e.getMessage();
        } catch (PersistenciaException e) {
            return "ERROR de persistencia: " + e.getMessage();
        }
    }

    public boolean existePaciente(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return false;
        }
        return pacientes.stream().anyMatch(p -> p.getRut().equalsIgnoreCase(rut.trim()));
    }

    public Optional<Paciente> buscarPaciente(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return Optional.empty();
        }
        return pacientes.stream()
                .filter(p -> p.getRut().equalsIgnoreCase(rut.trim()))
                .findFirst();
    }

    public String guardarPaciente(Paciente p) {
        try {
            if (p == null) {
                return "ERROR: Paciente no puede ser nulo";
            }
            Optional<Paciente> optExistente = buscarPaciente(p.getRut());
            if (optExistente.isPresent()) {
                Paciente existente = optExistente.get();
                existente.setNombre(p.getNombre());
                existente.setEdad(p.getEdad());
                existente.setClave(p.getClave());
            } else {
                pacientes.add(p);
            }
            guardarPacientes();
            return "EXITO: Paciente '" + p.getNombre() + "' guardado/actualizado";
        } catch (IllegalArgumentException e) {
            return "ERROR de validacion: " + e.getMessage();
        } catch (PersistenciaException e) {
            return "ERROR de persistencia: " + e.getMessage();
        }
    }

    public List<Paciente> listarPacientes() {
        return Collections.unmodifiableList(new ArrayList<>(pacientes));
    }

    public String eliminarPaciente(String rut) {
        if (!existePaciente(rut)) {
            return "ERROR: Paciente con RUT '" + rut + "' no existe";
        }
        pacientes.removeIf(p -> p.getRut().equalsIgnoreCase(rut));
        try {
            guardarPacientes();
            return "EXITO: Paciente con RUT '" + rut + "' eliminado";
        } catch (PersistenciaException e) {
            return "ERROR de persistencia al eliminar: " + e.getMessage();
        }
    }
}
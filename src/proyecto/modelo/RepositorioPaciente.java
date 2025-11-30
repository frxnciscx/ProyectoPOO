package proyecto.modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RepositorioPaciente {
    private final String RUTA_ARCHIVO = "pacientes.json";
    private final String RUTA_BACKUP = "pacientes_backup.json";
    private List<Paciente> pacientes;
    private final Gson gson;

    public RepositorioPaciente() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.pacientes = new ArrayList<>();
        cargarPacientes();
    }

    private void cargarPacientes() {
        File archivo = new File(RUTA_ARCHIVO);
        if (!archivo.exists()) {
            System.out.print("El archivo no fue encontrado, se creara uno al guardar");
            return;
        }

        try (Reader reader = new BufferedReader(new FileReader(archivo))) {
            Type listType = new TypeToken<ArrayList<Paciente>>() {
            }.getType();
            List<Paciente> datos = gson.fromJson(reader, listType);
            if (datos != null) {
                this.pacientes = datos;
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
            intentarRecuperarBackup();
        } catch (Exception e) {
            System.err.println("Error con el archivo JSON: " + e.getMessage());
        }
    }

    private void intentarRecuperarBackup() {
        File backup = new File(RUTA_BACKUP);
        if (backup.exists()) {
            System.out.println("Intentando recuperar datos...");
            try (Reader reader = new BufferedReader(new FileReader(backup))) {
                Type listType = new TypeToken<ArrayList<Paciente>>() {
                }.getType();
                this.pacientes = gson.fromJson(reader, listType);
                System.out.println("Recuperacion exitosa");
            } catch (IOException e) {
                System.err.println("Error al leer el archivo: " + e.getMessage());
            }
        } else {
            System.out.println("No existe un backup disponible para recuperar");
        }
    }

    private void guardarPacientes() {
        File archivo = new File(RUTA_ARCHIVO);
        if (archivo.exists()) {
            try {
                Files.copy(archivo.toPath(), new File(RUTA_BACKUP).toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("No se pudo crear el backup: " + e.getMessage());
            }
        }
        try (Writer writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
            gson.toJson(this.pacientes, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar el archivo paciente: " + e.getMessage());
        }
    }

    public String registrarPaciente(String rut, String nombre, int edad) {
        if (existePaciente(rut)) {
            return "ERROR: Paciente con RUT" + rut + "ya existe";
        }
        Paciente p = new Paciente(rut, nombre, edad);
        pacientes.add(p);
        guardarPacientes();
        return "EXITO: Paciente registrado correctamente";
    }

    public boolean existePaciente(String rut) {
        if (rut == null) return false;
        return pacientes.stream().anyMatch(p -> p.getRut().equalsIgnoreCase(rut));
    }

    public Optional<Paciente> buscarPaciente(String rut) {
        if (rut == null) return Optional.empty();
        return pacientes.stream()
                .filter(p -> p.getRut().equalsIgnoreCase(rut))
                .findFirst();
    }

    public List<Paciente> listarPacientes() {
        return Collections.unmodifiableList(pacientes);
    }
}
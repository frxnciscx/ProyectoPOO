package proyecto.modelo.datos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import proyecto.modelo.Insulina;
import proyecto.modelo.Medicamento;
import proyecto.modelo.Paciente;
import proyecto.modelo.Recordatorio;

import java.io.*;
import java.nio.file.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorDatosPaciente {
    private final Paciente paciente;
    private final String rutPaciente;
    private final Path datosCarpeta;
    private final Gson gson;

    private static class PacienteData {
        List<MedicamentoJSON> medicamentos;
        List<RecordatorioJSON> recordatorios;
        List<String> historial;
    }

    private static class MedicamentoJSON {
        String tipo;
        String nombre;
        int dosis;
        int cantidad;
        String fechaVencimiento;
        double glucosaMinima;
    }

    private static class RecordatorioJSON {
        String hora;
        int frecuencia;
        String nombreMedicamento;
    }

    public GestorDatosPaciente(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        this.paciente = paciente;
        this.rutPaciente = paciente.getRut().replaceAll("[^a-zA-Z0-9.-]", "_");
        this.datosCarpeta = Paths.get(rutPaciente);
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void guardarDatos() {
        try {
            Files.createDirectories(datosCarpeta);

            guardarMedicamentos();
            guardarRecordatorios();
            guardarHistorial();

        } catch (IOException e) {
            System.err.println("Error al crear la carpeta de datos para " + rutPaciente + ": " + e.getMessage());
        }
    }


    private void guardarMedicamentos() {
        try {
            Path medsDir = datosCarpeta.resolve("medicamentos");
            if (Files.exists(medsDir)) {
                Files.walk(medsDir)
                        .sorted(java.util.Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            }
            Files.createDirectories(medsDir);

            for (Medicamento med : paciente.getListaMedicamentos()) {
                MedicamentoJSON objJson = new MedicamentoJSON();
                objJson.nombre = med.getNombre();
                objJson.dosis = med.getDosis();
                objJson.cantidad = med.getCantidad();
                objJson.fechaVencimiento = med.getFechaVencimiento();

                if (med instanceof Insulina) {
                    objJson.tipo = "Insulina";
                    objJson.glucosaMinima = ((Insulina) med).getGlucosaMinima();
                } else {
                    objJson.tipo = "Medicamento";
                }

                String nombreArchivo = med.getNombre().replaceAll("[^a-zA-Z0-9]", "_") + ".json";
                Path archivo = medsDir.resolve(nombreArchivo);

                try (Writer writer = new BufferedWriter(new FileWriter(archivo.toFile()))) {
                    gson.toJson(objJson, writer);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar medicamentos: " + e.getMessage());
        }
    }

    private void guardarRecordatorios() {
        Path archivo = datosCarpeta.resolve("recordatorios.json");
        List<RecordatorioJSON> recordatoriosJSON = new ArrayList<>();

        for (Recordatorio rec : paciente.getListaRecordatorios()) {
            RecordatorioJSON objJson = new RecordatorioJSON();
            objJson.hora = rec.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
            objJson.frecuencia = rec.getFrecuenciaHoras();
            objJson.nombreMedicamento = rec.getMedicamentoAsociado().getNombre();
            recordatoriosJSON.add(objJson);
        }

        try (Writer writer = new BufferedWriter(new FileWriter(archivo.toFile()))) {
            gson.toJson(recordatoriosJSON, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar recordatorios: " + e.getMessage());
        }
    }

    private void guardarHistorial() {
        Path archivo = datosCarpeta.resolve("historial.json");

        List<String> historial = paciente.getHistorial().getRegistros();

        try (Writer writer = new BufferedWriter(new FileWriter(archivo.toFile()))) {
            gson.toJson(historial, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar historial: " + e.getMessage());
        }
    }

    public void cargarDatos() {
        if (!Files.exists(datosCarpeta)) {
            System.out.println("No hay datos guardados para el paciente: " + rutPaciente);
            return;
        }

        paciente.limpiarDatosCargados();

        cargarMedicamentos();
        cargarRecordatorios();
        cargarHistorial();

        System.out.println("Datos cargados exitosamente desde: " + datosCarpeta);
    }

    private void cargarMedicamentos() {
        Path medsDir = datosCarpeta.resolve("medicamentos");

        if (!Files.exists(medsDir)) return;

        try {
            Files.list(medsDir)
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(archivo -> {
                        try (Reader reader = new BufferedReader(new FileReader(archivo.toFile()))) {
                            MedicamentoJSON json = gson.fromJson(reader, MedicamentoJSON.class);
                            if (json != null) {
                                Medicamento med;
                                if ("Insulina".equals(json.tipo)) {
                                    med = new Insulina(json.nombre, json.dosis, json.cantidad, json.fechaVencimiento, json.glucosaMinima);
                                } else {
                                    med = new Medicamento(json.nombre, json.dosis, json.cantidad, json.fechaVencimiento);
                                }
                                paciente.cargarMedicamento(med);
                            }
                        } catch (IOException | JsonSyntaxException e) {
                            System.err.println("Error al leer archivo de medicamento " + archivo.getFileName() + ": " + e.getMessage());
                        } catch (Exception e) {
                            System.err.println("Error al construir medicamento " + archivo.getFileName() + ": " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error al listar archivos de medicamentos: " + e.getMessage());
        }
    }

    private void cargarRecordatorios() {
        Path archivo = datosCarpeta.resolve("recordatorios.json");
        if (!Files.exists(archivo)) return;

        try (Reader reader = new BufferedReader(new FileReader(archivo.toFile()))) {
            List<RecordatorioJSON> data = gson.fromJson(reader, new com.google.gson.reflect.TypeToken<List<RecordatorioJSON>>(){}.getType());

            if (data != null) {
                for (RecordatorioJSON json : data) {
                    try {
                        Optional<Medicamento> medAsociado = paciente.getListaMedicamentos().stream()
                                .filter(m -> m.getNombre().equalsIgnoreCase(json.nombreMedicamento))
                                .findFirst();

                        if (medAsociado.isPresent()) {
                            LocalTime hora = LocalTime.parse(json.hora, DateTimeFormatter.ofPattern("HH:mm"));
                            Recordatorio rec = new Recordatorio(hora, json.frecuencia, medAsociado.get());
                            paciente.cargarRecordatorio(rec);
                        } else {
                            System.err.println("Se omitió un recordatorio porque el medicamento " + json.nombreMedicamento + " no existe.");
                        }
                    } catch (Exception e) {
                        System.err.println("Error al recuperar recordatorio: " + e.getMessage());
                    }
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error al cargar recordatorios: " + e.getMessage());
        }
    }

    private void cargarHistorial() {
        Path archivo = datosCarpeta.resolve("historial.json");
        if (!Files.exists(archivo)) return;

        try (Reader reader = new BufferedReader(new FileReader(archivo.toFile()))) {
            List<String> historial = gson.fromJson(reader, new com.google.gson.reflect.TypeToken<List<String>>(){}.getType());

            if (historial != null) {
                for (String linea : historial) {
                    paciente.getHistorial().agregarRegistroDirecto(linea);
                }
            }
        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error al cargar historial: " + e.getMessage());
        }
    }
}
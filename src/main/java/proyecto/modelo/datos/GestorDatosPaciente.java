package proyecto.modelo.datos;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import proyecto.modelo.Insulina;
import proyecto.modelo.Medicamento;
import proyecto.modelo.Paciente;
import proyecto.modelo.Recordatorio;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestorDatosPaciente {
    private final Paciente paciente;
    private final String rutPaciente;
    private final String ARCHIVO_JSON;
    private final Gson gson;

    private static class PacienteData {
        List<MedicamentoJSON> medicamentos;
        List<RecordatorioJSON> recordatorios;
        List<String> historial;
    }

    private static class MedicamentoJSON {
        String tipo; //medicamento o insulina
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
        this.ARCHIVO_JSON = rutPaciente + ".json";
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public void guardarDatos() {
        PacienteData data = new PacienteData();
        data.medicamentos = new ArrayList<>();
        data.recordatorios = new ArrayList<>();
        data.historial = paciente.getHistorial().getRegistros();

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
            data.medicamentos.add(objJson);
        }

        for (Recordatorio rec : paciente.getListaRecordatorios()) {
            RecordatorioJSON objJson = new RecordatorioJSON();
            objJson.hora = rec.getHora().format(DateTimeFormatter.ofPattern("HH:mm"));
            objJson.frecuencia = rec.getFrecuenciaHoras();
            objJson.nombreMedicamento = rec.getMedicamentoAsociado().getNombre();
            data.recordatorios.add(objJson);
        }

        File archivoActual = new File(ARCHIVO_JSON);
        if (archivoActual.exists()) {
            try {
                File backup = new File(rutPaciente + "_backup.json");
                Files.copy(archivoActual.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.err.println("No se pudo crear backup de datos para " + rutPaciente);
            }
        }

        try (Writer writer = new BufferedWriter(new FileWriter(ARCHIVO_JSON))) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar JSON para " + rutPaciente + ": " + e.getMessage());
        }
    }

    public void cargarDatos() {
        File f = new File(ARCHIVO_JSON);
        if (!f.exists()) {
            return;
        }

        try (Reader reader = new BufferedReader(new FileReader(f))) {
            PacienteData data = gson.fromJson(reader, PacienteData.class);
            if (data == null) {
                return;
            }
            paciente.limpiarDatosCargados();

            if (data.medicamentos != null) {
                for (MedicamentoJSON json : data.medicamentos) {
                    try {
                        Medicamento med;
                        if ("Insulina".equals(json.tipo)) {
                            med = new Insulina(json.nombre, json.dosis, json.cantidad, json.fechaVencimiento, json.glucosaMinima);
                        } else {
                            med = new Medicamento(json.nombre, json.dosis, json.cantidad, json.fechaVencimiento);
                        }
                        paciente.cargarMedicamento(med);
                    } catch (Exception e) {
                        System.err.println("Error al recuperar medicamento " + json.nombre + ": " + e.getMessage());
                    }
                }
            }

            if (data.recordatorios != null) {
                for (RecordatorioJSON json : data.recordatorios) {
                    try {
                        Optional<Medicamento> medAsociado = paciente.getListaMedicamentos().stream()
                                .filter(m -> m.getNombre().equalsIgnoreCase(json.nombreMedicamento))
                                .findFirst();

                        if (medAsociado.isPresent()) {
                            LocalTime hora = LocalTime.parse(json.hora, DateTimeFormatter.ofPattern("HH:mm"));
                            Recordatorio rec = new Recordatorio(hora, json.frecuencia, medAsociado.get());
                            paciente.cargarRecordatorio(rec);
                        } else {
                            System.err.println("Se omitio un recordatorio porque el medicamento" + json.nombreMedicamento + "no existe");
                        }
                    } catch (Exception e) {
                        System.err.println("Error al recuperar recordatorio: " + e.getMessage());
                    }
                }
            }

            if (data.historial != null) {
                for (String linea : data.historial) {
                    paciente.getHistorial().agregarRegistroDirecto(linea);
                }
            }

        } catch (IOException | JsonSyntaxException e) {
            System.err.println("Error al cargar " + ARCHIVO_JSON + ": " + e.getMessage());
        }
    }
}
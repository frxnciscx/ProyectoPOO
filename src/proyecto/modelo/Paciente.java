package proyecto.modelo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Paciente {
    private final String rut;
    private String nombre;
    private int edad;
    private String clave;
    private final List<Medicamento> listaMedicamentos;
    private final List<Recordatorio> listaRecordatorios;
    private final HistorialMedico historial;

    public Paciente(String rut, String nombre, int edad, String clave) {
        if (rut == null || rut.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT no puede ser nulo o vacio");
        }
        if (!rut.matches("\\d{7,8}-?\\d{1}")) {
            throw new IllegalArgumentException("Formato de RUT invalido (ej 12345678-9)");
        }
        setNombre(nombre);
        setEdad(edad);
        setClave(clave);

        this.rut = rut.trim().toUpperCase();
        this.listaMedicamentos = new ArrayList<>();
        this.listaRecordatorios = new ArrayList<>();
        this.historial = new HistorialMedico();
    }

    public String agregarMedicamento(Medicamento m) {
        if (m == null) {
            return "ERROR: El medicamento no puede ser nulo";
        }
        if (listaMedicamentos.stream().anyMatch(existing -> existing.getNombre().equalsIgnoreCase(m.getNombre()))) {
            return "ERROR: Ya existe un medicamento con nombre '" + m.getNombre() + "'";
        }
        if (m.estaVencido()) {
            return "ERROR: El medicamento '" + m.getNombre() + "' esta vencido";
        }
        listaMedicamentos.add(m);
        historial.agregarRegistro("Agregado medicamento: " + m.getNombre() + " (dosis: " + m.getDosis() + ", stock: " + m.getCantidad() + ")");
        return "EXITO: Se agrego el medicamento '" + m.getNombre() + "'";
    }

    public String tomarMedicamento(String nombre) {
        Optional<Medicamento> optMed = listaMedicamentos.stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
        if (optMed.isEmpty()) {
            return "ERROR: Medicamento '" + nombre + "' no encontrado";
        }
        Medicamento m = optMed.get();
        if (m.estaVencido()) {
            return "ERROR: El medicamento '" + m.getNombre() + "' esta vencido";
        }
        if (m.getCantidad() <= 0) {
            return "ERROR: No hay stock disponible para '" + m.getNombre() + "'";
        }

        if (m instanceof Insulina) {
            Insulina ins = (Insulina) m;
            double glucosa = 100;
            if (glucosa < ins.getGlucosaMinima()) {
                return "ERROR: Glucosa (" + glucosa + " mg/dL) por debajo del minimo (" + ins.getGlucosaMinima() + ") para insulina";
            }
            historial.agregarRegistro("Tomada insulina " + m.getNombre() + " con glucosa: " + glucosa + " mg/dL");
        } else {
            historial.agregarRegistro("Tomada dosis de " + m.getNombre());
        }

        m.disminuirCantidad();
        return "EXITO: Se tomo una dosis de '" + m.getNombre() + "' Stock restante: " + m.getCantidad();
    }

    public String removerMedicamento(String nombre) {
        Optional<Medicamento> optMed = listaMedicamentos.stream()
                .filter(m -> m.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
        if (optMed.isEmpty()) {
            return "ERROR: Medicamento '" + nombre + "' no encontrado";
        }
        Medicamento m = optMed.get();
        listaMedicamentos.remove(m);
        historial.agregarRegistro("Removido medicamento: " + m.getNombre());
        return "EXITO: Se removio el medicamento '" + m.getNombre() + "'";
    }

    public String agregarRecordatorio(Recordatorio r) {
        if (r == null) {
            return "ERROR: El recordatorio no puede ser nulo";
        }
        if (listaRecordatorios.stream().anyMatch(existing ->
                existing.getMedicamentoAsociado().getNombre().equals(r.getMedicamentoAsociado().getNombre()) &&
                        existing.getHora().equals(r.getHora()))) {
            return "ERROR: Ya existe un recordatorio para '" + r.getMedicamentoAsociado().getNombre() + "' a esa hora";
        }
        if (listaMedicamentos.stream().noneMatch(m -> m.getNombre().equals(r.getMedicamentoAsociado().getNombre()))) {
            return "ERROR: El medicamento asociado no esta en la lista";
        }
        listaRecordatorios.add(r);
        historial.agregarRegistro("Agregado recordatorio para " + r.getMedicamentoAsociado().getNombre() + " a las " + r.getHora());
        return "EXITO: Se agrego el recordatorio para '" + r.getMedicamentoAsociado().getNombre() + "'";
    }

    public List<String> verificarRecordatoriosActivos() {
        List<String> activos = new ArrayList<>();
        for (Recordatorio r : listaRecordatorios) {
            if (r.esHoraDeTomar()) {
                activos.add("Activo: " + r.toFormattedString());
            }
        }
        return activos;
    }

    public String toFormattedMedicamentos() {
        if (listaMedicamentos.isEmpty()) {
            return "No hay medicamentos registrados";
        }
        StringBuilder sb = new StringBuilder("Medicamentos:\n");
        for (Medicamento m : listaMedicamentos) {
            sb.append("- ").append(m.toFormattedString()).append("\n");
        }
        return sb.toString();
    }

    public String toFormattedRecordatorios() {
        if (listaRecordatorios.isEmpty()) {
            return "No hay recordatorios configurados";
        }
        StringBuilder sb = new StringBuilder("Recordatorios:\n");
        for (Recordatorio r : listaRecordatorios) {
            sb.append(r.toFormattedString()).append("\n");
        }
        return sb.toString();
    }

    public String toFormattedHistorial() {
        return historial.toFormattedString();
    }

    public String getRut() { return rut; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede ser nulo o vacio");
        this.nombre = nombre.trim();
    }

    public int getEdad() { return edad; }

    public void setEdad(int edad) {
        if (edad < 0 || edad > 150) {
            throw new IllegalArgumentException("La edad debe ser entre 0 y 150");
        this.edad = edad;
    }

    public String getClave() { return clave; }

    public void setClave(String clave) {
        if (clave == null || clave.length() < 4) {throw new IllegalArgumentException("La clave debe tener al menos 4 caracteres");
            this.clave = clave;
    }

    public List<Medicamento> getListaMedicamentos() { return Collections.unmodifiableList(listaMedicamentos); }

    public List<Recordatorio> getListaRecordatorios() { return Collections.unmodifiableList(listaRecordatorios); }

    public HistorialMedico getHistorial() { return historial; }


    public void cargarMedicamento(Medicamento m) {
         if (m != null) listaMedicamentos.add(m);
    }

    public void cargarRecordatorio(Recordatorio r) {
         if (r != null) listaRecordatorios.add(r);
    }

    public void limpiarDatosCargados() {
         listaMedicamentos.clear();
         listaRecordatorios.clear();
         historial.limpiar();
    }

    @Override
    public String toString() {
        return "Paciente{" + "rut='" + rut + "', nombre='" + nombre + "', edad=" + edad + "}";
    }

    public String toCSV() {
        return rut + ";" + nombre + ";" + edad + ";" + clave;
    }
}


package proyecto.modelo;

import java.util.ArrayList;
import java.util.List;

public class Paciente {
    private String rut;
    private String nombre;
    private int edad;
    //se elimina el atributo clave para acceso directo
    private List<Medicamento> listaMedicamentos;
    private List<Recordatorio> listaRecordatorios;
    private HistorialMedico historial;

    public Paciente(String rut, String nombre, int edad) {
        if (rut == null || rut.trim().isEmpty()) {
            throw new IllegalArgumentException("El RUT no puede ser nulo o vacio");
        }
        //CORRECCION: ahora acepta digitos (0-9) y las letras k/K en el digito verificador
        if (!rut.matches("\\d{7,8}-?\\d{1}")) {
            throw new IllegalArgumentException("Formato de RUT invalido (ej 12345678-9)");
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacio");
        }
        if (edad < 1 || edad > 99) {
            throw new IllegalArgumentException("La edad debe ser entre 1 y 99");
        }

        this.rut = rut.trim().toUpperCase();
        this.nombre = nombre.trim();
        this.edad = edad;
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
        for (Medicamento m : listaMedicamentos) {
            if (m.getNombre().equalsIgnoreCase(nombre)) {
                if (m.estaVencido()) {
                    return "ERROR: medicamento vencido";
                }
                if (m.getCantidad() <= 0) {
                    return "ERROR: sin stock";
                }
                m.disminuirCantidad();
                if (m instanceof Insulina) {
                    historial.agregarRegistro("Insulina tomada");
                } else {
                    historial.agregarRegistro("Dosis tomada");
                }
                return "Stock de medicamento" + m.getCantidad();
            }
        }
        return "ERROR: medicamento no encontrado";
    }

    public String removerMedicamento(String nombre) {
        boolean removed = listaMedicamentos.removeIf(m ->m.getNombre().equalsIgnoreCase(nombre));
        if (removed) {
            historial.agregarRegistro("Medicamento removido");
            return "EXITO: Medicamento removido";
        }
        return "ERROR: medicamento no encontrado";
    }

    public String agregarRecordatorio(Recordatorio r) {
        listaRecordatorios.add(r);
        return "EXITO: recordatorio agregado";
    }

    public List<String> verificarRecordatoriosActivos() {
        List<String> activos = new ArrayList<>();
        for (Recordatorio r : listaRecordatorios) {
            if (r.esHoraDeTomar()) {
                //CORRECCION: se llama a metodo renombrado formatearInformacion()
                activos.add("Activo: " + r.formatearInformacion());
            }
        }
        return activos;
    }

    //CORRECCION: se renombran metodos por buenas practicas
    public String generarReporteMedicamentos() {
        if (listaMedicamentos.isEmpty()) {
            return "No hay medicamentos registrados";
        }
        StringBuilder sb = new StringBuilder("Medicamentos:\n");
        for (Medicamento m : listaMedicamentos) {
            //CORRECCION: se llama a metodo renombrado
            sb.append("- ").append(m.formatearInformacion()).append("\n");
        }
        return sb.toString();
    }

    public String generarReporteRecordatorios() {
        if (listaRecordatorios.isEmpty()) {
            return "No hay recordatorios configurados";
        }
        StringBuilder sb = new StringBuilder("Recordatorios:\n");
        for (Recordatorio r : listaRecordatorios) {
            sb.append(r.formatearInformacion()).append("\n");
        }
        return sb.toString();
    }

    public String generarReporteHistorial() {
        return historial.formatearHistorial();
    }

    public List<Medicamento> getListaMedicamentos() {
        return new ArrayList<>(listaMedicamentos);
    }
    public List<Recordatorio> getListaRecordatorios() {
        return new ArrayList<>(listaRecordatorios);
    }
    public HistorialMedico getHistorial() {
        return historial;
    }

    public void cargarMedicamento(Medicamento m) {
        if (m != null) {
            this.listaMedicamentos.add(m);
        }
    }
    public void cargarRecordatorio(Recordatorio r) {
        if (r != null) {
            this.listaRecordatorios.add(r);
        }
    }
    public void limpiarDatosCargados() {
        this.listaMedicamentos.clear();
        this.listaRecordatorios.clear();
        this.historial.limpiar();
    }

    //GETTERS Y SETTERS
    public String getRut() { return rut; }
    public void setRut(String rut) { this.rut = rut; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo o vacio");
        }
        this.nombre = nombre.trim();
    }
    public int getEdad() { return edad; }
    public void setEdad(int edad) {
        if (edad < 1 || edad > 99) {
            throw new IllegalArgumentException("La edad debe ser entre 1 y 99");
        }
        this.edad = edad;
    }

    @Override
    public String toString() {
        return "Paciente{" + "rut='" + rut + "', nombre='" + nombre + "', edad=" + edad + "}";
    }
}
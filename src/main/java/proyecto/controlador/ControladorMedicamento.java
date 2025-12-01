package proyecto.controlador;

import proyecto.modelo.Paciente;
import proyecto.modelo.Medicamento;
import proyecto.modelo.Recordatorio;
import proyecto.modelo.GestorDatosPaciente;
import java.util.List;

public class ControladorMedicamento {
    private final Paciente paciente;
    private final GestorDatosPaciente gestorDatos;

    public ControladorMedicamento(Paciente paciente) {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser nulo");
        }
        this.paciente = paciente;
        this.gestorDatos = new GestorDatosPaciente(paciente);
    }

    public String agregarMedicamento(Medicamento m) {
        String resultado = paciente.agregarMedicamento(m);
        if (resultado.startsWith("EXITO")) {
            gestorDatos.guardarDatos();
        }
        return resultado;
    }

    public String tomarMedicamento(String nombre) {
        String resultado = paciente.tomarMedicamento(nombre);
        if (resultado.startsWith("EXITO")) {
            gestorDatos.guardarDatos();
        }
        return resultado;
    }

    public String removerMedicamento(String nombre) {
        String resultado = paciente.removerMedicamento(nombre);
        if (resultado.startsWith("EXITO")) {
            gestorDatos.guardarDatos();
        }
        return resultado;
    }

    public String agregarRecordatorio(Recordatorio r) {
        try {
            if (r == null) {
                return "ERROR: El recordatorio no puede ser nulo";
            }
            //verificar si el medicamento existe
            if (paciente.getListaMedicamentos().stream().noneMatch(existing ->
                    existing.getNombre().equalsIgnoreCase(r.getMedicamentoAsociado().getNombre()))) {
                return "ERROR: El medicamento no se encuentra registrado";
            }
            String resultado = paciente.agregarRecordatorio(r);
            if (resultado.startsWith("EXITO")) {
                gestorDatos.guardarDatos();
            }
            return resultado;
        } catch (IllegalArgumentException e) {
            return "ERROR de validacion: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("Error inesperado en agregarRecordatorio: " + e.getMessage());
            return "ERROR inesperado: " + e.getMessage();
        }
    }

    public List<String> verificarRecordatoriosActivos() {
        return paciente.verificarRecordatoriosActivos();
    }

    //CORRECCION: renombrar metodos para implementar buenas practicas
    public String generarReporteMedicamentos() {
        return paciente.generarReporteMedicamentos();
    }

    public String generarReporteRecordatorios() {
        return paciente.generarReporteRecordatorios();
    }

    public String generarReporteHistorial() {
        return paciente.generarReporteHistorial();
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public List<String> listarNombresMedicamentos() {
        return paciente.getListaMedicamentos().stream()
                .map(Medicamento::getNombre)
                .toList();
    }
}
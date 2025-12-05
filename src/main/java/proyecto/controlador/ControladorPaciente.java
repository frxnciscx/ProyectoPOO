package proyecto.controlador;

import proyecto.modelo.Paciente;
import proyecto.modelo.datos.RepositorioPaciente;
import proyecto.modelo.datos.GestorDatosPaciente;
import java.util.Optional;

public class ControladorPaciente {
    private final RepositorioPaciente repositorioPaciente;

    public ControladorPaciente() {
        this.repositorioPaciente = new RepositorioPaciente();
    }

    //MODIFICACION: eliminacion del parametro clave
    public String registrarPaciente(String rut, String nombre, int edad) {
        if (rut == null || rut.trim().isEmpty()) {
            return "ERROR: El RUT no puede ser nulo o vacio";
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return "ERROR: El nombre no puede ser nulo o vacio";
        }
        if (edad < 1 || edad > 99) {
            return "ERROR: La edad debe ser entre 1 y 99";
        }
        return repositorioPaciente.registrarPaciente(rut.trim(), nombre.trim(), edad);
    }

    //MODIFICACION: login simplificado solo con RUT
    public Optional<Paciente> loginPaciente(String rut) {
        if (rut == null || rut.trim().isEmpty()) {
            return Optional.empty();
        }
        //CORRECCION: metodo renombrado a buscarPaciente (antes obtenerPaciente)
        Optional<Paciente> optPaciente = repositorioPaciente.buscarPaciente(rut.trim());
        if (optPaciente.isPresent()) {
            Paciente p = optPaciente.get();
            try {
                GestorDatosPaciente gestor = new GestorDatosPaciente(p);
                gestor.cargarDatos();
            } catch (Exception e) {
                System.err.println("ERROR: No se pudieron cargar los datos para " + p.getRut() + ": " + e.getMessage());
            }
            return Optional.of(p);
        }
        return Optional.empty();
    }

    //MODIFICACION: eliminacion del parámetro clave
    public String procesarLogin(String rut) {
        Optional<Paciente> opt = loginPaciente(rut);
        if (opt.isPresent()) {
            return "EXITO: Bienvenido, " + opt.get().getNombre();
        } else {
            return "ERROR: RUT no encontrado, registrese primero";
        }
    }

    public String listarPacientes() {
        return "Pacientes registrados: " + repositorioPaciente.listarPacientes().size();
    }
}
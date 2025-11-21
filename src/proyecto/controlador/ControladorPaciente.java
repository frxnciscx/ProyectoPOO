package proyecto.controlador;

import proyecto.modelo.Paciente;
import proyecto.modelo.RepositorioPaciente;
import proyecto.modelo.GestorDatosPaciente;
import java.util.Optional;

public class ControladorPaciente {
    private final RepositorioPaciente repositorioPaciente;

    public ControladorPaciente() {
        this.repositorioPaciente = new RepositorioPaciente();
    }

    public String registrarPaciente(String rut, String nombre, int edad, String clave) {
        if (rut == null || rut.trim().isEmpty()) {
            return "ERROR: El RUT no puede ser nulo o vacio";
        }
        if (nombre == null || nombre.trim().isEmpty()) {
            return "ERROR: El nombre no puede ser nulo o vacio";
        }
        if (clave == null || clave.trim().isEmpty()) {
            return "ERROR: La clave no puede ser nula o vacia";
        }
        if (edad < 1 || edad > 99) {
            return "ERROR: La edad debe ser entre 1 y 99";
        }
        return repositorioPaciente.registrarPaciente(rut.trim(), nombre.trim(), edad, clave);
    }

    public Optional<Paciente> loginPaciente(String rut, String clave) {
        if (rut == null || rut.trim().isEmpty() || clave == null || clave.trim().isEmpty()) {
            return Optional.empty();
        }
        //CORRECCION: metodo renombrado a buscarPaciente (antes obtenerPaciente)
        Optional<Paciente> optPaciente = repositorioPaciente.buscarPaciente(rut.trim());
        if (optPaciente.isPresent()) {
            Paciente p = optPaciente.get();
            if (p.getClave().equals(clave.trim())) {
                try {
                    GestorDatosPaciente gestor = new GestorDatosPaciente(p);
                    gestor.cargarDatos();
                } catch (Exception e) {
                    System.err.println("ERROR: No se pudieron cargar los datos para " + p.getRut() + ": " + e.getMessage());
                }
                return Optional.of(p);
            }
        }
        return Optional.empty();
    }

    public String procesarLogin(String rut, String clave) {
        Optional<Paciente> opt = loginPaciente(rut, clave);
        if (opt.isPresent()) {
            return "EXITO: Bienvenido, " + opt.get().getNombre();
        } else {
            return "ERROR: RUT o clave incorrectos";
        }
    }

    public String listarPacientes() {
        return "Pacientes registrados: " + repositorioPaciente.listarPacientes().size();
    }
}
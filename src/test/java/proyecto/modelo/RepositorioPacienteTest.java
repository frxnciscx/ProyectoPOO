package proyecto.modelo;

import org.junit.jupiter.api.Test;
import proyecto.modelo.datos.RepositorioPaciente;

import static org.junit.jupiter.api.Assertions.*;

class RepositorioPacienteTest {
    @Test
    void testRegistrarYBuscar() {
        RepositorioPaciente repo = new RepositorioPaciente();

        String msg = repo.registrarPaciente("12345678-9", "Juan", 30);
        assertTrue(msg.contains("EXITO"));

        assertTrue(repo.existePaciente("12345678-9"));
    }

    @Test
    void testEvitarDuplicados() {
        RepositorioPaciente repo = new RepositorioPaciente();

        repo.registrarPaciente("11111111-1", "A", 20);
        String msg = repo.registrarPaciente("11111111-1", "B", 50);

        assertTrue(msg.contains("ERROR"));
    }
}

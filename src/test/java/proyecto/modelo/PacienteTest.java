package proyecto.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PacienteTest {

    @Test
    void testConstructorValido() {
        Paciente p = new Paciente("12345678-9", "Juan Pérez", 30);

        assertNotNull(p);
        assertEquals("Juan Pérez", p.getNombre());
        assertEquals(30, p.getEdad());
        assertEquals("12345678-9", p.getRut());
    }

    @Test
    void testConstructorRutInvalido() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            new Paciente("1-1", "Juan", 30);
        });
        assertTrue(exception.getMessage().contains("Formato de RUT inválido"));
    }

    @Test
    void testConstructorRutConK() {
        Paciente p = new Paciente("12345678-k", "Ana", 25);
        assertEquals("12345678-K", p.getRut());
    }

    @Test
    void testConstructorEdadInvalida() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Paciente("12345678-5", "Juan", -50);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Paciente("12345678-5", "Juan", 100);
        });
    }
}
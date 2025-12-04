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
    void testAgregarMedicamento() {
        Paciente p = new Paciente("11111111-1", "A", 20);
        Medicamento m = new Medicamento("Paracetamol", 100, 10, "01/01/2030");

        String r = p.agregarMedicamento(m);
        assertTrue(r.contains("EXITO"));
        assertEquals(1, p.getListaMedicamentos().size());
    }

    @Test
    void testAgregarMedicamentoDuplicado() {
        Paciente p = new Paciente("11111111-1", "A", 20);
        p.agregarMedicamento(new Medicamento("Para", 10, 10, "01/01/2030"));

        String r2 = p.agregarMedicamento(new Medicamento("Para", 5, 2, "01/01/2030"));
        assertTrue(r2.contains("ERROR"));
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

    @Test
    void testTomarMedicamento() {
        Paciente p = new Paciente("11111111-1", "A", 20);
        p.agregarMedicamento(new Medicamento("Para", 10, 2, "01/01/2030"));

        String msg = p.tomarMedicamento("Para");

        assertTrue(msg.contains("Stock de medicamento"));
    }

    @Test
    void testRemoverMedicamento() {
        Paciente p = new Paciente("11111111-1", "A", 20);
        p.agregarMedicamento(new Medicamento("Para", 10, 2, "01/01/2030"));

        String res = p.removerMedicamento("Para");

        assertTrue(res.contains("EXITO"));
        assertEquals(0, p.getListaMedicamentos().size());
    }
}
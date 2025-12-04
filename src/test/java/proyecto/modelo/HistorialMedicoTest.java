package proyecto.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HistorialMedicoTest {
    @Test
    void testAgregarRegistro() {
        HistorialMedico h = new HistorialMedico();
        h.agregarRegistro("Tomó medicamento");

        assertEquals(1, h.getRegistros().size());
    }

    @Test
    void testAgregarRegistroInvalido() {
        HistorialMedico h = new HistorialMedico();
        assertThrows(IllegalArgumentException.class, () -> h.agregarRegistro(""));
    }

    @Test
    void testLimpiarHistorial() {
        HistorialMedico h = new HistorialMedico();
        h.agregarRegistro("X");
        h.limpiar();

        assertEquals(0, h.getRegistros().size());
    }

    @Test
    void testCSV() {
        HistorialMedico h = new HistorialMedico();
        h.agregarRegistro("Algo");

        String csv = h.toCSV();
        assertTrue(csv.contains("Algo"));
    }
}

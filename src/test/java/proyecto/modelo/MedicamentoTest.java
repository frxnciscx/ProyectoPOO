package proyecto.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MedicamentoTest {

    @Test
    void testMedicamentoVencido() {
        Medicamento m = new Medicamento("Vencido", 100, 10, "01/11/2025");
        assertTrue(m.estaVencido(), "El medicamento deberia marcarse como vencido");
    }

    @Test
    void testMedicamentoNoVencido() {
        Medicamento m = new Medicamento("Valido", 100, 10, "01/01/2027");
        assertFalse(m.estaVencido(), "El medicamento NO deberia estar vencido");
    }

    @Test
    void testDisminuirCantidad() {
        Medicamento m = new Medicamento("Paracetamol", 100, 2, "01/01/2027");

        // consumimos 1
        m.disminuirCantidad();
        assertEquals(1, m.getCantidad());

        // consumimos otro (queda 0)
        m.disminuirCantidad();
        assertEquals(0, m.getCantidad());

        // intentar consumir sin stock
        assertThrows(IllegalStateException.class, () -> {
            m.disminuirCantidad();
        });
    }
}
package proyecto.modelo;

import org.junit.jupiter.api.Test;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class RecordatorioTest {

    @Test
    void testConstructorValido() {
        Medicamento m = new Medicamento("Paracetamol", 500, 20, "01/01/2027");
        LocalTime hora = LocalTime.of(14, 00);

        Recordatorio r = new Recordatorio(hora, 8, m);

        assertNotNull(r);
        assertEquals(8, r.getFrecuenciaHoras());
        assertEquals(hora, r.getHora());
    }

    @Test
    void testConstructorInvalido() {
        Medicamento m = new Medicamento("Ibuprofeno", 400, 10, "01/01/2026");
        LocalTime hora = LocalTime.now();

        //frecuencia negativa o cero
        assertThrows(IllegalArgumentException.class, () -> {
            new Recordatorio(hora, -5, m);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            new Recordatorio(hora, 0, m);
        });

        //medicamento nulo
        assertThrows(IllegalArgumentException.class, () -> {
            new Recordatorio(hora, 8, null);
        });

        //hora nula
        assertThrows(IllegalArgumentException.class, () -> {
            new Recordatorio(null, 8, m);
        });
    }
}
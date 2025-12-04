package proyecto.modelo;

import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RecordatorioTest {

    private void setUltimaToma(Recordatorio r, LocalDateTime t) {
        try {
            Field f = Recordatorio.class.getDeclaredField("ultimaToma");
            f.setAccessible(true);
            f.set(r, t);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

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

    @Test
    void testEsHoraDeTomarPrimeraVezExactamenteLaHora() {
        Medicamento m = new Medicamento("Paracetamol", 500, 20, "01/01/2030");

        // La hora exacta en este instante (sin segundos)
        LocalTime hora = LocalTime.now().withSecond(0).withNano(0);

        Recordatorio r = new Recordatorio(hora, 8, m);

        assertTrue(r.esHoraDeTomar(),
                "Debe activarse exactamente en la hora actual");
    }

    @Test
    void testEsHoraDeTomarPrimeraVezHoraYaPasoHoy() {
        Medicamento m = new Medicamento("Paracetamol", 500, 20, "01/01/2030");

        // Hora que ocurrió hace 1 minuto
        LocalTime haceUnMinuto = LocalTime.now()
                .minusMinutes(1)
                .withSecond(0)
                .withNano(0);

        Recordatorio r = new Recordatorio(haceUnMinuto, 8, m);

        assertTrue(r.esHoraDeTomar(),
                "Debe activarse si la hora del recordatorio ya pasó hoy");
    }

    @Test
    void testNoEsHoraDeTomarPrimeraVez() {
        Medicamento m = new Medicamento("Paracetamol", 500, 20, "01/01/2030");

        // Hora dentro de 1 minuto
        LocalTime dentroDeUnMinuto = LocalTime.now()
                .plusMinutes(1)
                .withSecond(0)
                .withNano(0);

        Recordatorio r = new Recordatorio(dentroDeUnMinuto, 8, m);

        assertFalse(r.esHoraDeTomar(),
                "No debe activarse si todavía no llega la hora");
    }

    @Test
    void testEsHoraDeTomarLuegoDeFrecuencia() {
        Medicamento m = new Medicamento("Ibuprofeno", 400, 10, "01/01/2030");
        LocalTime hora = LocalTime.now().withSecond(0).withNano(0);

        Recordatorio r = new Recordatorio(hora, 1, m);

        // Registrar una toma
        r.registrarTomado();

        // Simular que fue tomado hace exactamente 1 hora
        r.setUltimaTomaForTesting(LocalDateTime.now().minusHours(1));

        assertTrue(r.esHoraDeTomar(),
                "Debe activarse nuevamente cuando se cumple la frecuencia de horas");
    }

    @Test
    void testFromCSV() {
        Medicamento m = new Medicamento("Paracetamol", 500, 10, "01/01/2030");
        Recordatorio r = Recordatorio.fromCSV("14:00;8;Paracetamol", List.of(m));

        assertEquals(8, r.getFrecuenciaHoras());
        assertEquals("Paracetamol", r.getMedicamentoAsociado().getNombre());
    }
}
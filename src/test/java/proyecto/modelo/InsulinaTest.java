package proyecto.modelo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InsulinaTest {

    @Test
    void testConstructorInsulina() {
        Insulina ins = new Insulina("Insulina", 10, 5, "01/01/2030", 70.0);

        assertEquals(70.0, ins.getGlucosaMinima());
        assertEquals("Insulina", ins.getNombre());
    }

    @Test
    void testSePuedeTomar() {
        Insulina ins = new Insulina("Rapida", 5, 10, "01/01/2030", 70.0);

        //caso 1: glucosa suficiente(100)->deberia dejar tomar
        assertTrue(ins.sePuedeTomar(100.0), "Deberia permitir tomar con glucosa 100");

        //caso 2: glucosa muy baja (50)->NO deberia dejar tomar
        assertFalse(ins.sePuedeTomar(50.0), "NO deberia permitir tomar con glucosa 50");

        //caso 3: glucosa exacta (70)->deberia dejar
        assertTrue(ins.sePuedeTomar(70.0), "Deberia permitir tomar con glucosa justa");
    }

    @Test
    void testValidacionGlucosaNegativa() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Insulina("Mala", 10, 5, "01/01/2030", -10.0);
        });
    }
}
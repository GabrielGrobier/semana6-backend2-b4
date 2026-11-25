package cl.taller.tienda.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Arrange común. Preparo un cliente nuevo antes de cada test.
        cliente = new Cliente("juan", "juan.perez@example.com");
    }

    @Test
    void testGetCorreo() {
        // Act. Leo el correo del cliente.
        String correo = cliente.getCorreo();

        // Assert. Comparo el resultado esperado con el obtenido.
        assertEquals("juan.perez@example.com", correo);
    }

    @Test
    void testGetNombre() {
        assertEquals("juan", cliente.getNombre());
    }

    @Test
    void idEsNuloAntesDeGuardar() {
        assertNull(cliente.getId());
    }
}

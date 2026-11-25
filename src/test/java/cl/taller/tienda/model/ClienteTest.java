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
        // Acá solo creo el objeto. JPA asigna el ID cuando lo guardo.
        assertNull(cliente.getId());
    }

    // Camino infeliz. JUnit ejecuta un caso por cada nombre del ejemplo.
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void rechazaNombreInvalido(String nombre) {
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Cliente(nombre, "juan.perez@example.com"));

        assertEquals("Nombre obligatorio", error.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "juan.example.com")
    void rechazaCorreoInvalido(String correo) {
        // Dejo el nombre válido para comprobar solo la regla del correo.
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
                () -> new Cliente("juan", correo));

        assertEquals("Correo invalido", error.getMessage());
    }
}

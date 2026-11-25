package cl.taller.tienda.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ProductoTest {
    private Producto producto;

    @BeforeEach
    void setUp() {
        producto = new Producto("Teclado", 15000, 10);
    }

    @Test
    void testCalcularValorInventario() {
        assertEquals(150000L, producto.calcularValorInventario());
    }

    @Test
    void testDescontarStock() {
        // Arrange
        int cantidad = 3;

        // Act
        producto.descontarStock(cantidad);

        // Assert
        assertEquals(7, producto.getStock());
    }

    @Test
    void conservaLosDatosDelConstructor() {
        assertNull(producto.getId());
        assertEquals("Teclado", producto.getNombre());
        assertEquals(15000, producto.getPrecio());
        assertEquals(10, producto.getStock());
    }

    @Test
    void testReponerStock() {
        producto.reponerStock(5);

        assertEquals(15, producto.getStock());
    }

    @Test
    void testTieneStock() {
        assertTrue(producto.tieneStock());
    }

    @Test
    void quedaSinStockAlDescontarTodasLasUnidades() {
        producto.descontarStock(10);

        assertEquals(0, producto.getStock());
        assertFalse(producto.tieneStock());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void rechazaNombreInvalido(String nombre) {
        assertThrows(IllegalArgumentException.class, () -> new Producto(nombre, 15000, 10));
    }

    @Test
    void rechazaPrecioNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("Teclado", -1, 10));
    }

    // Ejercicio de clase. Quita @Disabled y ejecuta el test antes de corregir el constructor.
    @Disabled("El constructor todavía acepta precio cero")
    @Test
    void rechazaPrecioCero() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("Teclado", 0, 10));
    }

    @Test
    void rechazaStockNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Producto("Teclado", 15000, -1));
    }
}

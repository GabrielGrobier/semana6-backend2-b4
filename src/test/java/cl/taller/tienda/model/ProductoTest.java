package cl.taller.tienda.model;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
class ProductoTest {
    @Test
    void deberiaDescontarStock() {
        // Arrange
        Producto producto = new Producto("Teclado", 15000, 10);
        // Act
        producto.descontarStock(3);
        // Assert
        assertEquals(7, producto.getStock());
    }
    // TODO: agregar casos de exito, error y limite durante el taller.
}

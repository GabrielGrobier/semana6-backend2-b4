package cl.taller.tienda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ProductoTest {
    private Producto producto;

    @BeforeEach 
    void setUp() {
        producto = new Producto("Teclado", 15000, 10);
        // Configuración inicial para cada test
    }

    @Test
    void testCalcularValorInventario() {

    }

    @Test
    void testDescontarStock() {

        int cantidad  = 3;
        int esperado = 7;
        producto.descontarStock(cantidad);

        int obtenido = producto.getStock();
        assertEquals(esperado, obtenido);
        

    }

    @Test
    void testGetId() {

    }

    @Test
    void testGetNombre() {

    }

    @Test
    void testGetPrecio() {

    }

    @Test
    void testGetStock() {

    }

    @Test
    void testReponerStock() {

    }

    @Test
    void testTieneStock() {
        //act 
        boolean tieneStock = producto.tieneStock();
        //assert 
        assertEquals(true, tieneStock);
    }
}

package cl.taller.tienda.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {
    private Cliente cliente;
    private Producto producto;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        cliente = new Cliente("Juan", "juan@example.com");
        producto = new Producto("Teclado", 15000, 10);
        pedido = new Pedido(cliente, producto, 3);
    }

    @Test
    void nacePendienteYCalculaElTotal() {
        assertEquals(EstadoPedido.PENDIENTE, pedido.getEstado());
        assertEquals(15000, pedido.getPrecioUnitario());
        assertEquals(45000L, pedido.calcularTotal());
        assertEquals(10, producto.getStock());
    }

    @Test
    void rechazaClienteOProductoNulo() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null, producto, 3));
        assertThrows(IllegalArgumentException.class, () -> new Pedido(cliente, null, 3));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    void rechazaCantidadInvalida(int cantidad) {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(cliente, producto, cantidad));
    }

    @Test
    void confirmaYDescuentaStock() {
        pedido.confirmar();

        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
        assertEquals(7, producto.getStock());
    }

    @Test
    void siguePendienteCuandoNoHayStockSuficiente() {
        Pedido pedidoGrande = new Pedido(cliente, producto, 11);

        assertThrows(IllegalArgumentException.class, pedidoGrande::confirmar);
        assertEquals(EstadoPedido.PENDIENTE, pedidoGrande.getEstado());
        assertEquals(10, producto.getStock());
    }

    @Test
    void rechazaUnaSegundaConfirmacion() {
        pedido.confirmar();

        assertThrows(IllegalStateException.class, pedido::confirmar);
        assertEquals(EstadoPedido.CONFIRMADO, pedido.getEstado());
        assertEquals(7, producto.getStock());
    }
}

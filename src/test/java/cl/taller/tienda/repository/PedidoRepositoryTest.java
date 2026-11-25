package cl.taller.tienda.repository;

import cl.taller.tienda.model.Cliente;
import cl.taller.tienda.model.EstadoPedido;
import cl.taller.tienda.model.Pedido;
import cl.taller.tienda.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class PedidoRepositoryTest {
    @Autowired ClienteRepository clientes;
    @Autowired ProductoRepository productos;
    @Autowired PedidoRepository pedidos;
    @Autowired TestEntityManager entityManager;

    @Test
    void recuperaPedidoConfirmadoConSuClienteYProducto() {
        // Primero guardo el cliente y el producto porque el pedido depende de ambos.
        Cliente cliente = clientes.save(new Cliente("Juan", "juan@example.com"));
        Producto producto = productos.save(new Producto("Teclado", 15000, 10));
        Pedido pedido = new Pedido(cliente, producto, 3);
        pedido.confirmar();

        Pedido guardado = pedidos.saveAndFlush(pedido);
        entityManager.clear();
        Pedido encontrado = pedidos.findById(guardado.getId()).orElseThrow();

        assertEquals(EstadoPedido.CONFIRMADO, encontrado.getEstado());
        assertEquals(cliente.getId(), encontrado.getCliente().getId());
        assertEquals("juan@example.com", encontrado.getCliente().getCorreo());
        assertEquals(producto.getId(), encontrado.getProducto().getId());
        assertEquals(7, encontrado.getProducto().getStock());
        assertEquals(45000L, encontrado.calcularTotal());
    }
}

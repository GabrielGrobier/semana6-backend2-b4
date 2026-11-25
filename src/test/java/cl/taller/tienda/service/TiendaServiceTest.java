package cl.taller.tienda.service;

import cl.taller.tienda.model.Cliente;
import cl.taller.tienda.model.EstadoPedido;
import cl.taller.tienda.model.Pedido;
import cl.taller.tienda.model.Producto;
import cl.taller.tienda.repository.ClienteRepository;
import cl.taller.tienda.repository.PedidoRepository;
import cl.taller.tienda.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TiendaServiceTest {
    // Mockito prepara los repositorios simulados e inyecta los tres en el servicio.
    @Mock ClienteRepository clientes;
    @Mock ProductoRepository productos;
    @Mock PedidoRepository pedidos;
    @InjectMocks TiendaService tiendaService;

    private final Cliente cliente = new Cliente("Juan", "juan@example.com");
    private final Producto producto = new Producto("Teclado", 15000, 10);
    private final Pedido pedido = new Pedido(cliente, producto, 3);

    @Test
    void creaClienteConLosDatosRecibidos() {
        when(clientes.save(any(Cliente.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Cliente resultado = tiendaService.crearCliente("Juan", "juan@example.com");

        assertEquals("Juan", resultado.getNombre());
        assertEquals("juan@example.com", resultado.getCorreo());
        verify(clientes).save(resultado);
    }

    @Test
    void creaProductoConLosDatosRecibidos() {
        // El repositorio simulado devuelve el mismo producto que recibió en save.
        when(productos.save(any(Producto.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Producto resultado = tiendaService.crearProducto("Teclado", 15000, 10);

        assertEquals("Teclado", resultado.getNombre());
        assertEquals(15000, resultado.getPrecio());
        assertEquals(10, resultado.getStock());
        verify(productos).save(resultado);
    }

    @Test
    void listaClientes() {
        when(clientes.findAll()).thenReturn(List.of(cliente));

        assertEquals(List.of(cliente), tiendaService.listarClientes());
    }

    @Test
    void listaProductos() {
        when(productos.findAll()).thenReturn(List.of(producto));

        assertEquals(List.of(producto), tiendaService.listarProductos());
    }

    @Test
    void listaPedidos() {
        when(pedidos.findAll()).thenReturn(List.of(pedido));

        assertEquals(List.of(pedido), tiendaService.listarPedidos());
    }

    @Test
    void encuentraPedidoPorId() {
        when(pedidos.findById(1L)).thenReturn(Optional.of(pedido));

        assertSame(pedido, tiendaService.buscarPedido(1L));
    }

    @Test
    void informaQueElPedidoNoExiste() {
        when(pedidos.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException error = assertThrows(NoSuchElementException.class,
                () -> tiendaService.buscarPedido(1L));

        assertEquals("Pedido no encontrado", error.getMessage());
    }

    @Test
    void creaPedidoConElClienteYElProductoEncontrados() {
        when(clientes.findById(1L)).thenReturn(Optional.of(cliente));
        when(productos.findById(2L)).thenReturn(Optional.of(producto));
        when(pedidos.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0));

        Pedido resultado = tiendaService.crearPedido(1L, 2L, 3);

        assertSame(cliente, resultado.getCliente());
        assertSame(producto, resultado.getProducto());
        assertEquals(3, resultado.getCantidad());
        assertEquals(EstadoPedido.PENDIENTE, resultado.getEstado());
        verify(pedidos).save(resultado);
    }

    @Test
    void noGuardaPedidoSiFaltaElCliente() {
        when(clientes.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException error = assertThrows(NoSuchElementException.class,
                () -> tiendaService.crearPedido(1L, 2L, 3));

        assertEquals("Cliente no encontrado", error.getMessage());
        verifyNoInteractions(pedidos);
    }

    @Test
    void noGuardaPedidoSiFaltaElProducto() {
        when(clientes.findById(1L)).thenReturn(Optional.of(cliente));
        when(productos.findById(2L)).thenReturn(Optional.empty());

        NoSuchElementException error = assertThrows(NoSuchElementException.class,
                () -> tiendaService.crearPedido(1L, 2L, 3));

        assertEquals("Producto no encontrado", error.getMessage());
        verifyNoInteractions(pedidos);
    }

    @Test
    void confirmaYGuardaPedido() {
        when(pedidos.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidos.save(pedido)).thenReturn(pedido);

        Pedido resultado = tiendaService.confirmarPedido(1L);

        assertEquals(EstadoPedido.CONFIRMADO, resultado.getEstado());
        assertEquals(7, producto.getStock());
        verify(pedidos).save(pedido);
    }

    @Test
    void cancelaYGuardaPedido() {
        when(pedidos.findById(1L)).thenReturn(Optional.of(pedido));
        when(pedidos.save(pedido)).thenReturn(pedido);

        Pedido resultado = tiendaService.cancelarPedido(1L);

        assertEquals(EstadoPedido.CANCELADO, resultado.getEstado());
        verify(pedidos).save(pedido);
    }

    @Test
    void noGuardaSiFallaLaConfirmacion() {
        pedido.cancelar();
        when(pedidos.findById(1L)).thenReturn(Optional.of(pedido));

        assertThrows(IllegalStateException.class, () -> tiendaService.confirmarPedido(1L));

        // Con never compruebo que el servicio no llegó a guardar el pedido.
        verify(pedidos, never()).save(any(Pedido.class));
    }
}

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
}

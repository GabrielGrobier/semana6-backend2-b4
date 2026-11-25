package cl.taller.tienda.controller;

import cl.taller.tienda.model.Cliente;
import cl.taller.tienda.model.Pedido;
import cl.taller.tienda.model.Producto;
import cl.taller.tienda.service.TiendaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TiendaController.class)
class TiendaControllerTest {
    // Con MockMvc envío peticiones al controlador sin levantar un servidor.
    @Autowired MockMvc mvc;
    @MockBean TiendaService tiendaService;

    private final Cliente cliente = new Cliente("Juan", "juan@example.com");
    private final Producto producto = new Producto("Teclado", 15000, 10);
    private final Pedido pedido = new Pedido(cliente, producto, 3);

    @Test
    void listaClientes() throws Exception {
        when(tiendaService.listarClientes()).thenReturn(List.of(cliente));

        mvc.perform(get("/api/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("juan@example.com"));
    }

    @Test
    void creaClienteYDevuelve201() throws Exception {
        when(tiendaService.crearCliente("Juan", "juan@example.com")).thenReturn(cliente);

        mvc.perform(post("/api/clientes").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Juan","correo":"juan@example.com"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.correo").value("juan@example.com"));
    }

    @Test
    void listaProductos() throws Exception {
        when(tiendaService.listarProductos()).thenReturn(List.of(producto));

        mvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Teclado"));
    }

    @Test
    void creaProductoYDevuelve201() throws Exception {
        when(tiendaService.crearProducto("Teclado", 15000, 10)).thenReturn(producto);

        mvc.perform(post("/api/productos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre":"Teclado","precio":15000,"stock":10}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.precio").value(15000))
                .andExpect(jsonPath("$.stock").value(10));
    }

    @Test
    void listaPedidos() throws Exception {
        when(tiendaService.listarPedidos()).thenReturn(List.of(pedido));

        mvc.perform(get("/api/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidad").value(3));
    }

    @Test
    void buscaPedidoPorId() throws Exception {
        when(tiendaService.buscarPedido(1L)).thenReturn(pedido);

        mvc.perform(get("/api/pedidos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void creaPedidoYDevuelve201() throws Exception {
        when(tiendaService.crearPedido(1L, 2L, 3)).thenReturn(pedido);

        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":1,"productoId":2,"cantidad":3}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cantidad").value(3))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void confirmaPedido() throws Exception {
        pedido.confirmar();
        when(tiendaService.confirmarPedido(1L)).thenReturn(pedido);

        mvc.perform(patch("/api/pedidos/{id}/confirmar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMADO"));
    }

    @Test
    void cancelaPedido() throws Exception {
        pedido.cancelar();
        when(tiendaService.cancelarPedido(1L)).thenReturn(pedido);

        mvc.perform(patch("/api/pedidos/{id}/cancelar", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
    }

    // Camino infeliz. Simulo la excepción y reviso cómo la API la traduce a HTTP.
    @Test
    void devuelve400CuandoLaCantidadEsInvalida() throws Exception {
        when(tiendaService.crearPedido(1L, 2L, 0))
                .thenThrow(new IllegalArgumentException("Cantidad invalida"));

        mvc.perform(post("/api/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"clienteId":1,"productoId":2,"cantidad":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Cantidad invalida"));
    }

    @Test
    void devuelve404CuandoElPedidoNoExiste() throws Exception {
        when(tiendaService.buscarPedido(99L)).thenThrow(new NoSuchElementException("Pedido no encontrado"));

        mvc.perform(get("/api/pedidos/{id}", 99L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Pedido no encontrado"));
    }

    @Test
    void devuelve409CuandoElPedidoYaFueCancelado() throws Exception {
        when(tiendaService.cancelarPedido(1L)).thenThrow(new IllegalStateException("Pedido ya cancelado"));

        mvc.perform(patch("/api/pedidos/{id}/cancelar", 1L))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Pedido ya cancelado"));
    }
}

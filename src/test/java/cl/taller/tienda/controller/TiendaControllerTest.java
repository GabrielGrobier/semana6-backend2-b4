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
}

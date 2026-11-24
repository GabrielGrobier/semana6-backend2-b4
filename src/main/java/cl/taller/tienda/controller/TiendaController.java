package cl.taller.tienda.controller;
import cl.taller.tienda.model.*;
import cl.taller.tienda.service.TiendaService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api")
public class TiendaController {
    private final TiendaService service;
    public TiendaController(TiendaService service) { this.service = service; }
    public record ClienteRequest(String nombre, String correo) {}
    public record ProductoRequest(String nombre, int precio, int stock) {}
    public record PedidoRequest(long clienteId, long productoId, int cantidad) {}
    @GetMapping("/clientes") public List<Cliente> clientes() { return service.listarClientes(); }
    @PostMapping("/clientes") @ResponseStatus(HttpStatus.CREATED)
    public Cliente crearCliente(@RequestBody ClienteRequest r) { return service.crearCliente(r.nombre(), r.correo()); }
    @GetMapping("/productos") public List<Producto> productos() { return service.listarProductos(); }
    @PostMapping("/productos") @ResponseStatus(HttpStatus.CREATED)
    public Producto crearProducto(@RequestBody ProductoRequest r) { return service.crearProducto(r.nombre(), r.precio(), r.stock()); }
    @GetMapping("/pedidos") public List<Pedido> pedidos() { return service.listarPedidos(); }
    @GetMapping("/pedidos/{id}") public Pedido pedido(@PathVariable long id) { return service.buscarPedido(id); }
    @PostMapping("/pedidos") @ResponseStatus(HttpStatus.CREATED)
    public Pedido crearPedido(@RequestBody PedidoRequest r) { return service.crearPedido(r.clienteId(), r.productoId(), r.cantidad()); }
    @PatchMapping("/pedidos/{id}/confirmar") public Pedido confirmar(@PathVariable long id) { return service.confirmarPedido(id); }
    @PatchMapping("/pedidos/{id}/cancelar") public Pedido cancelar(@PathVariable long id) { return service.cancelarPedido(id); }
}

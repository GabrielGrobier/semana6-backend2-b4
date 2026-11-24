package cl.taller.tienda.service;
import cl.taller.tienda.model.*;
import cl.taller.tienda.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
@Service
@Transactional
public class TiendaService {
    private final ClienteRepository clientes;
    private final ProductoRepository productos;
    private final PedidoRepository pedidos;
    public TiendaService(ClienteRepository clientes, ProductoRepository productos, PedidoRepository pedidos) {
        this.clientes = clientes; this.productos = productos; this.pedidos = pedidos;
    }
    public Cliente crearCliente(String nombre, String correo) { return clientes.save(new Cliente(nombre, correo)); }
    public Producto crearProducto(String nombre, int precio, int stock) { return productos.save(new Producto(nombre, precio, stock)); }
    public List<Cliente> listarClientes() { return clientes.findAll(); }
    public List<Producto> listarProductos() { return productos.findAll(); }
    public List<Pedido> listarPedidos() { return pedidos.findAll(); }
    public Pedido buscarPedido(long id) { return pedidos.findById(id).orElseThrow(() -> new NoSuchElementException("Pedido no encontrado")); }
    public Pedido crearPedido(long clienteId, long productoId, int cantidad) {
        Cliente cliente = clientes.findById(clienteId).orElseThrow(() -> new NoSuchElementException("Cliente no encontrado"));
        Producto producto = productos.findById(productoId).orElseThrow(() -> new NoSuchElementException("Producto no encontrado"));
        return pedidos.save(new Pedido(cliente, producto, cantidad));
    }
    public Pedido confirmarPedido(long id) { Pedido pedido = buscarPedido(id); pedido.confirmar(); return pedidos.save(pedido); }
    public Pedido cancelarPedido(long id) { Pedido pedido = buscarPedido(id); pedido.cancelar(); return pedidos.save(pedido); }
}

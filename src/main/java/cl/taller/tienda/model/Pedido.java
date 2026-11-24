package cl.taller.tienda.model;
import jakarta.persistence.*;
@Entity
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) private Cliente cliente;
    @ManyToOne(optional = false) private Producto producto;
    private int cantidad;
    private int precioUnitario;
    @Enumerated(EnumType.STRING) private EstadoPedido estado;
    protected Pedido() {}
    public Pedido(Cliente cliente, Producto producto, int cantidad) {
        if (cliente == null || producto == null) throw new IllegalArgumentException("Cliente y producto obligatorios");
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad invalida");
        this.cliente = cliente;
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecio();
        this.estado = EstadoPedido.PENDIENTE;
    }
    public long calcularTotal() { return (long) precioUnitario * cantidad; }
    public void confirmar() {
        if (estado != EstadoPedido.PENDIENTE) throw new IllegalStateException("Pedido no pendiente");
        producto.descontarStock(cantidad);
        estado = EstadoPedido.CONFIRMADO;
    }
    public void cancelar() {
        if (estado == EstadoPedido.CANCELADO) throw new IllegalStateException("Pedido ya cancelado");
        if (estado == EstadoPedido.CONFIRMADO) producto.reponerStock(cantidad);
        estado = EstadoPedido.CANCELADO;
    }
    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public int getPrecioUnitario() { return precioUnitario; }
    public EstadoPedido getEstado() { return estado; }
}

package cl.taller.tienda.model;
import jakarta.persistence.*;
@Entity
public class Producto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private int precio;
    private int stock;
    protected Producto() {}
    public Producto(String nombre, int precio, int stock) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        // Defecto didactico: el requisito exige precio estrictamente positivo.
        if (precio < 0) throw new IllegalArgumentException("Precio invalido");
        if (stock < 0) throw new IllegalArgumentException("Stock invalido");
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
    }
    public boolean tieneStock() { return stock > 0; }
    public long calcularValorInventario() { return (long) precio * stock; }
    public void descontarStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad invalida");
        if (cantidad > stock) throw new IllegalArgumentException("Stock insuficiente");
        stock -= cantidad;
    }
    public void reponerStock(int cantidad) {
        if (cantidad <= 0) throw new IllegalArgumentException("Cantidad invalida");
        stock = Math.addExact(stock, cantidad);
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public int getStock() { return stock; }
}

package cl.taller.tienda.model;
import jakarta.persistence.*;
@Entity
public class Cliente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String correo;
    protected Cliente() {}
    public Cliente(String nombre, String correo) {
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre obligatorio");
        if (correo == null || !correo.contains("@")) throw new IllegalArgumentException("Correo invalido");
        this.nombre = nombre;
        this.correo = correo;
    }
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
}

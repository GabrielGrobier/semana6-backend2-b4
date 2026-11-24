package cl.taller.tienda.repository;
import cl.taller.tienda.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
public interface ProductoRepository extends JpaRepository<Producto, Long> {}

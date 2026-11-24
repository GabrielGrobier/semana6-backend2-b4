package cl.taller.tienda.repository;
import cl.taller.tienda.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PedidoRepository extends JpaRepository<Pedido, Long> {}

package cl.taller.tienda.repository;

import cl.taller.tienda.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductoRepositoryTest {
    @Autowired ProductoRepository productos;
    @Autowired TestEntityManager entityManager;

    @Test
    void guardaYRecuperaProductoPorId() {
        Producto guardado = productos.saveAndFlush(new Producto("Teclado", 15000, 10));

        // Limpio el contexto de JPA para que findById vuelva a consultar H2.
        entityManager.clear();
        Producto encontrado = productos.findById(guardado.getId()).orElseThrow();

        assertNotNull(encontrado.getId());
        assertEquals("Teclado", encontrado.getNombre());
        assertEquals(15000, encontrado.getPrecio());
        assertEquals(10, encontrado.getStock());
    }
}

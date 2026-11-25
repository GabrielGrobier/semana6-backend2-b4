package cl.taller.tienda.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


// A A A
// Arrange --> Preparo los datos y objetos  
// Act  --> Ejecuto las funcionalidades 
// Assert  --> Verifico el resultado obtenido 



public class ClienteTest {
    private Cliente cliente;
    

    @BeforeEach 
    void setUp() {
        
        cliente = new Cliente("juan","juan.perez@example.com");
        // Configuración inicial para cada test
    }


    @Test
    void testGetCorreo() {

        // Act
        String correo = cliente.getCorreo();

        // Assert
        assertEquals("juan.perez@example.com", correo);

    }

    @Test
    void testGetId() {


    }

    @Test
    void testGetNombre() {
        

    }
    // camino infeliz 
    @Test 
    void tesNombreNule(){
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class,
            () -> new Cliente(null, "juan.perez@example.com"));

        assertEquals("Nombre obligatorio", error);


    }


}

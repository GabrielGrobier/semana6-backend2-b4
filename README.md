# Guía de estudio de pruebas en Java

## Contenido

1. [Qué es una prueba unitaria y por qué importa](#1-qué-es-una-prueba-unitaria-y-por-qué-importa)
2. [Cómo construir una prueba con JUnit 5](#2-cómo-construir-una-prueba-con-junit-5)
3. [Cómo usar mocks con Mockito](#3-cómo-usar-mocks-con-mockito)
4. [Cuándo usar pruebas de integración](#4-cuándo-usar-pruebas-de-integración)
5. [Cómo ejecutar las pruebas con Maven](#5-cómo-ejecutar-las-pruebas-con-maven)
6. [Cómo interpretar la cobertura de JaCoCo](#6-cómo-interpretar-la-cobertura-de-jacoco)
7. [Ejercicios para practicar](#7-ejercicios-para-practicar)

## 1. Qué es una prueba unitaria y por qué importa

Una **prueba unitaria** es código que prepara un escenario, ejecuta una unidad pequeña del programa y compara lo que ocurrió con lo que exige una regla. La unidad puede ser un método o una clase. Por ejemplo, si un pedido reserva tres productos de un stock de diez, el resultado esperado es que queden siete unidades.

Las pruebas ayudan a detectar errores al cambiar código, reproducir un problema con datos concretos y documentar reglas mediante ejemplos que se pueden ejecutar. También permiten distinguir dos cosas importantes para el alumno: que una operación devuelva lo esperado y que deje los datos en un estado correcto.

Una prueba unitaria debería poder ejecutarse sola, en cualquier orden y con el mismo resultado. Usa datos controlados y evita depender de servicios externos, una base de datos real o la hora actual. Si lo que quieres comprobar es justamente cómo se guarda un dato o cómo responde una ruta HTTP, conviene probar esa parte con la infraestructura correspondiente.

Para diseñar casos, parte de la regla:

| Tipo de caso | Ejemplo para una operación de stock |
|---|---|
| Camino feliz | Tengo 10, descuento 3 y quedan 7. |
| Límite válido | Tengo 10, descuento 10 y quedan 0. |
| Camino infeliz | Intento descontar 11 de 10 y espero un error. |
| Estado tras el error | Después del rechazo, sigo teniendo 10. |

## 2. Cómo construir una prueba con JUnit 5

**JUnit Jupiter** es la parte de JUnit 5 que usamos para escribir pruebas. La anotación `@Test` identifica un método de prueba. Las aserciones expresan el resultado esperado. En Maven, las pruebas se escriben en `src/test/java` y el código de la aplicación está en `src/main/java`.

### Arrange, Act, Assert

1. **Arrange.** Preparo los datos y el objeto que voy a probar.
2. **Act.** Ejecuto la operación.
3. **Assert.** Comparo el esperado con el resultado obtenido.

Este ejemplo se puede guardar como `StockBasicoTest.java` en `src/test/java/cl/taller/tienda/model`.

```java
package cl.taller.tienda.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StockBasicoTest {
    @Test
    void descontarTresUnidadesDejaSieteEnStock() {
        // Arrange
        Producto producto = new Producto("Teclado", 15_000, 10);

        // Act
        producto.descontarStock(3);

        // Assert
        assertEquals(7, producto.getStock());
    }
}
```

En `assertEquals(esperado, obtenido)`, el valor esperado va primero. Usa valores explícitos que salgan de la regla. Si calculas el esperado llamando al mismo método que estás probando, ambos lados podrían repetir el mismo error.

| Aserción | Cuándo usarla |
|---|---|
| `assertEquals(esperado, obtenido)` | Comparar valores como números, textos o estados. |
| `assertTrue(condicion)` / `assertFalse(condicion)` | Comprobar una condición booleana. |
| `assertNull(valor)` / `assertNotNull(valor)` | Comprobar si existe un valor. |
| `assertSame(esperado, obtenido)` | Comprobar que ambas referencias apuntan a la misma instancia. |
| `assertThrows(Tipo.class, () -> operacion())` | Ejecutar una operación y comprobar que lanza una excepción del tipo esperado o un subtipo. |

### Probar errores y casos repetidos

En un camino infeliz, pon dentro de `assertThrows` solo la operación que debería fallar. La aserción devuelve la excepción, así puedes verificar su mensaje con `getMessage()` cuando sea parte del comportamiento que quieres comprobar. Después puedes verificar que el estado quedó intacto.

Este ejemplo usa una sola regla con dos entradas inválidas. Se puede agregar a la clase anterior con los imports de `assertThrows`, `org.junit.jupiter.params.ParameterizedTest` y `org.junit.jupiter.params.provider.ValueSource`.

```java
@ParameterizedTest
@ValueSource(ints = {0, -1})
void rechazaCantidadesNoPositivasSinCambiarElStock(int cantidad) {
    Producto producto = new Producto("Teclado", 15_000, 10);

    IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> producto.descontarStock(cantidad)
    );

    assertEquals("Cantidad invalida", error.getMessage());
    assertEquals(10, producto.getStock());
}
```

JUnit ejecuta el método una vez con cada valor. `@NullAndEmptySource` permite añadir casos con `null` y texto vacío, y `@ValueSource(strings = " ")` permite probar texto con espacios. Puedes usar `@BeforeEach` para preparar objetos compartidos por varios escenarios. Se ejecuta antes de cada prueba y antes de cada invocación parametrizada. Si un dato explica el escenario, déjalo dentro del propio test.

Los nombres como `rechazaDescuentoConStockInsuficiente` ayudan a encontrar la regla cuando falla una prueba. Una prueba vacía puede aparecer como aprobada porque terminó sin errores. Asegúrate de que compruebe algo que fallaría si la regla dejara de cumplirse.

Ejemplos para leer en el código. [ClienteTest](src/test/java/cl/taller/tienda/model/ClienteTest.java), [ProductoTest](src/test/java/cl/taller/tienda/model/ProductoTest.java), [PedidoTest](src/test/java/cl/taller/tienda/model/PedidoTest.java), [guía oficial de JUnit 5](https://docs.junit.org/5.10.4/user-guide/).

## 3. Cómo usar mocks con Mockito

Un **doble de prueba** reemplaza a un colaborador durante el test. Un **mock** de Mockito puede devolver respuestas preparadas y registrar llamadas. Esto permite probar un servicio que consulta repositorios sin abrir una conexión a la base de datos.

- **Configurar una respuesta** (*stubbing*). Con `when(...).thenReturn(...)` indico qué devuelve el colaborador para una llamada. Con `thenThrow(...)` simulo un error.
- **Verificar una interacción.** Con `verify(...)` compruebo que el servicio pidió una operación concreta. Esto sirve cuando la llamada es un efecto importante, como guardar un pedido.
- **Comprobar el resultado.** Las aserciones de JUnit revisan lo que el servicio devolvió o cómo cambió el estado.

### Ejemplo completo de un servicio aislado

```java
package cl.taller.tienda.service;

import cl.taller.tienda.model.Cliente;
import cl.taller.tienda.model.Pedido;
import cl.taller.tienda.repository.ClienteRepository;
import cl.taller.tienda.repository.PedidoRepository;
import cl.taller.tienda.repository.ProductoRepository;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PedidoSinProductoTest {
    @Test
    void noGuardaElPedidoSiElProductoNoExiste() {
        // Arrange. El servicio es real y sus repositorios son mocks.
        ClienteRepository clientes = mock(ClienteRepository.class);
        ProductoRepository productos = mock(ProductoRepository.class);
        PedidoRepository pedidos = mock(PedidoRepository.class);
        TiendaService servicio = new TiendaService(clientes, productos, pedidos);

        when(clientes.findById(1L))
                .thenReturn(Optional.of(new Cliente("Juan", "juan@example.com")));
        when(productos.findById(2L)).thenReturn(Optional.empty());

        // Act y Assert. assertThrows ejecuta la llamada de la lambda.
        NoSuchElementException error = assertThrows(
                NoSuchElementException.class,
                () -> servicio.crearPedido(1L, 2L, 3)
        );

        assertEquals("Producto no encontrado", error.getMessage());
        verify(pedidos, never()).save(any(Pedido.class));
    }
}
```

Acá quedan comprobadas dos consecuencias de la misma regla. El servicio informa el problema y, aunque llegó a buscar el producto, jamás intenta guardar un pedido. `any(Pedido.class)` coincide con un argumento de tipo `Pedido` distinto de `null`. Si en una llamada usas un matcher junto a otro argumento literal, usa otro matcher como `eq(valor)` para ese argumento.

### Usar las anotaciones

En una clase con `@ExtendWith(MockitoExtension.class)`, JUnit activa la extensión de Mockito. `@Mock` crea cada repositorio simulado y `@InjectMocks` construye o prepara el servicio real e intenta inyectarle los mocks disponibles. En este caso usa el constructor de `TiendaService`. Es otra forma de preparar el mismo escenario sin escribir cada llamada a `mock(...)`.

Para el camino feliz, puedes configurar el guardado con `when(pedidos.save(any(Pedido.class))).thenAnswer(invocacion -> invocacion.getArgument(0))`. Eso devuelve el pedido que recibió el mock. Si quieres inspeccionar lo que se envió a `save`, puedes usar un `ArgumentCaptor<Pedido>` con `verify` y luego comprobar el objeto capturado. Un guardado simulado no asigna un ID de base de datos por su cuenta.

Dentro de un test que ya ejecutó la creación del pedido, el captor se usa así. Importa `org.mockito.ArgumentCaptor` para usarlo.

```java
ArgumentCaptor<Pedido> captor = ArgumentCaptor.forClass(Pedido.class);
verify(pedidos).save(captor.capture());
assertEquals(3, captor.getValue().getCantidad());
```

Configura solo las llamadas que el escenario necesita. Si Mockito indica que sobra un *stub*, revisa si el flujo realmente alcanza esa llamada. Si la regla se puede probar con un objeto sencillo, como `Producto`, úsalo directamente. Simular la clase que estás probando reemplazaría la lógica que quieres comprobar.

Ejemplos y referencia. [TiendaServiceTest](src/test/java/cl/taller/tienda/service/TiendaServiceTest.java), [documentación de Mockito](https://javadoc.io/static/org.mockito/mockito-core/5.24.0/org.mockito/org/mockito/Mockito.html).

## 4. Cuándo usar pruebas de integración

| Pregunta | Herramienta | Qué comprueba |
|---|---|---|
| ¿La regla de stock o de estado funciona? | JUnit con objetos reales | Validaciones, cálculos y cambios de estado. |
| ¿El servicio coordina bien sus dependencias? | JUnit y Mockito | Qué devuelve y cuándo consulta o guarda. |
| ¿JPA guarda y recupera los datos? | `@DataJpaTest` con H2 | Mapeos, relaciones y persistencia real en una base de prueba. |
| ¿La API devuelve el estado HTTP y JSON esperados? | `@WebMvcTest` y `MockMvc` | Rutas, conversión de JSON y manejo de errores. |

En `@DataJpaTest`, `saveAndFlush` fuerza la sincronización de los cambios pendientes con la base. Después, `entityManager.clear()` limpia el contexto de persistencia para que `findById` vuelva a leer el registro. H2 en memoria es útil para estudiar JPA. Si producción usa otro motor, una prueba en H2 por sí sola no comprueba diferencias entre motores.

En `@WebMvcTest`, Spring Boot 3.3 carga la parte web. `MockMvc` envía una petición simulada a través de Spring MVC sin abrir un servidor HTTP. `@MockBean` registra el servicio simulado en ese contexto. Así puedes comprobar el código de respuesta y campos JSON con `andExpect`, incluido cómo el controlador de excepciones transforma un error en `400`, `404` o `409`.

Ejemplos y referencia. [ProductoRepositoryTest](src/test/java/cl/taller/tienda/repository/ProductoRepositoryTest.java), [PedidoRepositoryTest](src/test/java/cl/taller/tienda/repository/PedidoRepositoryTest.java), [TiendaControllerTest](src/test/java/cl/taller/tienda/controller/TiendaControllerTest.java), [pruebas con Spring Boot 3.3](https://docs.spring.io/spring-boot/3.3/reference/testing/spring-boot-applications.html).

## 5. Cómo ejecutar las pruebas con Maven

Abre una terminal en la carpeta que contiene `pom.xml`. El proyecto tiene Java 17 como versión objetivo. Comprueba con `mvn -version` qué JDK está usando Maven. En el POM, `spring-boot-starter-test` incorpora JUnit Jupiter, Mockito y las herramientas de prueba de Spring.

| Comando | Qué ejecuta |
|---|---|
| `mvn test` | La suite de pruebas. |
| `mvn "-Dtest=ProductoTest" test` | Una clase. |
| `mvn "-Dtest=ProductoTest#testDescontarStock" test` | Un método. |
| `mvn clean verify` | Borra los resultados anteriores, ejecuta el ciclo hasta verificación y genera el informe JaCoCo si la compilación tiene éxito. |

Las comillas alrededor de `-Dtest=...` sirven también en PowerShell. Si un caso falla, busca primero el nombre de la prueba y el mensaje que compara lo esperado con lo obtenido. Maven Surefire deja el detalle en `target/surefire-reports/`. Revisa también los casos omitidos. `@Disabled` evita ejecutar el caso, así que aparecerá como omitido en el resumen.

## 6. Cómo interpretar la cobertura de JaCoCo

**JaCoCo** es una herramienta de cobertura para Java. Registra qué partes del código pasan por la JVM durante las pruebas y combina esos datos con las clases compiladas para generar un informe.

El [POM](pom.xml) configura dos objetivos. `prepare-agent` prepara el agente antes de ejecutar los tests. Este guarda los datos en `target/jacoco.exec`. `report` se ejecuta en la fase `verify` y genera el informe. Para estudiarlo:

1. Ejecuta `mvn clean verify`.
2. Abre `target/site/jacoco/index.html` en tu navegador.
3. Entra a un paquete y luego a una clase para ver el código marcado.
4. Usa las líneas y ramas pendientes para buscar escenarios que todavía no estén probados.

| Métrica | Qué indica |
|---|---|
| Instrucciones | Cuántas instrucciones del bytecode se ejecutaron. |
| Líneas | Una línea cuenta como ejecutada si se ejecutó al menos una de sus instrucciones. El color muestra si la ejecución fue completa, parcial o nula en esa línea. |
| Ramas | Qué resultados de decisiones como `if` y `switch` se recorrieron. Una rama faltante suele indicar una condición que aún no probaste. |

Una línea verde indica ejecución. La aserción es la que demuestra que el resultado se ajusta a la regla. Por ejemplo, ejecutar un constructor con un precio positivo puede cubrir una ruta válida, pero el requisito «el precio debe ser mayor que cero» también necesita casos como `0` y `-1`. Incluso con cobertura alta, revisa los límites, los errores y el resultado después de un fallo. El informe por sí solo tampoco fija un mínimo obligatorio. JaCoCo ofrece el objetivo `check` para configurar umbrales si el proyecto los necesita.

Si falta el HTML, confirma que ejecutaste `verify` y que la compilación llegó a la fase del informe. `mvn test` puede dejar el archivo de datos sin generar el HTML en esta configuración. Referencias. [Plugin de JaCoCo para Maven](https://www.jacoco.org/jacoco/trunk/doc/maven.html), [contadores de cobertura](https://www.jacoco.org/jacoco/trunk/doc/counters.html).

## 7. Ejercicios para practicar

1. **Empieza por una regla.** Escribe tres casos para descontar stock: una cantidad menor que el stock, exactamente el stock y una cantidad superior. Anota de antemano el stock esperado en cada escenario.
2. **Reproduce un defecto.** En una copia de trabajo, quita `@Disabled` de `rechazaPrecioCero` y ejecuta solo ese método. El requisito exige un precio positivo, pero actualmente el constructor permite cero. Mira el fallo, corrige la condición en tu copia y vuelve a ejecutar el método y la suite completa. Ese ciclo muestra que la prueba detectaba el defecto.
3. **Cambia la respuesta de un mock.** Configura un repositorio para devolver un producto y luego prueba el escenario en que devuelve `Optional.empty()`. Identifica qué aserción comprueba el resultado y cuál verifica el efecto sobre el repositorio.
4. **Lee el informe.** Busca una línea de una validación y explica qué entradas usaste para recorrer sus caminos. Luego responde qué pasaría si quitaras las aserciones y dejaras las llamadas al código. La ejecución podría seguir contando para la cobertura, pero la prueba perdería su comprobación.

Antes de dar un test por listo, confirma que se puede ejecutar solo, que su nombre describe la regla y que fallaría si alguien introdujera el error que buscas detectar.

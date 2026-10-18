# Prompts utilizados

Fase 2 de LavaCampus en la rama `con-ia`. La IA usada fue Claude Code (en lugar de Gemini), con el proyecto de la Fase 1 y la guía del laboratorio como contexto. Cada prompt corresponde a un commit.

## Prompt 1 · Pedidos atrasados (commit A)

**Texto enviado a la IA:** Tengo una app Kotlin con Jetpack Compose, Material 3, MVVM, Room y un LavaRepository (interface). Agrega en la bandeja del administrador una marca roja para los pedidos atrasados (fechaEntrega menor que hoy y estado distinto de ENTREGADO y CANCELADO), y que el panel los cuente. La consulta va en PedidoDao, la función en LavaRepository y su implementación, y el valor se expone como StateFlow en BandejaViewModel y PanelViewModel. La View solo lo muestra. No uses XML ni librerías externas. Muéstrame solo los archivos que cambian.

**Qué generó la IA:** La consulta `observarIdsAtrasados(hoy)` en `PedidoDao`, que devuelve un `Flow` con los ids de los pedidos vencidos. La función declarada en `LavaRepository` e implementada en `LavaRepositoryRoom`. Un `StateFlow<Set<Int>>` en `BandejaViewModel` y un conteo en `PanelViewModel`. En la vista, el parámetro `atrasado` de `PedidoCard` (borde rojo y etiqueta) y una alerta roja en el Panel con el botón Ver.

**Qué tuve que corregir yo:** Ningún pedido de los datos iniciales estaba vencido, así que la mejora no se veía. Agregué un pedido de prueba atrasado al final de `DatosIniciales` para no cambiar los números de los pedidos existentes. También revisé que la fecha de hoy se pase como texto `aaaa-MM-dd`, igual que la guarda la tabla, para que la comparación en SQL funcione.

**Respeta MVVM?:** Sí. La consulta está en el DAO, el Repository la declara primero en la interface, los ViewModels exponen el resultado como `StateFlow` y las pantallas solo pintan el borde y la alerta, sin consultar ni calcular nada.

## Prompt 2 · Descuento por volumen (commit B)

**Texto enviado a la IA:** En la misma app, si el lavado (servicios cobrados por kg) supera 5 kg, aplica un 10 % de descuento sobre el subtotal del lavado. Debe verse en el resumen de Nuevo pedido y guardarse en el pedido. La regla va en NuevoPedidoViewModel; la entidad Pedido gana la columna descuento, así que sube la versión de la base sin borrar los pedidos existentes. crearPedido se declara en la interface y resta el descuento del total. Muestra el descuento también en el detalle del estudiante y en la gestión del administrador.

**Qué generó la IA:** El campo `descuento` en `Pedido`, la base en versión 2 con la migración `MIGRACION_1_2` (`ALTER TABLE ... ADD COLUMN descuento REAL NOT NULL DEFAULT 0.0`), el parámetro `descuento` en `crearPedido`, la función `calcularDescuento()` con las constantes de 5 kg y 10 % en `NuevoPedidoViewModel`, y los `StateFlow` de subtotal, descuento y total. En la vista, las líneas de subtotal y descuento en el resumen, la pista "Con más de 5 kg de lavado tienes 10 % de descuento" y la línea del descuento en el Detalle y en la Gestión.

**Qué tuve que corregir yo:** Revisé que el descuento se aplique solo a los servicios por kg y no a todo el pedido, y que exactamente 5 kg no tenga descuento ("supera 5 kg"). El monto se redondea a dos decimales para que el total guardado coincida con el que se muestra. Comprobé la migración instalando sobre la base v1 sin borrar datos: conservó los pedidos y la app abrió sin errores.

**Respeta MVVM?:** Sí. La regla vive en el ViewModel, la entidad y la migración en la capa de datos, el Repository solo guarda el valor que recibe y la vista muestra los `StateFlow`. Se subió la versión de la base, como pide la guía al cambiar una entidad.

## Prompt 3 · Repetir un pedido (commit C)

**Texto enviado a la IA:** En el historial de Mis pedidos agrega un botón "Pedir de nuevo" que abra Nuevo pedido con las mismas líneas del pedido elegido, pero con los precios actuales. Si algún servicio ya no está activo, omítelo y avisa cuál. Solo cambian el ViewModel y la View; reutiliza las funciones que ya tiene LavaRepository. Pasa el id del pedido como argumento opcional de la ruta.

**Qué generó la IA:** La ruta `nuevo_pedido?repetir={repetir}` con valor por defecto 0, la función `repetir(pedidoId)` en `NuevoPedidoViewModel`, que lee el pedido con `observarPedido` y los servicios activos, copia solo las cantidades y arma el aviso, el botón con ícono en las tarjetas del historial y el aviso en la parte superior de Nuevo pedido.

**Qué tuve que corregir yo:** Revisé que se copien solo las cantidades y no los precios guardados, porque el total debe salir de los precios de hoy. Agregué una bandera para que el pedido no se vuelva a cargar al rotar la pantalla y borre lo que el estudiante ya cambió. Las cantidades se ajustan a los límites de cada unidad. Lo probé subiendo el precio del lavado y desactivando Planchado: se usó el precio nuevo y se avisó que Planchado ya no estaba disponible.

**Respeta MVVM?:** Sí. La vista solo envía el id y muestra el aviso. El ViewModel usa la interface `LavaRepository` sin agregarle funciones nuevas, y no hubo cambios en la base ni en el DAO.

## Reflexion (5 lineas)

- La IA aceleró el trabajo repetitivo de recorrer las capas (DAO, interface, implementación, ViewModel y vista) y mantuvo los nombres y el estilo del proyecto, pero no aportó los datos de prueba necesarios para ver cada mejora.
- Su error más claro fue no anticipar los casos límite: un pedido de prueba atrasado, exactamente 5 kg y servicios desactivados al repetir un pedido; esos casos los encontré al probar en el emulador.
- Aprendí que cambiar una entidad obliga a subir la versión de Room y que una migración evita perder los datos de los usuarios.
- Confirmé que las reglas de negocio deben vivir en el ViewModel y no en la vista ni en el DAO, porque así se pueden revisar y probar en un solo lugar.
- También vi que guardar el precio y el descuento en el pedido protege el historial, mientras que al repetir un pedido corresponde usar los precios actuales.

# TECSUP Store - Lab06

Aplicación Android desarrollada en Kotlin con Jetpack Compose que implementa una tienda básica (TECSUP Store) con un **DropdownMenu** contextual en cada producto y un **NavigationDrawer** para la navegación principal.

## Descripción

Este proyecto corresponde al Laboratorio 06 del curso de Desarrollo de Aplicaciones Móviles. Se partió de la base del Laboratorio 04 (carrito de compras) y se migró su lógica a la estructura del Laboratorio 06, integrando dos componentes clave de Material Design 3:

- **DropdownMenu**: menú contextual que se despliega al tocar el ícono de 3 puntos (⋮) en cada tarjeta de producto.
- **NavigationDrawer**: panel lateral que permite navegar entre las secciones principales de la app.
## Prompts

# Documentación de Prompts usados con IA — Fase 2

Este archivo documenta los prompts utilizados con el asistente de IA para implementar la mejora del **badge con contador de favoritos** en el ítem "Favoritos" del NavigationDrawer.

---

## Prompt 1: Agregar propiedad `esFavorito` al modelo

**Prompt:**
> "Tengo una data class Producto en Kotlin con nombre, precio y cantidad. Necesito agregar una propiedad booleana `esFavorito` con valor inicial `false` para poder marcar productos como favoritos. Dame el código completo de la clase."

**Resultado de la IA:**
Agregó la propiedad con valor por defecto. Funcionó correctamente sin necesidad de correcciones.

---

## Prompt 2: Conectar el DropdownMenu con el estado global

**Prompt:**
> "Tengo un Composable ProductCard con un DropdownMenu que tiene una opción 'Favoritos'. Necesito que al hacer clic en esa opción se actualice una lista global de productos en AppNavegacion. ¿Cómo paso un callback `onFavoritoClick` a ProductCard y cómo actualizo el producto en la lista?"

**Resultado de la IA:**
Proporcionó el parámetro `onFavoritoClick: () -> Unit` y una función `toggleFavorito` que usaba `listaProductos.indexOf(producto)`.

**Corrección realizada:**
La IA usó `indexOf(producto)` directamente, pero como `copy()` crea una nueva instancia del objeto, el `indexOf` podía fallar al no encontrar la misma referencia. Se corrigió reemplazándolo por `indexOfFirst { it.nombre == producto.nombre }`, que busca por nombre (un identificador estable) en lugar de por referencia de objeto.

---

## Prompt 3: Badge con contador en NavigationDrawerItem

**Prompt:**
> "Necesito agregar un Badge con un contador numérico al ítem 'Favoritos' de un NavigationDrawer en Material 3. El contador debe mostrar cuántos productos están marcados como favoritos. Dame el código usando BadgedBox y Badge."

**Resultado de la IA:**
Proporcionó el código con `BadgedBox` y `Badge`, pero lo colocó fuera del `label` del `NavigationDrawerItem`.

**Corrección realizada:**
Se movió el `BadgedBox` dentro del `label` con un `Row` para alinear el texto "Favoritos" con el badge. También se agregó la condición `if (cantidadFavoritos > 0)` para evitar que se muestre un badge vacío cuando no hay favoritos marcados.

---

## Prompt 4: Calcular el contador de favoritos reactivamente

**Prompt:**
> "¿Cómo calculo la cantidad de productos favoritos en una lista mutable de productos en Compose para que el contador se actualice automáticamente cuando cambia el estado?"

**Resultado de la IA:**
Sugirió usar `listaProductos.count { it.esFavorito }` directamente en el cuerpo del Composable.

**Corrección realizada:**
Ninguna. Funcionó correctamente porque Compose recompone automáticamente al detectar cambios en un `mutableStateListOf`.
## Objetivos

- Integrar el modelo de datos `Producto` del Lab04 en la estructura del Lab06.
- Implementar un `DropdownMenu` con opciones contextuales (Favoritos, Compartir, Reportar).
- Implementar un `NavigationDrawer` con al menos 4 destinos (Inicio, Mis pedidos, Favoritos, Perfil).
- Practicar el desarrollo **sin asistencia de IA**, reforzando el aprendizaje autónomo.

---

## Preguntas de reflexión

### ¿Por qué el DropdownMenu se declara dentro de un Box junto al ícono que lo activa, y no en cualquier parte de la pantalla?

El `DropdownMenu` debe declararse junto al ícono que lo activa porque su posición en pantalla está **anclada al componente padre**. Si se declarara en otra parte de la jerarquía, el menú aparecería en una posición incorrecta o desconectada visualmente del ícono. Al estar dentro del mismo `Row`/`Box` que el `IconButton`, el menú se posiciona automáticamente justo debajo del ícono, respetando el principio de **contextualidad**: el menú pertenece visual y lógicamente al elemento que lo invoca. Además, esto permite que el estado `expanded` viva en el mismo scope que el botón que lo controla, evitando estados huérfanos.

### ¿Qué diferencia de alcance hay entre las opciones del DropdownMenu (afectan solo a un producto) y las del NavigationDrawer (afectan a toda la app)?

El `DropdownMenu` tiene un **alcance local**: sus acciones (Favoritos, Compartir, Reportar) afectan únicamente al producto específico sobre el que se abrió el menú. En cambio, el `NavigationDrawer` tiene un **alcance global**: sus opciones (Inicio, Mis pedidos, Favoritos, Perfil, Cerrar sesión) cambian la pantalla completa de la aplicación y afectan la experiencia general del usuario. Esta diferencia refleja la jerarquía de navegación en Material Design: acciones contextuales sobre un ítem vs. navegación estructural de la app.

### ¿Cómo tuviste que estructurar tu código para que el contador de favoritos del drawer "se entere" de lo que pasa en el DropdownMenu de cada producto?

Fue necesario **elevar el estado** (state hoisting) desde `ProductCard` hasta `AppNavegacion`. El estado de los productos (`listaProductos`) vive en `AppNavegacion`, que es el ancestro común entre la lista de productos y el drawer. Desde ahí se pasa un callback `onFavoritoClick` a `ProductCard`; cuando el usuario toca "Favoritos" en el menú, el callback se dispara, la lista se actualiza con `copy()`, y como es un `mutableStateListOf`, Compose recompone. El contador `cantidadFavoritos` se calcula con `listaProductos.count { it.esFavorito }` en el cuerpo de `AppNavegacion`, por lo que se recalcula automáticamente en cada recomposición y se pasa al drawer como parámetro. Así, la acción del menú contextual se refleja visualmente en el badge del drawer sin acoplar directamente ambos componentes.

### ¿Qué tuviste que corregir del código que te generó la IA para la mejora del badge de favoritos?

Tres correcciones principales:

1. **`indexOf` por referencia:** La IA usó `listaProductos.indexOf(producto)` para encontrar el producto a modificar, pero como `copy()` crea una nueva instancia, el `indexOf` podía no encontrar el objeto. Se reemplazó por `indexOfFirst { it.nombre == producto.nombre }`, que busca por nombre (identificador estable).

2. **Badge mal ubicado:** La IA colocó el `BadgedBox` fuera del `label` del `NavigationDrawerItem`, por lo que no se alineaba con el texto. Se movió dentro del `label`, usando un `Row` para alinear texto y badge horizontalmente.

3. **Badge vacío:** La IA no condicionó la aparición del `Badge`, por lo que se mostraba un badge sin número cuando `cantidadFavoritos == 0`. Se agregó la condición `if (cantidadFavoritos > 0)` para evitar ese caso.

---

## Observaciones y conclusiones

### Observaciones

1. **Dificultad con el estado mutable en listas:** Al principio intenté actualizar el producto directamente con `listaProductos[index] = producto.copy(...)`, pero el `indexOf` fallaba porque `copy()` genera una nueva instancia. Fue necesario buscar el índice por nombre para que la actualización fuera confiable.

2. **Complejidad del BadgedBox en NavigationDrawerItem:** El `BadgedBox` no se integra de forma intuitiva dentro del `label` de un `NavigationDrawerItem`. Requiere un `content` vacío y un `Modifier.padding` para alinear el badge correctamente junto al texto, lo cual no es evidente en la documentación oficial.

3. **Diferencia entre Fase 1 y Fase 2:** En la Fase 1 (sin IA) tuve que razonar cada paso y comprender la estructura de los componentes antes de escribirlos. En la Fase 2 (con IA), la IA generó código funcional rápidamente, pero con pequeños errores de integración que requirieron ajustes manuales basados en la comprensión previa.

### Conclusiones

1. **La IA acelera pero no reemplaza el criterio:** La IA generó aproximadamente el 80% del código del badge correctamente, pero el 20% restante (alineación, condiciones, manejo de índices) requirió comprensión profunda del framework. Sin los conocimientos adquiridos en la Fase 1, no habría podido identificar ni corregir esos errores.

2. **El state hoisting es clave para la comunicación entre componentes:** La mejora del badge demostró la importancia de elevar el estado a un ancestro común. Sin esta técnica, el drawer nunca se habría "enterado" de los cambios en el DropdownMenu. Este patrón es fundamental en Compose.

3. **Documentar los prompts es tan importante como el código:** El archivo `PROMPTS.md` permite trazabilidad y aprendizaje. Al revisar los prompts, se identifican patrones de error recurrentes de la IA (como confundir referencias con valores) que se pueden anticipar en futuros usos.

4. **Comparación Fase 1 vs Fase 2:** La Fase 1 desarrolló la **comprensión estructural** de Compose (componentes, estados, navegación), mientras que la Fase 2 desarrolló la **capacidad de auditar y corregir** código generado por IA. Ambas son habilidades complementarias en el desarrollo moderno.
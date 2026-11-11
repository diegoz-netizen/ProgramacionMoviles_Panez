# PROMPTS — Laboratorio 12 (Pokédex)

Pareja: Victor y Diego Panez · Fase 2, rama `con-ia`.

En esta fase se pidió a la IA que revisara el proyecto de la Fase 1 y lo dejara con la estructura que pide la guía. Estos son los prompts de trabajo.

## Prompt 1 — Arquitectura MVVM

> Tengo una app Android en Kotlin con Jetpack Compose (Material 3) que muestra los Pokémon de tipo fuego de PokeAPI (`GET type/fire` y `GET pokemon/{nombre}`). Organízala con MVVM: capas `data` (remote, local, mapper, repository), `domain` y `ui`. El Repository debe ser una interface, el ViewModel no debe recibir Context, y debe haber tres modelos distintos: `ItemDto`, `ItemEntity` e `Item`, con mappers. Cada ViewModel expone un `StateFlow<UiState<T>>` con Cargando, Exito y Error, y se crea con un `ViewModelProvider.Factory` que recibe el `AppContainer`.

## Prompt 2 — Room como fuente de verdad y modo sin conexión

> Haz que Room sea la fuente única de verdad: la lista sale siempre de Room y Retrofit solo la actualiza con `refrescar()`. Si no hay internet y hay datos guardados, muestra la lista con un aviso de sin conexión; si no hay datos, muestra una pantalla de error con botón Reintentar. Los favoritos no se deben perder al sincronizar.

## Prompt 3 — Favoritos y panel Admin

> Agrega favoritos (estrella en la lista y pantalla Favoritos, guardados en Room) y un panel Admin que solo aparezca si la sesión tiene rol `ADMIN`, con los totales, un botón Sincronizar y un botón Limpiar caché.

## Prompt 4 — Firebase y notificación

> Integra Firebase Authentication (correo y contraseña, sesión guardada y cerrar sesión). El rol se lee de Firestore en `usuarios/{uid}`, campo `rol`; no se decide en la app. Al sincronizar, muestra una notificación local «Se guardaron N elementos» con canal y permiso `POST_NOTIFICATIONS`, pedida desde el ViewModel mediante la interface `Notificador`.

## Prompt 5 — Pruebas unitarias

> Escribe pruebas unitarias de los ViewModel (lista, detalle, favoritos y Admin) usando un repositorio falso en memoria (`FakeItemRepository`) y un notificador falso, sin red ni Room. Usa `kotlinx-coroutines-test`.

## Prompt 6 — Rediseño (Material 3)

> Mejora la interfaz con Material 3: tarjetas, colores coherentes con el tema fuego, y mensajes de error claros para cada caso (sin internet, error del servidor, sin datos).

## Qué se revisó y corrigió a mano

- Se agregó el manejo de `HttpException` en `DetalleViewModel` para mostrar el error del servidor.
- Se comprobó que los favoritos se conservan al sincronizar.
- Se verificó que `google-services.json` no se sube al repositorio.
- Las pruebas se ejecutan con `./gradlew :app:testDebugUnitTest`.

---

## Consultas de Diego (Fase 2)

Cada consulta sigue la plantilla de la guía.

### Consulta 7 — Tarjetas con doble borde

- **Fecha:** 10/11/2026
- **Herramienta:** Claude Code (Claude)
- **Prompt:** «En la lista, cada Pokémon se ve como una tarjeta dentro de otra y la barra inferior sale lila aunque el tema es de fuego. Encuentra la causa y corrígelo usando los colores del tema.»
- **Qué respondió (resumen):** La tarjeta usaba un color semitransparente (`surfaceVariant` con `alpha = 0.55f`) y además sombra; la sombra se veía a través del color y formaba un segundo borde. La barra salía lila porque el tema no definía los tonos `surfaceContainer*` y Material 3 usaba los suyos por defecto.
- **Qué usamos / qué cambiamos y por qué:** Agregamos los tonos `surfaceContainer*` y `outlineVariant` al tema claro y oscuro, y la tarjeta pasó a un color opaco con un borde fino. No pusimos colores escritos a mano en las pantallas: todo sale del tema.
- **Qué aprendimos:** En Material 3 los colores con transparencia y la elevación se combinan mal; los "contenedores" (tarjetas, barra inferior) tienen sus propios roles de color en el `ColorScheme`.

### Consulta 8 — Detalle con tipos y estadísticas

- **Fecha:** 10/11/2026
- **Herramienta:** Claude Code (Claude)
- **Prompt:** «Mejora la pantalla de detalle: los tipos como chips de color, altura y peso en tarjetas, y las estadísticas base con barras. Usa los datos que ya trae `GET pokemon/{nombre}` sin cambiar los métodos del `ItemRepository`.»
- **Qué respondió (resumen):** PokeAPI ya devuelve `stats[].base_stat` y `stats[].stat.name` en el mismo endpoint. Propuso agregarlos al `PokemonDetalleDto`, pasarlos por el mapper y añadir campos con valor por defecto al modelo `Item`, para no romper la lista, Room ni las pruebas.
- **Qué usamos / qué cambiamos y por qué:** Agregamos `EstadisticaDto`, la clase `Estadistica` y los campos `numero`, `alturaM`, `pesoKg`, `tipos` y `estadisticas` en `Item`. Los nombres y colores de los tipos quedaron en `ui/theme/TiposPokemon.kt`. Revisamos con Arcanine (PS 90, Ataque 110, Velocidad 95) que los valores coinciden con la API.
- **Qué aprendimos:** Un campo nuevo con valor por defecto en el modelo no obliga a cambiar el resto del código; y las barras se escalan contra 255, el valor máximo de una estadística base.

### Consulta 9 — Buscar por nombre

- **Fecha:** 10/11/2026
- **Herramienta:** Claude Code (Claude)
- **Prompt:** «Agrega una barra de búsqueda en la lista que filtre los Pokémon por nombre sin respetar mayúsculas. Debe respetar MVVM y tener pruebas.»
- **Qué respondió (resumen):** Guardar el texto en el `ListaViewModel` como `StateFlow` y combinarlo con `repo.items` usando `combine`, de modo que el filtro se aplique sobre lo que sale de Room sin volver a llamar a la API. La pantalla solo muestra el campo y llama a `buscar()`.
- **Qué usamos / qué cambiamos y por qué:** Agregamos `busqueda` y `buscar()` al ViewModel, el campo con botón para borrar en `ListaScreen` y el mensaje «Ningún Pokémon coincide con…». Sumamos 2 pruebas al `ListaViewModelTest` (filtra sin importar mayúsculas; sin coincidencias queda vacío y al borrar se recupera). Las 19 pruebas pasan.
- **Qué aprendimos:** Con `combine` el estado se recalcula solo cuando cambia la lista o el texto, y la lógica del filtro queda en el ViewModel, donde se puede probar sin la pantalla.

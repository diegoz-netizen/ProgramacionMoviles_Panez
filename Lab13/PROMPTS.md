# PROMPTS.md — Fase 2 de CampusGPS (rama `con-ia`)

**Alumno:** Diego Panez Rondinel · **Herramienta de IA:** Claude (Claude Code)

La guía propone Gemini en Android Studio. Usé Claude, así que la segunda columna resume lo que respondió Claude. La clave de Google Maps nunca se pegó en un prompt: está en `local.properties`, que no se sube.

## Prompts importantes

| # | Prompt que escribí | Qué respondió Claude | Qué cambié o descarté | Por qué |
|---|---|---|---|---|
| 1 | «Sí, Tecsup Lima Santa Anita, intenta el plano y solo serían pabellones, no por salón.» | Avisó que Google Maps solo publica algunos pabellones (10, 11, 15, 16), las canchas y la entrada. | No acepté posiciones inventadas. Pedí un modo en la app para ubicar yo mismo cada pabellón sobre el satélite y luego fijé esas coordenadas en `LugaresRepositoryTecsup`. | Conozco el campus. Un mapa con pabellones mal puestos no sirve para orientarse. |
| 2 | «Si lo volteo desaparece el mapa.» | Separó la pantalla en mapa y panel para la vista horizontal. | Acepté el cambio y lo probé girando el celular. | Era un error real de diseño: el panel ocupaba todo el alto disponible. |
| 3 | «El profesor hizo énfasis en el diseño, dijo que no va a aceptar lo que bote la IA.» | Propuso un sistema de diseño Tecsup con Material 3, splash con el logo y login. | Acepté el tema y el splash. El login con Google quedó pendiente porque necesita credenciales que debo crear en Google Cloud. | Colores, letras y medidas en `ui/theme` hacen que todas las pantallas se vean iguales. |
| 4 | «Siento que se ve muy simplona la app, quiero un diseño mejor.» | Propuso mapa a pantalla completa, búsqueda flotante, hoja inferior deslizable, tiempo a pie y ruta. | Acepté la propuesta, pero corregí en las pruebas el zoom excesivo, un nombre largo que chocaba con el botón y una hoja que cortaba el botón de la ruta. | Compilar no bastaba: esos problemas solo se vieron usando la app. |
| 5 | «Veo que le marca una línea recta, ¿no puede hacer que marque el camino según el mapa y no que “vuele”?» | Explicó que Google no conoce los pasillos internos y dio dos opciones: red de pasillos propia o Routes API de Google. | Elegí la red propia con Dijkstra y **descarté Routes API**. | Routes API solo guía por calles públicas, pide facturación y no resuelve el recorrido dentro del campus. |
| 6 | «¿Puedes revisar las rutas que puse dentro de Tecsup? La app está pensada para funcionar una vez que llegue a Tecsup o esté adentro.» | Analizó el grafo: todo conectado, pero 3 tramos pasaban sobre otros puntos sin unirse. | Acepté corregir esos 3 tramos y agregar la regla de 100 m: lejos del campus no se traza la ruta. | Uno de esos tramos convertía un cruce de 14 m en una vuelta de unos 170 m. |
| 7 | «Siento que se parece al del profesor… deberías quitar el dibujar pasillos porque si no cualquiera podría cambiarlo.» | Propuso tres diseños y quitar las herramientas de edición. | Elegí tres pestañas (Mapa, Lugares, Rutas) y quité las dos herramientas. Pabellones y pasillos quedaron fijos en el código. | Un usuario final no debe poder alterar el plano, y la lista bajo el mapa era igual al diseño de la guía. |
| 8 | «La entrada está mal, la entrada es debajo de donde está el P3.» | Movió la entrada a la puerta que marqué en la captura. | La fijé en el primer punto de mi red de pasillos, que ya estaba en esa puerta. | Así todas las rutas salen desde la puerta real. |
| 9 | «Ajusta las rutas para que terminen en la puerta.» | Hizo que la ruta termine en el punto del pasillo frente al pabellón, y aclaró que no conocía la ubicación de cada puerta. | Acepté esa mejora y dejé la puerta exacta como pendiente. | Evita que la línea cruce el techo sin inventar datos que no tengo. |

## Correcciones y decisiones de arquitectura

| Situación | Decisión |
|---|---|
| Dijkstra necesita medir distancias, y `Location.distanceBetween` depende de Android | `util/Rutas.kt` usa Haversine en Kotlin puro, por eso se prueba con JUnit sin emulador. |
| El ViewModel no debe conocer `Context` | El `Context` se queda en `CampusViewModelFactory`; el ViewModel solo recibe interfaces. |
| La ruta no debe calcularse en la pantalla | `MapaViewModel` publica `planificado` y `recorrido` en `StateFlow`; los Composables solo los dibujan. |
| Los pasillos no deben estar escritos en la lógica | La red está detrás de la interface `PasillosRepository`. |
| Al dibujar pasillos, tocar una etiqueta abría su ficha y movía la cámara | Lo detecté al probar y lo corregí. Después esa herramienta se retiró de la app final. |

## Qué mejoras cumplen la regla de la guía

| Mejora | Capas que cambian |
|---|---|
| Filtro por categoría | ViewModel + View |
| Rutas por pasillos con Dijkstra | Repository + ViewModel + View + util |
| Búsqueda de lugares | ViewModel + View |
| Planificador con origen y destino | ViewModel + View |

# PROMPTS — Fase 2 de AvisosCampus

**Herramienta usada:** Claude Code. La guía propone Gemini en Android Studio, pero estas mejoras se trabajaron con Claude.

**Mejoras elegidas:** dos canales (Urgentes y Avisos), texto largo con `BigTextStyle` e historial persistente con Room. Además, un rediseño completo de la pantalla.

**Rama:** `con-ia`. La guía la llama `mejora-ia`; en este repositorio todos los labs usan `sin-ia` para la Fase 1 y `con-ia` para la Fase 2.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Sí, Fase 2» (después de terminar la Fase 1, con la regla de hacer los commits como en la guía). | Eligió tres mejoras de la tabla de la guía: dos canales (Notificador + ViewModel), texto largo (Notificador) e historial persistente con Room (Repository + ViewModel). Descartó «Botón en la notificación» y «Receptor del cargador». | Se mantuvo. | Room cumple la regla de que una mejora cambie el Repository. El receptor del cargador no se puede probar bien en el emulador, y Posponer necesita un receptor en el Manifest que no llega al ViewModel sin más capas. |
| Misma solicitud, sobre los dos canales. | Creó el canal `urgentes` con `IMPORTANCE_HIGH` junto al canal `avisos`. Agregó `urgente` al modelo `Aviso`; el ViewModel marca el recordatorio como urgente y el `Notificador` elige el canal según ese dato. | Se mantuvo. Revisé que el ViewModel solo pase un `Boolean` y no toque el canal ni el `NotificationManager`. | El ViewModel decide qué es urgente; el `Notificador` es el único que conoce los canales. Así no se rompe la regla de oro de la guía. |
| Misma solicitud, sobre el texto largo. | Agregó `detalle` al modelo y `BigTextStyle().bigText(detalle)` en el `Notificador`. El ViewModel escribe el texto largo de «Tarea lista» y del recordatorio. | Dejé igual el texto corto de la guía («Tu entrega fue registrada»); el largo solo aparece al expandir. | La guía fija los datos ficticios de la notificación cerrada; el detalle se suma sin cambiarlos. |
| Misma solicitud, sobre Room. | Diseñó la tabla `aviso` en 4 pasos (`docs/BaseDeDatos.md`). Cambió la interface del Repository a `Flow` + `suspend`, creó `AvisoEntity`, `AvisoDao`, `AppDatabase`, `AvisosRepositoryRoom` y `AppContainer` en `AvisosApp`. El ViewModel convierte el `Flow` con `stateIn`. | Pedí que el id use `AUTOINCREMENT` y agregué el botón «Borrar historial». | El id del aviso es el id de la notificación: si se reutilizara después de borrar, una notificación nueva podría reemplazar a otra (Para pensar 3). Con el historial guardado hace falta una forma de vaciarlo. |
| «¿Qué identidad visual le doy?» (la IA propuso tres: Campus índigo, Tablón de anuncios y Centro de alertas). | Aplicó «Campus índigo»: tema propio (`Color`, `Type`, `Shape`, `Dimens`), componentes compartidos, cabecera con degradado y contador, anillo de cuenta regresiva, historial como línea de tiempo e ícono de la app. | Elegí Campus índigo y descarté las otras dos. | El índigo con ámbar es propio de una app educativa, y la línea de tiempo muestra mejor el orden de los avisos. |

## Lo que revisé en el código de la IA

- `viewmodel/` no usa `Context`, `NotificationManager` ni `BroadcastReceiver`.
- El receptor del modo avión se sigue quitando en `onDispose`.
- El `PendingIntent` sigue siendo `FLAG_IMMUTABLE`; no se necesitaba uno mutable.
- No usa `GlobalScope`: todo corre en `viewModelScope`.
- Ninguna pantalla usa la entidad de Room: el Repository la traduce a `Aviso`.
- La auditoría de diseño no encontró colores, tamaños de letra ni formas escritos a mano fuera de `ui/theme`.

## Cómo lo probé

| Prueba | Resultado |
|---|---|
| Revisar los canales con `dumpsys notification` | `avisos` con importancia 3 y `urgentes` con importancia 4 ✔ |
| Recordar en 10 s con la app minimizada | Aparece como notificación emergente arriba de la pantalla ✔ |
| Mostrar aviso y abrir el panel | Al expandir se ve el texto completo ✔ |
| Generar avisos, cerrar la app del todo y abrirla | El historial sigue ahí ✔ |
| Mostrar aviso después de reabrir | El id sigue la cuenta (3 después de 1 y 2), no se repite ✔ |
| Borrar historial y mostrar otro aviso | La lista queda vacía y el nuevo id es 5, no 1 ✔ |
| Pruebas de la Fase 1 (permiso, recordatorio, cancelar, modo avión, permiso denegado) | Siguen pasando con el diseño nuevo ✔ |

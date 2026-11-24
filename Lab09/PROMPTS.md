# PROMPTS — Fase 2 de MuroCampus

**Herramienta usada:** Claude Code. La guía propone Gemini en Android Studio, pero estas mejoras se trabajaron con Claude.

**Mejoras elegidas:** borrar publicación, búsqueda por título y encabezado con interceptor.

**Rama:** `con-ia`. La guía la llama `mejora-ia`; en este repositorio todos los labs usan `sin-ia` para la Fase 1 y `con-ia` para la Fase 2.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Fase 2 del Lab 9: haz al menos 3 mejoras sin romper MVVM + Repository. Haz los commits y el push en intervalos de 15 minutos, no todo de una.» | Eligió borrar (API + Repository + ViewModel + View), búsqueda (ViewModel + View) e interceptor (RetrofitClient). Hizo un commit y un push por mejora, con 15 minutos entre cada uno. | Se mantuvo. | Borrar cumple la regla de cambiar el Repository y la interface de la API. Las otras dos son cortas y se pueden explicar sin mirar la pantalla. |
| Misma solicitud, sobre borrar. | Agregó `@DELETE("posts/{id}")`, que devuelve `Response<Unit>`. En el Repository revisa `isSuccessful` y, si no es 2xx, lanza `HttpException(respuesta)` dentro de `llamar { }`. | Se mantuvo. Revisé que el `HttpException` quedara en el Repository y no en el ViewModel. | Con `Response<Unit>`, Retrofit no lanza error por un 404 o 500: hay que revisarlo a mano. Si no se revisa, un fallo del servidor se vería como éxito. La regla de la guía es que el ViewModel no conozca Retrofit. |
| Misma solicitud, sobre la pantalla de borrar. | Puso el botón «Eliminar publicación» con un diálogo de confirmación. Si el DELETE sale bien, quita la publicación de la lista ya descargada y vuelve a la lista. Si falla, muestra el error debajo del botón. | Agregué `limpiarBorrado()` también al botón Atrás. | Sin eso, un error de borrado anterior seguía apareciendo al abrir otra publicación. |
| Misma solicitud, sobre la búsqueda. | Creó `listaVisible` con `combine(_lista, _busqueda)` y `stateIn`, que filtra por título sin hacer otra llamada. La pantalla tiene un campo «Buscar por título» con un botón Borrar. | Se mantuvo. Probé en Logcat que al escribir no sale ningún GET nuevo. | La guía pide filtrar las publicaciones ya descargadas. El filtro va en el ViewModel, así que la pantalla solo dibuja. |
| Misma solicitud, sobre el interceptor. | Creó un `Interceptor` que agrega `Accept: application/json` y lo puso antes del `HttpLoggingInterceptor`. | Se mantuvo. | En ese orden, el log muestra la petición ya con el encabezado, y se puede comprobar en Logcat. |

## Lo que revisé en el código de la IA

- No tiene ningún `catch` vacío. El Repository sigue relanzando `CancellationException`.
- No usa `GlobalScope`: todo corre en `viewModelScope.launch`.
- Ningún Composable llama a la API: las pantallas solo llaman funciones del ViewModel.
- `PostsViewModel` no importa Retrofit, `PostApi` ni `HttpException`.
- La URL base sigue siendo `https://`.

## Cómo lo probé

| Prueba | Resultado |
|---|---|
| Abrir una publicación y pulsar Eliminar y luego confirmar | Logcat muestra `--> DELETE …/posts/1` con 200; vuelve a la lista sin esa publicación ✔ |
| Pulsar Cancelar en el diálogo | No se envía nada ✔ |
| Escribir «dolorem» en el buscador | Solo quedan los títulos con esa palabra y no hay GET nuevo ✔ |
| Buscar un texto que no existe | «Ninguna publicación coincide con «…»» ✔ |
| Revisar Logcat de cualquier petición | Sale `Accept: application/json` ✔ |

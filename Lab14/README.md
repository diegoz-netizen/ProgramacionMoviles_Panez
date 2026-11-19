# CentroTareas — Laboratorio 14 (Programación en Móviles)

**Alumno:** Diego Panez · **Paquete:** `com.panez.centrotareas`

App Android en Kotlin con Jetpack Compose y Material 3 que ejecuta tres trabajos que tardan sin congelar la pantalla: una tarea con barra de progreso, un cálculo pesado de números primos y dos cargas de datos simuladas. Todo el trabajo largo corre con corrutinas y se puede seguir desde la interfaz.

## Qué hace

| Función | Detalle |
|---|---|
| Prueba de vida | El botón «Tócame» sigue sumando mientras la app trabaja |
| Tarea con progreso | Barra de 0 a 100 %, con Iniciar y Cancelar |
| Números primos | Cuenta los primos hasta N en `Dispatchers.Default` y se puede cancelar |
| Dos cargas | Compara «Una por una» (≈ 5 s) con «A la vez» usando `async` (≈ 3 s) |
| Errores | «Simular error» muestra el recuadro rojo y el botón Reintentar |

## Estructura de paquetes

```
com.panez.centrotareas/
├── MainActivity.kt
├── model/
│   └── Estados.kt                  PrimosUi y CargaUi
├── data/
│   ├── TareasRepository.kt         interface
│   └── TareasRepositoryLocal.kt    cargas simuladas y conteo de primos
├── viewmodel/
│   ├── TareasViewModel.kt          estado de la pantalla y corrutinas
│   └── TareasViewModelFactory.kt
└── ui/
    ├── PantallaTareas.kt           las tres tarjetas
    └── theme/
        └── Theme.kt
```

## Arquitectura MVVM + Repository

```
View (PantallaTareas)
   |  eventos: botones              ^  collectAsState()
   v                                |
ViewModel (TareasViewModel)  --- StateFlow --->
   |  viewModelScope.launch { ... }
   v
TareasRepository (interface)  --->  TareasRepositoryLocal
                                    (cargas simuladas y contar primos)
```

- **View:** solo muestra el estado y avisa los eventos. No lanza corrutinas.
- **ViewModel:** lanza las corrutinas y publica el estado. Solo conoce la interface del Repository.
- **Repository:** hace el trabajo y no sabe nada de la interfaz.

## Observación del Paso 1

Con `Thread.sleep(4000)` en el hilo principal, la pantalla se congela durante 4 segundos: los botones no responden y el contador deja de sumar.

El texto «Trabajando...» no llega a verse porque el hilo principal es el que dibuja la pantalla. El mensaje cambia, pero el hilo queda dormido antes de poder redibujar. Cuando despierta, el mensaje ya es «Terminó».

Si la espera fuera de 10 segundos, Android mostraría el aviso ANR, «La aplicación no responde», y ofrecería cerrarla.

## Capturas

| Tarea con progreso | Tarea cancelada |
|---|---|
| ![Tarea con progreso](capturas/01_tarea_con_progreso.png) | ![Tarea cancelada](capturas/02_tarea_cancelada.png) |

| Números primos | Error simulado |
|---|---|
| ![Números primos](capturas/03_primos.png) | ![Error simulado](capturas/06_error_simulado.png) |

| Una por una | A la vez |
|---|---|
| ![Una por una](capturas/04_una_por_una.png) | ![A la vez](capturas/05_a_la_vez.png) |

## Pruebas de aceptación

| Prueba | Resultado |
|---|---|
| Tocar Tócame mientras trabaja | El contador sigue sumando |
| Logcat con filtro `HILO` | `main` antes y después, `DefaultDispatcher-worker` dentro |
| Iniciar la tarea con progreso | La barra avanza de 0 a 100 % |
| Cancelar a mitad | La barra se detiene, dice Cancelado y se puede iniciar otra vez |
| Pulsar Iniciar dos veces seguidas | Solo se lanza una tarea |
| Primos hasta 100, 1000, 10 000, 100 000 y 500 000 | 25, 168, 1229, 9592 y 41 538 |
| Primos con N grande | La pantalla no se congela y Cancelar funciona |
| Una por una | 5017 ms |
| A la vez | 3004 ms |
| Error simulado y Reintentar | Recuadro rojo y, sin el error, recuadro verde |
| Rotar la pantalla durante la tarea | La tarea continúa y el progreso no se reinicia |
| Revisar el código | Sin `GlobalScope`, `runBlocking` ni `Thread.sleep` |

El valor por defecto de primos es 3 000 000 porque en el emulador tarda entre 2 y 3 segundos. Con 500 000 tardaba menos de 1 segundo.

## Para pensar

**1. ¿Por qué no se veía «Trabajando...» en el Paso 1?**
Porque `Thread.sleep` bloqueaba el hilo principal, que es el encargado de dibujar. La pantalla no pudo actualizarse hasta que terminó la espera, y en ese momento el mensaje ya era «Terminó».

**2. ¿Qué diferencia hay entre `Thread.sleep` y `delay`?**
`Thread.sleep` bloquea el hilo: nadie más puede usarlo durante la espera. `delay` suspende solo la corrutina y libera el hilo para otros trabajos. Además, `delay` se puede cancelar.

**3. ¿Por qué `Dispatchers.Default` para los primos y `Dispatchers.IO` para la red?**
Contar primos usa el procesador todo el tiempo, y `Default` tiene un número de hilos acorde a los núcleos del dispositivo. La red pasa la mayor parte del tiempo esperando una respuesta, e `IO` dispone de más hilos para atender muchas esperas a la vez.

**4. ¿Qué pasaría con la corrutina si el usuario cierra la pantalla? ¿Qué papel cumple `viewModelScope`?**
Al cerrar la pantalla definitivamente se destruye el ViewModel y `viewModelScope` cancela todas sus corrutinas, lo que evita trabajo inútil y fugas de memoria. Al rotar la pantalla el ViewModel sobrevive, por eso la tarea continúa.

**5. ¿Por qué `async` tarda 3 s y no 5 s? ¿Qué pasa si una de las dos cargas falla?**
Las dos cargas corren al mismo tiempo, así que el total es el de la más lenta: máximo(2 s, 3 s). Si una falla, `coroutineScope` cancela la otra y `await()` relanza el error, que el `try/catch` convierte en el estado de error.

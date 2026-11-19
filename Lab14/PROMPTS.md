# PROMPTS — Fase 2 de CentroTareas

**Herramienta usada:** Claude Code. La guía propone Gemini en Android Studio, pero estas mejoras se trabajaron con Claude.

**Mejoras elegidas:** progreso desde el Repository, tiempo máximo y primos con progreso.

| Prompt que escribí | Qué respondió la IA | Qué cambié o descarté | Por qué |
|---|---|---|---|
| «Implementar 3 mejoras sobre CentroTareas sin romper MVVM + Repository: progreso desde el Repository con `Flow<Float>` y `flowOn(Dispatchers.Default)`, `withTimeout` de 2 s con el mensaje "Tardó demasiado" y primos con porcentaje en tiempo real. Prohibido `GlobalScope`, `runBlocking` y `Thread.sleep`; relanzar siempre `CancellationException`.» | Movió la tarea simulada a `progresoTarea()`, aplicó `withTimeout` a cada carga y agregó el porcentaje a `PrimosUi.Calculando`. | Se mantuvo el paquete `com.panez.centrotareas` en lugar de `com.tecsup.centrotareas`. | Es el paquete real del proyecto. |
| Misma solicitud, sobre el límite de 2 s. | Advirtió que los pedidos tardan 3 s, así que con el límite activo la carga siempre termina en "Tardó demasiado". Propuso el interruptor «Tiempo máximo (2 s por carga)». | Acepté el interruptor y lo dejé encendido por defecto. | Cumple el requisito y permite seguir comparando 5 s contra 3 s y probar el error simulado. |
| Misma solicitud, sobre el orden de los `catch`. | Capturó `TimeoutCancellationException` antes que `CancellationException`, y relanzó esta última. | Se mantuvo sin cambios. | `TimeoutCancellationException` es subclase de `CancellationException`; en otro orden el mensaje nunca aparecería. |
| «Este es el laboratorio 14, revísalo y compara que todo esté OK.» | Comparó la app con la guía y encontró diferencias de diseño y un valor por defecto de primos demasiado rápido. | Apliqué el orden de los controles, los textos, el botón Reintentar y el valor 3 000 000. | La guía pide los mismos elementos y orden, y un cálculo de 2 a 5 segundos. |

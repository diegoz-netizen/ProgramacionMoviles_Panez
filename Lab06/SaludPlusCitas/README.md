# SaludPlus Citas — App Paciente

App Android (Jetpack Compose) para agendar citas médicas en la Clínica SaludPlus. Los datos viven
en memoria dentro de `Repositorio` (sin base de datos), por lo que se pierden al cerrar la app.

**Usuario de prueba:** `987654321` / `123456`

## Ramas

- `sin-ia`: Fase 1, desarrollo propio sin asistente de IA.
- `con-ia`: Fase 2, mejora hecha con ayuda de IA (calendario dinámico).

## Fase 2 — Calendario dinámico (con IA)

- Muestra los próximos 5 días hábiles a partir de hoy con `java.time.LocalDate`.
- Las flechas `<` y `>` cambian de semana; no se puede retroceder antes de la semana actual.
- El mes y año del encabezado cambian según la semana mostrada.
- Al cambiar de día se recalculan los horarios disponibles y se reinicia la hora seleccionada.
- La pantalla de confirmación muestra la fecha en español ("Martes 16 de setiembre 2026").
- Se mantiene el bloqueo de horarios ya reservados.

## Prompts usados

### Prompt 1: Calendario dinámico y fecha en español

**Prompt:** Pasé a la IA el enunciado de la Fase 2: reemplazar la lista fija de días de la pantalla
Fecha y hora por un calendario dinámico con `java.time.LocalDate` (5 días hábiles desde hoy, flechas
por semana sin retroceder antes de la actual, mes y año dinámicos, reinicio de la hora al cambiar de
día y fecha en español en la Pantalla 7), sin romper el bloqueo de horarios reservados.

**Respuesta resumida:** La IA creó `FechaUtil.kt` con extensiones de `LocalDate` (días hábiles,
nombres de día y mes en español, `semanaHabil`), reescribió `FechaHoraScreen.kt` con el estado de la
semana, las flechas y el reinicio de la hora, y cambió `ConfirmarCitaScreen.kt` para mostrar la fecha
con `fechaLegible`.

**Qué tuve que corregir:**
- Mi proyecto usa minSdk 24, así que `java.time` necesita `coreLibraryDesugaring` en `build.gradle.kts`.
- Ajusté el formato de la fecha al del enunciado: "Martes 16 de setiembre 2026".
- (Agrega aquí cualquier otro error que te haya salido al probar en el emulador.)

### Prompt 2: Organizar los commits de la rama

**Prompt:** Pedí ayuda para partir del proyecto terminado de la Fase 1 y separar los cambios de la
Fase 2 en commits descriptivos en `con-ia`, sin mezclar commits de otros laboratorios.

**Respuesta resumida:** La IA indicó traer solo la carpeta `SaludPlusCitas` desde `sin-ia` con
`git checkout sin-ia -- <carpeta>` y propuso tres commits: utilidades de fecha y desugaring,
calendario dinámico con fecha en español, e imagen de inicio con este README.

**Qué tuve que corregir:** Adapté los comandos de PowerShell a Git Bash y agrupé los cambios en 3 commits.

### Prompt 3: Imagen en la pantalla de inicio

**Prompt:** Pedí cómo agregar la imagen del doctor del diseño de referencia a la pantalla Splash.

**Respuesta resumida:** La IA indicó copiar la imagen a `res/drawable` y mostrarla con `Image` y
`painterResource` en `SplashScreen.kt`.

**Qué tuve que corregir:** Cambié la primera imagen, que tenía un marco gris, por `doc.png`, que
tiene fondo blanco. Ajusté el tamaño con `aspectRatio` y centré el contenido para que la imagen no
quedara separada del texto y del botón.
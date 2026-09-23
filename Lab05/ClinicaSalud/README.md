# Clínica Salud+ — Programación en Móviles

App Android con Jetpack Compose para reservar citas médicas, fase 1.

## Estructura del proyecto
```
app/src/main/java/com/panez/clinicasalud/
├── MainActivity.kt
├── model/          → Medico, Cita
├── data/           → DatosMock (listaMedicos, citasAgendadas)
├── navigation/     → AppNavegacion (NavHost con 6 rutas)
├── screens/        → Inicio, Perfil, Agendar, Confirmacion, MisCitas, Historial
└── ui/theme/       → Colores morados de la clínica
```

## Requerimientos Funcionales

| RF | Descripción | Archivo | Cómo verificarlo |
|----|-------------|---------|------------------|
| **RF1** | Filtrar médicos por especialidad | `screens/InicioScreen.kt` | Toca un chip "Cardiología" → la lista se filtra |
| **RF2** | Listar médicos disponibles | `screens/InicioScreen.kt` | Al abrir la app se ven 3 médicos con nombre, especialidad y |
| **RF3** | Ver perfil del médico seleccionado | `screens/PerfilMedicoScreen.kt` | Toca una tarjeta → se abre el perfil con datos completos |
| **RF4** | Agendar cita eligiendo fecha y hora | `screens/AgendarCitaScreen.kt` | En perfil, toca "Agendar cita" → elige chip de fecha y hora |
| **RF5** | Confirmar cita con resumen | `screens/ConfirmacionScreen.kt` | Tras confirmar, se muestra nombre del médico + fecha + hora |
| **RF6** | Navegar entre secciones con menú lateral | `screens/InicioScreen.kt` | Toca el ícono ≡ → se abre el drawer con 3 destinos |
| **RF7** | Listar citas agendadas con estado | `screens/MisCitasScreen.kt` | Drawer → "Mis citas" → lista con chip verde "Confirmada" o gris "Completada" |
| **RF8** | Ver historial de citas completadas | `screens/HistorialScreen.kt` | Drawer → "Historial médico" → muestra la cita con estado "Completada" precargada en DatosMock.kt" |
| **RF9** | Volver al inicio desde confirmación | `screens/ConfirmacionScreen.kt` | Toca "Volver al inicio" en la pantalla de confirmación |

## Flujo de navegación

**Secuencial:** Inicio → Perfil médico → Agendar cita → Confirmación
Secundaria (drawer): Inicio / Mis citas / Historial médico

## Capturas
|   Pantalla Inicio   |   Lista de Elementos    |      Agendar cita       |     Cita confirmada     |
|:-------------------:|:-----------------------:|:-----------------------:|:-----------------------:|
| ![img.png](img.png) | ![img_1.png](img_1.png) | ![img_2.png](img_2.png) | ![img_3.png](img_3.png) |

|     Pantalla Inicio     |   Lista de Elementos    |
|:-----------------------:|:-----------------------:|
| ![img_4.png](img_4.png) | ![img_1.png](img_1.png) |
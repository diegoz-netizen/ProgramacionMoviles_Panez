# TECSUP Fit — Programación en Móviles

App Android con Jetpack Compose para reservar clases de gimnasio.

## Estructura del proyecto

app/src/main/java/com/panez/tecsupfit/

├── MainActivity.kt

├── model/ → Clase, Reserva

├── data/ → DatosMock (listaClases, reservasAgendadas)

├── navigation/ → AppNavegacion (NavHost con 7 rutas)

├── screens/ → Inicio, Detalle, Reservar, Confirmacion, Reservas, Rutinas, Perfil, BottomBar

└── ui/theme/ → Colores verdes de TecsupFit


## Requerimientos Funcionales

| RF | Descripción | Archivo | Cómo verificarlo |
|----|-------------|---------|------------------|
| **RF1** | Filtrar clases por "Hoy" / "Esta semana" | `screens/InicioScreen.kt` | Toca un chip de filtro → se resalta la opción |
| **RF2** | Listar clases disponibles | `screens/InicioScreen.kt` | Al abrir la app se ven 3 clases con nombre y horario |
| **RF3** | Ver detalle de la clase seleccionada | `screens/DetalleClaseScreen.kt` | Toca una tarjeta → se abre el detalle con datos completos |
| **RF4** | Reservar cupo eligiendo horario | `screens/ReservarCupoScreen.kt` | En detalle, toca "Reservar cupo" → elige uno de los 3 horarios |
| **RF5** | Confirmar reserva con resumen | `screens/ConfirmacionScreen.kt` | Tras confirmar, se muestra nombre de clase + horario + sala |
| **RF6** | Navegar entre secciones con bottomBar | `screens/BottomBar.kt` | Cambia entre pestañas Inicio/Reservas/Rutinas/Perfil |
| **RF7** | Listar reservas con estado | `screens/ReservasScreen.kt` | Pestaña "Reservas" → lista con chip "Confirmada" o "Completada" |
| **RF8** | Ver perfil con estadísticas | `screens/PerfilScreen.kt` | Pestaña "Perfil" → avatar DR + 14 clases + 3 rachas |
| **RF9** | Ver rutinas | `screens/RutinasScreen.kt` | Pestaña "Rutinas" → mensaje "Próximamente" |

## Flujo de navegación

**Secuencial:** Inicio → Detalle de clase → Reservar cupo → Confirmación
**Secundaria (bottomBar):** Inicio / Reservas / Rutinas / Perfil

## Capturas
|   Pantalla Inicio   |    Detalle de clase     |   Reservar cupo   |     Confirmación     |
|:-------------------:|:-----------------------:|:-----------------------:|:-----------------------:|
| ![img.png](img.png) | ![img_1.png](img_1.png) | ![img_2.png](img_2.png) | ![img_3.png](img_3.png)|

|        Reservas         |         Rutinas         |         Perfil          |
|:-----------------------:|:-----------------------:|:-----------------------:|
| ![img_4.png](img_4.png) | ![img_5.png](img_5.png) | ![img_6.png](img_6.png) |
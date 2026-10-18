# LavaCampus — Lavandería universitaria (Lab 08 · MVVM + Room)

App Android de la lavandería de un campus universitario, hecha con **Kotlin + Jetpack Compose + Material 3 (sin XML)**, arquitectura **MVVM + Repository**, base de datos **Room (SQLite)** y preferencias con **DataStore**.

Dos perfiles conviven en el mismo dispositivo y comparten la misma base de datos:

- **Estudiante:** arma un pedido eligiendo servicios y cantidades (kilos o prendas), ve el total y la fecha estimada de entrega, sigue el estado de su ropa y la recoge en un casillero.
- **Administrador:** recibe los pedidos, los avanza de estado (Recibido → En proceso → Listo → Entregado), asigna el casillero, cobra, administra las tarifas y consulta a sus clientes.

Cuando el administrador marca un pedido como **Listo**, el estudiante lo ve con su casillero al instante (los `Flow` de Room actualizan la pantalla solos).

| Dato | Detalle |
|---|---|
| Curso | Programación en Móviles · TECSUP · 4to ciclo |
| Alumno | Diego Panez |
| Ramas | `sin-ia` (Fase 1, sin IA) · `con-ia` (Fase 2, mejoras con IA; prompts en [PROMPTS.md](PROMPTS.md)) |
| Tecnología | Kotlin 2.2 · Compose BOM 2026.02 · Material 3 · Room 2.7 + KSP · DataStore · Navigation Compose |
| minSdk | 26 (para usar `java.time` sin librerías extra) |

## Cuentas de prueba (datos ficticios, Ley 29733)

| Perfil | Correo | Clave |
|---|---|---|
| Estudiante (Diego Panez) | `diego@correo.test` | `1234` |
| Estudiante (Luis Quispe) | `luis@correo.test` | `1234` |
| Estudiante (Marta Rojas) | `marta@correo.test` | `1234` |
| Administrador | `admin@lavacampus.test` | `admin` |

Los datos iniciales (4 servicios, 4 usuarios y 12 pedidos en distintos estados, uno de ellos atrasado) se cargan solo la primera vez que se abre la app. Si se cambia una entidad, hay que desinstalar la app (o subir la `version` de la base) para que Room no falle por esquema distinto.

## Capturas

Las 16 vistas de la app (15 pantallas; el login se muestra con cada perfil).

### Comunes

| Splash | Login · Estudiante | Login · Administrador |
|:---:|:---:|:---:|
| ![Splash](capturas/01-splash.png) | ![Login · Estudiante](capturas/02-login-estudiante.png) | ![Login · Administrador](capturas/03-login-administrador.png) |
| Pantalla de bienvenida con el ícono dibujado en Canvas | Un solo login; con la pestaña Estudiante toma el color turquesa | Con la pestaña Administrador toma el color violeta |

### Estudiante

| Inicio | Nuevo pedido | Detalle del pedido |
|:---:|:---:|:---:|
| ![Inicio](capturas/04-estudiante-inicio.png) | ![Nuevo pedido](capturas/05-estudiante-nuevo-pedido.png) | ![Detalle del pedido](capturas/06-estudiante-detalle-pedido.png) |
| Pedido activo con el seguimiento y los últimos pedidos | 3.5 kg de lavado y 5 prendas de planchado: S/ 40.50 | Seguimiento vertical, líneas, pago y casillero |

| Mis pedidos | Tarifas | Perfil |
|:---:|:---:|:---:|
| ![Mis pedidos](capturas/07-estudiante-mis-pedidos.png) | ![Tarifas](capturas/08-estudiante-tarifas.png) | ![Perfil](capturas/09-estudiante-perfil.png) |
| Pestañas Activos e Historial; los pedidos Listo muestran su casillero | Servicios activos con su precio, horario y reglas | Contadores, modo oscuro, avisos y cambio de perfil |

### Administrador

| Panel | Bandeja | Gestión del pedido | Servicios |
|:---:|:---:|:---:|:---:|
| ![Panel](capturas/10-admin-panel.png) | ![Bandeja](capturas/11-admin-bandeja.png) | ![Gestión del pedido](capturas/12-admin-gestion-pedido.png) | ![Servicios](capturas/13-admin-servicios.png) |
| Contadores, ingresos de la semana y gráficos | Buscador, filtros y acción según el estado | Casilleros ocupados en gris (C-05 y C-07) y cobro | Activar o desactivar servicios y editarlos |

| Formulario de servicio | Clientes | Ajustes |
|:---:|:---:|:---:|
| ![Formulario de servicio](capturas/14-admin-formulario-servicio.png) | ![Clientes](capturas/15-admin-clientes.png) | ![Ajustes](capturas/16-admin-ajustes.png) |
| Crear o editar un servicio | Pedidos y total gastado por estudiante | Cambio de perfil, modo oscuro, notificaciones y recibir pedidos |

### Mejoras de la Fase 2 (rama `con-ia`)

Pedidos atrasados:

| Panel | Bandeja |
|:---:|:---:|
| ![Panel con pedidos atrasados](capturas/17-ia-panel-atrasados.png) | ![Bandeja con pedido atrasado](capturas/18-ia-bandeja-atrasado.png) |
| Alerta roja con la cantidad de pedidos atrasados y el botón Ver | El pedido #12 de Marta, con entrega vencida y aún en proceso, aparece con borde rojo |

Descuento por volumen:

| Nuevo pedido | Detalle del pedido |
|:---:|:---:|
| ![Nuevo pedido con descuento](capturas/19-ia-nuevo-pedido-descuento.png) | ![Detalle con descuento](capturas/20-ia-detalle-descuento.png) |
| 6 kg de lavado y 2 prendas: subtotal S/ 53.00, descuento S/ 4.80, total S/ 48.20 | El descuento queda guardado en el pedido y se muestra en su detalle |

Repetir un pedido:

| Historial | Pedido repetido |
|:---:|:---:|
| ![Historial con Pedir de nuevo](capturas/21-ia-historial-pedir-de-nuevo.png) | ![Pedido repetido](capturas/22-ia-pedido-repetido.png) |
| Cada pedido del historial tiene el botón Pedir de nuevo | Nuevo pedido se abre con las mismas cantidades del pedido #1 y los precios de hoy |

## Arquitectura MVVM + Repository

```mermaid
flowchart TB
    V["VIEW · Composables (ui/)"] -- "eventos (clics)" --> VM["VIEWMODEL · StateFlow + reglas de negocio"]
    VM -- "collectAsStateWithLifecycle()" --> V
    VM -- "llama" --> R["LavaRepository (interface)"]
    R -- "Flow / resultados" --> VM
    R -. "la implementa" .-> RR["LavaRepositoryRoom"]
    RR --> D["DAOs"] --> DB[("SQLite · lavacampus.db")]
    P["PreferenciasStore (DataStore: perfil, modo oscuro, ajustes)"] --> VM
```

| Capa | Conoce a | No debe |
|---|---|---|
| View (`ui/`) | Solo a su ViewModel | Usar DAO, Repository ni reglas de negocio |
| ViewModel | Solo al Repository (interface) y a las preferencias | Conocer Composables ni DAOs |
| Repository | Los DAOs (en la implementación) | Conocer pantallas ni ViewModels |
| DAO / Room | SQL y entidades | Conocer ViewModels |

**Regla de oro:** lectura que debe actualizar la pantalla → `Flow` (sin `suspend`). Escritura o acción única → `suspend`.

## Estructura de paquetes

```
com.tecsup.lavacampus
├── MainActivity.kt                 tema según rol y modo oscuro
├── LavaApp.kt                      crea la base, el repository y las preferencias; siembra datos
├── data
│   ├── local
│   │   ├── entity                  Servicio · Usuario · Pedido · DetallePedido
│   │   ├── dao                     ServicioDao · UsuarioDao · PedidoDao · DetalleDao
│   │   ├── relation                DetalleConServicio · PedidoConDetalle
│   │   ├── LavaDatabase.kt         singleton de Room (4 tablas)
│   │   └── DatosIniciales.kt       datos de prueba en una transacción
│   ├── repository
│   │   ├── LavaRepository.kt       interface (el contrato)
│   │   └── LavaRepositoryRoom.kt   implementación con Room (crearPedido en withTransaction)
│   └── prefs
│       └── PreferenciasStore.kt    DataStore: perfil activo, modo oscuro, avisos, recibir pedidos
├── model                           Rol · EstadoPedido · Unidad · LineaPedido · IngresoServicio · ClienteResumen · Casilleros
├── viewmodel                       LavaViewModelFactory · Sesion · Inicio · NuevoPedido · DetallePedido · MisPedidos
│                                   Panel · Bandeja · GestionPedido · Servicios · Clientes
└── ui
    ├── theme                       Color · Type · Theme (LavaTheme por rol, claro y oscuro)
    ├── components                  AppTopBar · LavaButton · LavaTextField · EstadoPedidoChip · PedidoCard
    │   │                           StepperEstado · IconoServicio (Canvas) · SelectorCantidad · BarraInferior
    │   │                           AvatarIniciales · FilaAjuste · TarjetaUsuario · Formatos
    │   └── graficos                DatoGrafico · GraficoDona · GraficoBarras
    ├── navigation                  Rutas · NavLava (grafo de cada perfil)
    ├── estudiante                  Splash · Login · Inicio · NuevoPedido · DetallePedido · MisPedidos · Tarifas · Perfil
    └── administrador               Panel · Bandeja · GestionPedido · Servicios · FormularioServicio · Clientes · Ajustes
```

## Base de datos en 4 pasos

### 1. Modelo conceptual

| Entidad | Qué representa | Atributos principales |
|---|---|---|
| **Usuario** | Persona que usa la app: estudiante o administrador | nombre, código de estudiante, correo, clave, rol |
| **Servicio** | Lo que ofrece la lavandería | nombre, descripción, unidad de cobro, precio, horas de entrega, activo, ícono |
| **Pedido** | Encargo de un estudiante | fecha de registro, fecha de entrega, estado, total, casillero, pagado, nota |
| **Detalle de pedido** | Cada servicio dentro de un pedido | cantidad, precio unitario, subtotal |

Relaciones:

- Un **estudiante registra** muchos pedidos; cada pedido pertenece a un solo estudiante.
- Un **pedido incluye** varios servicios y un servicio aparece en muchos pedidos; la cantidad y el precio pertenecen a esa combinación, por eso nace **Detalle de pedido**.

Reglas de negocio que afectan a los datos: el precio se congela al crear el pedido; un servicio usado en algún pedido no se elimina (solo se desactiva); un casillero solo puede estar en un pedido LISTO a la vez; un estudiante tiene como máximo 3 pedidos activos.

### 2. MER

```mermaid
erDiagram
    USUARIO ||--o{ PEDIDO : registra
    PEDIDO ||--|{ DETALLE_PEDIDO : contiene
    SERVICIO ||--o{ DETALLE_PEDIDO : "aparece en"
```

- **Usuario 1 : N Pedido** — un estudiante puede tener muchos pedidos; un pedido es de un solo estudiante.
- **Pedido N : M Servicio** — un pedido puede tener lavado y planchado, y el lavado aparece en muchos pedidos. Se resuelve con la entidad **Detalle de pedido**, que además guarda la cantidad y el precio de esa línea.
- **Pedido 1 : N Detalle** (al menos uno) y **Servicio 1 : N Detalle**.

### 3. Modelo lógico

```mermaid
erDiagram
    usuarios ||--o{ pedidos : "usuarioId"
    pedidos ||--|{ detalles_pedido : "pedidoId"
    servicios ||--o{ detalles_pedido : "servicioId"
    usuarios {
        int id PK
        string nombre
        string codigo
        string correo UK
        string clave
        string rol
    }
    servicios {
        int id PK
        string nombre
        string descripcion
        string unidad
        double precio
        int horasEntrega
        bool activo
        string icono
    }
    pedidos {
        int id PK
        int usuarioId FK
        string fechaRegistro
        string fechaEntrega
        string estado
        double total
        double descuento
        string casillero
        bool pagado
        string nota
        long fechaCreacion
    }
    detalles_pedido {
        int id PK
        int pedidoId FK
        int servicioId FK
        double cantidad
        double precioUnitario
        double subtotal
    }
```

Normalización:

- **1FN:** todos los valores son atómicos. Un pedido con varios servicios no guarda columnas repetidas (`servicio1`, `servicio2`…): cada línea es una fila de `detalles_pedido`.
- **2FN:** todas las tablas tienen clave primaria simple (`id`), así que ningún atributo depende de solo una parte de la clave.
- **3FN:** los datos del estudiante y del servicio no se copian en el pedido; se obtienen por su clave foránea (`usuarioId`, `servicioId`).
- **Redundancia controlada (decisión de negocio):** `precioUnitario`, `subtotal` y `total` se podrían calcular, pero se guardan a propósito como una *foto* del momento de la compra: si mañana sube el precio del kilo, los pedidos antiguos conservan lo que costaron y los ingresos del panel no cambian. Se escriben una sola vez, dentro de la transacción que crea el pedido, por eso no se desincronizan.

### 4. Modelo físico (SQLite generado por Room)

```sql
CREATE TABLE usuarios (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  nombre TEXT NOT NULL, codigo TEXT NOT NULL, correo TEXT NOT NULL, clave TEXT NOT NULL,
  rol TEXT NOT NULL                                   -- CLIENTE | ADMIN
);
CREATE UNIQUE INDEX index_usuarios_correo ON usuarios (correo);

CREATE TABLE servicios (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  nombre TEXT NOT NULL, descripcion TEXT NOT NULL,
  unidad TEXT NOT NULL,                               -- KG | PRENDA | UNIDAD
  precio REAL NOT NULL, horasEntrega INTEGER NOT NULL,
  activo INTEGER NOT NULL,                            -- 0 / 1
  icono TEXT NOT NULL                                 -- wash | iron | shirt
);

CREATE TABLE pedidos (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  usuarioId INTEGER NOT NULL,
  fechaRegistro TEXT NOT NULL, fechaEntrega TEXT NOT NULL,   -- 'aaaa-MM-dd'
  estado TEXT NOT NULL,                               -- RECIBIDO | EN_PROCESO | LISTO | ENTREGADO | CANCELADO
  total REAL NOT NULL, casillero TEXT,                -- casillero nulo hasta estar LISTO
  descuento REAL NOT NULL DEFAULT 0.0,                -- Fase 2 (versión 2 de la base)
  pagado INTEGER NOT NULL, nota TEXT NOT NULL, fechaCreacion INTEGER NOT NULL,
  FOREIGN KEY (usuarioId) REFERENCES usuarios(id) ON DELETE CASCADE
);
CREATE INDEX index_pedidos_usuarioId ON pedidos (usuarioId);
CREATE INDEX index_pedidos_estado ON pedidos (estado);  -- acelera panel y bandeja

CREATE TABLE detalles_pedido (
  id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
  pedidoId INTEGER NOT NULL, servicioId INTEGER NOT NULL,
  cantidad REAL NOT NULL, precioUnitario REAL NOT NULL, subtotal REAL NOT NULL,
  FOREIGN KEY (pedidoId) REFERENCES pedidos(id) ON DELETE CASCADE,       -- al borrar un pedido se borran sus líneas
  FOREIGN KEY (servicioId) REFERENCES servicios(id) ON DELETE RESTRICT   -- un servicio usado no se puede borrar
);
CREATE INDEX index_detalles_pedido_pedidoId ON detalles_pedido (pedidoId);
CREATE INDEX index_detalles_pedido_servicioId ON detalles_pedido (servicioId);
```

En la Fase 2 la columna `descuento` llegó con la versión 2 de la base. La migración `MIGRACION_1_2` ejecuta `ALTER TABLE pedidos ADD COLUMN descuento REAL NOT NULL DEFAULT 0.0`, así que los pedidos existentes se conservan con descuento 0.

Traducción a Room:

| Tabla | `@Entity` | DAO | Relaciones de lectura |
|---|---|---|---|
| `usuarios` | `Usuario` | `UsuarioDao` (login, clientes con `LEFT JOIN` + `COUNT` + `SUM`) | — |
| `servicios` | `Servicio` | `ServicioDao` | — |
| `pedidos` | `Pedido` | `PedidoDao` (lecturas con `@Transaction`, panel con `COALESCE(SUM)`) | `PedidoConDetalle` (`@Embedded` + `@Relation` a Usuario y a la lista de detalles) |
| `detalles_pedido` | `DetallePedido` | `DetalleDao` (ingresos por servicio con `JOIN` + `SUM` + `GROUP BY`) | `DetalleConServicio` (`@Embedded` + `@Relation` a Servicio) |

Las reglas que SQLite no puede expresar con `CHECK` en Room (casillero libre, máximo 3 pedidos activos, cobro previo a la entrega, pasos de la máquina de estados) viven en los **ViewModels**.

## Reglas de negocio implementadas

| Regla | Dónde vive |
|---|---|
| El estudiante solo ve sus pedidos; el administrador ve todos | `InicioViewModel`, `MisPedidosViewModel` / `BandejaViewModel` |
| Cantidades: KG de 1.0 a 10.0 en pasos de 0.5; PRENDA y UNIDAD de 1 a 30; solo servicios activos | `NuevoPedidoViewModel` |
| Máximo 3 pedidos activos | `NuevoPedidoViewModel.confirmar()` |
| Total = Σ cantidad × precio − descuento, guardado para siempre; estado inicial RECIBIDO y sin pagar | `LavaRepositoryRoom.crearPedido()` |
| Fecha de entrega = hoy + mayor tiempo de los servicios; si cae domingo pasa al lunes | `NuevoPedidoViewModel.calcularEntrega()` |
| Pedido y líneas en una sola transacción | `db.withTransaction { }` |
| Estudiante cancela solo en RECIBIDO; administrador en RECIBIDO o EN_PROCESO | `DetallePedidoViewModel`, `MisPedidosViewModel`, `GestionPedidoViewModel` |
| Estados en un solo sentido, un paso a la vez | `EstadoPedido.siguiente()` + `GestionPedidoViewModel.avanzar()` |
| LISTO exige casillero C-01…C-30 libre; ENTREGADO exige pago | `GestionPedidoViewModel.avanzar()` |
| Servicio usado no se elimina, solo se desactiva | `ServiciosViewModel.eliminar()` |
| "Recibir pedidos" apagado bloquea nuevos pedidos | `NuevoPedidoViewModel` + `PreferenciasStore` |
| Perfil activo y modo oscuro persisten al cerrar la app | `PreferenciasStore` + `SesionViewModel` |
| Fase 2: un pedido está atrasado si su fecha de entrega ya pasó y no está entregado ni cancelado | `PedidoDao.observarIdsAtrasados()` + `BandejaViewModel` / `PanelViewModel` |
| Fase 2: si el lavado (servicios por kg) supera 5 kg, 10% de descuento sobre su subtotal | `NuevoPedidoViewModel.calcularDescuento()` |
| Fase 2: Pedir de nuevo copia las cantidades con los precios actuales y omite los servicios desactivados | `NuevoPedidoViewModel.repetir()` |

## Pruebas de aceptación

Todas se ejecutaron en el emulador (Pixel 8) antes de cerrar la Fase 1.

| Prueba | Resultado esperado | Estado |
|---|---|---|
| Login con clave incorrecta | Mensaje de error, no entra | Cumple |
| Estudiante: nuevo pedido sin servicios | El botón Confirmar está apagado | Cumple |
| Estudiante: 3.5 kg de lavado + 5 prendas de planchado | Total S/ 40.50 y entrega a 2 días (saltando domingo) | Cumple |
| Estudiante: crear un cuarto pedido activo | Mensaje de límite y no se crea | Cumple |
| Estudiante: cancelar un pedido EN_PROCESO | No se permite cancelar | Cumple |
| Administrador: pasar a Listo sin casillero | Se rechaza con un mensaje | Cumple |
| Administrador: elegir un casillero ocupado | Aparece gris y no se puede elegir | Cumple |
| Administrador: entregar un pedido sin cobrar | Se rechaza; al marcar Cobrado sí se entrega | Cumple |
| Estudiante: ver el cambio de estado | El seguimiento se actualiza sin cerrar la pantalla | Cumple |
| Administrador: ingresos de la semana | Coinciden con la suma de los pedidos pagados | Cumple |
| Administrador: eliminar un servicio usado | No se elimina; se muestra un mensaje | Cumple |
| Cerrar y abrir la app | Los pedidos siguen ahí y el perfil y el modo oscuro se recuerdan | Cumple |

\* Como ambos perfiles están en el mismo dispositivo, se comprobó cambiando de perfil: el administrador pasó el pedido #13 a Listo con el casillero C-10 y, al volver al estudiante, su Inicio y su Detalle ya mostraban "Recógelo en el casillero C-10" sin reiniciar la app. Las pantallas leen el pedido con `Flow` de Room, que vuelve a emitir cada vez que la tabla cambia.

Pruebas de las mejoras de la Fase 2:

| Prueba | Resultado esperado | Estado |
|---|---|---|
| Administrador: abrir el Panel con un pedido vencido | Alerta "Hay 1 pedido atrasado" | Cumple |
| Administrador: ver la Bandeja | El pedido #12 aparece en rojo; los entregados y cancelados no | Cumple |
| Estudiante: 5 kg de lavado | Sin descuento, con la pista de los 5 kg | Cumple |
| Estudiante: 6 kg de lavado | Descuento de S/ 4.80 en el resumen, guardado en el pedido | Cumple |
| Actualizar la app sobre la base v1 | La migración agrega la columna y conserva los pedidos | Cumple |
| Estudiante: Pedir de nuevo con el precio del lavado cambiado | Se usan los precios actuales | Cumple |
| Estudiante: Pedir de nuevo con Planchado desactivado | Se omite y se avisa "Ya no disponible: Planchado" | Cumple |

## Para pensar

1. ¿Por qué un pedido tiene detalles en otra tabla?

Un pedido puede llevar varios servicios, cada uno con su cantidad. Guardarlos como columnas del pedido obligaría a repetir columnas y a fijar un máximo de servicios. En una tabla aparte, cada servicio del pedido es una fila, lo que resuelve la relación de muchos a muchos entre pedidos y servicios y permite sumar los ingresos por servicio con una sola consulta.

2. ¿Por qué guardar el precio unitario y el subtotal en el detalle?

Porque el pedido debe conservar lo que costó el día en que se hizo. Si el precio del servicio cambia después, los pedidos anteriores y los ingresos ya registrados no deben cambiar con él. Esos valores se guardan una sola vez, al crear el pedido, y no se vuelven a modificar.

3. ¿La función cambia datos una sola vez o debe avisar cuando cambian?

Esa pregunta define el tipo de cada función del repositorio. Las que guardan o consultan algo una vez, como crear un pedido o cambiar su estado, son suspend. Las que alimentan una pantalla que debe actualizarse sola, como la lista de pedidos o los contadores del panel, devuelven un Flow. Así, cuando el administrador cambia un pedido, la pantalla del estudiante muestra el cambio sin recargar.

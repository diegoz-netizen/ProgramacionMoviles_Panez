package com.panez.campusgps.ui

import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.TextButton
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.panez.campusgps.model.Recorrido
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.EditLocationAlt
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.model.UbicacionUi
import com.panez.campusgps.ui.components.EstadoVacio
import com.panez.campusgps.ui.components.InsigniaLugar
import com.panez.campusgps.ui.components.iconoDe
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.util.formatearDistancia
import com.panez.campusgps.util.minutosAPie
import com.panez.campusgps.viewmodel.MapaViewModel

/**
 * Pantalla principal: el mapa ocupa todo; encima flotan la búsqueda, los filtros y el botón de ubicación,
 * y abajo una hoja que se arrastra con el lugar más cercano, la ficha del elegido y la lista por categorías.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()
    val modoEdicion by vm.modoEdicion.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val visibles by vm.visibles.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val ruta by vm.ruta.collectAsState()
    val recorrido by vm.recorrido.collectAsState()
    val fueraDelCampus by vm.fueraDelCampus.collectAsState()
    val red by vm.red.collectAsState()
    val modoPasillos by vm.modoPasillos.collectAsState()
    val nodoActual by vm.nodoActual.collectAsState()

    val contexto = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        // Basta con uno (FINE o COARSE) para buscar; si niega los dos, la app sigue abierta
        if (resultado.values.any { it }) vm.buscar() else vm.permisoDenegado()
    }

    fun tienePermiso(permiso: String) =
        ContextCompat.checkSelfPermission(contexto, permiso) == PackageManager.PERMISSION_GRANTED

    // Pantalla de la app en Ajustes, para conceder el permiso a mano
    fun abrirAjustes() {
        contexto.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", contexto.packageName, null)
            )
        )
    }

    fun alPulsar() {
        if (tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION) ||
            tienePermiso(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            vm.buscar()
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // En horizontal la hoja recogida es más baja, para que el mapa no se quede sin espacio
    val horizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    val barraSistema = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val alturaHoja = (if (horizontal) Dimens.hojaHorizontal else Dimens.hojaRecogida) + barraSistema

    val hoja = rememberBottomSheetScaffoldState()
    // Al elegir un lugar, la hoja baja para que se vea el mapa con su ficha
    LaunchedEffect(seleccionado) {
        if (seleccionado != null) hoja.bottomSheetState.partialExpand()
    }

    val elegido = lugares.firstOrNull { it.lugar.id == seleccionado }
    val cercano = lugares.firstOrNull { it.lugar.id == masCercano?.id }

    BottomSheetScaffold(
        scaffoldState = hoja,
        sheetPeekHeight = alturaHoja,
        sheetShape = MaterialTheme.shapes.extraLarge.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        sheetShadowElevation = Dimens.sm,
        sheetContent = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
                contentPadding = PaddingValues(
                    start = Dimens.lg,
                    end = Dimens.lg,
                    bottom = barraSistema + Dimens.lg
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.xs)
            ) {
                // Parte de arriba: error, ficha del lugar elegido o el más cercano
                item(key = "cabecera") {
                    val error = estado as? UbicacionUi.Error
                    when {
                        error != null -> MensajeError(
                            error = error,
                            onAbrirAjustes = { abrirAjustes() },
                            onReintentar = { alPulsar() },
                            modifier = Modifier.padding(bottom = Dimens.md)
                        )
                        elegido != null && !modoEdicion && !modoPasillos -> FichaLugar(
                            item = elegido,
                            esMasCercano = elegido.lugar.id == masCercano?.id,
                            ruta = ruta,
                            recorrido = recorrido,
                            fueraDelCampus = fueraDelCampus,
                            onRuta = vm::alternarRuta,
                            onUbicar = { alPulsar() },
                            onCerrar = { vm.seleccionar(null) }
                        )
                        else -> ResumenCercano(
                            cercano = cercano,
                            buscando = estado is UbicacionUi.Buscando,
                            onUbicar = { alPulsar() },
                            onLlevar = { cercano?.let { vm.llevarA(it.lugar.id) } }
                        )
                    }
                }
                if (visibles.isEmpty()) {
                    item(key = "vacio") {
                        EstadoVacio(Icons.Filled.SearchOff, "No hay lugares que coincidan con «$busqueda»")
                    }
                }
                // La lista agrupada por categoría, en el orden de distancia
                ORDEN_CATEGORIAS.forEach { (categoria, titulo) ->
                    val grupo = visibles.filter { it.lugar.categoria == categoria }
                    if (grupo.isNotEmpty()) {
                        item(key = "titulo_$categoria") {
                            EncabezadoSeccion(titulo, grupo.size)
                        }
                        items(grupo, key = { it.lugar.id }) { item ->
                            TarjetaLugar(
                                item = item,
                                seleccionada = item.lugar.id == seleccionado,
                                esMasCercano = item.lugar.id == masCercano?.id,
                                onClick = { vm.seleccionar(item.lugar.id) }
                            )
                        }
                    }
                }
            }
        }
    ) {
        Box(Modifier.fillMaxSize()) {
            MapaUbicacion(
                estado = estado,
                // En modo edición se ven todos, para poder ubicar cualquiera
                lugares = if (modoEdicion) lugares else visibles,
                seleccionado = seleccionado,
                masCercano = masCercano,
                modoEdicion = modoEdicion,
                recorrido = recorrido,
                red = red,
                modoPasillos = modoPasillos,
                nodoActual = nodoActual,
                onTocarNodo = vm::tocarNodo,
                onTocarMapa = vm::tocarMapa,
                onTocarMarcador = { vm.seleccionar(it) },
                contentPadding = PaddingValues(top = Dimens.cabeceraMapa, bottom = alturaHoja),
                modifier = Modifier.fillMaxSize()
            )
            // Lo que flota sobre el mapa, debajo de la barra de estado
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Dimens.lg, vertical = Dimens.sm),
                verticalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                BarraBusqueda(
                    texto = busqueda,
                    onCambiar = vm::buscarTexto,
                    editando = modoEdicion || modoPasillos,
                    onUbicarPabellones = vm::alternarEdicion,
                    onDibujarPasillos = vm::alternarPasillos,
                    onTerminar = { if (modoPasillos) vm.alternarPasillos() else vm.alternarEdicion() }
                )
                if (modoPasillos) {
                    BarraPasillos(
                        puntos = red.nodos.size,
                        tramos = red.tramos.size,
                        onNuevoTramo = vm::nuevoTramo,
                        onDeshacer = vm::deshacerPasillo,
                        onBorrarTodo = vm::borrarPasillos
                    )
                } else if (modoEdicion) {
                    BarraEdicion(
                        nombre = elegido?.lugar?.nombre ?: "",
                        pendientes = lugares.count { !it.lugar.ubicado },
                        onAnterior = vm::anterior,
                        onSiguiente = vm::siguiente
                    )
                } else {
                    FiltrosCategoria(filtro, vm::filtrar)
                }
            }
            // Botón propio de "mi ubicación", justo encima de la hoja
            FloatingActionButton(
                onClick = { alPulsar() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimens.lg, bottom = alturaHoja + Dimens.lg),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                if (estado is UbicacionUi.Buscando) {
                    CircularProgressIndicator(Modifier.size(Dimens.icono), strokeWidth = Dimens.xs / 2)
                } else {
                    Icon(Icons.Filled.MyLocation, contentDescription = "Obtener mi ubicación")
                }
            }
        }
    }
}

// Orden y título de cada grupo de la lista
private val ORDEN_CATEGORIAS = listOf(
    "Pabellón" to "Pabellones",
    "Deportes" to "Deportes",
    "Servicios" to "Servicios"
)

/** Barra flotante para buscar un lugar por nombre o número ("P7"), con el botón para ubicar pabellones. */
@Composable
private fun BarraBusqueda(
    texto: String,
    onCambiar: (String) -> Unit,
    editando: Boolean,
    onUbicarPabellones: () -> Unit,
    onDibujarPasillos: () -> Unit,
    onTerminar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    var menuAbierto by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxWidth().height(Dimens.altoBusqueda),
        shape = MaterialTheme.shapes.extraLarge,
        color = colores.surface,
        shadowElevation = Dimens.xs
    ) {
        Row(
            modifier = Modifier.padding(start = Dimens.lg, end = Dimens.xs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = colores.onSurfaceVariant)
            Spacer(Modifier.width(Dimens.md))
            Box(Modifier.weight(1f)) {
                if (texto.isEmpty()) {
                    Text(
                        "Buscar pabellón o lugar",
                        style = MaterialTheme.typography.bodyLarge,
                        color = colores.onSurfaceVariant
                    )
                }
                BasicTextField(
                    value = texto,
                    onValueChange = onCambiar,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = colores.onSurface),
                    cursorBrush = SolidColor(colores.primary),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onCambiar("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Borrar búsqueda")
                }
            }
            // Herramientas del plano: ubicar pabellones o dibujar pasillos (con el check se termina)
            Box {
                IconButton(onClick = { if (editando) onTerminar() else menuAbierto = true }) {
                    Icon(
                        if (editando) Icons.Filled.Check else Icons.Filled.EditLocationAlt,
                        contentDescription = if (editando) "Terminar" else "Editar el plano",
                        tint = colores.primary
                    )
                }
                DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                    DropdownMenuItem(
                        text = { Text("Ubicar pabellones") },
                        leadingIcon = { Icon(Icons.Filled.EditLocationAlt, contentDescription = null) },
                        onClick = {
                            menuAbierto = false
                            onUbicarPabellones()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Dibujar pasillos") },
                        leadingIcon = { Icon(Icons.Filled.Route, contentDescription = null) },
                        onClick = {
                            menuAbierto = false
                            onDibujarPasillos()
                        }
                    )
                }
            }
        }
    }
}

/** Chips flotantes para ver solo pabellones, deportes o servicios. */
@Composable
private fun FiltrosCategoria(filtro: String?, onFiltrar: (String?) -> Unit) {
    val colores = MaterialTheme.colorScheme
    val opciones = listOf(null to "Todos") + ORDEN_CATEGORIAS
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
    ) {
        opciones.forEach { (categoria, texto) ->
            FilterChip(
                selected = filtro == categoria,
                onClick = { onFiltrar(categoria) },
                label = { Text(texto) },
                leadingIcon = categoria?.let {
                    { Icon(iconoDe(it), contentDescription = null, modifier = Modifier.size(Dimens.lg)) }
                },
                shape = MaterialTheme.shapes.extraLarge,
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = colores.surface,
                    labelColor = colores.onSurface,
                    iconColor = colores.primary,
                    selectedContainerColor = colores.primary,
                    selectedLabelColor = colores.onPrimary,
                    selectedLeadingIconColor = colores.onPrimary
                ),
                elevation = FilterChipDefaults.filterChipElevation(elevation = Dimens.xs / 2)
            )
        }
    }
}

/** Tarjeta destacada: el lugar más cercano con su distancia y el botón "Llévame". */
@Composable
private fun ResumenCercano(
    cercano: LugarConDistancia?,
    buscando: Boolean,
    onUbicar: () -> Unit,
    onLlevar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    val metros = cercano?.metros
    Surface(
        modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.md),
        shape = MaterialTheme.shapes.extraLarge,
        color = colores.primaryContainer,
        contentColor = colores.onPrimaryContainer
    ) {
        Row(
            modifier = Modifier.padding(Dimens.lg),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (cercano != null && metros != null) {
                InsigniaLugar(cercano.lugar, masCercano = true)
                Spacer(Modifier.width(Dimens.md))
                Column(Modifier.weight(1f)) {
                    Text("Lo más cercano a ti", style = MaterialTheme.typography.labelMedium)
                    Text(
                        cercano.lugar.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${formatearDistancia(metros)} · ${minutosAPie(metros)} min a pie",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(onClick = onLlevar) {
                    Icon(Icons.Filled.Directions, contentDescription = null, modifier = Modifier.size(Dimens.lg))
                    Spacer(Modifier.width(Dimens.xs))
                    Text("Llévame")
                }
            } else {
                Column(Modifier.weight(1f)) {
                    Text("¿Dónde estás?", style = MaterialTheme.typography.titleLarge)
                    Text(
                        "Activa tu ubicación para ver qué pabellón tienes más cerca",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Spacer(Modifier.width(Dimens.md))
                Button(onClick = onUbicar, enabled = !buscando) {
                    Text(if (buscando) "Buscando…" else "Activar")
                }
            }
        }
    }
}

/** Ficha del lugar elegido: qué es, a qué distancia está, cuánto se tarda a pie y la ruta. */
@Composable
private fun FichaLugar(
    item: LugarConDistancia,
    esMasCercano: Boolean,
    ruta: Boolean,
    recorrido: Recorrido?,
    fueraDelCampus: Boolean,
    onRuta: () -> Unit,
    onUbicar: () -> Unit,
    onCerrar: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    // Con la ruta trazada, la distancia es la del camino por los pasillos (no la línea recta)
    val metros = recorrido?.metros ?: item.metros
    Column(
        modifier = Modifier.fillMaxWidth().padding(bottom = Dimens.md),
        verticalArrangement = Arrangement.spacedBy(Dimens.lg)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InsigniaLugar(item.lugar, elegido = true, tamano = Dimens.insigniaGrande)
            Spacer(Modifier.width(Dimens.lg))
            Column(Modifier.weight(1f)) {
                Text(
                    if (esMasCercano) "${item.lugar.categoria} · el más cercano" else item.lugar.categoria,
                    style = MaterialTheme.typography.labelLarge,
                    color = colores.primary
                )
                Text(item.lugar.nombre, style = MaterialTheme.typography.headlineSmall)
                Text(
                    descripcionDe(item.lugar.categoria),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colores.onSurfaceVariant
                )
            }
            IconButton(onClick = onCerrar) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar ficha")
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.md)) {
            DatoLugar(
                icono = Icons.Filled.Straighten,
                valor = metros?.let(::formatearDistancia) ?: "—",
                etiqueta = when {
                    recorrido == null -> "En línea recta"
                    recorrido.porPasillos -> "Por los pasillos"
                    else -> "Sin pasillos aún"
                },
                modifier = Modifier.weight(1f)
            )
            DatoLugar(
                icono = Icons.AutoMirrored.Filled.DirectionsWalk,
                valor = metros?.let { "${minutosAPie(it)} min" } ?: "—",
                etiqueta = "Caminando",
                modifier = Modifier.weight(1f)
            )
        }
        if (fueraDelCampus) {
            OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth().height(Dimens.altoBoton)) {
                Text("La ruta se activa al llegar a Tecsup")
            }
        } else if (metros != null) {
            Button(onClick = onRuta, modifier = Modifier.fillMaxWidth().height(Dimens.altoBoton)) {
                Icon(if (ruta) Icons.Filled.Close else Icons.Filled.Directions, contentDescription = null)
                Spacer(Modifier.width(Dimens.sm))
                Text(if (ruta) "Ocultar ruta" else "Cómo llegar")
            }
        } else {
            OutlinedButton(onClick = onUbicar, modifier = Modifier.fillMaxWidth().height(Dimens.altoBoton)) {
                Icon(Icons.Filled.MyLocation, contentDescription = null)
                Spacer(Modifier.width(Dimens.sm))
                Text("Usa tu ubicación para ver la ruta")
            }
        }
    }
}

/** Recuadro con un dato de la ficha (distancia o minutos a pie). */
@Composable
private fun DatoLugar(icono: ImageVector, valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh
    ) {
        Row(Modifier.padding(Dimens.md), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Dimens.md))
            Column {
                Text(valor, style = MaterialTheme.typography.titleLarge)
                Text(
                    etiqueta,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/** Título de cada grupo de la lista, con cuántos lugares tiene. */
@Composable
private fun EncabezadoSeccion(titulo: String, cantidad: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = Dimens.md, bottom = Dimens.xs, start = Dimens.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(titulo, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.width(Dimens.sm))
        Text(
            "$cantidad",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Franja flotante del modo edición: qué pabellón ubicar y flechas para saltar al anterior o al siguiente. */
@Composable
private fun BarraEdicion(nombre: String, pendientes: Int, onAnterior: () -> Unit, onSiguiente: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.large,
        shadowElevation = Dimens.xs,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            Modifier.padding(horizontal = Dimens.xs, vertical = Dimens.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAnterior) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Anterior")
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Toca en el mapa dónde está:", style = MaterialTheme.typography.bodySmall)
                Text(nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (pendientes == 0) "Todos los lugares están ubicados" else "Faltan $pendientes por ubicar",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onSiguiente) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Siguiente")
            }
        }
    }
}

/** Franja flotante para dibujar los pasillos: cuántos hay y las acciones para corregir. */
@Composable
private fun BarraPasillos(
    puntos: Int,
    tramos: Int,
    onNuevoTramo: () -> Unit,
    onDeshacer: () -> Unit,
    onBorrarTodo: () -> Unit
) {
    var confirmar by remember { mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        shape = MaterialTheme.shapes.large,
        shadowElevation = Dimens.xs,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(Dimens.md), verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            Text("Toca el mapa por donde se camina", style = MaterialTheme.typography.titleSmall)
            Text(
                "Cada toque se une al punto anterior. Toca un punto existente para unir un cruce. " +
                    "$puntos puntos · $tramos tramos",
                style = MaterialTheme.typography.bodySmall
            )
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                AssistChip(
                    onClick = onNuevoTramo,
                    label = { Text("Nuevo tramo") },
                    leadingIcon = { Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(Dimens.lg)) }
                )
                AssistChip(
                    onClick = onDeshacer,
                    enabled = puntos > 0,
                    label = { Text("Deshacer") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = null, modifier = Modifier.size(Dimens.lg)) }
                )
                AssistChip(
                    onClick = { confirmar = true },
                    enabled = puntos > 0,
                    label = { Text("Borrar") },
                    leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(Dimens.lg)) }
                )
            }
        }
    }
    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            title = { Text("¿Borrar todos los pasillos?") },
            text = { Text("Se perderán los $puntos puntos dibujados. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmar = false
                    onBorrarTodo()
                }) { Text("Borrar") }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("Cancelar") } }
        )
    }
}

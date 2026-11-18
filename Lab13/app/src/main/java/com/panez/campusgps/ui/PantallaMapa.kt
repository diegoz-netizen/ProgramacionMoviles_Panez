package com.panez.campusgps.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.content.ContextCompat
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.model.Recorrido
import com.panez.campusgps.model.UbicacionUi
import com.panez.campusgps.ui.components.InsigniaLugar
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.util.formatearDistancia
import com.panez.campusgps.util.minutosAPie
import com.panez.campusgps.viewmodel.MapaViewModel

private enum class Pestana(val titulo: String, val icono: ImageVector) {
    MAPA("Mapa", Icons.Filled.Map),
    LUGARES("Lugares", Icons.Filled.GridView),
    RUTAS("Rutas", Icons.Filled.Route)
}

/** Pantalla principal con tres pestañas. El mapa sigue vivo debajo para no perder la cámara. */
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val visibles by vm.visibles.collectAsState()
    val resultados by vm.resultados.collectAsState()
    val filtro by vm.filtro.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val origen by vm.origen.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val ruta by vm.ruta.collectAsState()
    val recorrido by vm.recorrido.collectAsState()
    val planificado by vm.planificado.collectAsState()
    val fueraDelCampus by vm.fueraDelCampus.collectAsState()

    var pestana by rememberSaveable { mutableStateOf(Pestana.MAPA) }
    BackHandler(enabled = pestana != Pestana.MAPA) { pestana = Pestana.MAPA }

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

    val elegido = lugares.firstOrNull { it.lugar.id == seleccionado }
    val cercano = lugares.firstOrNull { it.lugar.id == masCercano?.id }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Pestana.entries.forEach { opcion ->
                    NavigationBarItem(
                        selected = pestana == opcion,
                        onClick = { pestana = opcion },
                        icon = { Icon(opcion.icono, contentDescription = null) },
                        label = { Text(opcion.titulo) }
                    )
                }
            }
        }
    ) { interior ->
        Box(Modifier.fillMaxSize().padding(bottom = interior.calculateBottomPadding())) {
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercano = masCercano,
                recorrido = recorrido,
                onTocarMapa = { vm.seleccionar(null) },
                onTocarMarcador = { vm.seleccionar(it) },
                contentPadding = PaddingValues(top = Dimens.cabeceraMapa, bottom = Dimens.tarjetaFlotante),
                modifier = Modifier.fillMaxSize()
            )
            // Búsqueda flotante con sus sugerencias
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = Dimens.lg, vertical = Dimens.sm),
                verticalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                BarraBusqueda(busqueda, vm::buscarTexto)
                if (busqueda.isNotBlank()) {
                    ResultadosBusqueda(resultados) { id ->
                        vm.buscarTexto("")
                        vm.seleccionar(id)
                    }
                }
            }
            // Botón de ubicación y tarjeta del lugar, sobre el mapa
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .widthIn(max = Dimens.anchoMaximo)
                    .fillMaxWidth()
                    .padding(Dimens.lg),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(Dimens.md)
            ) {
                FloatingActionButton(
                    onClick = { alPulsar() },
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    if (estado is UbicacionUi.Buscando) {
                        CircularProgressIndicator(Modifier.size(Dimens.icono), strokeWidth = Dimens.xs / 2)
                    } else {
                        Icon(Icons.Filled.MyLocation, contentDescription = "Obtener mi ubicación")
                    }
                }
                val error = estado as? UbicacionUi.Error
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = Dimens.sm
                ) {
                    Box(Modifier.padding(Dimens.lg)) {
                        when {
                            error != null -> MensajeError(
                                error = error,
                                onAbrirAjustes = { abrirAjustes() },
                                onReintentar = { alPulsar() }
                            )
                            elegido != null -> TarjetaElegido(
                                item = elegido,
                                recorrido = recorrido,
                                hayRuta = planificado != null,
                                rutaActiva = ruta,
                                fueraDelCampus = fueraDelCampus,
                                onRuta = vm::alternarRuta,
                                onUbicar = { alPulsar() },
                                onCerrar = { vm.seleccionar(null) }
                            )
                            cercano?.metros != null -> TarjetaCercano(cercano) { vm.llevarA(cercano.lugar.id) }
                            else -> TarjetaInicio(estado is UbicacionUi.Buscando) { alPulsar() }
                        }
                    }
                }
            }

            when (pestana) {
                Pestana.MAPA -> Unit
                Pestana.LUGARES -> PantallaLugares(
                    lugares = visibles,
                    filtro = filtro,
                    masCercanoId = masCercano?.id,
                    onFiltrar = vm::filtrar,
                    onElegir = { id ->
                        vm.seleccionar(id)
                        pestana = Pestana.MAPA
                    },
                    modifier = Modifier.fillMaxSize()
                )
                Pestana.RUTAS -> PantallaRutas(
                    lugares = lugares.map { it.lugar }.sortedBy { it.id },
                    origen = origen,
                    destino = seleccionado,
                    planificado = planificado,
                    fueraDelCampus = fueraDelCampus,
                    hayUbicacion = estado is UbicacionUi.Lista,
                    onOrigen = vm::cambiarOrigen,
                    onDestino = { vm.seleccionar(it) },
                    onFrecuente = vm::planificar,
                    onUbicar = { alPulsar() },
                    onIniciar = {
                        vm.iniciarRuta()
                        pestana = Pestana.MAPA
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

/** Barra flotante para buscar un lugar por nombre o número ("P7"). */
@Composable
private fun BarraBusqueda(texto: String, onCambiar: (String) -> Unit) {
    val colores = MaterialTheme.colorScheme
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
        }
    }
}

/** Sugerencias debajo de la búsqueda. */
@Composable
private fun ResultadosBusqueda(resultados: List<LugarConDistancia>, onElegir: (Int) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = Dimens.xs
    ) {
        if (resultados.isEmpty()) {
            Text(
                "No encontramos ese lugar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(Dimens.lg)
            )
        } else {
            LazyColumn(Modifier.heightIn(max = Dimens.altoLista)) {
                items(resultados, key = { it.lugar.id }) { item ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onElegir(item.lugar.id) }
                            .padding(horizontal = Dimens.md, vertical = Dimens.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InsigniaLugar(item.lugar)
                        Spacer(Modifier.width(Dimens.md))
                        Text(item.lugar.nombre, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

/** Tarjeta inicial: invita a activar la ubicación. */
@Composable
private fun TarjetaInicio(buscando: Boolean, onUbicar: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("¿Dónde estás?", style = MaterialTheme.typography.titleMedium)
            Text(
                "Activa tu ubicación para guiarte en Tecsup",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(Modifier.width(Dimens.md))
        Button(onClick = onUbicar, enabled = !buscando) {
            Text(if (buscando) "Buscando…" else "Activar")
        }
    }
}

/** Tarjeta con el lugar más cercano y el botón "Llévame". */
@Composable
private fun TarjetaCercano(item: LugarConDistancia, onLlevar: () -> Unit) {
    val metros = item.metros ?: return
    Row(verticalAlignment = Alignment.CenterVertically) {
        InsigniaLugar(item.lugar, masCercano = true)
        Spacer(Modifier.width(Dimens.md))
        Column(Modifier.weight(1f)) {
            Text(
                "Lo más cercano a ti",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                item.lugar.nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${formatearDistancia(metros)} · ${minutosAPie(metros)} min a pie",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Spacer(Modifier.width(Dimens.sm))
        Button(onClick = onLlevar) { Text("Llévame") }
    }
}

/** Tarjeta del lugar elegido, con su distancia y la ruta. */
@Composable
private fun TarjetaElegido(
    item: LugarConDistancia,
    recorrido: Recorrido?,
    hayRuta: Boolean,
    rutaActiva: Boolean,
    fueraDelCampus: Boolean,
    onRuta: () -> Unit,
    onUbicar: () -> Unit,
    onCerrar: () -> Unit
) {
    val metros = recorrido?.metros ?: item.metros
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.md)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            InsigniaLugar(item.lugar, elegido = true)
            Spacer(Modifier.width(Dimens.md))
            Column(Modifier.weight(1f)) {
                Text(
                    item.lugar.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    when {
                        metros == null -> descripcionDe(item.lugar.categoria)
                        recorrido != null -> "${formatearDistancia(metros)} · ${minutosAPie(metros)} min por los pasillos"
                        else -> "${formatearDistancia(metros)} en línea recta"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onCerrar) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar")
            }
        }
        when {
            hayRuta -> Button(onClick = onRuta, modifier = Modifier.fillMaxWidth()) {
                Icon(if (rutaActiva) Icons.Filled.Close else Icons.Filled.Directions, contentDescription = null)
                Spacer(Modifier.width(Dimens.sm))
                Text(if (rutaActiva) "Ocultar ruta" else "Cómo llegar")
            }
            fueraDelCampus -> OutlinedButton(onClick = {}, enabled = false, modifier = Modifier.fillMaxWidth()) {
                Text("La ruta se activa al llegar a Tecsup")
            }
            else -> OutlinedButton(onClick = onUbicar, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.MyLocation, contentDescription = null)
                Spacer(Modifier.width(Dimens.sm))
                Text("Usa tu ubicación para ver la ruta")
            }
        }
    }
}

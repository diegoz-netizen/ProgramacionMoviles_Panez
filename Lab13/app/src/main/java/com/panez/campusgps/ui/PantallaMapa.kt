package com.panez.campusgps.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.FilterChip
import androidx.compose.ui.draw.clip
import com.panez.campusgps.ui.components.iconoDe
import com.panez.campusgps.ui.components.BarraTecsup
import com.panez.campusgps.ui.theme.Dimens
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.platform.LocalConfiguration
import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.Icons
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.panez.campusgps.model.UbicacionUi
import com.panez.campusgps.util.formatearDistancia
import com.panez.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()
    val modoEdicion by vm.modoEdicion.collectAsState()

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

    Scaffold(
        topBar = {
            BarraTecsup(
                titulo = "CampusGPS · Tecsup",
                acciones = {
                    // Lápiz: entra o sale del modo para ubicar los pabellones en el mapa
                    IconButton(onClick = { vm.alternarEdicion() }) {
                        Icon(
                            if (modoEdicion) Icons.Filled.Check else Icons.Filled.Edit,
                            contentDescription = if (modoEdicion) "Terminar de ubicar" else "Ubicar pabellones",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { padding ->
        val lugares by vm.lugares.collectAsState()
        val visibles by vm.visibles.collectAsState()
        val filtro by vm.filtro.collectAsState()
        val seleccionado by vm.seleccionado.collectAsState()
        val masCercano by vm.masCercano.collectAsState()

        val error = estado as? UbicacionUi.Error
        // En horizontal el mapa y el panel van lado a lado (si no, el mapa se quedaba sin espacio)
        val horizontal = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

        val mapa: @Composable (Modifier) -> Unit = { m ->
            MapaUbicacion(
                estado = estado,
                // En modo edición se ven todos, para poder ubicar cualquiera
                lugares = if (modoEdicion) lugares else visibles,
                seleccionado = seleccionado,
                masCercano = masCercano,
                modoEdicion = modoEdicion,
                onTocarMapa = vm::tocarMapa,
                onTocarMarcador = { vm.seleccionar(it) },
                modifier = m
            )
        }
        val barraEdicion: @Composable () -> Unit = {
            if (modoEdicion) {
                val actual = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar
                BarraEdicion(
                    nombre = actual?.nombre ?: "",
                    pendientes = lugares.count { !it.lugar.ubicado },
                    onAnterior = vm::anterior,
                    onSiguiente = vm::siguiente
                )
            }
        }
        // Botones, mensajes y lista. listaLlena: la lista ocupa todo el alto que sobra (modo horizontal)
        val panel: @Composable (Modifier, Boolean) -> Unit = { m, listaLlena ->
          HojaInferior(m) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = Dimens.lg, vertical = Dimens.sm),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                Button(onClick = { alPulsar() }, enabled = estado !is UbicacionUi.Buscando) {
                    Text("Obtener mi ubicación")
                }
                when (val e = estado) {
                    UbicacionUi.Inicial -> Text(
                        "Pulsa el botón para empezar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    UbicacionUi.Buscando -> Text("Buscando tu ubicación…")
                    is UbicacionUi.Lista -> Unit
                    is UbicacionUi.Error -> MensajeError(
                        error = e,
                        onAbrirAjustes = { abrirAjustes() },
                        onReintentar = { alPulsar() },
                        modifier = Modifier.padding(top = Dimens.sm)
                    )
                }
                if (error == null) {
                    OutlinedButton(
                        onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                        enabled = masCercano != null,
                        // Azul como en el diseño (Material 3 lo pinta gris por defecto)
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (masCercano != null) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text("Ir al más cercano")
                    }
                    val cercano = lugares.firstOrNull { it.lugar.id == masCercano?.id }
                    if (cercano?.metros != null) {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Más cercano: ") }
                                append("${cercano.lugar.nombre} (${formatearDistancia(cercano.metros)})")
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    FiltrosCategoria(filtro, vm::filtrar)
                    // La lista va debajo del mapa, con altura fija
                    ListaLugares(
                        lugares = visibles,
                        seleccionado = seleccionado,
                        onClick = { vm.seleccionar(it) },
                        masCercanoId = masCercano?.id,
                        modifier = if (listaLlena) Modifier.fillMaxWidth().weight(1f)
                        else Modifier.fillMaxWidth().height(Dimens.altoLista)
                    )
                }
            }
          }
        }

        if (horizontal && error == null) {
            Row(Modifier.fillMaxSize().padding(padding)) {
                mapa(Modifier.weight(1f).fillMaxHeight())
                Column(Modifier.weight(1f).fillMaxHeight()) {
                    barraEdicion()
                    panel(Modifier.fillMaxWidth().weight(1f), true)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // El mapa ocupa el espacio libre de arriba
                if (error == null) mapa(Modifier.fillMaxWidth().weight(1f))
                barraEdicion()
                panel(Modifier.fillMaxWidth(), false)
            }
        }
    }
}

/** Franja del modo edición: dice qué pabellón ubicar y permite saltar al anterior o al siguiente. */
@Composable
private fun BarraEdicion(nombre: String, pendientes: Int, onAnterior: () -> Unit, onSiguiente: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.tertiaryContainer, modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(horizontal = Dimens.sm, vertical = Dimens.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onAnterior) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Anterior")
            }
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Toca en el mapa dónde está:", style = MaterialTheme.typography.bodySmall)
                Text(nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (pendientes == 0) "Todos los pabellones están ubicados" else "Faltan $pendientes por ubicar",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onSiguiente) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Siguiente")
            }
        }
    }
}

/** Panel con forma de hoja inferior: esquinas de arriba redondeadas y una agarradera. */
@Composable
private fun HojaInferior(modifier: Modifier, contenido: @Composable () -> Unit) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        tonalElevation = Dimens.xs,
        shadowElevation = Dimens.xs
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                Modifier
                    .padding(top = Dimens.sm)
                    .size(width = Dimens.xxl, height = Dimens.xs)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
            contenido()
        }
    }
}

/** Chips para ver solo pabellones, deportes o servicios. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltrosCategoria(filtro: String?, onFiltrar: (String?) -> Unit) {
    val opciones = listOf(null to "Todos", "Pabellón" to "Pabellones", "Deportes" to "Deportes", "Servicios" to "Servicios")
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
    ) {
        opciones.forEach { (categoria, texto) ->
            FilterChip(
                selected = filtro == categoria,
                onClick = { onFiltrar(categoria) },
                label = { Text(texto) },
                leadingIcon = categoria?.let { { Icon(iconoDe(it), contentDescription = null, modifier = Modifier.size(Dimens.lg)) } }
            )
        }
    }
}

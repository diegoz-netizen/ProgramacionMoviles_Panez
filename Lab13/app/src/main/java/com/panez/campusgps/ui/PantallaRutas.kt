package com.panez.campusgps.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.panez.campusgps.model.Lugar
import com.panez.campusgps.model.Recorrido
import com.panez.campusgps.ui.components.DatoRuta
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.util.formatearDistancia
import com.panez.campusgps.util.minutosAPie

private const val ID_ENTRADA = 18
private val DESTINOS_FRECUENTES = listOf(1, 7, 14, 17)

/** Planificador: eliges desde dónde y hasta dónde, y ves el recorrido antes de iniciarlo. */
@Composable
fun PantallaRutas(
    lugares: List<Lugar>,
    origen: Int?,
    destino: Int?,
    planificado: Recorrido?,
    fueraDelCampus: Boolean,
    hayUbicacion: Boolean,
    onOrigen: (Int?) -> Unit,
    onDestino: (Int) -> Unit,
    onFrecuente: (origen: Int, destino: Int) -> Unit,
    onUbicar: () -> Unit,
    onIniciar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val porId = lugares.associateBy { it.id }
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        Column(
            Modifier
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.lg)
        ) {
            Column {
                Text("Planifica tu ruta", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Te guiamos por los pasillos del campus",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceContainerLow) {
                Column(Modifier.padding(Dimens.md), verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                    SelectorLugar(
                        icono = Icons.Filled.MyLocation,
                        etiqueta = "Desde",
                        valor = origen?.let { porId[it]?.nombre } ?: "Mi ubicación",
                        opciones = listOf<Pair<Int?, String>>(null to "Mi ubicación") + lugares.map { it.id to it.nombre },
                        onElegir = onOrigen
                    )
                    SelectorLugar(
                        icono = Icons.Filled.Flag,
                        etiqueta = "Hasta",
                        valor = destino?.let { porId[it]?.nombre } ?: "Elige un lugar",
                        opciones = lugares.map { it.id to it.nombre },
                        onElegir = { id -> id?.let(onDestino) }
                    )
                }
            }
            when {
                destino == null -> Aviso("Elige a dónde quieres ir para ver el recorrido.")
                origen == destino -> Aviso("El origen y el destino son el mismo lugar.")
                fueraDelCampus -> Aviso("Estás fuera de Tecsup. Elige un punto de partida del campus o acércate a la entrada.")
                origen == null && !hayUbicacion -> OutlinedButton(onClick = onUbicar, modifier = Modifier.fillMaxWidth().height(Dimens.altoBoton)) {
                    Icon(Icons.Filled.MyLocation, contentDescription = null)
                    Spacer(Modifier.width(Dimens.sm))
                    Text("Activar mi ubicación")
                }
                planificado != null -> {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.md)) {
                        DatoRuta(Icons.Filled.Straighten, formatearDistancia(planificado.metros), "Por los pasillos", Modifier.weight(1f))
                        DatoRuta(Icons.AutoMirrored.Filled.DirectionsWalk, "${minutosAPie(planificado.metros)} min", "Caminando", Modifier.weight(1f))
                    }
                    Button(onClick = onIniciar, modifier = Modifier.fillMaxWidth().height(Dimens.altoBoton)) {
                        Icon(Icons.Filled.Navigation, contentDescription = null)
                        Spacer(Modifier.width(Dimens.sm))
                        Text("Iniciar ruta")
                    }
                }
            }
            Text("Desde la entrada", style = MaterialTheme.typography.titleMedium)
            Column(verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                DESTINOS_FRECUENTES.mapNotNull { porId[it] }.forEach { lugar ->
                    Surface(
                        onClick = { onFrecuente(ID_ENTRADA, lugar.id) },
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceContainerLow,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(Modifier.padding(Dimens.md), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Navigation, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(Dimens.md))
                            Text("Entrada → ${lugar.nombre}", style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Aviso(texto: String) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.secondaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(Dimens.lg))
    }
}

@Composable
private fun SelectorLugar(
    icono: ImageVector,
    etiqueta: String,
    valor: String,
    opciones: List<Pair<Int?, String>>,
    onElegir: (Int?) -> Unit
) {
    var abierto by remember { mutableStateOf(false) }
    Box {
        Row(
            Modifier
                .fillMaxWidth()
                .clickable { abierto = true }
                .padding(Dimens.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(Dimens.md))
            Column(Modifier.weight(1f)) {
                Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(valor, style = MaterialTheme.typography.titleMedium)
            }
            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Elegir $etiqueta")
        }
        DropdownMenu(expanded = abierto, onDismissRequest = { abierto = false }) {
            opciones.forEach { (id, nombre) ->
                DropdownMenuItem(
                    text = { Text(nombre) },
                    onClick = {
                        abierto = false
                        onElegir(id)
                    }
                )
            }
        }
    }
}

package com.tecsup.pokemon.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.domain.model.Estadistica
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.ErrorView
import com.tecsup.pokemon.ui.lista.ImagenPokemon
import com.tecsup.pokemon.ui.theme.nombreEstadistica
import com.tecsup.pokemon.ui.theme.tipoPokemon

/** La estadística base más alta de PokeAPI ronda 255: la barra se llena con respecto a ese valor. */
private const val ESTADISTICA_MAXIMA = 255f

@Composable
fun DetalleScreen(
    estado: UiState<Item>,
    onReintentar: () -> Unit,
    onFavorito: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (estado) {
        is UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> ErrorView(estado.mensaje, onReintentar, modifier)
        is UiState.Exito -> {
            val item = estado.datos
            Column(
                modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ImagenPokemon(item.imagenUrl, 200.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    item.numero?.let {
                        Text(
                            "N.º ${it.toString().padStart(3, '0')}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(item.titulo, style = MaterialTheme.typography.headlineLarge)
                }

                if (item.tipos.isEmpty()) {
                    // Sin datos del detalle (por ejemplo en las pruebas): se muestra el subtítulo tal cual
                    Text(item.subtitulo, style = MaterialTheme.typography.bodyLarge)
                } else {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item.tipos.forEach { ChipTipo(it) }
                    }
                }

                if (item.alturaM != null && item.pesoKg != null) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        DatoMedida(Icons.Filled.Height, "Altura", "${item.alturaM} m")
                        DatoMedida(Icons.Filled.MonitorWeight, "Peso", "${item.pesoKg} kg")
                    }
                }

                if (item.estadisticas.isNotEmpty()) TarjetaEstadisticas(item.estadisticas)

                BotonFavorito(item.esFavorito, onFavorito)
            }
        }
    }
}

/** Chip con el nombre del tipo en español y su color de la Pokédex. */
@Composable
private fun ChipTipo(tipo: String) {
    val datos = tipoPokemon(tipo)
    Surface(color = datos.color, contentColor = Color.White, shape = CircleShape) {
        Text(
            datos.nombre,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

@Composable
private fun RowScope.DatoMedida(icono: ImageVector, etiqueta: String, valor: String) {
    Card(
        Modifier.weight(1f),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.fillMaxWidth().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(valor, style = MaterialTheme.typography.titleMedium)
            Text(etiqueta, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaEstadisticas(estadisticas: List<Estadistica>) {
    Card(
        Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Estadísticas base", style = MaterialTheme.typography.titleMedium)
            estadisticas.forEach { e ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        nombreEstadistica(e.nombre),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.width(84.dp)
                    )
                    Text(
                        e.valor.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(36.dp)
                    )
                    LinearProgressIndicator(
                        progress = { (e.valor / ESTADISTICA_MAXIMA).coerceIn(0f, 1f) },
                        modifier = Modifier.weight(1f).padding(start = 8.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun BotonFavorito(esFavorito: Boolean, onFavorito: () -> Unit) {
    if (esFavorito) {
        OutlinedButton(onClick = onFavorito, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Star, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Quitar de favoritos", modifier = Modifier.padding(start = 8.dp))
        }
    } else {
        Button(onClick = onFavorito, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.StarBorder, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Marcar como favorito", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

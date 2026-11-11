package com.tecsup.pokemon.ui.lista

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.ErrorView
import com.tecsup.pokemon.ui.components.VacioView

// Datos de prueba: se reemplazan cuando la lista consuma la API
val itemsDePrueba = listOf(
    Item("charmander", "Charmander", "Tipo fuego", null),
    Item("charmeleon", "Charmeleon", "Tipo fuego", null),
    Item("charizard", "Charizard", "Tipo fuego", null),
    Item("vulpix", "Vulpix", "Tipo fuego", null),
    Item("growlithe", "Growlithe", "Tipo fuego", null)
)

@Composable
fun ListaScreen(
    estado: UiState<List<Item>>,
    onItemClick: (Item) -> Unit,
    onFavorito: (Item) -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier,
    aviso: String? = null,
    busqueda: String = "",
    onBuscar: (String) -> Unit = {}
) {
    when (estado) {
        is UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> ErrorView(estado.mensaje, onReintentar, modifier)
        is UiState.Exito -> Column(modifier.fillMaxSize()) {
            if (aviso != null) AvisoSinConexion(aviso, onReintentar)
            BarraBusqueda(busqueda, onBuscar)
            if (estado.datos.isEmpty()) {
                VacioView(
                    if (busqueda.isBlank()) "No hay Pokémon para mostrar"
                    else "Ningún Pokémon coincide con \"${busqueda.trim()}\""
                )
            } else {
                ListaItems(estado.datos, onItemClick, onFavorito)
            }
        }
    }
}

/** Campo para filtrar la lista por nombre, con botón para borrar lo escrito. */
@Composable
private fun BarraBusqueda(texto: String, onBuscar: (String) -> Unit) {
    OutlinedTextField(
        value = texto,
        onValueChange = onBuscar,
        placeholder = { Text("Buscar por nombre") },
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        trailingIcon = {
            if (texto.isNotEmpty()) {
                IconButton(onClick = { onBuscar("") }) {
                    Icon(Icons.Filled.Close, contentDescription = "Borrar búsqueda")
                }
            }
        },
        singleLine = true,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
        ),
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 12.dp)
    )
}

/** Franja superior que avisa que los datos son los guardados en el celular. */
@Composable
private fun AvisoSinConexion(texto: String, onReintentar: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(start = 16.dp, end = 8.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.CloudOff,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(18.dp)
            )
            Text(
                texto,
                color = MaterialTheme.colorScheme.onErrorContainer,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
            TextButton(onClick = onReintentar) { Text("Reintentar") }
        }
    }
}

/** Imagen del Pokémon sobre un círculo de color suave. */
@Composable
fun ImagenPokemon(url: String?, tamano: androidx.compose.ui.unit.Dp, modifier: Modifier = Modifier) {
    Box(
        modifier.size(tamano).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(tamano * 0.85f)
        )
    }
}

@Composable
fun ListaItems(
    items: List<Item>,
    onItemClick: (Item) -> Unit,
    onFavorito: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items, key = { it.id }) { item ->
            Card(
                Modifier.fillMaxWidth().clickable { onItemClick(item) },
                // Color opaco del tema: con un color translúcido la sombra se veía a través y formaba un doble borde
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    ImagenPokemon(item.imagenUrl, 72.dp)
                    Column(Modifier.weight(1f).padding(horizontal = 16.dp)) {
                        Text(item.titulo, style = MaterialTheme.typography.titleMedium)
                        Text(
                            item.subtitulo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { onFavorito(item) }) {
                        Icon(
                            if (item.esFavorito) Icons.Filled.Star else Icons.Filled.StarBorder,
                            contentDescription = if (item.esFavorito) "Quitar de favoritos" else "Marcar como favorito",
                            tint = if (item.esFavorito) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

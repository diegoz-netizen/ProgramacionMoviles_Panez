package com.tecsup.pokemon.ui.detalle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.ErrorView

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
        is UiState.Exito -> Column(modifier.fillMaxSize().padding(24.dp)) {
            Text(estado.datos.titulo, style = MaterialTheme.typography.headlineMedium)
            Text(estado.datos.subtitulo, style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onFavorito, modifier = Modifier.padding(top = 16.dp)) {
                Text(if (estado.datos.esFavorito) "Quitar de favoritos" else "Marcar como favorito")
            }
        }
    }
}

package com.tecsup.pokemon.ui.favoritos

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.components.VacioView
import com.tecsup.pokemon.ui.lista.ListaItems

@Composable
fun FavoritosScreen(
    estado: UiState<List<Item>>,
    onItemClick: (Item) -> Unit,
    onFavorito: (Item) -> Unit,
    modifier: Modifier = Modifier
) {
    when (estado) {
        is UiState.Cargando -> Cargando(modifier)
        is UiState.Error -> Text(estado.mensaje, modifier.padding(24.dp))
        is UiState.Exito ->
            if (estado.datos.isEmpty()) {
                VacioView("Aún no tienes favoritos.\nToca la estrella de un Pokémon para guardarlo aquí.", modifier)
            } else {
                ListaItems(estado.datos, onItemClick, onFavorito, modifier)
            }
    }
}

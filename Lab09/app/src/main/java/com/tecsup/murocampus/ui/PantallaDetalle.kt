package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

/** Detalle: la publicación completa y sus comentarios. */
@Composable
fun PantallaDetalle(vm: PostsViewModel, id: Int, modifier: Modifier = Modifier) {
    val estado by vm.detalle.collectAsStateWithLifecycle()

    when (val e = estado) {
        is UiState.Cargando -> Cargando("Cargando publicación…", modifier)
        is UiState.Error -> ErrorConReintento(e.mensaje, onReintentar = { vm.abrir(id) }, modifier = modifier)
        is UiState.Exito -> LazyColumn(
            modifier = modifier,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                TarjetaPost(e.datos.post.userId, e.datos.post.title, e.datos.post.body)
            }
            item {
                Text(
                    "Comentarios (${e.datos.comentarios.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(e.datos.comentarios, key = { it.id }) { comentario ->
                Column {
                    Text(
                        comentario.email,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(comentario.body, style = MaterialTheme.typography.bodyMedium)
                    HorizontalDivider(Modifier.padding(top = 12.dp))
                }
            }
        }
    }
}

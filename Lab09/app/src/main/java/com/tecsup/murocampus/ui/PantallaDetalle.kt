package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

/** Detalle: la publicación completa, sus comentarios y el botón Eliminar. */
@Composable
fun PantallaDetalle(
    vm: PostsViewModel,
    id: Int,
    onBorrado: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.detalle.collectAsStateWithLifecycle()
    val borrado by vm.borrado.collectAsStateWithLifecycle()
    var confirmando by rememberSaveable { mutableStateOf(false) }

    // Cuando el DELETE sale bien, se vuelve a la lista
    LaunchedEffect(borrado) {
        if (borrado is UiState.Exito) onBorrado()
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text("Eliminar publicación") },
            text = { Text("¿Seguro que quieres eliminar esta publicación?") },
            confirmButton = {
                TextButton(onClick = {
                    confirmando = false
                    vm.borrar(id)
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmando = false }) { Text("Cancelar") }
            }
        )
    }

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
                val borrando = borrado is UiState.Cargando
                OutlinedButton(
                    onClick = { confirmando = true },
                    enabled = !borrando,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (borrando) "Eliminando…" else "Eliminar publicación")
                }
                val error = borrado
                if (error is UiState.Error) {
                    Text(
                        "No se pudo eliminar: ${error.mensaje}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
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

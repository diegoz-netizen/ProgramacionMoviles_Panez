package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

/** Lista de publicaciones con el filtro por usuario arriba. */
@Composable
fun PantallaLista(
    vm: PostsViewModel,
    onAbrir: (Int) -> Unit,
    onCrear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val estado by vm.listaVisible.collectAsStateWithLifecycle()
    val filtro by vm.usuarioFiltro.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()

    Box(modifier) {
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = vm::buscar,
            label = { Text("Buscar por título") },
            singleLine = true,
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    TextButton(onClick = { vm.buscar("") }) { Text("Borrar") }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp)
        )
        FiltroUsuarios(filtro, onElegir = vm::filtrar)

        val contenido = Modifier.weight(1f)
        when (val e = estado) {
            is UiState.Cargando -> Cargando(modifier = contenido)
            is UiState.Error -> ErrorConReintento(e.mensaje, onReintentar = vm::cargar, modifier = contenido)
            is UiState.Exito ->
                if (e.datos.isEmpty()) {
                    Box(contenido.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            if (busqueda.isBlank()) "Sin publicaciones"
                            else "Ninguna publicación coincide con «${busqueda.trim()}»"
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = contenido,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(e.datos, key = { it.id }) { post ->
                            TarjetaPost(
                                post.userId, post.title, post.body,
                                maxLines = 2,
                                onClick = { onAbrir(post.id) }
                            )
                        }
                    }
                }
        }
    }
        FloatingActionButton(
            onClick = onCrear,
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Text("+", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

/** Fila desplazable de chips: Todos y Usuario 1 a Usuario 10. */
@Composable
private fun FiltroUsuarios(seleccionado: Int?, onElegir: (Int?) -> Unit) {
    val opciones: List<Int?> = listOf(null) + (1..10)
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(opciones) { userId ->
            FilterChip(
                selected = seleccionado == userId,
                onClick = { onElegir(userId) },
                label = { Text(if (userId == null) "Todos" else "Usuario $userId") }
            )
        }
    }
}

package com.panez.campusgps.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.ui.components.IconoCategoria
import com.panez.campusgps.ui.components.InsigniaDistancia
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.util.formatearDistancia

// Solo dibuja lo que recibe: no conoce el ViewModel
@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    onClick: (id: Int) -> Unit,
    modifier: Modifier = Modifier,
    masCercanoId: Int? = null
) {
    val estadoLista = rememberLazyListState()
    // Cuando la lista se reordena por distancia, vuelve arriba para que se vea el más cercano
    // (con key, LazyColumn seguía al primer elemento visible y quedaba desplazada)
    LaunchedEffect(lugares.firstOrNull()?.lugar?.id) {
        estadoLista.scrollToItem(0)
    }
    LazyColumn(
        modifier = modifier,
        state = estadoLista,
        verticalArrangement = Arrangement.spacedBy(Dimens.sm)
    ) {
        items(lugares, key = { it.lugar.id }) { item ->
            TarjetaLugar(
                item = item,
                seleccionada = item.lugar.id == seleccionado,
                esMasCercano = item.lugar.id == masCercanoId,
                onClick = { onClick(item.lugar.id) }
            )
        }
    }
}

@Composable
private fun TarjetaLugar(
    item: LugarConDistancia,
    seleccionada: Boolean,
    esMasCercano: Boolean,
    onClick: () -> Unit
) {
    val colores = MaterialTheme.colorScheme
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) colores.tertiaryContainer else colores.surfaceContainerLow
        ),
        border = BorderStroke(1.dp, if (seleccionada) colores.tertiary else colores.outlineVariant)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.md, vertical = Dimens.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconoCategoria(item.lugar.categoria, resaltado = seleccionada)
            Spacer(Modifier.width(Dimens.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.lugar.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (esMasCercano) "${item.lugar.categoria} · el más cercano" else item.lugar.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
            }
            InsigniaDistancia(
                texto = item.metros?.let(::formatearDistancia) ?: "—",
                esMasCercano = esMasCercano
            )
        }
    }
}

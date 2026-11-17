package com.panez.campusgps.ui

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.ui.theme.VerdeCercano
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
        verticalArrangement = Arrangement.spacedBy(6.dp)
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
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionada) colores.primaryContainer else colores.surfaceContainerHigh
        ),
        border = if (seleccionada) BorderStroke(1.5.dp, colores.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Punto de color: el mismo del marcador (verde el más cercano, azul los demás)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (esMasCercano) VerdeCercano else colores.primary, CircleShape)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.lugar.nombre,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    item.lugar.categoria,
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
            }
            Text(
                text = item.metros?.let(::formatearDistancia) ?: "Sin calcular",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = colores.primary
            )
        }
    }
}

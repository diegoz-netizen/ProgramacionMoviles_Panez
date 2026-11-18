package com.panez.campusgps.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.ui.components.InsigniaLugar
import com.panez.campusgps.ui.components.iconoDe
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.util.formatearDistancia
import com.panez.campusgps.util.minutosAPie

private val CATEGORIAS = listOf(
    null to "Todos",
    "Pabellón" to "Pabellones",
    "Deportes" to "Deportes",
    "Servicios" to "Servicios"
)

/** Directorio del campus: los lugares en cuadrícula, con filtro por categoría. */
@Composable
fun PantallaLugares(
    lugares: List<LugarConDistancia>,
    filtro: String?,
    masCercanoId: Int?,
    onFiltrar: (String?) -> Unit,
    onElegir: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier, color = MaterialTheme.colorScheme.background) {
        Column(Modifier.statusBarsPadding().padding(top = Dimens.lg)) {
            Column(Modifier.padding(horizontal = Dimens.lg)) {
                Text("Lugares de Tecsup", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "${lugares.size} lugares en el campus de Santa Anita",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = Dimens.lg, vertical = Dimens.sm),
                horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                CATEGORIAS.forEach { (categoria, texto) ->
                    FilterChip(
                        selected = filtro == categoria,
                        onClick = { onFiltrar(categoria) },
                        label = { Text(texto) },
                        leadingIcon = categoria?.let {
                            { Icon(iconoDe(it), contentDescription = null, modifier = Modifier.size(Dimens.lg)) }
                        }
                    )
                }
            }
            LazyVerticalGrid(
                columns = GridCells.Adaptive(Dimens.celdaLugar),
                contentPadding = PaddingValues(start = Dimens.lg, end = Dimens.lg, bottom = Dimens.lg),
                horizontalArrangement = Arrangement.spacedBy(Dimens.md),
                verticalArrangement = Arrangement.spacedBy(Dimens.md)
            ) {
                items(lugares, key = { it.lugar.id }) { item ->
                    CeldaLugar(item, item.lugar.id == masCercanoId) { onElegir(item.lugar.id) }
                }
            }
        }
    }
}

@Composable
private fun CeldaLugar(item: LugarConDistancia, esMasCercano: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Column(Modifier.padding(Dimens.md), verticalArrangement = Arrangement.spacedBy(Dimens.xs)) {
            InsigniaLugar(item.lugar, masCercano = esMasCercano)
            Text(
                item.lugar.nombre,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                descripcionDe(item.lugar.categoria),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                minLines = 2,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                item.metros?.let { "${formatearDistancia(it)} · ${minutosAPie(it)} min" } ?: "Distancia sin calcular",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

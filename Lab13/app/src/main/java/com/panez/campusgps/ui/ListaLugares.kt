package com.panez.campusgps.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.ui.components.InsigniaLugar
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.ui.theme.VerdeCercano
import com.panez.campusgps.util.formatearDistancia
import com.panez.campusgps.util.minutosAPie

/** Fila de un lugar en la hoja: insignia, nombre, para qué sirve y distancia a pie. Solo dibuja lo que recibe. */
@Composable
fun TarjetaLugar(
    item: LugarConDistancia,
    seleccionada: Boolean,
    esMasCercano: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colores = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = if (seleccionada) colores.secondaryContainer else colores.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Dimens.sm, vertical = Dimens.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InsigniaLugar(item.lugar, elegido = seleccionada, masCercano = esMasCercano)
            Spacer(Modifier.width(Dimens.md))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.lugar.nombre, style = MaterialTheme.typography.titleMedium)
                Text(
                    if (item.lugar.ubicado) descripcionDe(item.lugar.categoria) else "Falta ubicarlo en el mapa",
                    style = MaterialTheme.typography.bodySmall,
                    color = colores.onSurfaceVariant
                )
            }
            val metros = item.metros
            if (metros != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        formatearDistancia(metros),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (esMasCercano) VerdeCercano else colores.onSurface
                    )
                    Text(
                        "${minutosAPie(metros)} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = colores.onSurfaceVariant
                    )
                }
            }
        }
    }
}

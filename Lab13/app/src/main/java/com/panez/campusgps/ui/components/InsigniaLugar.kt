package com.panez.campusgps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.panez.campusgps.model.Lugar
import com.panez.campusgps.ui.theme.AmbarSeleccion
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.ui.theme.VerdeCercano

/**
 * Identificador visual de un lugar, igual que su etiqueta en el mapa:
 * el número del pabellón ("P7") o el ícono de deportes o servicios.
 * Mismo código de color que el plano: ámbar elegido, verde el más cercano.
 */
@Composable
fun InsigniaLugar(
    lugar: Lugar,
    modifier: Modifier = Modifier,
    elegido: Boolean = false,
    masCercano: Boolean = false,
    tamano: Dp = Dimens.avatar
) {
    val colores = MaterialTheme.colorScheme
    val fondo = when {
        elegido -> AmbarSeleccion
        masCercano -> VerdeCercano
        !lugar.ubicado -> colores.outline
        else -> colores.primary
    }
    Box(
        modifier = modifier.size(tamano).background(fondo, MaterialTheme.shapes.medium),
        contentAlignment = Alignment.Center
    ) {
        if (lugar.categoria == "Pabellón") {
            Text(
                "P${lugar.id}",
                style = if (tamano > Dimens.avatar) MaterialTheme.typography.titleLarge
                else MaterialTheme.typography.titleSmall,
                color = colores.onPrimary
            )
        } else {
            Icon(iconoDe(lugar.categoria), contentDescription = lugar.categoria, tint = colores.onPrimary)
        }
    }
}

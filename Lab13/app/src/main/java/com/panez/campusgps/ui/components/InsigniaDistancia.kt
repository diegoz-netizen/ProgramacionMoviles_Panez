package com.panez.campusgps.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.panez.campusgps.ui.theme.Dimens
import com.panez.campusgps.ui.theme.VerdeCercano

/** Pastilla con la distancia. El más cercano va en verde, igual que su marcador. */
@Composable
fun InsigniaDistancia(texto: String, esMasCercano: Boolean, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = if (esMasCercano) VerdeCercano else MaterialTheme.colorScheme.secondaryContainer,
        contentColor = if (esMasCercano) MaterialTheme.colorScheme.onPrimary
        else MaterialTheme.colorScheme.onSecondaryContainer
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = Dimens.md, vertical = Dimens.xs)
        )
    }
}

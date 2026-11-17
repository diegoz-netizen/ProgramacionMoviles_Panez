package com.panez.campusgps.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.panez.campusgps.ui.theme.Dimens

/** Ícono de cada categoría del campus. */
fun iconoDe(categoria: String): ImageVector = when (categoria) {
    "Pabellón" -> Icons.Filled.School
    "Deportes" -> Icons.Filled.SportsSoccer
    else -> Icons.Filled.Info
}

/** Círculo con el ícono de la categoría. Resaltado: colores de acento (el pabellón elegido). */
@Composable
fun IconoCategoria(categoria: String, resaltado: Boolean = false, modifier: Modifier = Modifier) {
    val colores = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(Dimens.avatar)
            .background(
                if (resaltado) colores.tertiaryContainer else colores.primaryContainer,
                MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            iconoDe(categoria),
            contentDescription = categoria,
            tint = if (resaltado) colores.onTertiaryContainer else colores.onPrimaryContainer
        )
    }
}

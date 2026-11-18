package com.panez.campusgps.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.ui.graphics.vector.ImageVector

/** Ícono de cada categoría del campus. */
fun iconoDe(categoria: String): ImageVector = when (categoria) {
    "Pabellón" -> Icons.Filled.School
    "Deportes" -> Icons.Filled.SportsSoccer
    else -> Icons.Filled.Info
}

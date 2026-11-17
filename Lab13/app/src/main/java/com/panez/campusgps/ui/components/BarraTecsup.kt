package com.panez.campusgps.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

/** Barra superior de todas las pantallas: título y, si hay, un subtítulo (por ejemplo el saludo). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraTecsup(
    titulo: String,
    subtitulo: String? = null,
    acciones: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Column {
                Text(titulo, style = MaterialTheme.typography.titleLarge)
                if (subtitulo != null) {
                    Text(subtitulo, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

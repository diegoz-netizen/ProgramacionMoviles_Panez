package com.panez.centrotareas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Esquema = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E4FF),
    onPrimaryContainer = Color(0xFF001A41)
)

@Composable
fun CentroTareasTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Esquema, content = content)
}

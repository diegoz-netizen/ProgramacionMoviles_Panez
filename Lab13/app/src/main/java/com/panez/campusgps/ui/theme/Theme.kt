package com.panez.campusgps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = AzulCampus,
    onPrimary = Color.White,
    primaryContainer = AzulCampusContenedor,
    onPrimaryContainer = AzulCampusOscuro,
    secondary = AzulCampusOscuro,
    onSurfaceVariant = GrisTexto
)

private val EsquemaOscuro = darkColorScheme(
    primary = AzulCampusClaro,
    onPrimary = AzulCampusOscuro,
    primaryContainer = AzulCampusContenedorOscuro,
    onPrimaryContainer = AzulCampusContenedor,
    secondary = AzulCampusContenedor
)

@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = Typography,
        content = content
    )
}

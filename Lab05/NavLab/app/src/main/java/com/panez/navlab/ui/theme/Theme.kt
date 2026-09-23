package com.panez.navlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = MoradoPrincipal,
    onPrimary = Blanco,
    primaryContainer = LavandaClaro,
    onPrimaryContainer = MoradoOscuro,
    secondary = MoradoMedio,
    background = LavandaClaro,
    onBackground = Color(0xFF2B2B2B),
    surface = Blanco,
    onSurface = Color(0xFF2B2B2B),
    surfaceVariant = GrisCard,
    onSurfaceVariant = Color(0xFF6E6E6E),
    error = RojoCoral
)

@Composable
fun NavLabTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}

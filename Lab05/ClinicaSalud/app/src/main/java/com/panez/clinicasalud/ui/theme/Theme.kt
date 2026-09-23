package com.panez.clinicasalud.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MoradoPrincipal,
    secondary = MoradoOscuro,
    tertiary = MoradoClaro
)

private val LightColorScheme = lightColorScheme(
    primary = MoradoPrincipal,
    onPrimary = Color.White,
    primaryContainer = MoradoClaro,
    secondary = MoradoOscuro,
    background = Color.White,
    surface = Color.White
)

@Composable
fun ClinicaSaludTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
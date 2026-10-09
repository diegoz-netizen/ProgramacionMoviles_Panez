package com.panez.saludpluscitas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val EsquemaClaro = lightColorScheme(
    primary = VerdePrincipal,
    onPrimary = Color.White,
    secondary = VerdeMedio,
    onSecondary = Color.White,
    background = Fondo,
    surface = Color.White,
    secondaryContainer = VerdeSuave,
    onSecondaryContainer = VerdePrincipal
)

@Composable
fun SaludPlusTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = EsquemaClaro, typography = Typography, content = content)
}
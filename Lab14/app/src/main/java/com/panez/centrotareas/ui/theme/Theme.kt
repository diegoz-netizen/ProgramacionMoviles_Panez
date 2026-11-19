package com.panez.centrotareas.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val EsquemaControl = darkColorScheme(
    primary = Primario,
    onPrimary = SobrePrimario,
    primaryContainer = ContenedorPrimario,
    onPrimaryContainer = SobreContenedorPrimario,
    secondary = Secundario,
    onSecondary = SobreSecundario,
    secondaryContainer = ContenedorSecundario,
    onSecondaryContainer = SobreContenedorSecundario,
    tertiary = Terciario,
    onTertiary = SobreTerciario,
    tertiaryContainer = ContenedorTerciario,
    onTertiaryContainer = SobreContenedorTerciario,
    error = ErrorControl,
    onError = SobreErrorControl,
    errorContainer = ContenedorError,
    onErrorContainer = SobreContenedorError,
    background = Fondo,
    onBackground = SobreFondo,
    surface = Fondo,
    onSurface = SobreFondo,
    surfaceVariant = VarianteSuperficie,
    onSurfaceVariant = SobreVarianteSuperficie,
    surfaceContainerLow = SuperficieBaja,
    surfaceContainer = Superficie,
    surfaceContainerHigh = SuperficieAlta,
    outline = Contorno,
    outlineVariant = VarianteContorno
)

@Composable
fun CentroTareasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EsquemaControl,
        typography = TipografiaControl,
        shapes = FormasControl,
        content = content
    )
}

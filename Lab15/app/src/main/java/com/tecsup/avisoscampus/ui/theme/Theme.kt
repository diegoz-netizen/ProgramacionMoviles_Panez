package com.tecsup.avisoscampus.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val Esquema = lightColorScheme(
    primary = Primario,
    onPrimary = SobrePrimario,
    primaryContainer = PrimarioContenedor,
    onPrimaryContainer = SobrePrimarioContenedor,
    secondary = Secundario,
    onSecondary = SobreSecundario,
    secondaryContainer = SecundarioContenedor,
    onSecondaryContainer = SobreSecundarioContenedor,
    tertiary = Terciario,
    onTertiary = SobreTerciario,
    tertiaryContainer = TerciarioContenedor,
    onTertiaryContainer = SobreTerciarioContenedor,
    background = Fondo,
    onBackground = SobreFondo,
    surface = Superficie,
    onSurface = SobreFondo,
    surfaceVariant = SuperficieVariante,
    onSurfaceVariant = SobreSuperficieVariante,
    surfaceContainerLow = SuperficieTarjeta,
    outline = Contorno,
    outlineVariant = ContornoVariante,
    error = Error,
    errorContainer = ErrorContenedor,
    onErrorContainer = SobreErrorContenedor
)

/** Colores de la app que no existen en Material 3 (éxito, acento ámbar y cabecera). */
@Immutable
data class ColoresAvisos(
    val exito: Color,
    val exitoContenedor: Color,
    val sobreExitoContenedor: Color,
    val ambar: Color,
    val cabeceraInicio: Color,
    val cabeceraFin: Color
)

private val LocalColoresAvisos = staticCompositionLocalOf {
    ColoresAvisos(Exito, ExitoContenedor, SobreExitoContenedor, Ambar, CabeceraInicio, CabeceraFin)
}

/** Se usa como MaterialTheme.avisos.exito, igual que colorScheme. */
val MaterialTheme.avisos: ColoresAvisos
    @Composable @ReadOnlyComposable get() = LocalColoresAvisos.current

/** Sin color dinámico: la identidad no cambia con el fondo de pantalla del celular. */
@Composable
fun AvisosCampusTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalColoresAvisos provides LocalColoresAvisos.current) {
        MaterialTheme(
            colorScheme = Esquema,
            typography = AppTypography,
            shapes = AppShapes,
            content = content
        )
    }
}

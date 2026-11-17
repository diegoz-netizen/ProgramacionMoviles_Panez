package com.panez.campusgps.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val EsquemaClaro = lightColorScheme(
    primary = PrimarioClaro, onPrimary = SobrePrimarioClaro,
    primaryContainer = ContenedorPrimarioClaro, onPrimaryContainer = SobreContenedorPrimarioClaro,
    secondary = SecundarioClaro, onSecondary = SobreSecundarioClaro,
    secondaryContainer = ContenedorSecundarioClaro, onSecondaryContainer = SobreContenedorSecundarioClaro,
    tertiary = TerciarioClaro, onTertiary = SobreTerciarioClaro,
    tertiaryContainer = ContenedorTerciarioClaro, onTertiaryContainer = SobreContenedorTerciarioClaro,
    background = FondoClaro, onBackground = SobreFondoClaro,
    surface = FondoClaro, onSurface = SobreFondoClaro,
    surfaceVariant = SuperficieVarianteClaro, onSurfaceVariant = SobreSuperficieVarianteClaro,
    outline = ContornoClaro, outlineVariant = ContornoVarianteClaro,
    surfaceContainerLow = ContenedorBajoClaro, surfaceContainer = ContenedorClaro,
    surfaceContainerHigh = ContenedorAltoClaro, surfaceContainerHighest = ContenedorMasAltoClaro
)

private val EsquemaOscuro = darkColorScheme(
    primary = PrimarioOscuro, onPrimary = SobrePrimarioOscuro,
    primaryContainer = ContenedorPrimarioOscuro, onPrimaryContainer = SobreContenedorPrimarioOscuro,
    secondary = SecundarioOscuro, onSecondary = SobreSecundarioOscuro,
    secondaryContainer = ContenedorSecundarioOscuro, onSecondaryContainer = SobreContenedorSecundarioOscuro,
    tertiary = TerciarioOscuro, onTertiary = SobreTerciarioOscuro,
    tertiaryContainer = ContenedorTerciarioOscuro, onTertiaryContainer = SobreContenedorTerciarioOscuro,
    background = FondoOscuro, onBackground = SobreFondoOscuro,
    surface = FondoOscuro, onSurface = SobreFondoOscuro,
    surfaceVariant = SuperficieVarianteOscuro, onSurfaceVariant = SobreSuperficieVarianteOscuro,
    outline = ContornoOscuro, outlineVariant = ContornoVarianteOscuro,
    surfaceContainerLow = ContenedorBajoOscuro, surfaceContainer = ContenedorOscuro,
    surfaceContainerHigh = ContenedorAltoOscuro, surfaceContainerHighest = ContenedorMasAltoOscuro
)

/** Tema Tecsup. Sin colores dinámicos: la identidad no cambia con el fondo de pantalla del teléfono. */
@Composable
fun CampusGPSTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) EsquemaOscuro else EsquemaClaro,
        typography = TipografiaTecsup,
        shapes = FormasTecsup,
        content = content
    )
}

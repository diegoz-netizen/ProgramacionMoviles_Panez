package com.tecsup.lavacampus.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tecsup.lavacampus.model.Rol

private fun esquemaClaro(primario: Color, contenedor: Color, fondo: Color) = lightColorScheme(
    primary = primario,
    onPrimary = Color.White,
    primaryContainer = contenedor,
    onPrimaryContainer = primario,
    secondary = primario,
    secondaryContainer = contenedor, // elementos seleccionados (selector, NavigationBar, chips)
    onSecondaryContainer = primario,
    background = fondo,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    // Diálogos, menús y hojas usan los surfaceContainer: se dejan neutros para no heredar el lila por defecto
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color.White,
    surfaceContainer = Color.White,
    surfaceContainerHigh = Color.White,
    surfaceContainerHighest = fondo,
    surfaceVariant = fondo,
    onSurfaceVariant = TextoSecundario,
    outline = Borde,
    error = RojoError
)

private fun esquemaOscuro(primario: Color, contenedor: Color) = darkColorScheme(
    primary = primario,
    onPrimary = FondoOscuro,
    primaryContainer = contenedor,
    onPrimaryContainer = Color.White,
    secondary = primario,
    secondaryContainer = contenedor,
    onSecondaryContainer = Color.White,
    background = FondoOscuro,
    surface = SuperficieOscura,
    surfaceContainerLowest = FondoOscuro,
    surfaceContainerLow = SuperficieOscura,
    surfaceContainer = SuperficieOscura,
    surfaceContainerHigh = SuperficieOscura,
    surfaceContainerHighest = SuperficieOscura,
    surfaceVariant = SuperficieOscura,
    error = RojoError
)

/** Entrega un ColorScheme según el perfil. En las pantallas usa MaterialTheme.colorScheme, nunca colores fijos. */
fun colorSchemePara(rol: Rol, oscuro: Boolean): ColorScheme = when (rol) {
    Rol.CLIENTE -> if (oscuro) esquemaOscuro(TurquesaOscuro, TurquesaContenedorOscuro)
                   else esquemaClaro(Turquesa, TurquesaContenedor, TurquesaFondo)
    Rol.ADMIN -> if (oscuro) esquemaOscuro(VioletaOscuro, VioletaContenedorOscuro)
                 else esquemaClaro(Violeta, VioletaContenedor, VioletaFondo)
}

@Composable
fun LavaTheme(rol: Rol, oscuro: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = colorSchemePara(rol, oscuro), typography = Typography, content = content)
}

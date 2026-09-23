package com.panez.navlab.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = CardWhite,
    primaryContainer = LavenderLight,
    onPrimaryContainer = PurpleDark,
    secondary = PurpleMedium,
    background = LavenderLight,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = GraySecondaryBox,
    onSurfaceVariant = TextSecondary,
    error = LogoutRed,
    errorContainer = LogoutRedBg
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

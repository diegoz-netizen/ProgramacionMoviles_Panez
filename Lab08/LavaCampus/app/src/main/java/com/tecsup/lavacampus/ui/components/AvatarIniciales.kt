package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Círculo con las iniciales del nombre ("Diego Panez" -> "DP"), con el color del rol. */
@Composable
fun AvatarIniciales(nombre: String, modifier: Modifier = Modifier, tam: Dp = 46.dp) {
    val iniciales = nombre.split(" ").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
    Box(
        modifier.size(tam).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(iniciales, style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

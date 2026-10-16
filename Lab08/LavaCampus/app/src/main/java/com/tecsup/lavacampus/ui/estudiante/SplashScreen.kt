package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.lavacampus.ui.components.IconoServicio
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTerminado: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(1500)
        onTerminado()
    }
    Column(
        Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primary),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        IconoServicio(
            "wash",
            tam = 112.dp,
            tinte = MaterialTheme.colorScheme.primary,
            fondo = MaterialTheme.colorScheme.onPrimary
        )
        Spacer(Modifier.height(24.dp))
        Text("LavaCampus", style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onPrimary)
        Text("Lavandería universitaria", style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
    }
}

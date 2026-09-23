package com.panez.clinicasalud.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.panez.clinicasalud.ui.theme.GrisTexto
import com.panez.clinicasalud.ui.theme.MoradoPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial médico") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Sin registros por ahora", color = GrisTexto)
        }
    }
}
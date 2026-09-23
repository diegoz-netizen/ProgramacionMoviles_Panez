package com.panez.tecsupfit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.tecsupfit.ui.theme.GrisTexto
import com.panez.tecsupfit.ui.theme.VerdePrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinasScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutinas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdePrincipal,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomBar(navController, "rutinas") }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Próximamente", color = GrisTexto)
        }
    }
}
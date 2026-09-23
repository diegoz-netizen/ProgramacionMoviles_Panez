package com.panez.tecsupfit.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
@Composable
fun ConfirmacionScreen(navController: NavHostController, claseId: Int, horario: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Confirmado") }
}

@Composable
fun ReservasScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Reservas") }
}

@Composable
fun RutinasScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Rutinas") }
}

@Composable
fun PerfilScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Perfil") }
}
package com.panez.tecsupfit.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
@Composable
fun DetalleClaseScreen(navController: NavHostController, claseId: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Detalle #$claseId") }
}

@Composable
fun ReservarCupoScreen(navController: NavHostController, claseId: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Reservar #$claseId") }
}

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
package com.panez.clinicasalud.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController

@Composable
fun InicioScreen(navController: NavHostController) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Inicio (pendiente)")
    }
}

@Composable
fun PerfilMedicoScreen(navController: NavHostController, medicoId: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Perfil médico #$medicoId (pendiente)")
    }
}

@Composable
fun AgendarCitaScreen(navController: NavHostController, medicoId: Int) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Agendar cita médico #$medicoId (pendiente)")
    }
}

@Composable
fun ConfirmacionScreen(navController: NavHostController, medicoId: Int, fecha: String, hora: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Confirmación: médico #$medicoId · $fecha · $hora")
    }
}

@Composable
fun MisCitasScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Mis citas (pendiente)")
    }
}

@Composable
fun HistorialScreen() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Historial médico (pendiente)")
    }
}
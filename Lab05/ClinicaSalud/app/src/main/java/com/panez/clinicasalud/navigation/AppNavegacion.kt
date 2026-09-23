package com.panez.clinicasalud.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panez.clinicasalud.screens.*

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") {
            InicioScreen(navController)
        }
        composable("perfil/{medicoId}") { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getString("medicoId")?.toIntOrNull() ?: 0
            PerfilMedicoScreen(navController, medicoId)
        }
        composable("agendar/{medicoId}") { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getString("medicoId")?.toIntOrNull() ?: 0
            AgendarCitaScreen(navController, medicoId)
        }
        composable("confirmacion/{medicoId}/{fecha}/{hora}") { backStackEntry ->
            val medicoId = backStackEntry.arguments?.getString("medicoId")?.toIntOrNull() ?: 0
            val fecha = backStackEntry.arguments?.getString("fecha") ?: ""
            val hora = backStackEntry.arguments?.getString("hora") ?: ""
            ConfirmacionScreen(navController, medicoId, fecha, hora)
        }
        composable("mis_citas") {
            MisCitasScreen()
        }
        composable("historial") {
            HistorialScreen()
        }
    }
}
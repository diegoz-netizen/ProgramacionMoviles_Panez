package com.panez.tecsupfit.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.panez.tecsupfit.screens.*

@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "inicio") {
        composable("inicio") { InicioScreen(navController) }
        composable("detalle/{claseId}") { backStackEntry ->
            val claseId = backStackEntry.arguments?.getString("claseId")?.toIntOrNull() ?: 0
            DetalleClaseScreen(navController, claseId)
        }
        composable("reservar/{claseId}") { backStackEntry ->
            val claseId = backStackEntry.arguments?.getString("claseId")?.toIntOrNull() ?: 0
            ReservarCupoScreen(navController, claseId)
        }
        composable("confirmacion/{claseId}/{horario}") { backStackEntry ->
            val claseId = backStackEntry.arguments?.getString("claseId")?.toIntOrNull() ?: 0
            val horario = backStackEntry.arguments?.getString("horario") ?: ""
            ConfirmacionScreen(navController, claseId, horario)
        }
        composable("reservas") { ReservasScreen(navController) }
        composable("rutinas") { RutinasScreen(navController) }
        composable("perfil") { PerfilScreen(navController) }
    }
}
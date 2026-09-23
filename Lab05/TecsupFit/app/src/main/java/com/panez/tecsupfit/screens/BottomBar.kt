package com.panez.tecsupfit.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun BottomBar(
    navController: NavHostController, rutaActual: String
) {
    val items = listOf(
        Triple("inicio", "Inicio", Icons.Default.Home),
        Triple("reservas", "Reservas", Icons.Default.List),
        Triple("rutinas", "Rutinas", Icons.Default.FitnessCenter),
        Triple("perfil", "Perfil", Icons.Default.Person)
    )

    NavigationBar {
        items.forEach { (ruta, label, icono) ->
            NavigationBarItem(selected = rutaActual == ruta, onClick = {
                if (rutaActual != ruta) {
                    navController.navigate(ruta) {
                        popUpTo("inicio") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }, icon = { Icon(icono, contentDescription = label) }, label = { Text(label) })
        }
    }
}
package com.tecsup.lavacampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel

@Composable
fun NavLava(factory: LavaViewModelFactory) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            SplashScreen(onTerminado = {
                // TODO (Paso 6): ir a LOGIN, o directo a INICIO/PANEL si hay perfil guardado
                nav.navigate(Rutas.SERVICIOS) { popUpTo(Rutas.SPLASH) { inclusive = true } }
            })
        }

        composable(Rutas.SERVICIOS) {
            val vm: ServiciosViewModel = viewModel(factory = factory)
            ServiciosScreen(vm = vm, onEditar = { /* TODO (Paso 13): nav.navigate(Rutas.formularioServicio(it)) */ })
        }

        // TODO (Pasos 6 a 13): destinos del estudiante (Scaffold con NavigationBar de 4 ítems)
        //  y del administrador (5 ítems), cada uno con su ViewModel creado por la factory.
    }
}

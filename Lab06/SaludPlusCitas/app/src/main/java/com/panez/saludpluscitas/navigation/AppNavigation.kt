package com.panez.saludpluscitas.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.screens.agendamiento.*
import com.panez.saludpluscitas.ui.screens.auth.*
import com.panez.saludpluscitas.ui.screens.citas.*
import com.panez.saludpluscitas.ui.screens.home.HomeScreen
import com.panez.saludpluscitas.ui.screens.notificaciones.NotificacionesScreen
import com.panez.saludpluscitas.ui.screens.perfil.PerfilScreen
import com.panez.saludpluscitas.ui.screens.resultados.ResultadosScreen

private fun NavController.irATab(ruta: String) {
    if (ruta == Rutas.HOME) {
        popBackStack(Rutas.HOME, false)
    } else {
        navigate(ruta) {
            popUpTo(Rutas.HOME)
            launchSingleTop = true
        }
    }
}

@Composable
fun AppNavigation() {
    val nav = rememberNavController()
    val onTab: (String) -> Unit = { nav.irATab(it) }

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            SplashScreen(
                onComenzar = { nav.navigate(Rutas.REGISTRO) },
                onLogin = { nav.navigate(Rutas.LOGIN) }
            )
        }
        composable(Rutas.REGISTRO) {
            RegistroScreen(
                onRegistrado = { nav.navigate(Rutas.HOME) { popUpTo(Rutas.SPLASH) { inclusive = true } } },
                onTerminos = { nav.navigate(Rutas.TERMINOS) },
                onLogin = { nav.navigate(Rutas.LOGIN) }
            )
        }
        composable(Rutas.LOGIN) {
            LoginScreen(
                onIngresar = { nav.navigate(Rutas.HOME) { popUpTo(Rutas.SPLASH) { inclusive = true } } },
                onRegistro = { nav.popBackStack() }
            )
        }
        composable(Rutas.TERMINOS) { TerminosScreen(onBack = { nav.popBackStack() }) }

        composable(Rutas.HOME) {
            HomeScreen(
                onAgendar = { nav.navigate(Rutas.ESPECIALIDADES) },
                onMisCitas = { nav.navigate(Rutas.MIS_CITAS) },
                onPerfil = { nav.navigate(Rutas.PERFIL) },
                onResultados = { nav.navigate(Rutas.RESULTADOS) },
                onNotificaciones = { nav.navigate(Rutas.NOTIFICACIONES) },
                onVerTodas = { nav.navigate(Rutas.ESPECIALIDADES) },
                onEspecialidad = { nav.navigate(Rutas.medicos(it)) },
                onTab = onTab
            )
        }

        composable(Rutas.ESPECIALIDADES) {
            EspecialidadesScreen(
                onBack = { nav.popBackStack() },
                onElegir = { nav.navigate(Rutas.medicos(it)) }
            )
        }
        composable(
            Rutas.MEDICOS,
            arguments = listOf(navArgument("especialidadId") { type = NavType.IntType })
        ) { entry ->
            MedicosScreen(
                especialidadId = entry.arguments?.getInt("especialidadId") ?: 0,
                onBack = { nav.popBackStack() },
                onElegir = { nav.navigate(Rutas.fechaHora(it)) }
            )
        }
        composable(
            Rutas.FECHA_HORA,
            arguments = listOf(navArgument("medicoId") { type = NavType.IntType })
        ) { entry ->
            val medicoId = entry.arguments?.getInt("medicoId") ?: 0
            FechaHoraScreen(
                medicoId = medicoId,
                onBack = { nav.popBackStack() },
                onContinuar = { fecha, hora -> nav.navigate(Rutas.confirmar(medicoId, fecha, hora)) }
            )
        }
        composable(
            Rutas.CONFIRMAR,
            arguments = listOf(
                navArgument("medicoId") { type = NavType.IntType },
                navArgument("fecha") { type = NavType.StringType },
                navArgument("hora") { type = NavType.StringType }
            )
        ) { entry ->
            ConfirmarCitaScreen(
                medicoId = entry.arguments?.getInt("medicoId") ?: 0,
                fecha = entry.arguments?.getString("fecha") ?: "",
                hora = entry.arguments?.getString("hora") ?: "",
                onBack = { nav.popBackStack() },
                onConfirmada = { citaId ->
                    nav.navigate(Rutas.citaExitosa(citaId)) {
                        popUpTo(Rutas.HOME) { inclusive = false }
                    }
                }
            )
        }
        composable(
            Rutas.CITA_EXITOSA,
            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
        ) { entry ->
            CitaExitosaScreen(
                citaId = entry.arguments?.getInt("citaId") ?: 0,
                onVerMisCitas = { nav.navigate(Rutas.MIS_CITAS) { popUpTo(Rutas.HOME) } },
                onInicio = { nav.popBackStack(Rutas.HOME, false) }
            )
        }

        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(onDetalle = { nav.navigate(Rutas.detalleCita(it)) }, onTab = onTab)
        }
        composable(
            Rutas.DETALLE_CITA,
            arguments = listOf(navArgument("citaId") { type = NavType.IntType })
        ) { entry ->
            DetalleCitaScreen(
                citaId = entry.arguments?.getInt("citaId") ?: 0,
                onBack = { nav.popBackStack() }
            )
        }
        composable(Rutas.PERFIL) {
            PerfilScreen(
                onTab = onTab,
                onCerrarSesion = {
                    Repositorio.cerrarSesion()
                    nav.navigate(Rutas.SPLASH) { popUpTo(nav.graph.id) { inclusive = true } }
                }
            )
        }
        composable(Rutas.RESULTADOS) { ResultadosScreen(onTab = onTab) }
        composable(Rutas.NOTIFICACIONES) { NotificacionesScreen(onBack = { nav.popBackStack() }) }
    }
}
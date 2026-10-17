package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun NavLava(factory: LavaViewModelFactory, sesion: SesionViewModel) {
    val nav = rememberNavController()
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()

    /** Pantalla principal de cada perfil; limpia la pila para que "atrás" no vuelva al login. */
    fun irAPrincipal(rol: Rol) {
        nav.navigate(if (rol == Rol.ADMIN) Rutas.PANEL else Rutas.INICIO) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun irALogin() {
        nav.navigate(Rutas.LOGIN) { popUpTo(0) { inclusive = true } }
    }

    NavHost(navController = nav, startDestination = Rutas.SPLASH) {

        composable(Rutas.SPLASH) {
            val revisada by sesion.sesionRevisada.collectAsStateWithLifecycle()
            var animacionTerminada by remember { mutableStateOf(false) }
            SplashScreen(onTerminado = { animacionTerminada = true })
            // Si ya hay perfil guardado, salta el login y va directo a su pantalla principal
            LaunchedEffect(animacionTerminada, revisada) {
                if (animacionTerminada && revisada) {
                    val u = usuario
                    if (u != null) irAPrincipal(Rol.valueOf(u.rol)) else irALogin()
                }
            }
        }

        composable(Rutas.LOGIN) {
            LoginScreen(vm = sesion, onIngresar = ::irAPrincipal)
        }

        // TODO (Paso 7): reemplazar por InicioScreen
        composable(Rutas.INICIO) {
            PantallaProvisional(
                titulo = "Hola, ${usuario?.nombre ?: ""}",
                mensaje = "Inicio del estudiante (Paso 7)",
                onCerrarSesion = { sesion.cerrarSesion(); irALogin() }
            )
        }

        // TODO (Paso 11): reemplazar por PanelScreen
        composable(Rutas.PANEL) {
            PantallaProvisional(
                titulo = "Hola, ${usuario?.nombre ?: ""}",
                mensaje = "Panel del administrador (Paso 11)",
                onCerrarSesion = { sesion.cerrarSesion(); irALogin() },
                accionExtra = "Ver servicios" to { nav.navigate(Rutas.SERVICIOS) }
            )
        }

        composable(Rutas.SERVICIOS) {
            val vm: ServiciosViewModel = viewModel(factory = factory)
            ServiciosScreen(vm = vm, onEditar = { /* TODO (Paso 13): nav.navigate(Rutas.formularioServicio(it)) */ })
        }

        // TODO (Pasos 7 a 13): destinos del estudiante (Scaffold con NavigationBar de 4 ítems)
        //  y del administrador (5 ítems), cada uno con su ViewModel creado por la factory.
    }
}

/** Marcador temporal para las pantallas principales que se construyen en pasos siguientes. */
@Composable
private fun PantallaProvisional(
    titulo: String,
    mensaje: String,
    onCerrarSesion: () -> Unit,
    accionExtra: Pair<String, () -> Unit>? = null
) {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        Text(mensaje, style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        if (accionExtra != null) {
            LavaButton(accionExtra.first, onClick = accionExtra.second)
            Spacer(Modifier.height(8.dp))
        }
        OutlinedButton(onClick = onCerrarSesion) { Text("Cerrar sesión") }
    }
}

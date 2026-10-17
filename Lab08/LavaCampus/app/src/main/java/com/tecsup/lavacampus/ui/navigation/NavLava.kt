package com.tecsup.lavacampus.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.administrador.BandejaScreen
import com.tecsup.lavacampus.ui.administrador.GestionPedidoScreen
import com.tecsup.lavacampus.ui.administrador.PanelScreen
import com.tecsup.lavacampus.ui.administrador.ServiciosScreen
import com.tecsup.lavacampus.ui.components.BarraInferior
import com.tecsup.lavacampus.ui.components.ItemBarra
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.estudiante.DetallePedidoScreen
import com.tecsup.lavacampus.ui.estudiante.InicioScreen
import com.tecsup.lavacampus.ui.estudiante.LoginScreen
import com.tecsup.lavacampus.ui.estudiante.MisPedidosScreen
import com.tecsup.lavacampus.ui.estudiante.NuevoPedidoScreen
import com.tecsup.lavacampus.ui.estudiante.SplashScreen
import com.tecsup.lavacampus.ui.estudiante.TarifasScreen
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel
import com.tecsup.lavacampus.viewmodel.InicioViewModel
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

/** Los 5 destinos de la barra inferior del administrador. */
private val itemsAdministrador = listOf(
    ItemBarra(Rutas.PANEL, "Panel", Icons.Filled.Dashboard),
    ItemBarra(Rutas.BANDEJA, "Pedidos", Icons.Filled.Inbox),
    ItemBarra(Rutas.SERVICIOS, "Servicios", Icons.Filled.LocalLaundryService),
    ItemBarra(Rutas.CLIENTES, "Clientes", Icons.Filled.Groups),
    ItemBarra(Rutas.AJUSTES, "Ajustes", Icons.Filled.Settings)
)

/** Los 4 destinos de la barra inferior del estudiante. */
private val itemsEstudiante = listOf(
    ItemBarra(Rutas.INICIO, "Inicio", Icons.Filled.Home),
    ItemBarra(Rutas.MIS_PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
    ItemBarra(Rutas.TARIFAS, "Tarifas", Icons.Filled.LocalOffer),
    ItemBarra(Rutas.PERFIL, "Perfil", Icons.Filled.Person)
)

@Composable
fun NavLava(factory: LavaViewModelFactory, sesion: SesionViewModel) {
    val nav = rememberNavController()
    val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
    val entradaActual by nav.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route

    /** Pantalla principal de cada perfil; limpia la pila para que "atrás" no vuelva al login. */
    fun irAPrincipal(rol: Rol) {
        nav.navigate(if (rol == Rol.ADMIN) Rutas.PANEL else Rutas.INICIO) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun irALogin() {
        nav.navigate(Rutas.LOGIN) { popUpTo(0) { inclusive = true } }
    }

    /**
     * Cambio de pestaña: vuelve a la raíz del perfil (Inicio o Panel) sin apilar pestañas repetidas
     * y conserva el estado de cada una.
     */
    fun irAPestana(ruta: String) {
        val raiz = if (usuario?.rol == Rol.ADMIN.name) Rutas.PANEL else Rutas.INICIO
        nav.navigate(ruta) {
            popUpTo(raiz) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    val barraEstudiante: @Composable () -> Unit = {
        BarraInferior(itemsEstudiante, rutaActual, ::irAPestana)
    }
    val barraAdministrador: @Composable () -> Unit = {
        BarraInferior(itemsAdministrador, rutaActual, ::irAPestana)
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

        // ---------- Estudiante ----------
        composable(Rutas.INICIO) {
            val u = usuario ?: return@composable
            val vm: InicioViewModel = viewModel(factory = factory)
            InicioScreen(
                vm = vm,
                usuario = u,
                onNuevoPedido = { nav.navigate(Rutas.NUEVO_PEDIDO) },
                onMisPedidos = { irAPestana(Rutas.MIS_PEDIDOS) },
                onTarifas = { irAPestana(Rutas.TARIFAS) },
                onPedido = { nav.navigate(Rutas.detallePedido(it)) },
                barraInferior = barraEstudiante
            )
        }

        composable(Rutas.NUEVO_PEDIDO) {
            val u = usuario ?: return@composable
            val vm: NuevoPedidoViewModel = viewModel(factory = factory)
            NuevoPedidoScreen(
                vm = vm,
                usuario = u,
                onBack = { nav.popBackStack() },
                onConfirmado = {
                    nav.popBackStack()
                    irAPestana(Rutas.MIS_PEDIDOS)
                }
            )
        }

        composable(
            Rutas.DETALLE_PEDIDO,
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("pedidoId") ?: 0
            val vm: DetallePedidoViewModel = viewModel(factory = factory)
            DetallePedidoScreen(vm = vm, pedidoId = id, onBack = { nav.popBackStack() })
        }

        composable(Rutas.MIS_PEDIDOS) {
            val u = usuario ?: return@composable
            val vm: MisPedidosViewModel = viewModel(factory = factory)
            MisPedidosScreen(
                vm = vm,
                usuario = u,
                onPedido = { nav.navigate(Rutas.detallePedido(it)) },
                onNuevoPedido = { nav.navigate(Rutas.NUEVO_PEDIDO) },
                barraInferior = barraEstudiante
            )
        }

        composable(Rutas.TARIFAS) {
            val vm: ServiciosViewModel = viewModel(factory = factory)
            TarifasScreen(vm = vm, barraInferior = barraEstudiante)
        }

        // TODO (Paso 13): reemplazar por PerfilScreen
        composable(Rutas.PERFIL) {
            PantallaProvisional(
                usuario?.nombre ?: "Perfil", "El perfil se construye en el Paso 13",
                acciones = listOf("Cerrar sesión" to { sesion.cerrarSesion(); irALogin() }),
                barraInferior = barraEstudiante
            )
        }

        // ---------- Administrador ----------
        composable(Rutas.PANEL) {
            val vm: PanelViewModel = viewModel(factory = factory)
            PanelScreen(
                vm = vm,
                nombreAdmin = usuario?.nombre ?: "",
                onVerPorRecoger = { irAPestana(Rutas.BANDEJA) },
                barraInferior = barraAdministrador
            )
        }

        composable(Rutas.BANDEJA) {
            val vm: BandejaViewModel = viewModel(factory = factory)
            BandejaScreen(
                vm = vm,
                onAbrir = { nav.navigate(Rutas.gestionPedido(it)) },
                barraInferior = barraAdministrador
            )
        }

        composable(
            Rutas.GESTION_PEDIDO,
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { entrada ->
            val id = entrada.arguments?.getInt("pedidoId") ?: 0
            val vm: GestionPedidoViewModel = viewModel(factory = factory)
            GestionPedidoScreen(vm = vm, pedidoId = id, onBack = { nav.popBackStack() })
        }

        composable(Rutas.SERVICIOS) {
            val vm: ServiciosViewModel = viewModel(factory = factory)
            ServiciosScreen(
                vm = vm,
                onEditar = { /* TODO (Paso 13): nav.navigate(Rutas.formularioServicio(it)) */ },
                barraInferior = barraAdministrador
            )
        }

        // TODO (Paso 13): reemplazar por ClientesScreen
        composable(Rutas.CLIENTES) {
            PantallaProvisional("Clientes", "Se construye en el Paso 13", barraInferior = barraAdministrador)
        }

        // TODO (Paso 13): reemplazar por AjustesScreen
        composable(Rutas.AJUSTES) {
            PantallaProvisional(
                usuario?.nombre ?: "Ajustes", "Los ajustes se construyen en el Paso 13",
                acciones = listOf("Cerrar sesión" to { sesion.cerrarSesion(); irALogin() }),
                barraInferior = barraAdministrador
            )
        }
    }
}

/** Marcador temporal para las pantallas que se construyen en pasos siguientes. */
@Composable
private fun PantallaProvisional(
    titulo: String,
    mensaje: String,
    acciones: List<Pair<String, () -> Unit>> = emptyList(),
    barraInferior: @Composable () -> Unit = {}
) {
    Scaffold(bottomBar = barraInferior, containerColor = MaterialTheme.colorScheme.background) { pad ->
        Column(
            Modifier.fillMaxSize().padding(pad).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(titulo, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
            Text(mensaje, style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
            Spacer(Modifier.height(24.dp))
            acciones.forEachIndexed { i, (texto, accion) ->
                if (i == 0) LavaButton(texto, onClick = accion)
                else OutlinedButton(onClick = accion) { Text(texto) }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

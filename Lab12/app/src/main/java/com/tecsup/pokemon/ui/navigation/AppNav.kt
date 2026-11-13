package com.tecsup.pokemon.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.pokemon.AppContainer
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.Sesion
import com.tecsup.pokemon.ui.AppViewModelFactory
import com.tecsup.pokemon.ui.admin.AdminScreen
import com.tecsup.pokemon.ui.admin.AdminViewModel
import com.tecsup.pokemon.ui.components.Cargando
import com.tecsup.pokemon.ui.detalle.DetalleScreen
import com.tecsup.pokemon.ui.detalle.DetalleViewModel
import com.tecsup.pokemon.ui.favoritos.FavoritosScreen
import com.tecsup.pokemon.ui.favoritos.FavoritosViewModel
import com.tecsup.pokemon.ui.lista.ListaScreen
import com.tecsup.pokemon.ui.lista.ListaViewModel
import com.tecsup.pokemon.ui.login.LoginScreen
import com.tecsup.pokemon.ui.login.LoginViewModel
import com.tecsup.pokemon.ui.login.RegistroScreen

object Rutas {
    const val LISTA = "lista"
    const val FAVORITOS = "favoritos"
    const val ADMIN = "admin"
    const val DETALLE = "detalle/{id}"
    fun detalle(id: String) = "detalle/$id"
}

/** Sin sesión muestra el login; con sesión, el catálogo. El rol de la sesión llega aquí desde Firestore. */
@Composable
fun AppNav(container: AppContainer) {
    val loginVm: LoginViewModel = viewModel(factory = AppViewModelFactory(container))
    val login by loginVm.estado.collectAsStateWithLifecycle()
    val sesion = login.sesion

    when {
        login.revisandoSesion -> Cargando()
        sesion == null && login.registrando -> RegistroScreen(
            estado = login,
            onRegistrar = loginVm::registrar,
            onEscribir = loginVm::limpiarError,
            onVolver = loginVm::volverALogin,
            modifier = Modifier.safeDrawingPadding()
        )
        sesion == null -> LoginScreen(
            estado = login,
            onIngresar = loginVm::iniciarSesion,
            onEscribir = loginVm::limpiarError,
            onIrARegistro = loginVm::irARegistro,
            modifier = Modifier.safeDrawingPadding()
        )
        else -> Catalogo(container, sesion, onCerrarSesion = loginVm::cerrarSesion)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Catalogo(container: AppContainer, sesion: Sesion, onCerrarSesion: () -> Unit) {
    // El panel Admin solo existe para la sesión con rol ADMIN (el rol viene de Firestore)
    val esAdmin = sesion.rol == Rol.ADMIN
    val nav = rememberNavController()
    val entrada by nav.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    // En el detalle no hay pestaña propia: se resalta la pestaña desde la que se abrió (Pokémon o Favoritos)
    val pestanaActiva = if (rutaActual == Rutas.DETALLE) nav.previousBackStackEntry?.destination?.route else rutaActual

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Pokédex de fuego")
                        Text(sesion.correo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                    }
                },
                // En el detalle aparece la flecha para volver a la pantalla anterior
                navigationIcon = {
                    if (rutaActual == Rutas.DETALLE) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onCerrarSesion) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                PestanaInferior(nav, pestanaActiva, Rutas.LISTA, Icons.Filled.LocalFireDepartment, "Pokémon")
                PestanaInferior(nav, pestanaActiva, Rutas.FAVORITOS, Icons.Filled.Star, "Favoritos")
                if (esAdmin) PestanaInferior(nav, pestanaActiva, Rutas.ADMIN, Icons.Filled.Settings, "Admin")
            }
        }
    ) { padding ->
        NavHost(nav, startDestination = Rutas.LISTA, modifier = Modifier.padding(padding)) {

            composable(Rutas.LISTA) {
                val vm: ListaViewModel = viewModel(factory = AppViewModelFactory(container))
                val estado by vm.estado.collectAsStateWithLifecycle()
                val aviso by vm.aviso.collectAsStateWithLifecycle()
                val busqueda by vm.busqueda.collectAsStateWithLifecycle()
                val tipos by vm.tiposDisponibles.collectAsStateWithLifecycle()
                val filtroTipo by vm.filtroTipo.collectAsStateWithLifecycle()
                ListaScreen(
                    estado = estado,
                    aviso = aviso,
                    busqueda = busqueda,
                    onBuscar = vm::buscar,
                    tipos = tipos,
                    filtroTipo = filtroTipo,
                    onFiltrarTipo = vm::filtrarPorTipo,
                    onItemClick = { nav.navigate(Rutas.detalle(it.id)) },
                    onFavorito = { vm.alternarFavorito(it.id) },
                    onReintentar = vm::refrescar
                )
            }

            composable(
                Rutas.DETALLE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { e ->
                val id = e.arguments?.getString("id").orEmpty()
                val vm: DetalleViewModel = viewModel(factory = AppViewModelFactory(container, id))
                val estado by vm.estado.collectAsStateWithLifecycle()
                DetalleScreen(estado = estado, onReintentar = vm::cargar, onFavorito = vm::alternarFavorito)
            }

            if (esAdmin) {
                composable(Rutas.ADMIN) {
                    val vm: AdminViewModel = viewModel(factory = AppViewModelFactory(container))
                    val ui by vm.ui.collectAsStateWithLifecycle()
                    AdminScreen(ui = ui, onSincronizar = vm::sincronizar, onLimpiarCache = vm::limpiarCache)
                }
            }

            composable(Rutas.FAVORITOS) {
                val vm: FavoritosViewModel = viewModel(factory = AppViewModelFactory(container))
                val estado by vm.estado.collectAsStateWithLifecycle()
                FavoritosScreen(
                    estado = estado,
                    onItemClick = { nav.navigate(Rutas.detalle(it.id)) },
                    onFavorito = { vm.alternarFavorito(it.id) }
                )
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.PestanaInferior(
    nav: NavHostController,
    rutaActual: String?,
    ruta: String,
    icono: ImageVector,
    etiqueta: String
) {
    NavigationBarItem(
        selected = rutaActual == ruta,
        onClick = {
            nav.navigate(ruta) {
                popUpTo(Rutas.LISTA) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
        icon = { Icon(icono, contentDescription = null) },
        label = { Text(etiqueta) }
    )
}

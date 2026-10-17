package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.BotonCerrarSesion
import com.tecsup.lavacampus.ui.components.FilaAjuste
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.components.TarjetaSeccion
import com.tecsup.lavacampus.ui.components.TarjetaUsuario
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    sesion: SesionViewModel,
    usuario: Usuario,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
    val notificaciones by sesion.notificaciones.collectAsStateWithLifecycle()
    val recibir by sesion.recibirPedidos.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Ajustes") },
        bottomBar = barraInferior,
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaUsuario(usuario)
            LavaButton("Cambiar de perfil", onClick = onCambiarPerfil, icono = Icons.Filled.SwapHoriz)

            TarjetaSeccion {
                FilaAjuste(Icons.Filled.DarkMode, "Modo oscuro", oscuro, sesion::cambiarModoOscuro)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                FilaAjuste(Icons.Filled.Notifications, "Notificaciones", notificaciones, sesion::cambiarNotificaciones,
                    descripcion = "Avisar cuando llegue un pedido nuevo")
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                FilaAjuste(Icons.Filled.Inbox, "Recibir pedidos", recibir, sesion::cambiarRecibirPedidos,
                    descripcion = if (recibir) "Los estudiantes pueden crear pedidos"
                                  else "Los estudiantes no pueden crear pedidos")
            }

            BotonCerrarSesion(onCerrarSesion)
        }
    }
}

package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

@Composable
fun PerfilScreen(
    sesion: SesionViewModel,
    pedidosVm: MisPedidosViewModel,
    usuario: Usuario,
    onCambiarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    LaunchedEffect(usuario.id) { pedidosVm.cargar(usuario.id) }
    val stats by pedidosVm.estadisticas.collectAsStateWithLifecycle()
    val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
    val avisos by sesion.notificaciones.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Mi perfil") },
        bottomBar = barraInferior,
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            TarjetaUsuario(usuario)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador("Activos", stats.activos.toString(), Modifier.weight(1f))
                Contador("Pedidos", stats.totales.toString(), Modifier.weight(1f))
                val kilos = if (stats.kilosLavados % 1.0 == 0.0) stats.kilosLavados.toInt().toString()
                            else "%.1f".format(stats.kilosLavados)
                Contador("Kilos lavados", kilos, Modifier.weight(1f))
            }

            TarjetaSeccion {
                FilaAjuste(Icons.Filled.DarkMode, "Modo oscuro", oscuro, sesion::cambiarModoOscuro)
                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                FilaAjuste(Icons.Filled.Notifications, "Avisos", avisos, sesion::cambiarNotificaciones,
                    descripcion = "Cuando tu pedido esté listo")
            }

            LavaButton("Cambiar de perfil", onClick = onCambiarPerfil, icono = Icons.Filled.SwapHoriz)
            BotonCerrarSesion(onCerrarSesion)
        }
    }
}

@Composable
private fun Contador(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier
    ) {
        Column(Modifier.padding(14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Text(titulo, style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

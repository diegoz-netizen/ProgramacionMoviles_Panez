package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.components.PedidoCard
import com.tecsup.lavacampus.ui.components.fechaCorta
import com.tecsup.lavacampus.ui.components.resumen
import com.tecsup.lavacampus.viewmodel.BandejaViewModel
import com.tecsup.lavacampus.viewmodel.FiltroBandeja

@Composable
fun BandejaScreen(
    vm: BandejaViewModel,
    onAbrir: (Int) -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val pedidos by vm.pedidos.collectAsStateWithLifecycle()
    val filtro by vm.filtro.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Pedidos") },
        bottomBar = barraInferior,
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(Modifier.padding(pad)) {
            Column(Modifier.padding(horizontal = 16.dp)) {
                LavaTextField(
                    valor = busqueda,
                    onCambio = vm::buscar,
                    etiqueta = "Buscar por estudiante o número",
                    icono = Icons.Filled.Search
                )
                Row(
                    Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroBandeja.entries.forEach { f ->
                        FilterChip(
                            selected = filtro == f,
                            onClick = { vm.elegirFiltro(f) },
                            label = { Text(f.etiqueta) }
                        )
                    }
                }
                if (mensaje != null) {
                    Text(mensaje!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 4.dp))
                }
            }

            if (pedidos.isEmpty()) {
                Text("No hay pedidos con este filtro", style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(24.dp))
            }
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos, key = { it.pedido.id }) { p ->
                    val estado = EstadoPedido.valueOf(p.pedido.estado)
                    PedidoCard(
                        numero = p.pedido.id,
                        titulo = p.usuario.nombre,
                        fecha = "Pedido #${p.pedido.id} · ${fechaCorta(p.pedido.fechaRegistro)}",
                        resumen = p.resumen(),
                        total = p.pedido.total,
                        estado = estado,
                        casillero = p.pedido.casillero,
                        onClick = { onAbrir(p.pedido.id) },
                        accion = accionPara(p, estado, vm, onAbrir)
                    )
                }
            }
        }
    }
}

/** Botón de acción según el estado: Iniciar, Marcar listo (abre la gestión para elegir casillero) o Entregar. */
private fun accionPara(
    p: PedidoConDetalle,
    estado: EstadoPedido,
    vm: BandejaViewModel,
    onAbrir: (Int) -> Unit
): (@Composable () -> Unit)? {
    val (texto, accion) = when (estado) {
        EstadoPedido.RECIBIDO -> "Iniciar" to { vm.iniciar(p) }
        EstadoPedido.EN_PROCESO -> "Marcar listo" to { onAbrir(p.pedido.id) }
        EstadoPedido.LISTO -> (if (p.pedido.pagado) "Entregar" else "Entregar (sin cobrar)") to { vm.entregar(p) }
        else -> return null
    }
    return {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            FilledTonalButton(onClick = accion) { Text(texto) }
        }
    }
}

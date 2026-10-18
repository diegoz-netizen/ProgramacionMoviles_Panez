package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.PedidoCard
import com.tecsup.lavacampus.ui.components.fechaCorta
import com.tecsup.lavacampus.ui.components.resumen
import com.tecsup.lavacampus.viewmodel.MisPedidosViewModel

@Composable
fun MisPedidosScreen(
    vm: MisPedidosViewModel,
    usuario: Usuario,
    onPedido: (Int) -> Unit,
    onNuevoPedido: () -> Unit,
    onRepetir: (Int) -> Unit,
    barraInferior: @Composable () -> Unit
) {
    LaunchedEffect(usuario.id) { vm.cargar(usuario.id) }
    val activos by vm.activos.collectAsStateWithLifecycle()
    val historial by vm.historial.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()

    var pestana by rememberSaveable { mutableIntStateOf(0) }
    var aCancelar by remember { mutableStateOf<PedidoConDetalle?>(null) }

    Scaffold(
        topBar = { AppTopBar("Mis pedidos") },
        bottomBar = barraInferior,
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNuevoPedido,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Nuevo pedido") }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(Modifier.padding(pad)) {
            PrimaryTabRow(selectedTabIndex = pestana, containerColor = MaterialTheme.colorScheme.background) {
                Tab(selected = pestana == 0, onClick = { pestana = 0 },
                    text = { Text("Activos (${activos.size})") })
                Tab(selected = pestana == 1, onClick = { pestana = 1 },
                    text = { Text("Historial (${historial.size})") })
            }
            if (mensaje != null) {
                Text(mensaje!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            }

            val lista = if (pestana == 0) activos else historial
            if (lista.isEmpty()) {
                Vacio(if (pestana == 0) "No tienes pedidos en curso" else "Aún no tienes pedidos terminados")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(lista, key = { it.pedido.id }) { p ->
                        PedidoCard(
                            numero = p.pedido.id,
                            fecha = fechaCorta(p.pedido.fechaRegistro),
                            resumen = p.resumen(),
                            total = p.pedido.total,
                            estado = EstadoPedido.valueOf(p.pedido.estado),
                            casillero = p.pedido.casillero,
                            onClick = { onPedido(p.pedido.id) },
                            accion = if (pestana == 1) {
                                // Historial: "Pedir de nuevo" carga las mismas líneas con los precios actuales
                                {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        FilledTonalButton(onClick = { onRepetir(p.pedido.id) }) {
                                            Icon(Icons.Filled.Replay, contentDescription = null)
                                            Text("  Pedir de nuevo")
                                        }
                                    }
                                }
                            } else if (vm.puedeCancelar(p)) {
                                {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        TextButton(onClick = { aCancelar = p }) {
                                            Text("Cancelar", color = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            } else null
                        )
                    }
                }
            }
        }
    }

    aCancelar?.let { p ->
        AlertDialog(
            onDismissRequest = { aCancelar = null },
            icon = { Icon(Icons.Filled.Cancel, contentDescription = null) },
            title = { Text("¿Cancelar el pedido #${p.pedido.id}?") },
            text = { Text("La lavandería no procesará este pedido. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { vm.cancelar(p); aCancelar = null }) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { aCancelar = null }) { Text("No") } }
        )
    }
}

@Composable
private fun Vacio(texto: String) {
    Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null,
                tint = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(bottom = 8.dp))
            Text(texto, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(48.dp))
        }
    }
}

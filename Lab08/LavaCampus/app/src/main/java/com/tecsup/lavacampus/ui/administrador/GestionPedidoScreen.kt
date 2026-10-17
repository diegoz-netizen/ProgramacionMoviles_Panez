package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.model.Casilleros
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.EstadoPedidoChip
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.components.StepperEstado
import com.tecsup.lavacampus.ui.components.cantidadConUnidad
import com.tecsup.lavacampus.ui.components.fechaLarga
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.viewmodel.GestionPedidoViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GestionPedidoScreen(vm: GestionPedidoViewModel, pedidoId: Int, onBack: () -> Unit) {
    LaunchedEffect(pedidoId) { vm.cargar(pedidoId) }
    val datos by vm.pedido.collectAsStateWithLifecycle()
    val ocupados by vm.casillerosOcupados.collectAsStateWithLifecycle()
    val elegido by vm.casilleroElegido.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var confirmarCancelar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar("Pedido #$pedidoId", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        val d = datos
        if (d == null) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        val p = d.pedido
        val estado = EstadoPedido.valueOf(p.estado)
        val siguiente = estado.siguiente()
        val casilleroActual = elegido ?: p.casillero

        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Estudiante
            Bloque {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val iniciales = d.usuario.nombre.split(" ").filter { it.isNotBlank() }.take(2)
                        .joinToString("") { it.first().uppercase() }
                    Box(Modifier.size(46.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center) {
                        Text(iniciales, style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(d.usuario.nombre, style = MaterialTheme.typography.titleMedium)
                        Text("${d.usuario.codigo} · ${d.usuario.correo}", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Registrado: ${fechaLarga(p.fechaRegistro)} · Entrega: ${fechaLarga(p.fechaEntrega)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    EstadoPedidoChip(estado)
                }
            }

            // Seguimiento
            if (estado != EstadoPedido.CANCELADO) {
                Bloque {
                    Text("Seguimiento", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    StepperEstado(estado, vertical = true)
                }
            }

            // Selector de casillero (ocupados en gris y deshabilitados)
            if (estado == EstadoPedido.EN_PROCESO || estado == EstadoPedido.LISTO) {
                Bloque {
                    Text("Casillero", style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (estado == EstadoPedido.LISTO) "Asignado: ${p.casillero ?: "-"}"
                        else "Elige un casillero libre para marcarlo como listo",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(10.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                        Casilleros.todos.forEach { c ->
                            val ocupado = c in ocupados
                            FilterChip(
                                selected = c == casilleroActual,
                                onClick = { vm.elegirCasillero(c) },
                                enabled = !ocupado && estado == EstadoPedido.EN_PROCESO,
                                label = { Text(c) }
                            )
                        }
                    }
                }
            }

            // Total y cobro
            Bloque {
                d.detalles.forEach { l ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                        Text("${cantidadConUnidad(l.detalle.cantidad, l.servicio.unidad)} ${l.servicio.nombre}",
                            style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                        Text(soles(l.detalle.subtotal), style = MaterialTheme.typography.bodyMedium)
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Total", style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(soles(p.total), style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                    Text("Cobrado", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.width(8.dp))
                    Switch(checked = p.pagado, onCheckedChange = vm::marcarPagado, enabled = !estado.esFinal)
                }
                if (p.nota.isNotBlank()) {
                    Text("Nota: ${p.nota}", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
                }
            }

            // Recordatorio de la regla del siguiente paso
            val regla = when (siguiente) {
                EstadoPedido.LISTO -> "Para marcarlo como listo debes elegir un casillero libre."
                EstadoPedido.ENTREGADO -> "Solo se entrega si está cobrado. Marca \"Cobrado\" al recibir el pago."
                EstadoPedido.EN_PROCESO -> "Al iniciar, el estudiante ya no podrá cancelar el pedido."
                else -> null
            }
            if (regla != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(regla, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (mensaje != null) {
                Text(mensaje!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
            }

            // Acciones
            if (siguiente != null) {
                LavaButton("Marcar como ${siguiente.etiqueta.lowercase()}", onClick = vm::avanzar)
            }
            if (vm.puedeCancelar(estado)) {
                OutlinedButton(
                    onClick = { confirmarCancelar = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Cancelar pedido", style = MaterialTheme.typography.labelLarge) }
            }
        }
    }

    if (confirmarCancelar) {
        AlertDialog(
            onDismissRequest = { confirmarCancelar = false },
            icon = { Icon(Icons.Filled.Cancel, contentDescription = null) },
            title = { Text("¿Cancelar el pedido #$pedidoId?") },
            text = { Text("El estudiante verá su pedido como cancelado.") },
            confirmButton = {
                TextButton(onClick = { confirmarCancelar = false; vm.cancelar() }) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmarCancelar = false }) { Text("No") } }
        )
    }
}

@Composable
private fun Bloque(contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) { contenido() }
    }
}

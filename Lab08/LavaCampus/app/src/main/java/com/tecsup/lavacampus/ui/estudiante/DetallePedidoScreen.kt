package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.EstadoPedidoChip
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.StepperEstado
import com.tecsup.lavacampus.ui.components.cantidadConUnidad
import com.tecsup.lavacampus.ui.components.fechaLarga
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.viewmodel.DetallePedidoViewModel

@Composable
fun DetallePedidoScreen(vm: DetallePedidoViewModel, pedidoId: Int, onBack: () -> Unit) {
    LaunchedEffect(pedidoId) { vm.cargar(pedidoId) }
    val datos by vm.pedido.collectAsStateWithLifecycle()
    val puedeCancelar by vm.puedeCancelar.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    var confirmar by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar("Pedido #$pedidoId", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        val d = datos
        if (d == null) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }
        val p = d.pedido
        val estado = EstadoPedido.valueOf(p.estado)

        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Cabecera: fecha y estado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Registrado el", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(fechaLarga(p.fechaRegistro), style = MaterialTheme.typography.titleMedium)
                }
                EstadoPedidoChip(estado)
            }

            // Seguimiento vertical (o aviso si fue cancelado)
            Bloque {
                if (estado == EstadoPedido.CANCELADO) {
                    Aviso(Icons.Filled.Cancel, "Este pedido fue cancelado", EstadoPedido.CANCELADO.color)
                } else {
                    Text("Seguimiento", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    StepperEstado(estado, vertical = true)
                    Spacer(Modifier.height(4.dp))
                    Text("Entrega estimada: ${fechaLarga(p.fechaEntrega)}", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Líneas del pedido
            Bloque {
                Text("Detalle", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                d.detalles.forEach { linea ->
                    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconoServicio(linea.servicio.icono, tam = 40.dp)
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(linea.servicio.nombre, style = MaterialTheme.typography.bodyLarge)
                            Text("${cantidadConUnidad(linea.detalle.cantidad, linea.servicio.unidad)} × ${soles(linea.detalle.precioUnitario)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Text(soles(linea.detalle.subtotal), style = MaterialTheme.typography.titleMedium)
                    }
                }
                HorizontalDivider(Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                    Text(soles(p.total), style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary)
                }
                if (p.nota.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Row {
                        Icon(Icons.AutoMirrored.Filled.Notes, null, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(p.nota, style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Pago y casillero
            if (estado != EstadoPedido.CANCELADO) {
                if (p.pagado) Aviso(Icons.Filled.CheckCircle, "Pagado", EstadoPedido.LISTO.color)
                else Aviso(Icons.Filled.Payments, "Pago pendiente, se paga al recoger", EstadoPedido.RECIBIDO.color)

                if (estado == EstadoPedido.LISTO && p.casillero != null) {
                    Aviso(Icons.Filled.Inventory2, "Recógelo en el casillero ${p.casillero}", EstadoPedido.LISTO.color)
                } else if (estado != EstadoPedido.ENTREGADO) {
                    Aviso(Icons.Filled.Inventory2, "Casillero: se asignará al estar listo",
                        MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Cancelación (solo RECIBIDO)
            if (!estado.esFinal) {
                OutlinedButton(
                    onClick = { confirmar = true },
                    enabled = puedeCancelar,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) { Text("Cancelar pedido", style = MaterialTheme.typography.labelLarge) }
                if (!puedeCancelar) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Info, null, tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Solo se cancela antes de empezar el lavado", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (mensaje != null) {
                Text(mensaje!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }

    if (confirmar) {
        AlertDialog(
            onDismissRequest = { confirmar = false },
            icon = { Icon(Icons.Filled.Cancel, contentDescription = null) },
            title = { Text("¿Cancelar el pedido #$pedidoId?") },
            text = { Text("La lavandería no procesará este pedido. Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = { confirmar = false; vm.cancelar() }) {
                    Text("Sí, cancelar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = { TextButton(onClick = { confirmar = false }) { Text("No") } }
        )
    }
}

/** Tarjeta blanca que agrupa cada sección del detalle. */
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

/** Recuadro de aviso con el color del estado (pago, casillero, cancelado). */
@Composable
private fun Aviso(icono: ImageVector, texto: String, color: Color) {
    Surface(color = color.copy(alpha = 0.10f), shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = color)
            Spacer(Modifier.width(10.dp))
            Text(texto, style = MaterialTheme.typography.titleMedium, color = color)
        }
    }
}

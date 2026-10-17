package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.Unidad
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.components.SelectorCantidad
import com.tecsup.lavacampus.ui.components.cantidadConUnidad
import com.tecsup.lavacampus.ui.components.fechaLarga
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.viewmodel.LineaResumen
import com.tecsup.lavacampus.viewmodel.NuevoPedidoViewModel
import com.tecsup.lavacampus.viewmodel.reglaCantidad
import java.time.LocalDate

@Composable
fun NuevoPedidoScreen(
    vm: NuevoPedidoViewModel,
    usuario: Usuario,
    onBack: () -> Unit,
    onConfirmado: () -> Unit
) {
    LaunchedEffect(usuario.id) { vm.cargar(usuario.id) }

    val servicios by vm.servicios.collectAsStateWithLifecycle()
    val cantidades by vm.cantidades.collectAsStateWithLifecycle()
    val nota by vm.nota.collectAsStateWithLifecycle()
    val lineas by vm.lineas.collectAsStateWithLifecycle()
    val total by vm.total.collectAsStateWithLifecycle()
    val fechaEntrega by vm.fechaEntrega.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val guardando by vm.guardando.collectAsStateWithLifecycle()
    val pedidoCreado by vm.pedidoCreado.collectAsStateWithLifecycle()
    val recibe by vm.recibirPedidos.collectAsStateWithLifecycle()

    LaunchedEffect(pedidoCreado) {
        if (pedidoCreado != null) onConfirmado()
    }

    Scaffold(
        topBar = { AppTopBar("Nuevo pedido", onBack = onBack) },
        bottomBar = {
            Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
                Column(Modifier.navigationBarsPadding().padding(16.dp)) {
                    if (mensaje != null) {
                        Text(mensaje!!, color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 8.dp))
                    }
                    LavaButton(
                        "Confirmar pedido",
                        onClick = vm::confirmar,
                        enabled = lineas.isNotEmpty() && !guardando && recibe,
                        icono = Icons.Filled.CheckCircle
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!recibe) {
                item {
                    Surface(color = MaterialTheme.colorScheme.error.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(14.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("La lavandería no está recibiendo pedidos", color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(14.dp))
                    }
                }
            }
            item {
                Text("Elige los servicios y la cantidad", style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            items(servicios, key = { it.id }) { s ->
                TarjetaServicio(
                    servicio = s,
                    cantidad = cantidades[s.id] ?: 0.0,
                    onCambio = { nueva -> vm.cambiarCantidad(s.id, nueva - (cantidades[s.id] ?: 0.0)) }
                )
            }
            item {
                LavaTextField(
                    valor = nota,
                    onCambio = vm::cambiarNota,
                    etiqueta = "Nota para la lavandería (opcional)",
                    icono = Icons.AutoMirrored.Filled.Notes,
                    unaLinea = false
                )
            }
            item { Resumen(lineas, total, fechaEntrega) }
        }
    }
}

@Composable
private fun TarjetaServicio(servicio: Servicio, cantidad: Double, onCambio: (Double) -> Unit) {
    val elegido = cantidad > 0
    val regla = servicio.reglaCantidad()
    val unidad = runCatching { Unidad.valueOf(servicio.unidad).abreviatura }.getOrDefault(servicio.unidad)
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = if (elegido) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconoServicio(servicio.icono)
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(servicio.nombre, style = MaterialTheme.typography.titleMedium)
                    Text("${soles(servicio.precio)} por $unidad · ${servicio.horasEntrega} h",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (elegido) "Subtotal ${soles(cantidad * servicio.precio)}" else "Toca + para agregar",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (elegido) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                SelectorCantidad(
                    valor = cantidad,
                    paso = regla.paso,
                    minimo = regla.minimo,
                    maximo = regla.maximo,
                    onCambio = onCambio,
                    unidad = if (servicio.unidad == Unidad.KG.name) "kg" else ""
                )
            }
        }
    }
}

@Composable
private fun Resumen(lineas: List<LineaResumen>, total: Double, fechaEntrega: LocalDate?) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Resumen", style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(8.dp))
            if (lineas.isEmpty()) {
                Text("Aún no elegiste servicios", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            lineas.forEach { l ->
                Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
                    Text("${cantidadConUnidad(l.cantidad, l.servicio.unidad)} ${l.servicio.nombre}",
                        style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(soles(l.subtotal), style = MaterialTheme.typography.bodyMedium)
                }
            }
            HorizontalDivider(Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline)
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Total", style = MaterialTheme.typography.titleMedium, modifier = Modifier.weight(1f))
                Text(soles(total), style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary)
            }
            if (fechaEntrega != null) {
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(6.dp))
                    Text("Entrega estimada: ${fechaLarga(fechaEntrega.toString())}",
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

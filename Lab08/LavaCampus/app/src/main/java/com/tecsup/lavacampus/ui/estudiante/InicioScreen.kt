package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.components.PedidoCard
import com.tecsup.lavacampus.ui.components.StepperEstado
import com.tecsup.lavacampus.ui.components.fechaCorta
import com.tecsup.lavacampus.ui.components.fechaLarga
import com.tecsup.lavacampus.ui.components.resumen
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.viewmodel.InicioViewModel

@Composable
fun InicioScreen(
    vm: InicioViewModel,
    usuario: Usuario,
    onNuevoPedido: () -> Unit,
    onMisPedidos: () -> Unit,
    onTarifas: () -> Unit,
    onPedido: (Int) -> Unit,
    barraInferior: @Composable () -> Unit
) {
    LaunchedEffect(usuario.id) { vm.cargar(usuario.id) }
    val activo by vm.pedidoActivo.collectAsStateWithLifecycle()
    val recientes by vm.recientes.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = barraInferior,
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Saludo(usuario) }
            item { TarjetaPedidoActivo(activo, onPedido) }
            item { LavaButton("Nuevo pedido", onClick = onNuevoPedido, icono = Icons.Filled.Add) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Acceso("Mis pedidos", Icons.AutoMirrored.Filled.ReceiptLong, onMisPedidos, Modifier.weight(1f))
                    Acceso("Tarifas", Icons.Filled.LocalOffer, onTarifas, Modifier.weight(1f))
                }
            }
            item {
                Text("Últimos pedidos", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 4.dp))
            }
            if (recientes.isEmpty()) {
                item {
                    Text("Aún no tienes pedidos.", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(recientes, key = { it.pedido.id }) { p ->
                PedidoCard(
                    numero = p.pedido.id,
                    fecha = fechaCorta(p.pedido.fechaRegistro),
                    resumen = p.resumen(),
                    total = p.pedido.total,
                    estado = EstadoPedido.valueOf(p.pedido.estado),
                    casillero = p.pedido.casillero,
                    onClick = { onPedido(p.pedido.id) }
                )
            }
        }
    }
}

@Composable
private fun Saludo(usuario: Usuario) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("Hola, ${usuario.nombre.substringBefore(' ')}",
                style = MaterialTheme.typography.headlineMedium)
            Text("Código ${usuario.codigo}", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        val iniciales = usuario.nombre.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
        Box(
            Modifier.size(48.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(iniciales, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

/** Tarjeta turquesa (color primario del rol) con el pedido en curso y su seguimiento. */
@Composable
private fun TarjetaPedidoActivo(activo: PedidoConDetalle?, onPedido: (Int) -> Unit) {
    val primario = MaterialTheme.colorScheme.primary
    val sobrePrimario = MaterialTheme.colorScheme.onPrimary
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = primario, contentColor = sobrePrimario),
        modifier = Modifier.fillMaxWidth().then(
            if (activo != null) Modifier.clickable { onPedido(activo.pedido.id) } else Modifier
        )
    ) {
        if (activo == null) {
            Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                IconoServicio("wash", tam = 52.dp, tinte = primario, fondo = sobrePrimario)
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("No tienes pedidos activos", style = MaterialTheme.typography.titleMedium)
                    Text("Crea uno y sigue aquí el estado de tu ropa",
                        style = MaterialTheme.typography.bodyMedium, color = sobrePrimario.copy(alpha = 0.85f))
                }
            }
            return@Card
        }

        val p = activo.pedido
        val estado = EstadoPedido.valueOf(p.estado)
        Column(Modifier.padding(20.dp)) {
            Text("Tu pedido en curso", style = MaterialTheme.typography.labelMedium,
                color = sobrePrimario.copy(alpha = 0.85f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Pedido #${p.id}", style = MaterialTheme.typography.titleLarge, modifier = Modifier.weight(1f))
                Text(soles(p.total), style = MaterialTheme.typography.titleLarge)
            }
            Text(activo.resumen(), style = MaterialTheme.typography.bodyMedium,
                color = sobrePrimario.copy(alpha = 0.85f))
            Spacer(Modifier.height(14.dp))

            // El stepper va sobre una superficie clara para que se vean los colores del seguimiento
            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()) {
                StepperEstado(estado, Modifier.padding(horizontal = 8.dp, vertical = 12.dp))
            }
            Spacer(Modifier.height(14.dp))

            if (estado == EstadoPedido.LISTO && p.casillero != null) {
                Surface(shape = RoundedCornerShape(14.dp), color = sobrePrimario.copy(alpha = 0.18f)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Inventory2, contentDescription = null)
                        Spacer(Modifier.width(10.dp))
                        Text("Recógelo en el casillero ${p.casillero}", style = MaterialTheme.typography.titleMedium)
                    }
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Event, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Entrega estimada: ${fechaLarga(p.fechaEntrega)}", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun Acceso(titulo: String, icono: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = modifier
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(10.dp))
            Text(titulo, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

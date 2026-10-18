package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.tecsup.lavacampus.model.EstadoPedido


/**
 * Tarjeta de un pedido para las listas. Recibe datos simples para no depender de la entidad;
 * [accion] permite poner un botón o enlace al pie (Cancelar, Iniciar, Entregar...).
 */
@Composable
fun PedidoCard(
    numero: Int,
    fecha: String,
    resumen: String,
    total: Double,
    estado: EstadoPedido,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    casillero: String? = null,
    titulo: String? = null,
    atrasado: Boolean = false,
    accion: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        // Pedido atrasado: borde rojo para que destaque en la bandeja
        border = if (atrasado) BorderStroke(2.dp, MaterialTheme.colorScheme.error) else null
    ) {
        Column(Modifier.padding(16.dp)) {
            if (atrasado) {
                Surface(color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.padding(bottom = 8.dp)) {
                    Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Atrasado: ya pasó su fecha de entrega", color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(titulo ?: "Pedido #$numero", style = MaterialTheme.typography.titleMedium)
                    Text(fecha, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                EstadoPedidoChip(estado)
            }
            Spacer(Modifier.height(8.dp))
            Text(resumen, style = MaterialTheme.typography.bodyMedium, maxLines = 2,
                overflow = TextOverflow.Ellipsis)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()) {
                Text("S/ %.2f".format(total), style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
                if (estado == EstadoPedido.LISTO && casillero != null) {
                    Surface(color = EstadoPedido.LISTO.color.copy(alpha = 0.12f), shape = RoundedCornerShape(10.dp)) {
                        Row(Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Inventory2, null, tint = EstadoPedido.LISTO.color,
                                modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Casillero $casillero", color = EstadoPedido.LISTO.color,
                                style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }
            }
            if (accion != null) {
                Spacer(Modifier.height(8.dp))
                accion()
            }
        }
    }
}

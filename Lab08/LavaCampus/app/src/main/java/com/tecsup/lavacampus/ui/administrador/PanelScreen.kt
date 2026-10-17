package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.fechaLarga
import com.tecsup.lavacampus.ui.components.graficos.GraficoBarras
import com.tecsup.lavacampus.ui.components.graficos.GraficoDona
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.viewmodel.PanelViewModel
import java.time.LocalDate

@Composable
fun PanelScreen(
    vm: PanelViewModel,
    nombreAdmin: String,
    onVerPorRecoger: () -> Unit,
    barraInferior: @Composable () -> Unit
) {
    val pedidosHoy by vm.pedidosHoy.collectAsStateWithLifecycle()
    val enProceso by vm.enProceso.collectAsStateWithLifecycle()
    val porRecoger by vm.porRecoger.collectAsStateWithLifecycle()
    val ingresos by vm.ingresosSemana.collectAsStateWithLifecycle()
    val porEstado by vm.porEstado.collectAsStateWithLifecycle()
    val porServicio by vm.porServicio.collectAsStateWithLifecycle()

    Scaffold(bottomBar = barraInferior, containerColor = MaterialTheme.colorScheme.background) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column {
                    Text("Panel de la lavandería", style = MaterialTheme.typography.headlineMedium)
                    Text("${fechaLarga(LocalDate.now().toString())} · $nombreAdmin",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Alerta: pedidos listos esperando recojo
            if (porRecoger > 0) {
                item { AlertaPorRecoger(porRecoger, onVerPorRecoger) }
            }

            // 3 contadores de color
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Contador("Pedidos de hoy", pedidosHoy, Icons.Filled.Today, EstadoPedido.RECIBIDO.color, Modifier.weight(1f))
                    Contador("En proceso", enProceso, Icons.Filled.LocalLaundryService, EstadoPedido.EN_PROCESO.color, Modifier.weight(1f))
                    Contador("Por recoger", porRecoger, Icons.Filled.Inventory2, EstadoPedido.LISTO.color, Modifier.weight(1f))
                }
            }

            // Ingresos de la semana (tarjeta violeta = color primario del administrador)
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("Ingresos de la semana", style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                            Text(soles(ingresos), style = MaterialTheme.typography.headlineMedium)
                            Text("Pedidos pagados desde el lunes", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f))
                        }
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, modifier = Modifier.size(40.dp))
                    }
                }
            }

            item {
                Seccion("Pedidos por estado") { GraficoDona(porEstado) }
            }
            item {
                Seccion("Ingresos por servicio (semana)") {
                    if (porServicio.isEmpty()) {
                        Text("Aún no hay pedidos pagados esta semana", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        GraficoBarras(porServicio, formato = { soles(it.toDouble()) })
                    }
                }
            }
        }
    }
}

@Composable
private fun Contador(titulo: String, valor: Int, icono: ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
        modifier = modifier
    ) {
        Column(Modifier.padding(14.dp)) {
            Icon(icono, contentDescription = null, tint = color)
            Spacer(Modifier.height(8.dp))
            Text(valor.toString(), style = MaterialTheme.typography.headlineMedium, color = color)
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun AlertaPorRecoger(cantidad: Int, onVer: () -> Unit) {
    val verde = EstadoPedido.LISTO.color
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = verde.copy(alpha = 0.12f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.NotificationsActive, contentDescription = null, tint = verde)
            Spacer(Modifier.width(10.dp))
            Text(
                if (cantidad == 1) "Hay 1 pedido listo esperando recojo"
                else "Hay $cantidad pedidos listos esperando recojo",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onVer,
                colors = ButtonDefaults.buttonColors(containerColor = verde, contentColor = Color.White)
            ) { Text("Ver") }
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(14.dp))
            contenido()
        }
    }
}

package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.model.ClienteResumen
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.AvatarIniciales
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.components.soles
import com.tecsup.lavacampus.viewmodel.ClientesViewModel

@Composable
fun ClientesScreen(vm: ClientesViewModel, barraInferior: @Composable () -> Unit) {
    val clientes by vm.clientes.collectAsStateWithLifecycle()
    val busqueda by vm.busqueda.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar("Clientes") },
        bottomBar = barraInferior,
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        LazyColumn(
            Modifier.padding(pad),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                LavaTextField(busqueda, vm::buscar, "Buscar por nombre o código", Icons.Filled.Search)
            }
            if (clientes.isEmpty()) {
                item {
                    Text("No se encontraron estudiantes", style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            items(clientes, key = { it.id }) { c -> TarjetaCliente(c) }
        }
    }
}

@Composable
private fun TarjetaCliente(c: ClienteResumen) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(c.nombre)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(c.nombre, style = MaterialTheme.typography.titleMedium)
                Text(c.codigo, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(if (c.cantidadPedidos == 1) "1 pedido" else "${c.cantidadPedidos} pedidos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(soles(c.totalGastado), style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
                Text("gastado", style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.End,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

package com.tecsup.pokemon.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Panel solo para el administrador: totales, sincronizar y limpiar caché. */
@Composable
fun AdminScreen(
    ui: AdminUi,
    onSincronizar: () -> Unit,
    onLimpiarCache: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Panel Admin", style = MaterialTheme.typography.headlineSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Total("Guardados", ui.totalGuardado, Modifier.weight(1f))
            Total("Favoritos", ui.totalFavoritos, Modifier.weight(1f))
        }
        Button(onClick = onSincronizar, enabled = !ui.sincronizando, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(if (ui.sincronizando) "Sincronizando…" else "Sincronizar", modifier = Modifier.padding(start = 8.dp))
        }
        OutlinedButton(onClick = onLimpiarCache, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.DeleteSweep, contentDescription = null, modifier = Modifier.size(18.dp))
            Text("Limpiar caché", modifier = Modifier.padding(start = 8.dp))
        }
        ui.mensaje?.let {
            Card(
                Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Text(it, Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun Total(etiqueta: String, valor: Int, modifier: Modifier = Modifier) {
    Card(
        modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
            Text("$valor", style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

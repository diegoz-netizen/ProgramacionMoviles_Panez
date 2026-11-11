package com.tecsup.pokemon.ui.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Pokémon guardados: ${ui.totalGuardado}")
                Text("Favoritos: ${ui.totalFavoritos}")
            }
        }
        Button(onClick = onSincronizar, enabled = !ui.sincronizando, modifier = Modifier.fillMaxWidth()) {
            Text(if (ui.sincronizando) "Sincronizando…" else "Sincronizar")
        }
        OutlinedButton(onClick = onLimpiarCache, modifier = Modifier.fillMaxWidth()) {
            Text("Limpiar caché")
        }
        ui.mensaje?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
    }
}

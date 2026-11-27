package com.tecsup.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.avisoscampus.model.Aviso

/**
 * Tarjeta Historial: los 20 avisos más recientes, lo nuevo arriba.
 * No conoce el ViewModel: solo recibe la lista. Usa Column con forEach porque
 * la pantalla ya se desplaza y una LazyColumn dentro daría error.
 */
@Composable
fun HistorialAvisos(avisos: List<Aviso>, onLimpiar: () -> Unit) {
    Tarjeta("Historial") {
        if (avisos.isEmpty()) {
            Text("Sin avisos todavía", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            avisos.take(20).forEachIndexed { i, aviso ->
                if (i > 0) HorizontalDivider()
                FilaAviso(aviso)
            }
            TextButton(onClick = onLimpiar) { Text("Borrar historial") }
        }
    }
}

@Composable
private fun FilaAviso(aviso: Aviso) {
    Column(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                aviso.origen.texto,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 2.dp)
            )
            Text(
                aviso.hora,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Text(aviso.titulo, fontWeight = FontWeight.Bold)
        Text(aviso.texto, style = MaterialTheme.typography.bodyMedium)
    }
}

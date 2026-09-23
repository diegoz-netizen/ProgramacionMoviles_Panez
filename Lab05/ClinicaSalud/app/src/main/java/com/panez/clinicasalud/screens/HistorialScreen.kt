package com.panez.clinicasalud.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.panez.clinicasalud.data.citasAgendadas
import com.panez.clinicasalud.ui.theme.GrisTexto
import com.panez.clinicasalud.ui.theme.MoradoPrincipal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistorialScreen() {
    val citasCompletadas = citasAgendadas.filter { it.estado == "Completada" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial médico") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (citasCompletadas.isEmpty()) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Sin registros por ahora", color = GrisTexto)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(citasCompletadas) { cita ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                cita.medico.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${cita.fecha} · ${cita.hora}",
                                style = MaterialTheme.typography.bodySmall,
                                color = GrisTexto
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                cita.medico.especialidad,
                                style = MaterialTheme.typography.labelSmall,
                                color = MoradoPrincipal
                            )
                        }
                    }
                }
            }
        }
    }
}
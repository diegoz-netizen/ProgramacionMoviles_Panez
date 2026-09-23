package com.panez.clinicasalud.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.panez.clinicasalud.data.citasAgendadas
import com.panez.clinicasalud.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis citas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (citasAgendadas.isEmpty()) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes citas agendadas", color = GrisTexto)
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(citasAgendadas) { cita ->
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
                            Spacer(Modifier.height(12.dp))

                            val (colorFondo, colorTexto) = when (cita.estado) {
                                "Confirmada" -> Color(0xFFC8E6C9) to Color(0xFF2E7D32)
                                "Completada" -> Color(0xFFE0E0E0) to GrisTexto
                                else -> Color(0xFFE0E0E0) to GrisTexto
                            }

                            Surface(
                                color = colorFondo,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    cita.estado,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colorTexto
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
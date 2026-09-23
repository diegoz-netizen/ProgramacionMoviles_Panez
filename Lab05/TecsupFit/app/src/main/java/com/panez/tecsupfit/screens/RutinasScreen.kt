package com.panez.tecsupfit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.tecsupfit.data.listaRutinas
import com.panez.tecsupfit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RutinasScreen(navController: NavHostController) {
    val completadas = listaRutinas.count { it.completada }
    val total = listaRutinas.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutinas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdePrincipal,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomBar(navController, "rutinas") }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Progreso: $completadas de $total completadas",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto)
            Spacer(Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { if (total > 0) completadas.toFloat() / total.toFloat() else 0f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = VerdePrincipal,
                trackColor = VerdeClaro
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listaRutinas, key = { it.id }) { rutina ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(rutina.nombre,
                                    style = MaterialTheme.typography.titleMedium)
                                Spacer(Modifier.height(4.dp))
                                Text("${rutina.duracion} · ${rutina.ejercicios} ejercicios",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = GrisTexto)
                            }

                            if (rutina.completada) {
                                Surface(color = VerdeChip, shape = RoundedCornerShape(12.dp)) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Check,
                                            contentDescription = null,
                                            tint = VerdePrincipal,
                                            modifier = Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Hecha",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = VerdePrincipal)
                                    }
                                }
                            } else {
                                TextButton(onClick = {
                                    val i = listaRutinas.indexOfFirst { it.id == rutina.id }
                                    if (i >= 0) {
                                        listaRutinas[i] = listaRutinas[i].copy(completada = true)
                                    }
                                }) {
                                    Text("Completar", color = VerdePrincipal)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
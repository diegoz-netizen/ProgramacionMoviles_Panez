package com.panez.tecsupfit.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.tecsupfit.data.listaClases
import com.panez.tecsupfit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(navController: NavHostController) {
    var filtroSeleccionado by remember { mutableStateOf("Hoy") }
    val filtros = listOf("Hoy", "Esta semana")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TECSUP Fit") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdePrincipal,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            BottomBar(navController, "inicio")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Hola, Diego",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto)
            Spacer(Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtros) { filtro ->
                    FilterChip(
                        selected = filtro == filtroSeleccionado,
                        onClick = { filtroSeleccionado = filtro },
                        label = { Text(filtro) }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Clases disponibles", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listaClases, key = { it.id }) { clase ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate("detalle/${clase.id}")
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(VerdeClaro, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.FitnessCenter,
                                        contentDescription = null,
                                        tint = VerdePrincipal)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(clase.nombre,
                                        style = MaterialTheme.typography.titleSmall)
                                    Text("${clase.horario} · ${clase.sala}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GrisTexto)
                                }

                                if (clase.cuposDisponibles == 0) {
                                    Surface(
                                        color = Color(0xFFFFCDD2),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Sin cupos",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFC62828))
                                    }
                                } else if (clase.cuposDisponibles <= 3) {
                                    Surface(
                                        color = Color(0xFFFFE0B2),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text("Últimos cupos",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = Color(0xFFEF6C00))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.panez.tecsupfit.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.tecsupfit.data.reservasAgendadas
import com.panez.tecsupfit.model.Reserva
import com.panez.tecsupfit.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservasScreen(navController: NavHostController) {
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    var reservaACancelar by remember { mutableStateOf<Reserva?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis reservas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = VerdePrincipal,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomBar(navController, "reservas") },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (reservasAgendadas.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No tienes reservas", color = GrisTexto)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(reservasAgendadas) { reserva ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = reserva.estado == "Confirmada") {
                                reservaACancelar = reserva
                            },
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                reserva.clase.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                reserva.horario,
                                style = MaterialTheme.typography.bodySmall,
                                color = GrisTexto
                            )
                            Spacer(Modifier.height(12.dp))

                            val (colorFondo, colorTexto) = when (reserva.estado) {
                                "Confirmada" -> VerdeChip to VerdePrincipal
                                "Completada" -> Color(0xFFE0E0E0) to GrisTexto
                                else -> Color(0xFFE0E0E0) to GrisTexto
                            }

                            Surface(color = colorFondo, shape = RoundedCornerShape(12.dp)) {
                                Text(
                                    reserva.estado,
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 4.dp
                                    ),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colorTexto
                                )
                            }
                        }
                    }
                }
            }
        }

        if (reservaACancelar != null) {
            AlertDialog(
                onDismissRequest = { reservaACancelar = null },
                title = { Text("¿Cancelar esta reserva?") },
                text = { Text("Se eliminará la reserva de ${reservaACancelar?.clase?.nombre}.") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val item = reservaACancelar
                            if (item != null) {
                                reservasAgendadas.remove(item)
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Reserva cancelada")
                                }
                            }
                            reservaACancelar = null
                        }
                    ) {
                        Text("Sí, cancelar", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { reservaACancelar = null }) {
                        Text("No")
                    }
                }
            )
        }
    }
}

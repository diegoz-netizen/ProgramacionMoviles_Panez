package com.panez.clinicasalud.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.clinicasalud.data.listaMedicos
import com.panez.clinicasalud.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendarCitaScreen(navController: NavHostController, medicoId: Int) {
    val medico = listaMedicos.find { it.id == medicoId }
    val fechas = listOf("Jue 26", "Vie 27", "Sáb 28")
    val horas = listOf("9:00", "10:30", "3:00")

    var fechaSeleccionada by remember { mutableStateOf("") }
    var horaSeleccionada by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar cita") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->

        if (medico == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Médico no encontrado", color = GrisTexto)
            }
            return@Scaffold
        }

        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {

            Text("Médico: ${medico.nombre}",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto)
            Spacer(Modifier.height(24.dp))

            Text("Selecciona fecha", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fechas.forEach { fecha ->
                    FilterChip(
                        selected = fecha == fechaSeleccionada,
                        onClick = { fechaSeleccionada = fecha },
                        label = { Text(fecha) }
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Text("Selecciona hora", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                horas.forEach { hora ->
                    FilterChip(
                        selected = hora == horaSeleccionada,
                        onClick = { horaSeleccionada = hora },
                        label = { Text(hora) }
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("confirmacion/${medico.id}/$fechaSeleccionada/$horaSeleccionada")
                },
                enabled = fechaSeleccionada.isNotEmpty() && horaSeleccionada.isNotEmpty(),
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal)
            ) {
                Text("Confirmar cita", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
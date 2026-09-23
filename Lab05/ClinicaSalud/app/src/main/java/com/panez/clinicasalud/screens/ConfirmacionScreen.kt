package com.panez.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.clinicasalud.data.citasAgendadas
import com.panez.clinicasalud.data.listaMedicos
import com.panez.clinicasalud.model.Cita
import com.panez.clinicasalud.ui.theme.*

@Composable
fun ConfirmacionScreen(
    navController: NavHostController,
    medicoId: Int,
    fecha: String,
    hora: String
) {
    val medico = listaMedicos.find { it.id == medicoId }

    LaunchedEffect(medicoId) {
        if (medico != null) {
            citasAgendadas.add(Cita(medico, fecha, hora, "Confirmada"))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(96.dp).background(Color(0xFFC8E6C9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = VerdeEstado,
                modifier = Modifier.size(56.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("¡Cita agendada!", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(12.dp))
        Text(
            medico?.nombre ?: "Médico",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "$fecha, $hora",
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                navController.navigate("mis_citas") {
                    popUpTo("inicio") { inclusive = false }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal)
        ) {
            Text("Ver mis citas", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                navController.navigate("inicio") {
                    popUpTo("inicio") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Text("Volver al inicio", style = MaterialTheme.typography.titleMedium)
        }
    }
}
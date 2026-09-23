    package com.panez.tecsupfit.screens

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
import com.panez.tecsupfit.data.listaClases
import com.panez.tecsupfit.data.reservasAgendadas
import com.panez.tecsupfit.model.Reserva
import com.panez.tecsupfit.ui.theme.*

@Composable
fun ConfirmacionScreen(
    navController: NavHostController,
    claseId: Int,
    horario: String
) {
    val clase = listaClases.find { it.id == claseId }

    LaunchedEffect(Unit) {
        if (clase != null) {
            reservasAgendadas.add(Reserva(clase, horario, "Confirmada"))

            val index = listaClases.indexOfFirst { it.id == clase.id }
            if (index >= 0 && listaClases[index].cuposDisponibles > 0) {
                listaClases[index] = listaClases[index].copy(
                    cuposDisponibles = listaClases[index].cuposDisponibles - 1
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(96.dp).background(VerdeClaro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Check,
                contentDescription = null,
                tint = VerdePrincipal,
                modifier = Modifier.size(56.dp))
        }

        Spacer(Modifier.height(24.dp))
        Text("¡Cupo reservado!", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Text(clase?.nombre ?: "Clase", style = MaterialTheme.typography.bodyLarge)
        Text("$horario · ${clase?.sala ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            color = GrisTexto)

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = {
                navController.navigate("reservas") {
                    popUpTo("inicio") { inclusive = false }
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
        ) {
            Text("Ver mis reservas", style = MaterialTheme.typography.titleMedium)
        }
    }
}
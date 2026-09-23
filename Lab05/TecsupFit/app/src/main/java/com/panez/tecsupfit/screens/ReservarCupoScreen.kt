package com.panez.tecsupfit.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
fun ReservarCupoScreen(navController: NavHostController, claseId: Int) {
    val clase = listaClases.find { it.id == claseId }
    val horarios = listOf("Hoy, 6:00 pm", "Mañana, 6:00 pm", "Viernes, 6:00 pm")

    var horarioSeleccionado by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservar cupo") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = VerdePrincipal,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White
            )
            )
        }) { padding ->
        if (clase == null || clase.cuposDisponibles == 0) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay cupos disponibles", color = GrisTexto)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Text(
                "Clase: ${clase.nombre}",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto
            )
            Spacer(Modifier.height(24.dp))

            Text("Selecciona horario", style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                horarios.forEach { h ->
                    FilterChip(
                        selected = h == horarioSeleccionado,
                        onClick = { horarioSeleccionado = h },
                        label = { Text(h) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    navController.navigate("confirmacion/${clase.id}/$horarioSeleccionado")
                },
                enabled = horarioSeleccionado.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Text("Confirmar reserva", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
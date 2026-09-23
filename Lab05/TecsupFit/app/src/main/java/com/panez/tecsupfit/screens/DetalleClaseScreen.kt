package com.panez.tecsupfit.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.tecsupfit.data.listaClases
import com.panez.tecsupfit.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleClaseScreen(navController: NavHostController, claseId: Int) {
    val clase = listaClases.find { it.id == claseId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de clase") }, navigationIcon = {
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
        if (clase == null) {
            Box(
                Modifier
                    .padding(padding)
                    .fillMaxSize(), contentAlignment = Alignment.Center
            ) {
                Text("Clase no encontrada", color = GrisTexto)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(VerdeClaro, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = VerdePrincipal,
                    modifier = Modifier.size(56.dp)
                )
            }

            Spacer(Modifier.height(24.dp))
            Text(clase.nombre, style = MaterialTheme.typography.headlineSmall)
            Text(
                "${clase.horario} · ${clase.sala} · ${clase.duracion}",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto
            )

            Spacer(Modifier.height(16.dp))
            Text(clase.descripcion, style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))
            Text(
                "${clase.cuposDisponibles} de ${clase.cuposTotales} cupos disponibles",
                style = MaterialTheme.typography.bodySmall,
                color = GrisTexto
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("reservar/${clase.id}") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
            ) {
                Text("Reservar cupo", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
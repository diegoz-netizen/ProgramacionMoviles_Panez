package com.panez.clinicasalud.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.panez.clinicasalud.data.listaMedicos
import com.panez.clinicasalud.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilMedicoScreen(navController: NavHostController, medicoId: Int) {
    // Busca el médico por id. Si no existe, muestra mensaje simple.
    val medico = listaMedicos.find { it.id == medicoId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil del médico") },
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
                contentAlignment = Alignment.Center
            ) {
                Text("Médico no encontrado", color = GrisTexto)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(MoradoClaro, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = MoradoPrincipal,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(medico.nombre, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(4.dp))
            Text(
                "${medico.especialidad} · 12 años exp.",
                style = MaterialTheme.typography.bodyMedium,
                color = GrisTexto
            )

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    "${medico.calificacion} (${medico.resenas} reseñas)",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = medico.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = GrisTexto
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = { navController.navigate("agendar/${medico.id}") },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal)
            ) {
                Text("Agendar cita", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
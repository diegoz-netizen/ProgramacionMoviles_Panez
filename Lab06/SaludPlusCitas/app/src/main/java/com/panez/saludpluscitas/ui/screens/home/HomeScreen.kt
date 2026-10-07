package com.panez.saludpluscitas.ui.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun HomeScreen(
    onAgendar: () -> Unit,
    onMisCitas: () -> Unit,
    onPerfil: () -> Unit,
    onResultados: () -> Unit,
    onNotificaciones: () -> Unit,
    onVerTodas: () -> Unit,
    onEspecialidad: (Int) -> Unit,
    onTab: (String) -> Unit
) {
    val nombre = Repositorio.usuarioActual?.nombre?.split(" ")?.firstOrNull() ?: "Paciente"
    Scaffold(containerColor = Fondo, bottomBar = { BarraInferior(0, onTab) }) { pad ->
        Column(Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("¡Hola, $nombre!", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("¿Qué deseas hacer hoy?", color = TextoSecundario)
                }
                IconButton(onClick = onNotificaciones) {
                    Icon(Icons.Filled.Notifications, contentDescription = "Notificaciones", tint = AzulPrimario)
                }
            }
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccion("Agendar cita", Icons.Filled.CalendarMonth, AzulClaro, AzulPrimario, onAgendar, Modifier.weight(1f))
                TarjetaAccion("Mis citas", Icons.Filled.EventAvailable, VerdeClaro, VerdeOk, onMisCitas, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccion("Mis datos", Icons.Filled.Person, NaranjaClaro, NaranjaAcento, onPerfil, Modifier.weight(1f))
                TarjetaAccion("Resultados", Icons.Filled.Description, NaranjaClaro, NaranjaAcento, onResultados, Modifier.weight(1f))
            }
            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Especialidades destacadas", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                TextButton(onClick = onVerTodas) { Text("Ver todas") }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Repositorio.especialidadesDestacadas()) { e ->
                    Card(
                        modifier = Modifier.width(110.dp).clickable { onEspecialidad(e.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(48.dp).background(AzulClaro, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(iconoEspecialidad(e.id), null, tint = AzulPrimario)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(e.nombre, fontSize = 12.sp, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}
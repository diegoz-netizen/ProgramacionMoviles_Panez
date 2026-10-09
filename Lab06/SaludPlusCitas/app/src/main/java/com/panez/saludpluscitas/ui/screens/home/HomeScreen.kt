package com.panez.saludpluscitas.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun HomeScreen(
    onMenu: () -> Unit,
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
    val sede = Repositorio.sedeSeleccionada?.nombre ?: "Sin sede"

    Scaffold(
        containerColor = Fondo,
        topBar = { BarraSuperior("SaludPlus", onMenu = onMenu) },
        bottomBar = { BarraInferior(0, onTab) }
    ) { pad ->
        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Text("¡Hola, $nombre!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = VerdePrincipal)
            Text("¿Qué deseas hacer hoy?", color = TextoSecundario, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))
            Surface(color = VerdeSuave, shape = RoundedCornerShape(10.dp)) {
                Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, null, tint = VerdePrincipal, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(sede, color = VerdePrincipal, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(Modifier.height(20.dp))

            TarjetaAccion("Agendar cita", Icons.Filled.CalendarMonth, VerdeSuave, VerdePrincipal, onAgendar, Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccion("Mi agenda", Icons.Filled.EventAvailable, VerdeClaro, VerdeOk, onMisCitas, Modifier.weight(1f))
                TarjetaAccion("Resultados", Icons.Filled.Description, NaranjaClaro, NaranjaAcento, onResultados, Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TarjetaAccion("Mis datos", Icons.Filled.Person, VerdeSuave, VerdePrincipal, onPerfil, Modifier.weight(1f))
                TarjetaAccion("Avisos", Icons.Filled.Notifications, NaranjaClaro, NaranjaAcento, onNotificaciones, Modifier.weight(1f))
            }

            Spacer(Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Especialidades destacadas", fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.weight(1f))
                TextButton(onClick = onVerTodas) { Text("Ver todas", fontSize = 16.sp) }
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(Repositorio.especialidadesDeSede().take(4)) { e ->
                    Card(
                        modifier = Modifier.width(130.dp).clickable { onEspecialidad(e.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(14.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(Modifier.size(54.dp).background(VerdeSuave, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(iconoEspecialidad(e.id), null, tint = VerdePrincipal, modifier = Modifier.size(30.dp))
                            }
                            Spacer(Modifier.height(10.dp))
                            Text(e.nombre, fontSize = 14.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }
    }
}
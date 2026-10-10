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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*
import com.panez.saludpluscitas.util.fechaLegible

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
    onMedico: (Int) -> Unit,
    onCita: (Int) -> Unit,
    onTab: (String) -> Unit
) {
    val nombre = Repositorio.usuarioActual?.nombre?.split(" ")?.firstOrNull() ?: "Paciente"
    val sede = Repositorio.sedeSeleccionada?.nombre ?: "Sin sede"
    val proxima = Repositorio.proximaCita()

    Scaffold(
        containerColor = Fondo,
        topBar = { BarraSuperior("SaludPlus", onMenu = onMenu) },
        bottomBar = { BarraInferior(0, onTab) }
    ) { pad ->
        Column(Modifier.padding(pad).verticalScroll(rememberScrollState())) {

            // ---- Banner de saludo con la sede actual ----
            Column(
                Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        Brush.linearGradient(listOf(VerdePrincipal, VerdeMedio)),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Text("¡Hola, $nombre!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("¿Qué deseas hacer hoy?", color = VerdeSuave, fontSize = 16.sp)
                Spacer(Modifier.height(14.dp))
                Surface(color = Color.White.copy(alpha = 0.18f), shape = RoundedCornerShape(50)) {
                    Row(Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.LocationOn, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(sede, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = onAgendar,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = VerdePrincipal),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.CalendarMonth, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Agendar cita", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                }
            }

            Column(Modifier.padding(horizontal = 16.dp)) {

                // ---- Próxima cita (solo si existe) ----
                if (proxima != null) {
                    val m = Repositorio.obtenerMedico(proxima.medicoId)
                    Spacer(Modifier.height(12.dp))
                    Titulo("Tu próxima cita")
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onCita(proxima.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            if (m != null) FotoMedico(m, 60)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(m?.nombre ?: "Médico", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                Text(m?.descripcion ?: "", color = TextoSecundario, fontSize = 14.sp)
                                Spacer(Modifier.height(6.dp))
                                EtiquetaEstado(
                                    "${proxima.fecha.fechaLegible()} · ${proxima.hora}",
                                    VerdeClaro, VerdeMedio, Icons.Filled.Schedule
                                )
                            }
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextoSecundario)
                        }
                    }
                }

                // ---- Accesos rápidos en una sola fila ----
                Spacer(Modifier.height(20.dp))
                Titulo("Accesos rápidos")
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    AccesoRapido("Mi agenda", Icons.Filled.EventAvailable, VerdeClaro, VerdeOk, onMisCitas)
                    AccesoRapido("Resultados", Icons.Filled.Description, NaranjaClaro, NaranjaAcento, onResultados)
                    AccesoRapido("Mis datos", Icons.Filled.Person, VerdeSuave, VerdePrincipal, onPerfil)
                    AccesoRapido("Avisos", Icons.Filled.Notifications, NaranjaClaro, NaranjaAcento, onNotificaciones)
                }

                // ---- Médicos de la sede con foto ----
                Spacer(Modifier.height(24.dp))
                Titulo("Nuestros médicos")
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(Repositorio.medicosDeSede().sortedByDescending { it.rating }) { m ->
                    val yaTiene = Repositorio.yaTieneCitaCon(m.id)
                    Card(
                        modifier = Modifier.width(150.dp).clickable(enabled = !yaTiene) { onMedico(m.id) },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            FotoMedico(m, 80, borde = VerdeSuave)
                            Spacer(Modifier.height(10.dp))
                            Text(m.nombre, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(m.descripcion, fontSize = 12.sp, color = TextoSecundario,
                                textAlign = TextAlign.Center, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Spacer(Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, null, tint = NaranjaAcento, modifier = Modifier.size(16.dp))
                                Text(" ${m.rating}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            // Siempre una línea de estado para que todas las tarjetas midan lo mismo
                            Spacer(Modifier.height(4.dp))
                            Text(
                                if (yaTiene) "Ya agendado" else "Disponible",
                                fontSize = 11.sp, fontWeight = FontWeight.Medium,
                                color = if (yaTiene) Color(0xFFB00020) else VerdeOk
                            )
                        }
                    }
                }
            }

            // ---- Especialidades al final ----
            Column(Modifier.padding(16.dp)) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Titulo("Especialidades", Modifier.weight(1f))
                    TextButton(onClick = onVerTodas) { Text("Ver todas", fontSize = 15.sp) }
                }
                Repositorio.especialidadesDeSede().take(4).forEach { e ->
                    ItemEspecialidad(e.id, e.nombre, e.descripcion) { onEspecialidad(e.id) }
                    Spacer(Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun Titulo(texto: String, modifier: Modifier = Modifier) {
    Text(
        texto, fontWeight = FontWeight.Bold, fontSize = 19.sp, color = VerdePrincipal,
        modifier = modifier.padding(bottom = 10.dp)
    )
}

@Composable
private fun AccesoRapido(titulo: String, icono: ImageVector, fondo: Color, tinte: Color, onClick: () -> Unit) {
    Column(
        Modifier.width(78.dp).clickable(onClick = onClick).padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(Modifier.size(60.dp).background(fondo, CircleShape), contentAlignment = Alignment.Center) {
            Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(28.dp))
        }
        Spacer(Modifier.height(6.dp))
        Text(titulo, fontSize = 13.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

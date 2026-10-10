package com.panez.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.panez.saludpluscitas.util.fechaLegible

@Composable
fun ConfirmarCitaScreen(
    medicoId: Int,
    fecha: String,
    hora: String,
    onBack: () -> Unit,
    onConfirmada: (Int) -> Unit
) {
    val medico = Repositorio.obtenerMedico(medicoId)
    var motivo by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val confirmar = {
        val cita = Repositorio.agendarCita(medicoId, fecha, hora, motivo.trim())
        if (cita != null) onConfirmada(cita.id)
        else error = "Ya tienes una cita con este médico o ese horario ya está reservado."
    }

    Scaffold(
        topBar = { BarraSuperior("Confirmar cita", onBack) },
        bottomBar = {
            if (medico != null) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Column(Modifier.navigationBarsPadding().padding(16.dp)) {
                        BotonPrincipal("Confirmar cita", onClick = confirmar)
                    }
                }
            }
        },
        containerColor = Fondo
    ) { pad ->
        Column(Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp)) {
            if (medico == null) {
                EstadoVacio(Icons.Filled.ErrorOutline, "Médico no encontrado")
                return@Column
            }

            // ---- Perfil del médico centrado con foto grande ----
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    FotoMedico(medico, 112, borde = VerdeSuave)
                    Spacer(Modifier.height(12.dp))
                    Text(medico.nombre, fontWeight = FontWeight.Bold, fontSize = 21.sp, textAlign = TextAlign.Center)
                    Text(medico.descripcion, color = TextoSecundario, fontSize = 15.sp)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        EtiquetaEstado(medico.cmp, VerdeSuave, VerdePrincipal, Icons.Filled.Badge)
                        EtiquetaEstado("${medico.rating} (${medico.resenas})", NaranjaClaro, NaranjaAcento, Icons.Filled.Star)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Resumen de la cita", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = VerdePrincipal)
            Spacer(Modifier.height(8.dp))
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                    FilaDato(Icons.Filled.CalendarMonth, "Fecha", fecha.fechaLegible())
                    FilaDato(Icons.Filled.Schedule, "Hora", hora)
                    FilaDato(Icons.Filled.LocationOn, "Sede", Repositorio.sedeSeleccionada?.nombre ?: "-")
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = motivo, onValueChange = { motivo = it },
                label = { Text("Motivo (opcional)", fontSize = 16.sp) },
                leadingIcon = { Icon(Icons.Filled.EditNote, null) },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )
            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 15.sp)
            }
        }
    }
}

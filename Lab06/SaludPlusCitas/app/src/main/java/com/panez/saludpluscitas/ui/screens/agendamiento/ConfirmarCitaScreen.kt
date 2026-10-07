package com.panez.saludpluscitas.ui.screens.agendamiento

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

    Scaffold(topBar = { BarraSuperior("Confirmar cita", onBack) }, containerColor = Fondo) { pad ->
        Column(Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp)) {
            if (medico == null) {
                EstadoVacio(Icons.Filled.ErrorOutline, "Médico no encontrado")
                return@Column
            }
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Avatar(medico.nombre, 56)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(medico.nombre, fontWeight = FontWeight.Bold)
                            Text(medico.descripcion, color = TextoSecundario, fontSize = 13.sp)
                            Text(medico.cmp, color = TextoSecundario, fontSize = 12.sp)
                        }
                    }
                    HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    FilaDato(Icons.Filled.CalendarMonth, "Fecha", fecha.fechaLegible())
                    FilaDato(Icons.Filled.Schedule, "Hora", hora)
                    FilaDato(Icons.Filled.MedicalServices, "Tipo de atención", "Consulta presencial")
                    FilaDato(Icons.Filled.LocationOn, "Dirección", medico.direccion)
                }
            }
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = motivo, onValueChange = { motivo = it },
                label = { Text("Motivo de consulta (opcional)") },
                modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)
            )
            if (error != null) Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            Spacer(Modifier.height(16.dp))
            BotonPrincipal("Agendar cita", onClick = {
                val cita = Repositorio.agendarCita(medicoId, fecha, hora, motivo.trim())
                if (cita != null) onConfirmada(cita.id) else error = "Ese horario ya fue reservado. Elige otro."
            })
        }
    }
}
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

@Composable
fun CitaExitosaScreen(citaId: Int, onVerMisCitas: () -> Unit, onInicio: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    Column(
        Modifier.fillMaxSize().background(Color.White).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(96.dp).background(VerdeClaro, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.CheckCircle, null, tint = VerdeOk, modifier = Modifier.size(64.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("¡Cita agendada!", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Te esperamos en la clínica", color = TextoSecundario)
        Spacer(Modifier.height(24.dp))
        if (cita != null && medico != null) {
            Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Fondo)) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    FilaDato(Icons.Filled.Person, "Médico", medico.nombre)
                    FilaDato(Icons.Filled.MedicalServices, "Especialidad", medico.descripcion)
                    FilaDato(Icons.Filled.CalendarMonth, "Fecha", cita.fecha)
                    FilaDato(Icons.Filled.Schedule, "Hora", cita.hora)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        BotonPrincipal("Ver mis citas", onVerMisCitas)
        TextButton(onClick = onInicio) { Text("Volver al inicio") }
    }
}
package com.panez.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.BotonPrincipal
import com.panez.saludpluscitas.ui.theme.*
import com.panez.saludpluscitas.util.fechaLegible

@Composable
fun CitaExitosaScreen(citaId: Int, onVerMisCitas: () -> Unit, onInicio: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }

    Column(
        Modifier.fillMaxSize().background(VerdePrincipal).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(120.dp).background(VerdeMedio, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.EventAvailable, null, tint = Color.White, modifier = Modifier.size(72.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text("¡Cita registrada!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(Modifier.height(8.dp))
        Text("Presentate 10 minutos antes", color = VerdeSuave, fontSize = 17.sp)
        Spacer(Modifier.height(28.dp))

        if (cita != null && medico != null) {
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = VerdeMedio)) {
                Column(Modifier.padding(20.dp).fillMaxWidth()) {
                    Text("Médico", color = VerdeSuave, fontSize = 14.sp)
                    Text(medico.nombre, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(12.dp))
                    Text("Especialidad", color = VerdeSuave, fontSize = 14.sp)
                    Text(medico.descripcion, color = Color.White, fontSize = 20.sp)
                    Spacer(Modifier.height(12.dp))
                    Text("Fecha y hora", color = VerdeSuave, fontSize = 14.sp)
                    Text("${cita.fecha.fechaLegible()} · ${cita.hora}",
                        color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(28.dp))
        BotonPrincipal("Ver mi agenda", onVerMisCitas)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onInicio) {
            Text("Volver al inicio", color = VerdeSuave, fontSize = 17.sp)
        }
    }
}
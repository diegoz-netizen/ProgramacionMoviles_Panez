package com.panez.saludpluscitas.ui.screens.citas

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
fun DetalleCitaScreen(citaId: Int, onBack: () -> Unit) {
    val cita = Repositorio.obtenerCita(citaId)
    val medico = cita?.let { Repositorio.obtenerMedico(it.medicoId) }
    var confirmando by remember { mutableStateOf(false) }

    Scaffold(topBar = { BarraSuperior("Detalle de cita", onBack) }, containerColor = Fondo) { pad ->
        if (cita == null || medico == null) {
            EstadoVacio(Icons.Filled.ErrorOutline, "La cita ya no existe", Modifier.padding(pad))
        } else {
            Column(Modifier.padding(pad).padding(16.dp)) {
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(16.dp).fillMaxWidth()) {
                        FilaDato(Icons.Filled.Person, "Médico", medico.nombre)
                        FilaDato(Icons.Filled.MedicalServices, "Especialidad", medico.descripcion)
                        FilaDato(Icons.Filled.CalendarMonth, "Fecha", cita.fecha)
                        FilaDato(Icons.Filled.Schedule, "Hora", cita.hora)
                        FilaDato(Icons.Filled.LocationOn, "Dirección", medico.direccion)
                        if (cita.motivo.isNotBlank()) FilaDato(Icons.Filled.Notes, "Motivo", cita.motivo)
                    }
                }
                Spacer(Modifier.height(20.dp))
                OutlinedButton(
                    onClick = { confirmando = true },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancelar cita", color = MaterialTheme.colorScheme.error) }
            }
        }
    }

    if (confirmando) {
        AlertDialog(
            onDismissRequest = { confirmando = false },
            title = { Text("¿Cancelar cita?") },
            text = { Text("Esta acción liberará el horario reservado.") },
            confirmButton = {
                TextButton(onClick = {
                    Repositorio.cancelarCita(citaId)
                    confirmando = false
                    onBack()
                }) { Text("Sí, cancelar") }
            },
            dismissButton = { TextButton(onClick = { confirmando = false }) { Text("No") } }
        )
    }
}
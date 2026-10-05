package com.panez.saludpluscitas.ui.screens.notificaciones

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
fun NotificacionesScreen(onBack: () -> Unit) {
    val avisos = Repositorio.citasDelUsuario().map { c ->
        val m = Repositorio.obtenerMedico(c.medicoId)?.nombre ?: "tu médico"
        "Recordatorio: tienes cita con $m el ${c.fecha} a las ${c.hora}."
    }
    Scaffold(topBar = { BarraSuperior("Notificaciones", onBack) }, containerColor = Fondo) { pad ->
        if (avisos.isEmpty()) {
            EstadoVacio(Icons.Filled.NotificationsNone, "No tienes notificaciones", Modifier.padding(pad).padding(top = 48.dp))
        } else {
            LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(avisos) { a ->
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Notifications, null, tint = AzulPrimario)
                            Spacer(Modifier.width(12.dp))
                            Text(a)
                        }
                    }
                }
            }
        }
    }
}
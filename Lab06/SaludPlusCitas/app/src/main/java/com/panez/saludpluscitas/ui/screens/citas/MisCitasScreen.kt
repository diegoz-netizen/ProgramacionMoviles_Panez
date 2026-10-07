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
fun MisCitasScreen(onDetalle: (Int) -> Unit, onTab: (String) -> Unit) {
    val citas = Repositorio.citasDelUsuario()
    Scaffold(
        topBar = { BarraSuperior("Mis citas") },
        bottomBar = { BarraInferior(1, onTab) },
        containerColor = Fondo
    ) { pad ->
        if (citas.isEmpty()) {
            EstadoVacio(Icons.Filled.EventBusy, "Aún no tienes citas agendadas", Modifier.padding(pad).padding(top = 48.dp))
        } else {
            LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(citas) { c ->
                    val m = Repositorio.obtenerMedico(c.medicoId)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onDetalle(c.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Avatar(m?.nombre ?: "?")
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(m?.nombre ?: "Médico", fontWeight = FontWeight.SemiBold)
                                Text(m?.descripcion ?: "", color = TextoSecundario, fontSize = 13.sp)
                                Text("${c.fecha} · ${c.hora}", color = AzulPrimario, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
package com.panez.saludpluscitas.ui.screens.citas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun MisCitasScreen(onMenu: () -> Unit, onDetalle: (Int) -> Unit, onTab: (String) -> Unit) {
    val citas = Repositorio.citasDelUsuario()
    Scaffold(
        topBar = { BarraSuperior("Mi agenda", onMenu = onMenu) },
        bottomBar = { BarraInferior(1, onTab) },
        containerColor = Fondo
    ) { pad ->
        if (citas.isEmpty()) {
            EstadoVacio(Icons.Filled.EventBusy, "Aún no tienes citas agendadas", Modifier.padding(pad).padding(top = 48.dp))
        } else {
            LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(citas) { c ->
                    val m = Repositorio.obtenerMedico(c.medicoId)
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onDetalle(c.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Avatar(m?.nombre ?: "?")
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(m?.nombre ?: "Médico", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                                Text(m?.descripcion ?: "", color = TextoSecundario, fontSize = 15.sp)
                                Text("${c.fecha} · ${c.hora}", color = VerdePrincipal, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }
        }
    }
}
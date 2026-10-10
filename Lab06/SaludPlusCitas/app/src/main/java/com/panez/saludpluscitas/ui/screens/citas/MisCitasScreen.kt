package com.panez.saludpluscitas.ui.screens.citas

import androidx.compose.foundation.background
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
import com.panez.saludpluscitas.util.nombreDia
import com.panez.saludpluscitas.util.nombreMes
import java.time.LocalDate

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
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            // Bloque de fecha a la izquierda, foto del médico a la derecha
                            val fecha = runCatching { LocalDate.parse(c.fecha) }.getOrNull()
                            Column(
                                Modifier.width(64.dp).background(VerdePrincipal, RoundedCornerShape(16.dp)).padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(fecha?.dayOfMonth?.toString() ?: "--", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text(fecha?.nombreMes()?.take(3)?.uppercase() ?: "", color = VerdeSuave, fontSize = 12.sp)
                                Text(c.hora, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(m?.nombre ?: "Médico", fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                                Text(m?.descripcion ?: "", color = TextoSecundario, fontSize = 14.sp)
                                Spacer(Modifier.height(4.dp))
                                Text(fecha?.nombreDia() ?: c.fecha, color = VerdeMedio, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            if (m != null) FotoMedico(m, 52)
                        }
                    }
                }
            }
        }
    }
}
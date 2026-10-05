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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items

private data class DiaFijo(val etiqueta: String, val numero: Int, val iso: String)

private val diasFijos = listOf(
    DiaFijo("Lun", 14, "2026-09-14"),
    DiaFijo("Mar", 15, "2026-09-15"),
    DiaFijo("Mié", 16, "2026-09-16"),
    DiaFijo("Jue", 17, "2026-09-17"),
    DiaFijo("Vie", 18, "2026-09-18")
)

@Composable
fun FechaHoraScreen(medicoId: Int, onBack: () -> Unit, onContinuar: (String, String) -> Unit) {
    var dia by remember { mutableStateOf(diasFijos[0]) }
    var hora by remember { mutableStateOf<String?>(null) }
    val medico = Repositorio.obtenerMedico(medicoId)
    val horarios = Repositorio.horariosDisponibles(medicoId, dia.iso)

    Scaffold(topBar = { BarraSuperior("Seleccionar fecha y hora", onBack) }, containerColor = Fondo) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            if (medico != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(medico.nombre)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(medico.nombre, fontWeight = FontWeight.SemiBold)
                        Text(medico.descripcion, color = TextoSecundario, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
            Text("Setiembre 2026", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                diasFijos.forEach { d ->
                    val sel = d == dia
                    Column(
                        Modifier.weight(1f)
                            .background(if (sel) AzulPrimario else Color.White, RoundedCornerShape(12.dp))
                            .clickable { dia = d; hora = null }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(d.etiqueta, fontSize = 12.sp, color = if (sel) Color.White else TextoSecundario)
                        Text("${d.numero}", fontWeight = FontWeight.Bold, color = if (sel) Color.White else Color.Black)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
            Text("Horarios disponibles", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(horarios) { h -> ChipHorario(h, h == hora) { hora = h } }
            }
            if (horarios.isEmpty()) EstadoVacio(Icons.Filled.EventBusy, "No hay horarios disponibles este día")
            Spacer(Modifier.height(8.dp))
            BotonPrincipal("Continuar", onClick = { onContinuar(dia.iso, hora!!) }, enabled = hora != null)
        }
    }
}
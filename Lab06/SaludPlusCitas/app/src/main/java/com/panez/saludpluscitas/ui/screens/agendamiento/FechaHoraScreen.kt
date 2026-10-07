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
import com.panez.saludpluscitas.util.*
import java.time.LocalDate

@Composable
fun FechaHoraScreen(medicoId: Int, onBack: () -> Unit, onContinuar: (String, String) -> Unit) {
    val hoy = remember { LocalDate.now() }
    var semana by remember { mutableIntStateOf(0) }
    val dias = semanaHabil(hoy, semana)
    var seleccionado by remember { mutableStateOf(dias.first()) }
    var hora by remember { mutableStateOf<String?>(null) }

    val medico = Repositorio.obtenerMedico(medicoId)
    val horarios = Repositorio.horariosDisponibles(medicoId, seleccionado.toString())

    fun cambiarSemana(nueva: Int) {
        if (nueva < 0) return                 // no retroceder antes de la semana actual
        semana = nueva
        seleccionado = semanaHabil(hoy, nueva).first()
        hora = null                           // se reinicia la hora al cambiar de semana
    }

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
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { cambiarSemana(semana - 1) }, enabled = semana > 0) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Semana anterior")
                }
                Text(
                    dias.first().mesAnio(),
                    fontWeight = FontWeight.Bold, fontSize = 18.sp,
                    modifier = Modifier.weight(1f), textAlign = TextAlign.Center
                )
                IconButton(onClick = { cambiarSemana(semana + 1) }) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Semana siguiente")
                }
            }
            Spacer(Modifier.height(8.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                dias.forEach { d ->
                    val sel = d == seleccionado
                    Column(
                        Modifier.weight(1f)
                            .background(if (sel) AzulPrimario else Color.White, RoundedCornerShape(12.dp))
                            .clickable { seleccionado = d; hora = null }   // reinicia la hora al cambiar de día
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(d.etiquetaCorta(), fontSize = 12.sp, color = if (sel) Color.White else TextoSecundario)
                        Text("${d.dayOfMonth}", fontWeight = FontWeight.Bold, color = if (sel) Color.White else Color.Black)
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
            BotonPrincipal("Continuar", onClick = { onContinuar(seleccionado.toString(), hora!!) }, enabled = hora != null)
        }
    }
}
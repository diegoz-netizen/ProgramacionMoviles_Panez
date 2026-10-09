package com.panez.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*
import com.panez.saludpluscitas.util.etiquetaCorta
import com.panez.saludpluscitas.util.mesAnio
import java.time.LocalDate

@Composable
fun FechaHoraScreen(medicoId: Int, onBack: () -> Unit, onContinuar: (String, String) -> Unit) {
    val hoy = remember { LocalDate.now() }
    var offset by remember { mutableIntStateOf(0) }        // cuántos "saltos" hacia adelante
    var seleccionado by remember { mutableStateOf<LocalDate?>(null) }
    var hora by remember { mutableStateOf<String?>(null) }

    val medico = Repositorio.obtenerMedico(medicoId)

    // Lista de días de atención del médico (todos los futuros, en orden)
    val todosLosDias = remember(medicoId) { Repositorio.diasDeAtencion(medicoId) }

    // Bloque de 5 días visibles a partir del offset
    val diasVisibles = remember(offset, todosLosDias) {
        todosLosDias.drop(offset * 5).take(5)
    }

    // Si el seleccionado ya no está visible, selecciona el primero
    LaunchedEffect(diasVisibles) {
        if (seleccionado == null || seleccionado !in diasVisibles) {
            seleccionado = diasVisibles.firstOrNull()
            hora = null
        }
    }

    val horarios = seleccionado?.let {
        Repositorio.horariosDisponibles(medicoId, it.toString())
    } ?: emptyList()

    val hayAnterior = offset > 0
    val haySiguiente = todosLosDias.size > (offset + 1) * 5

    Scaffold(topBar = { BarraSuperior("Fecha y hora", onBack) }, containerColor = Fondo) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {

            if (medico != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Avatar(medico.nombre)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(medico.nombre, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                        Text(medico.descripcion, color = TextoSecundario, fontSize = 15.sp)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            if (diasVisibles.isEmpty()) {
                EstadoVacio(Icons.Filled.EventBusy, "No hay días disponibles para este médico")
                return@Column
            }

            // ---- Cabecera con mes + flechas ----
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { if (hayAnterior) offset-- }, enabled = hayAnterior) {
                    Icon(Icons.Filled.ChevronLeft, contentDescription = "Anterior",
                        tint = if (hayAnterior) VerdePrincipal else TextoSecundario.copy(alpha = 0.4f))
                }
                Text(
                    diasVisibles.first().mesAnio(),
                    fontWeight = FontWeight.Bold, fontSize = 19.sp,
                    modifier = Modifier.weight(1f), textAlign = TextAlign.Center,
                    color = VerdePrincipal
                )
                IconButton(onClick = { if (haySiguiente) offset++ }, enabled = haySiguiente) {
                    Icon(Icons.Filled.ChevronRight, contentDescription = "Siguiente",
                        tint = if (haySiguiente) VerdePrincipal else TextoSecundario.copy(alpha = 0.4f))
                }
            }

            Spacer(Modifier.height(8.dp))
            Text("Días de atención", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                diasVisibles.forEach { d ->
                    val sel = d == seleccionado
                    Column(
                        Modifier.weight(1f)
                            .background(if (sel) VerdePrincipal else Color.White, RoundedCornerShape(12.dp))
                            .clickable { seleccionado = d; hora = null }
                            .padding(vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(d.etiquetaCorta(), fontSize = 13.sp, color = if (sel) Color.White else TextoSecundario)
                        Text("${d.dayOfMonth}", fontWeight = FontWeight.Bold, fontSize = 17.sp,
                            color = if (sel) Color.White else Color.Black)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Horarios disponibles", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(horarios) { h -> ChipHorario(h, h == hora) { hora = h } }
            }

            if (horarios.isEmpty()) {
                EstadoVacio(Icons.Filled.EventBusy, "No hay horarios disponibles este día")
            }

            Spacer(Modifier.height(8.dp))
            BotonPrincipal(
                "Continuar",
                onClick = { onContinuar(seleccionado!!.toString(), hora!!) },
                enabled = hora != null && seleccionado != null
            )
        }
    }
}
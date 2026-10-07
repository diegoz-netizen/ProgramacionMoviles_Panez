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
fun MedicosScreen(especialidadId: Int, onBack: () -> Unit, onElegir: (Int) -> Unit) {
    var busqueda by remember { mutableStateOf("") }
    val esp = Repositorio.obtenerEspecialidad(especialidadId)
    val lista = Repositorio.buscarMedicos(especialidadId, busqueda)
    Scaffold(topBar = { BarraSuperior("Médicos de ${esp?.nombre ?: ""}", onBack) }, containerColor = Fondo) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            CampoTexto(busqueda, { busqueda = it }, "Buscar médico", Icons.Filled.Search)
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(lista) { m -> TarjetaMedico(m) { onElegir(m.id) } }
                if (lista.isEmpty()) item { EstadoVacio(Icons.Filled.SearchOff, "No hay médicos disponibles") }
            }
        }
    }
}
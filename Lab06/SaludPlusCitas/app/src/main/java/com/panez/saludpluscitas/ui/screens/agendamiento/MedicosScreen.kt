package com.panez.saludpluscitas.ui.screens.agendamiento

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun MedicosScreen(especialidadId: Int, onBack: () -> Unit, onElegir: (Int) -> Unit) {
    var busqueda by remember { mutableStateOf("") }
    val esp = Repositorio.obtenerEspecialidad(especialidadId)
    val lista = Repositorio.buscarMedicos(especialidadId, busqueda)

    Scaffold(topBar = { BarraSuperior(esp?.nombre ?: "Médicos", onBack) }, containerColor = Fondo) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            CampoTexto(busqueda, { busqueda = it }, "Buscar médico", Icons.Filled.Search)
            Spacer(Modifier.height(12.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(lista) { m -> TarjetaMedico(m) { onElegir(m.id) } }
                if (lista.isEmpty()) item { EstadoVacio(Icons.Filled.SearchOff, "No hay médicos disponibles") }
            }
        }
    }
}
package com.panez.saludpluscitas.ui.screens.resultados

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
fun ResultadosScreen(onTab: (String) -> Unit) {
    Scaffold(
        topBar = { BarraSuperior("Resultados") },
        bottomBar = { BarraInferior(2, onTab) },
        containerColor = Fondo
    ) { pad ->
        LazyColumn(Modifier.padding(pad), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(Repositorio.resultados) { r ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(r.titulo, fontWeight = FontWeight.SemiBold)
                        Text(r.fecha, color = AzulPrimario, fontSize = 13.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(r.detalle, color = TextoSecundario, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}
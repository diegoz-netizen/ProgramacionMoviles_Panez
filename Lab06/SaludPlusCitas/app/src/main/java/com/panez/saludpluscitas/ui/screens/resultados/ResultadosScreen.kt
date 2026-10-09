package com.panez.saludpluscitas.ui.screens.resultados

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun ResultadosScreen(onMenu: () -> Unit, onTab: (String) -> Unit) {
    val resultados = Repositorio.resultados

    Scaffold(
        topBar = { BarraSuperior("Resultados", onMenu = onMenu) },
        bottomBar = { BarraInferior(2, onTab) },
        containerColor = Fondo
    ) { pad ->
        if (resultados.isEmpty()) {
            EstadoVacio(
                Icons.Filled.Description,
                "Aún no tienes resultados disponibles",
                Modifier.padding(pad).padding(top = 48.dp)
            )
        } else {
            LazyColumn(
                Modifier.padding(pad),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Tus exámenes recientes",
                        fontWeight = FontWeight.Bold, fontSize = 20.sp,
                        color = VerdePrincipal,
                        modifier = Modifier.padding(bottom = 4.dp))
                }
                items(resultados) { r ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(r.titulo, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                            Text(r.fecha, color = VerdePrincipal, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                            Spacer(Modifier.height(6.dp))
                            Text(r.detalle, color = TextoSecundario, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
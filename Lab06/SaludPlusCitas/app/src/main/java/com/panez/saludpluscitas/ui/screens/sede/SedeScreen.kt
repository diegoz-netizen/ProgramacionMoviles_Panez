package com.panez.saludpluscitas.ui.screens.sede

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun SedeScreen(onElegir: () -> Unit) {
    Scaffold(
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            CenterAlignedTopAppBar(
                title = { Text("Elige una sede", fontSize = 21.sp, fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Fondo
    ) { pad ->
        Column(Modifier.padding(pad).padding(20.dp)) {
            Text("Selecciona la sede donde quieres atenderte",
                color = TextoSecundario, fontSize = 17.sp)
            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(Repositorio.sedes) { s ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable {
                            Repositorio.seleccionarSede(s)
                            onElegir()
                        },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(58.dp).background(VerdeSuave, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.LocationOn, null, tint = VerdePrincipal, modifier = Modifier.size(32.dp))
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(s.nombre, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text(s.direccion, color = TextoSecundario, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
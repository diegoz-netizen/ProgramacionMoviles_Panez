package com.panez.saludpluscitas.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
fun PerfilScreen(onMenu: () -> Unit, onCerrarSesion: () -> Unit, onTab: (String) -> Unit) {
    val u = Repositorio.usuarioActual
    Scaffold(
        topBar = { BarraSuperior("Mis datos", onMenu = onMenu) },
        bottomBar = { BarraInferior(3, onTab) },
        containerColor = Fondo
    ) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Avatar(u?.nombre ?: "?", 100)
            Spacer(Modifier.height(10.dp))
            Text(u?.nombre ?: "Sin sesión", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = VerdePrincipal)
            Spacer(Modifier.height(18.dp))
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp).fillMaxWidth()) {
                    FilaDato(Icons.Filled.Phone, "Teléfono", u?.telefono ?: "-")
                    FilaDato(Icons.Filled.Email, "Correo", u?.correo?.ifBlank { "No registrado" } ?: "-")
                    FilaDato(Icons.Filled.LocationOn, "Sede", Repositorio.sedeSeleccionada?.nombre ?: "-")
                    FilaDato(Icons.Filled.EventAvailable, "Citas agendadas", "${Repositorio.citasDelUsuario().size}")
                }
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(
                onClick = onCerrarSesion,
                modifier = Modifier.fillMaxWidth().height(58.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Filled.Logout, null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar sesión", fontSize = 18.sp)
            }
        }
    }
}
package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.lavacampus.data.local.entity.Usuario

/** Piezas comunes de Perfil (estudiante) y Ajustes (administrador). */

/** Datos del usuario: avatar con iniciales, nombre, código y correo. */
@Composable
fun TarjetaUsuario(usuario: Usuario) {
    TarjetaSeccion {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AvatarIniciales(usuario.nombre, tam = 60.dp)
            Spacer(Modifier.width(14.dp))
            Column {
                Text(usuario.nombre, style = MaterialTheme.typography.titleLarge)
                if (usuario.codigo.isNotBlank()) {
                    Text("Código ${usuario.codigo}", style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(usuario.correo, style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

/** Botón de cerrar sesión (contorno rojo). */
@Composable
fun BotonCerrarSesion(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
        modifier = Modifier.fillMaxWidth().height(52.dp)
    ) {
        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null)
        Text("  Cerrar sesión", style = MaterialTheme.typography.labelLarge)
    }
}

/** Tarjeta blanca que agrupa una sección (ajustes, datos). */
@Composable
fun TarjetaSeccion(contenido: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) { contenido() }
    }
}

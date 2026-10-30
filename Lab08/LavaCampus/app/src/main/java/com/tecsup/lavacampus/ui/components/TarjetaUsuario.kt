package com.tecsup.lavacampus.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.tecsup.lavacampus.data.local.entity.Usuario

/** Piezas comunes de Perfil (estudiante) y Ajustes (administrador). */

/**
 * Datos del usuario: foto (o iniciales), nombre, código y correo.
 * Si se pasa [onCambiarFoto], al tocar la foto se abre el selector de fotos del sistema
 * (no necesita permisos) y se envía la imagen elegida al ViewModel.
 */
@Composable
fun TarjetaUsuario(
    usuario: Usuario,
    onCambiarFoto: ((String) -> Unit)? = null,
    onQuitarFoto: (() -> Unit)? = null,
    aviso: String? = null,
    onEditarDatos: (() -> Unit)? = null
) {
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) onCambiarFoto?.invoke(uri.toString())
    }
    val elegirFoto = {
        selector.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    TarjetaSeccion {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box {
                AvatarIniciales(
                    usuario.nombre, tam = 64.dp, foto = usuario.foto,
                    modifier = if (onCambiarFoto != null)
                        Modifier.clip(CircleShape).clickable(onClickLabel = "Cambiar foto de perfil") { elegirFoto() }
                    else Modifier
                )
                if (onCambiarFoto != null) {
                    // Insignia de cámara para que se note que la foto se puede cambiar
                    Box(
                        Modifier.align(Alignment.BottomEnd).size(24.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(13.dp))
                    }
                }
            }
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
        if (onCambiarFoto != null) {
            Row {
                TextButton(onClick = elegirFoto) {
                    Text(if (usuario.foto == null) "Agregar foto" else "Cambiar foto")
                }
                if (usuario.foto != null && onQuitarFoto != null) {
                    TextButton(onClick = onQuitarFoto) {
                        Text("Quitar foto", color = MaterialTheme.colorScheme.error)
                    }
                }
                if (onEditarDatos != null) {
                    TextButton(onClick = onEditarDatos) { Text("Editar datos") }
                }
            }
        }
        if (aviso != null) {
            Text(aviso, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 6.dp))
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

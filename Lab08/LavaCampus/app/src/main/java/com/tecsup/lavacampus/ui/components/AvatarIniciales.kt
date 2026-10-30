package com.tecsup.lavacampus.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.tecsup.lavacampus.R
import com.tecsup.lavacampus.data.local.FotosPerfil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Avatares ilustrados incluidos en la app (res/drawable), por nombre. */
private fun avatarIncluido(foto: String?): Int? = when (foto?.removePrefix(FotosPerfil.PREFIJO_AVATAR)) {
    "diego" -> R.drawable.avatar_diego
    "luis" -> R.drawable.avatar_luis
    "marta" -> R.drawable.avatar_marta
    "admin" -> R.drawable.avatar_admin
    else -> null
}

/**
 * Avatar circular del usuario. Según [foto] muestra:
 * un avatar incluido ("avatar:diego"), la foto elegida de la galería (ruta del archivo)
 * o, si no tiene, sus iniciales ("Diego Panez" -> "DP") con el color del rol.
 */
@Composable
fun AvatarIniciales(nombre: String, modifier: Modifier = Modifier, tam: Dp = 46.dp, foto: String? = null) {
    val incluido = if (foto?.startsWith(FotosPerfil.PREFIJO_AVATAR) == true) avatarIncluido(foto) else null
    // La foto de la galería se lee del archivo fuera del hilo principal; mientras carga se ven las iniciales
    val imagen by produceState<ImageBitmap?>(initialValue = null, foto) {
        value = if (foto == null || foto.startsWith(FotosPerfil.PREFIJO_AVATAR)) null
                else withContext(Dispatchers.IO) { BitmapFactory.decodeFile(foto)?.asImageBitmap() }
    }

    val actual = imagen
    if (incluido != null) {
        Image(
            painter = painterResource(incluido),
            contentDescription = "Foto de $nombre",
            contentScale = ContentScale.Crop,
            modifier = modifier.size(tam).clip(CircleShape)
        )
    } else if (actual != null) {
        Image(
            bitmap = actual,
            contentDescription = "Foto de $nombre",
            contentScale = ContentScale.Crop,
            modifier = modifier.size(tam).clip(CircleShape)
        )
    } else {
        val iniciales = nombre.split(" ").filter { it.isNotBlank() }.take(2)
            .joinToString("") { it.first().uppercase() }
        Box(
            modifier.size(tam).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(iniciales, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

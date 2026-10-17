package com.tecsup.lavacampus.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

/** Un destino de la barra inferior. */
data class ItemBarra(val ruta: String, val etiqueta: String, val icono: ImageVector)

/**
 * NavigationBar común: 4 ítems para el estudiante y 5 para el administrador.
 * El color del ítem activo sale del tema del rol.
 */
@Composable
fun BarraInferior(items: List<ItemBarra>, rutaActual: String?, onNavegar: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        items.forEach { item ->
            NavigationBarItem(
                selected = rutaActual == item.ruta,
                onClick = { if (rutaActual != item.ruta) onNavegar(item.ruta) },
                icon = { Icon(item.icono, contentDescription = item.etiqueta) },
                label = { Text(item.etiqueta) }
            )
        }
    }
}

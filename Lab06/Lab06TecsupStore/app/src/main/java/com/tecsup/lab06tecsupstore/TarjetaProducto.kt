package com.tecsup.lab06tecsupstore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProductCard(
    producto: Producto,
    onFavoritoClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                Text(text = "Precio: S/ ${producto.precio}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Cantidad: ${producto.cantidad}", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Opciones")
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Favoritos") },
                    onClick = {
                        onFavoritoClick()
                        expanded = false
                    },
                    leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Compartir") },
                    onClick = { expanded = false },
                    leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) }
                )
                HorizontalDivider()
                DropdownMenuItem(
                    text = { Text("Reportar") },
                    onClick = { expanded = false },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) }
                )
            }
        }
    }
}
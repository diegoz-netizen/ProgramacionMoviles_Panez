package com.tecsup.lab06tecsupstore

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawerContent(
    pantallaActual: String,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Maria Rojas", style = MaterialTheme.typography.headlineSmall)
            Text("maria@tecsup.edu.pe", style = MaterialTheme.typography.bodyMedium)
        }
        HorizontalDivider()

        NavigationDrawerItem(
            label = { Text("Inicio") },
            selected = pantallaActual == "Inicio",
            onClick = { onItemClick("Inicio") }
        )
        NavigationDrawerItem(
            label = { Text("Mis pedidos") },
            selected = pantallaActual == "Mis pedidos",
            onClick = { onItemClick("Mis pedidos") }
        )
        NavigationDrawerItem(
            label = { Text("Favoritos") },
            selected = pantallaActual == "Favoritos",
            onClick = { onItemClick("Favoritos") }
        )
        NavigationDrawerItem(
            label = { Text("Perfil") },
            selected = pantallaActual == "Perfil",
            onClick = { onItemClick("Perfil") }
        )
    }
}
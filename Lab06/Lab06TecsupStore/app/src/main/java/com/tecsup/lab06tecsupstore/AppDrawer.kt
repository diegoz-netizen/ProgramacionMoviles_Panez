package com.tecsup.lab06tecsupstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawerContent(
    pantallaActual: String,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "MR",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                "Diego Panez",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp)
            )
            Text(
                "diego.panez@tecsup.edu.pe",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
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
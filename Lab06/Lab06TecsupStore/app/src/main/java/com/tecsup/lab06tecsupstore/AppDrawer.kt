package com.tecsup.lab06tecsupstore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
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
    cantidadFavoritos: Int,
    onItemClick: (String) -> Unit
) {
    ModalDrawerSheet {
        // Encabezado con avatar, nombre y correo
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "DP",
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
                "diego@tecsup.edu.pe",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        HorizontalDivider()

        // Item: Inicio
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
            label = { Text("Inicio") },
            selected = pantallaActual == "Inicio",
            onClick = { onItemClick("Inicio") },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Item: Mis pedidos
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = null) },
            label = { Text("Mis pedidos") },
            selected = pantallaActual == "Mis pedidos",
            onClick = { onItemClick("Mis pedidos") },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Item: Favoritos (con badge)
        NavigationDrawerItem(
            icon = {
                BadgedBox(
                    badge = {
                        if (cantidadFavoritos > 0) {
                            Badge { Text(cantidadFavoritos.toString()) }
                        }
                    }
                ) {
                    Icon(Icons.Outlined.FavoriteBorder, contentDescription = null)
                }
            },
            label = { Text("Favoritos") },
            selected = pantallaActual == "Favoritos",
            onClick = { onItemClick("Favoritos") },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Item: Perfil
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            label = { Text("Perfil") },
            selected = pantallaActual == "Perfil",
            onClick = { onItemClick("Perfil") },
            colors = NavigationDrawerItemDefaults.colors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )

        // Item: Cerrar sesión
        NavigationDrawerItem(
            icon = { Icon(Icons.Outlined.Logout, contentDescription = null) },
            label = { Text("Cerrar sesión") },
            selected = false,
            onClick = { onItemClick("Cerrar sesión") }
        )
    }
}
package com.tecsup.lab06tecsupstore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.lab06tecsupstore.ui.theme.Lab06TecsupStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab06TecsupStoreTheme {
                val listaProductos = remember {
                    mutableStateListOf(
                        Producto("Laptop", 2500.0, 1),
                        Producto("Mouse", 50.0, 2),
                        Producto("Teclado", 120.0, 1)
                    )
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(listaProductos) { producto ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = producto.nombre, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Precio: S/ ${producto.precio}")
                                    Text(text = "Cantidad: ${producto.cantidad}")
                                    Text(text = "Subtotal: S/ ${producto.subtotal}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
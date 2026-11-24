package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel
import com.tecsup.murocampus.viewmodel.UiState

/** Formulario para crear una publicación con POST. */
@Composable
fun PantallaCrear(vm: PostsViewModel, modifier: Modifier = Modifier) {
    val envio by vm.envio.collectAsStateWithLifecycle()
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }
    val enviando = envio is UiState.Cargando

    // Si se publicó bien, se limpia el formulario; si hubo error, se conserva lo escrito
    LaunchedEffect(envio) {
        if (envio is UiState.Exito) {
            titulo = ""
            texto = ""
        }
    }

    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Nueva publicación", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Contenido") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = { vm.publicar(titulo.trim(), texto.trim()) },
            enabled = titulo.isNotBlank() && texto.isNotBlank() && !enviando,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (enviando) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else Text("Publicar")
        }

        when (val e = envio) {
            is UiState.Exito -> Aviso(
                "Publicado con id ${e.datos.id}",
                MaterialTheme.colorScheme.primaryContainer
            )
            is UiState.Error -> Aviso(e.mensaje, MaterialTheme.colorScheme.errorContainer)
            else -> Unit
        }
    }
}

@Composable
private fun Aviso(texto: String, fondo: androidx.compose.ui.graphics.Color) {
    Card(
        colors = CardDefaults.cardColors(containerColor = fondo),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(texto, Modifier.padding(16.dp))
    }
}

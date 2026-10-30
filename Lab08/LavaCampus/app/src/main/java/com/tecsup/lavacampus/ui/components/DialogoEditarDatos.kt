package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.viewmodel.SesionViewModel

/**
 * Formulario para editar el nombre y el correo del usuario conectado.
 * La pantalla solo guarda lo que se escribe; las validaciones (nombre, formato y correo
 * repetido) las hace SesionViewModel.guardarDatos y aquí solo se muestran sus errores.
 */
@Composable
fun DialogoEditarDatos(sesion: SesionViewModel, usuario: Usuario, onCerrar: () -> Unit) {
    var nombre by rememberSaveable { mutableStateOf(usuario.nombre) }
    var correo by rememberSaveable { mutableStateOf(usuario.correo) }
    val errorNombre by sesion.errorNombre.collectAsStateWithLifecycle()
    val errorCorreo by sesion.errorCorreo.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { sesion.limpiarErroresDatos() }
    val cerrar = {
        sesion.limpiarErroresDatos()
        onCerrar()
    }

    AlertDialog(
        onDismissRequest = cerrar,
        title = { Text("Editar mis datos") },
        text = {
            Column {
                LavaTextField(nombre, { nombre = it }, "Nombre completo", Icons.Filled.Person, error = errorNombre)
                LavaTextField(correo, { correo = it }, "Correo", Icons.Filled.Email,
                    teclado = KeyboardType.Email, error = errorCorreo)
                Text("Con este correo inicias sesión.")
            }
        },
        confirmButton = {
            TextButton(onClick = { sesion.guardarDatos(nombre, correo, alGuardar = cerrar) }) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = cerrar) { Text("Cancelar") } }
    )
}

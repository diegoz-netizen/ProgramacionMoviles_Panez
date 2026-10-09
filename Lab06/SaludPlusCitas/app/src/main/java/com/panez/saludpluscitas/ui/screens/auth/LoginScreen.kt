package com.panez.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun LoginScreen(onIngresar: () -> Unit, onRegistro: () -> Unit) {
    var id by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(48.dp))
        Text("Iniciar sesión", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = VerdePrincipal)
        Text("Ingresa con tu teléfono o correo", color = TextoSecundario, fontSize = 17.sp)
        Spacer(Modifier.height(24.dp))
        CampoTexto(id, { id = it; error = null }, "Teléfono o correo", Icons.Filled.Person)
        CampoTexto(clave, { clave = it; error = null }, "Contraseña", Icons.Filled.Lock, esClave = true, error = error)
        Spacer(Modifier.height(12.dp))
        BotonPrincipal("Ingresar", onClick = {
            if (id.isBlank() || clave.isBlank()) error = "Completa todos los campos"
            else if (Repositorio.iniciarSesion(id, clave)) onIngresar()
            else error = "Credenciales incorrectas"
        })
        TextButton(onClick = onRegistro) { Text("¿No tienes cuenta? Regístrate", fontSize = 16.sp) }
        Text("Demo: 987654321 / 123456", fontSize = 14.sp, color = TextoSecundario)
    }
}
package com.panez.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun RegistroScreen(onRegistrado: () -> Unit, onTerminos: () -> Unit, onLogin: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var errNombre by remember { mutableStateOf<String?>(null) }
    var errTel by remember { mutableStateOf<String?>(null) }
    var errCorreo by remember { mutableStateOf<String?>(null) }
    var errClave by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Crear cuenta", fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Regístrate para agendar tus citas", color = TextoSecundario)
        Spacer(Modifier.height(20.dp))

        CampoTexto(nombre, { nombre = it }, "Nombre completo", Icons.Filled.Person, error = errNombre)
        CampoTexto(telefono, { telefono = it.filter { c -> c.isDigit() }.take(9) }, "Teléfono", Icons.Filled.Phone,
            teclado = KeyboardType.Phone, error = errTel)
        CampoTexto(correo, { correo = it }, "Correo (opcional)", Icons.Filled.Email,
            teclado = KeyboardType.Email, error = errCorreo)
        CampoTexto(clave, { clave = it }, "Contraseña", Icons.Filled.Lock, esClave = true, error = errClave)

        Spacer(Modifier.height(12.dp))
        BotonPrincipal("Registrarme", onClick = {
            errNombre = if (nombre.isBlank()) "Ingresa tu nombre" else null
            errTel = if (telefono.length != 9) "Debe tener 9 dígitos" else null
            errCorreo = if (correo.isNotBlank() && !(correo.contains("@") && correo.contains("."))) "Correo no válido" else null
            errClave = if (clave.length < 6) "Mínimo 6 caracteres" else null
            if (listOf(errNombre, errTel, errCorreo, errClave).all { it == null }) {
                val ok = Repositorio.registrarUsuario(
                    com.panez.saludpluscitas.data.model.Usuario(nombre.trim(), telefono, correo.trim(), clave)
                )
                if (ok) onRegistrado() else errTel = "Este teléfono ya está registrado"
            }
        })
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Al registrarte aceptas los ", fontSize = 12.sp, color = TextoSecundario)
            Text("Términos y Condiciones", fontSize = 12.sp, color = AzulPrimario, modifier = Modifier.clickable(onClick = onTerminos))
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿Ya tienes cuenta?", fontSize = 13.sp)
            TextButton(onClick = onLogin) { Text("Iniciar sesión") }
        }
    }
}
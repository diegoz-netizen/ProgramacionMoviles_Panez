package com.panez.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.model.Usuario
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun RegistroScreen(onRegistrado: () -> Unit, onTerminos: () -> Unit, onLogin: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

    var errNombre by remember { mutableStateOf<String?>(null) }
    var errTel by remember { mutableStateOf<String?>(null) }
    var errCorreo by remember { mutableStateOf<String?>(null) }
    var errClave by remember { mutableStateOf<String?>(null) }
    var errConfirmar by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier.fillMaxSize().background(Color.White).verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))
        Text("Crear cuenta", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = VerdePrincipal)
        Text("Regístrate para agendar tus citas", color = TextoSecundario, fontSize = 17.sp)
        Spacer(Modifier.height(20.dp))

        CampoTexto(nombre, { nombre = it; errNombre = null }, "Nombre completo", Icons.Filled.Person, error = errNombre)
        CampoTexto(
            telefono,
            { telefono = it.filter { c -> c.isDigit() }.take(9); errTel = null },
            "Teléfono", Icons.Filled.Phone,
            teclado = KeyboardType.Phone, error = errTel
        )
        CampoTexto(
            correo,
            { correo = it; errCorreo = null },
            "Correo (opcional)", Icons.Filled.Email,
            teclado = KeyboardType.Email, error = errCorreo
        )
        CampoTexto(
            clave,
            { clave = it; errClave = null; if (errConfirmar != null) errConfirmar = null },
            "Contraseña", Icons.Filled.Lock, esClave = true, error = errClave
        )
        CampoTexto(
            confirmar,
            { confirmar = it; errConfirmar = null },
            "Confirmar contraseña", Icons.Filled.Lock, esClave = true, error = errConfirmar
        )

        Spacer(Modifier.height(12.dp))
        BotonPrincipal("Registrarme", onClick = {
            errNombre = if (nombre.isBlank()) "Ingresa tu nombre" else null
            errTel = if (telefono.length != 9) "Debe tener 9 dígitos" else null
            errCorreo = if (correo.isNotBlank() && !(correo.contains("@") && correo.contains(".")))
                "Correo no válido" else null
            errClave = if (clave.length < 6) "Mínimo 6 caracteres" else null
            errConfirmar = when {
                confirmar.isBlank() -> "Confirma tu contraseña"
                confirmar != clave -> "Las contraseñas no coinciden"
                else -> null
            }

            val sinErrores = listOf(errNombre, errTel, errCorreo, errClave, errConfirmar).all { it == null }
            if (sinErrores) {
                val ok = Repositorio.registrarUsuario(
                    Usuario(nombre.trim(), telefono, correo.trim(), clave)
                )
                if (ok) onRegistrado() else errTel = "Este teléfono ya está registrado"
            }
        })
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Al registrarte aceptas los ", fontSize = 14.sp, color = TextoSecundario)
            Text(
                "Términos y Condiciones", fontSize = 14.sp, color = VerdePrincipal,
                fontWeight = FontWeight.Medium, modifier = Modifier.clickable(onClick = onTerminos)
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿Ya tienes cuenta?", fontSize = 15.sp)
            TextButton(onClick = onLogin) { Text("Iniciar sesión", fontSize = 16.sp) }
        }
    }
}
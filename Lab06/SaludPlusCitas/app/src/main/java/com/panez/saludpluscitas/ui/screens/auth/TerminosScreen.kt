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
fun TerminosScreen(onBack: () -> Unit) {
    Scaffold(topBar = { BarraSuperior("Términos y condiciones", onBack) }, containerColor = Fondo) { pad ->
        Column(Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(20.dp)) {
            val secciones = listOf(
                "1. Uso de la aplicación" to "La App Paciente de Clínica SaludPlus permite agendar y consultar citas médicas. El usuario debe proporcionar datos verdaderos.",
                "2. Datos personales" to "Los datos ingresados se usan solo para gestionar tus citas dentro de la aplicación.",
                "3. Citas" to "Las citas pueden cancelarse desde el detalle de la cita. Se recomienda llegar 10 minutos antes.",
                "4. Alcance" to "Esta versión es académica: los datos viven en memoria y se pierden al cerrar la app."
            )
            secciones.forEach { (t, c) ->
                Text(t, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(4.dp))
                Text(c, color = TextoSecundario)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
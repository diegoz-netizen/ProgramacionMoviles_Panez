package com.panez.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.ui.components.BarraSuperior
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
                Text(t, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(4.dp))
                Text(c, color = TextoSecundario, fontSize = 16.sp)
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
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
fun SplashScreen(onComenzar: () -> Unit, onLogin: () -> Unit) {
    Column(
        Modifier.fillMaxSize().background(Color.White).padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(120.dp).background(AzulClaro, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.LocalHospital, null, tint = AzulPrimario, modifier = Modifier.size(72.dp))
        }
        Spacer(Modifier.height(20.dp))
        Text("Clínica", fontSize = 22.sp, color = AzulPrimario)
        Text("SaludPlus", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = AzulPrimario)
        Text("Tu salud, nuestra prioridad", color = TextoSecundario)
        Spacer(Modifier.height(48.dp))
        BotonPrincipal("Comenzar", onComenzar)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onLogin) { Text("Ya tengo una cuenta") }
    }
}
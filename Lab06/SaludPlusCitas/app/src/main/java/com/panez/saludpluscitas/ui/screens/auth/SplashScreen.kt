package com.panez.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.R
import com.panez.saludpluscitas.ui.components.*
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun SplashScreen(onComenzar: () -> Unit, onLogin: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(Modifier.size(72.dp).background(AzulClaro, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.LocalHospital, null, tint = AzulPrimario, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(8.dp))
        Text("Clínica", fontSize = 22.sp, color = AzulPrimario)
        Text("SaludPlus", fontSize = 34.sp, fontWeight = FontWeight.Bold, color = AzulPrimario)
        Text("Tu salud, nuestra prioridad", color = TextoSecundario)

        Spacer(Modifier.height(16.dp))
        Image(
            painter = painterResource(id = R.drawable.doc),
            contentDescription = "Doctor de Clínica SaludPlus",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.05f),
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.height(24.dp))

        BotonPrincipal("Comenzar", onComenzar)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onLogin) { Text("Ya tengo una cuenta") }
    }
}
package com.panez.saludpluscitas.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.ui.components.BotonPrincipal
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun SplashScreen(onComenzar: () -> Unit, onLogin: () -> Unit) {
    Box(
        Modifier.fillMaxSize().background(Color.White)
    ) {
        // Banda verde superior (mitad de pantalla)
        Box(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .background(VerdePrincipal)
        )

        Column(
            Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))

            // Ícono circular blanco sobre la banda verde
            Surface(
                shape = CircleShape,
                color = Color.White,
                modifier = Modifier.size(110.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Filled.HealthAndSafety,
                        contentDescription = null,
                        tint = VerdePrincipal,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Clínica", fontSize = 20.sp, color = Color.White.copy(alpha = 0.85f))
            Text(
                "SaludPlus",
                fontSize = 44.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Cuidamos tu salud, todos los días",
                fontSize = 17.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(48.dp))

            // Tarjeta blanca con información
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    Modifier.padding(24.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Bienvenido",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = VerdePrincipal
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        "Agenda tus citas con nuestros especialistas de forma rápida y sencilla.",
                        fontSize = 16.sp,
                        color = TextoSecundario,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(22.dp))

                    BotonPrincipal("Comenzar", onComenzar)
                    Spacer(Modifier.height(10.dp))
                    TextButton(onClick = onLogin) {
                        Text(
                            "Ya tengo una cuenta",
                            color = VerdePrincipal,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text(
                "Sede Santa Anita · Sede La Molina",
                fontSize = 13.sp,
                color = TextoSecundario
            )
        }
    }
}
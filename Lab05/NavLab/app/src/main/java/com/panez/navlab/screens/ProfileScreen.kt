package com.panez.navlab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.panez.navlab.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración de Perfil",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2B2B2B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF2B2B2B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header con degradado DIAGONAL de tres tonos: morado/azul (0xFF4A3E85) -> salmón (0xFFB55375) -> dorado suave (0xFFD67B5C)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4A3E85),
                                Color(0xFFB55375),
                                Color(0xFFD67B5C)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(1000f, 1000f)
                        )
                    )
                    .padding(vertical = 28.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(88.dp),
                        shape = CircleShape,
                        color = Color(0xFFEDE7F6),
                        border = BorderStroke(3.dp, Color.White),
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = Color(0xFF4A3E85),
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Diego Panez",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // SECCIÓN 1: INFORMACIÓN PERSONAL (Lista limpia sin cajas ni dividores visibles)
                Text(
                    text = "INFORMACIÓN PERSONAL",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2E83),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileItemRowNoDivider(
                    icon = Icons.Default.Person,
                    label = "Nombre Completo",
                    value = "Diego Panez"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileItemRowNoDivider(
                    icon = Icons.Default.Email,
                    label = "Correo",
                    value = "diego.panez@tecsup.edu.pe"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileItemRowNoDivider(
                    icon = Icons.Default.Phone,
                    label = "Teléfono",
                    value = "+51 987 654 321"
                )

                Spacer(modifier = Modifier.height(28.dp))

                // SECCIÓN 2: ACADÉMICO (Lista limpia sin cajas ni dividores visibles)
                Text(
                    text = "ACADÉMICO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4A2E83),
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                ProfileItemRowNoDivider(
                    icon = Icons.Default.Star,
                    label = "Carrera",
                    value = "Diseño y Desarrollo de Software"
                )

                Spacer(modifier = Modifier.height(16.dp))

                ProfileItemRowNoDivider(
                    icon = Icons.Default.DateRange,
                    label = "Ciclo Actual",
                    value = "V Ciclo"
                )

                Spacer(modifier = Modifier.height(36.dp))

                // Botón "Cerrar Sesión"
                Button(
                    onClick = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFDE8E8),
                        contentColor = Color(0xFFE05C5C)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Cerrar Sesión",
                        tint = Color(0xFFE05C5C)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar Sesión",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun ProfileItemRowNoDivider(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF2F1F5), // Cuadrado gris muy claro a la izquierda
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF49454F), // Ícono de color GRIS OSCURO / NEGRO SUAVE (0xFF49454F)
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF6E6E6E)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2B2B2B)
            )
        }
    }
}
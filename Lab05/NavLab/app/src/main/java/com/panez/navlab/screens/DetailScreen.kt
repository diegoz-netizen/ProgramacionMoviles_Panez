package com.panez.navlab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

data class DetailStudentData(
    val id: Int,
    val name: String,
    val career: String,
    val studentIdCode: String,
    val email: String,
    val faculty: String,
    val bio: String
)

fun getStudentById(id: Int): DetailStudentData {
    return when (id) {
        2 -> DetailStudentData(
            id = 2,
            name = "María García",
            career = "Arquitectura",
            studentIdCode = "2024-0002",
            email = "maria.garcia@example.com",
            faculty = "Arquitectura y Diseño",
            bio = "Estudiante destacada apasionada por el diseño urbano sostenible y arquitectura digital."
        )
        3 -> DetailStudentData(
            id = 3,
            name = "Carlos Pérez",
            career = "Medicina",
            studentIdCode = "2024-0003",
            email = "carlos.perez@example.com",
            faculty = "Ciencias de la Salud",
            bio = "Estudiante de medicina enfocado en investigación clínica y salud pública."
        )
        4 -> DetailStudentData(
            id = 4,
            name = "Ana López",
            career = "Derecho",
            studentIdCode = "2024-0004",
            email = "ana.lopez@example.com",
            faculty = "Derecho y Ciencias Políticas",
            bio = "Interesada en derecho corporativo y tecnología jurídica."
        )
        5 -> DetailStudentData(
            id = 5,
            name = "Luis Ramírez",
            career = "Administración",
            studentIdCode = "2024-0005",
            email = "luis.ramirez@example.com",
            faculty = "Gestión y Negocios",
            bio = "Líder estudiantil con enfoque en emprendimiento e innovación digital."
        )
        6 -> DetailStudentData(
            id = 6,
            name = "Diego Panez ",
            career = "Software",
            studentIdCode = "2026-0006",
            email = "diego.panez@tecsup.edu.pe",
            faculty = "Tecnologia",
            bio = "Lidel de Software",
        )
        else -> DetailStudentData(
            id = if (id > 0) id else 1,
            name = "Juan León",
            career = "Ingeniería de Sistemas",
            studentIdCode = if (id > 0) "2024-000$id" else "2024-0001",
            email = "juan.leon@example.com",
            faculty = "Ingeniería y Tecnología",
            bio = "Estudiante destacado con interés en desarrollo Android."
        )
    }
}

val ShortWideTrapezoidShape = GenericShape { size, _ ->
    val width = size.width
    val height = size.height

    moveTo(0f, 0f)
    lineTo(width, 0f)

    lineTo(width * 0.94f, height - 16f)
    quadraticTo(
        width * 0.92f, height,
        width * 0.85f, height
    )

    lineTo(width * 0.15f, height)
    quadraticTo(
        width * 0.08f, height,
        width * 0.06f, height - 16f
    )

    close()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(navController: NavController, itemId: Int) {
    val student = getStudentById(itemId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Expediente Académico",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A2E83)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color(0xFF4A2E83)
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .background(
                            color = Color(0xFF5B4296),
                            shape = ShortWideTrapezoidShape
                        )
                )

                Surface(
                    modifier = Modifier
                        .size(88.dp)
                        .align(Alignment.TopCenter)
                        .offset(y = 46.dp),
                    shape = CircleShape,
                    color = Color(0xFFEDE7F6),
                    border = BorderStroke(3.dp, Color.White),
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color(0xFF6C4AB6),
                            modifier = Modifier.size(52.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = student.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2B2B2B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = student.career,
                    fontSize = 14.sp,
                    color = Color(0xFF7C5CBF)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF3EDF7)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "INFORMACIÓN ACADÉMICA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A2E83),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DetailItemRow(
                            icon = Icons.Default.AccountBox,
                            label = "ID Estudiante",
                            value = student.studentIdCode
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFE7E0EC)
                        )

                        DetailItemRow(
                            icon = Icons.Default.Email,
                            label = "Correo Electrónico",
                            value = student.email
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = Color(0xFFE7E0EC)
                        )

                        DetailItemRow(
                            icon = Icons.Default.Face,
                            label = "Facultad",
                            value = student.faculty
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "BIOGRAFÍA",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4A2E83),
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = student.bio,
                            fontSize = 14.sp,
                            color = Color(0xFF2B2B2B),
                            lineHeight = 20.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DetailItemRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFEDE7F6),
            modifier = Modifier.size(42.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF6C4AB6),
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

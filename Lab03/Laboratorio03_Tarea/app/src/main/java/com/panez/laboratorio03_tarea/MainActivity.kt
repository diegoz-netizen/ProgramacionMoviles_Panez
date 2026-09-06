package com.panez.laboratorio03_tarea

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.laboratorio03_tarea.ui.theme.Laboratorio03_TareaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Laboratorio03_TareaTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaRegistroNotas(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun PantallaRegistroNotas(modifier: Modifier = Modifier) {
    var nota1 by remember { mutableStateOf(0f) }
    var nota2 by remember { mutableStateOf(0f) }
    var nota3 by remember { mutableStateOf(0f) }
    var nota4 by remember { mutableStateOf(0f) }

    var redondear by remember { mutableStateOf(false) }
    var confirmado by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFEDE7F6), Color(0xFFF3E5F5))
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF5E35B1))
                .padding(20.dp)
        ) {
            Text(
                text = "Registro de Notas",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text("Notas del ciclo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Desliza para asignar cada nota (0 a 20)", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(12.dp))

            ItemCurso("Fundamentos de Programación", "(20%)", nota1) { nota1 = it }
            ItemCurso("Programación Orientada a Objetos", "(25%)", nota2) { nota2 = it }
            ItemCurso("Programación en Móviles", "(30%)", nota3) { nota3 = it }
            ItemCurso("Base de Datos", "(25%)", nota4) { nota4 = it }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Redondear promedio final")
                Switch(checked = redondear, onCheckedChange = { redondear = it })
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 4.dp)
            ) {
                Checkbox(checked = confirmado, onCheckedChange = { confirmado = it })
                Text("Confirmo que las notas son correctas")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* Lógica pendiente para commit 2 */ },
                enabled = confirmado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5E35B1))
            ) {
                Text("CALCULAR PROMEDIO", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Asigna las notas y confirma para calcular",
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Desarrollado por: Diego Panez",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun ItemCurso(nombre: String, peso: String, valor: Float, onValueChange: (Float) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Text(nombre, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(peso, color = Color(0xFF7E57C2), fontSize = 12.sp)
            }
            Surface(
                color = Color(0xFFEDE7F6),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "${valor.toInt()}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5E35B1)
                )
            }
        }
        Slider(
            value = valor,
            onValueChange = onValueChange,
            valueRange = 0f..20f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF5E35B1),
                activeTrackColor = Color(0xFF7E57C2)
            )
        )
    }
}
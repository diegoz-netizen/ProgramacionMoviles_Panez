package com.panez.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.panez.centrotareas.model.CargaUi
import com.panez.centrotareas.model.PrimosUi
import com.panez.centrotareas.viewmodel.TareasViewModel

private val VerdeExito = Color(0xFFDDF3E0)
private val SobreVerdeExito = Color(0xFF0B3D1E)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaProgreso(vm)
            TarjetaPrimos(vm)
            TarjetaCargas(vm)
        }
    }
}

@Composable
private fun TarjetaProgreso(vm: TareasViewModel) {
    val toques by vm.toques.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()

    TarjetaTarea("Tarea con progreso") {
        Button(onClick = vm::tocar) {
            Text("Tócame: $toques")
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = vm::tareaLarga, enabled = !corriendo) {
                Text("Iniciar")
            }
            OutlinedButton(onClick = vm::cancelar, enabled = corriendo) {
                Text("Cancelar")
            }
        }
        LinearProgressIndicator(
            progress = { progreso },
            modifier = Modifier.fillMaxWidth()
        )
        Text("${(progreso * 100).toInt()} %")
        Text(mensaje)
    }
}

@Composable
private fun TarjetaPrimos(vm: TareasViewModel) {
    val primos by vm.primos.collectAsState()
    var texto by rememberSaveable { mutableStateOf("3000000") }
    var hastaContado by rememberSaveable { mutableStateOf(0) }
    val calculando = primos is PrimosUi.Calculando

    TarjetaTarea("Contar números primos") {
        OutlinedTextField(
            value = texto,
            onValueChange = { nuevo -> texto = nuevo.filter(Char::isDigit).take(9) },
            label = { Text("Hasta el número") },
            singleLine = true,
            enabled = !calculando,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    texto.toIntOrNull()?.takeIf { it >= 2 }?.let { hasta ->
                        hastaContado = hasta
                        vm.contar(hasta)
                    }
                },
                enabled = !calculando
            ) {
                Text("Contar")
            }
            OutlinedButton(onClick = vm::cancelarPrimos, enabled = calculando) {
                Text("Cancelar")
            }
        }

        when (val estado = primos) {
            PrimosUi.Inicial -> Unit
            is PrimosUi.Calculando -> {
                LinearProgressIndicator(
                    progress = { estado.porcentaje / 100f },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Calculando: ${estado.porcentaje} %")
            }
            is PrimosUi.Listo -> RecuadroExito(
                titulo = "Primos hasta $hastaContado: ${estado.cantidad}",
                detalle = "Tiempo: ${estado.milisegundos} ms"
            )
            PrimosUi.Cancelado -> Text("Cancelado")
        }
    }
}

@Composable
private fun TarjetaCargas(vm: TareasViewModel) {
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val tiempoMaximoActivo by vm.tiempoMaximoActivo.collectAsState()
    val cargando = carga is CargaUi.Cargando

    TarjetaTarea("Dos cargas") {
        FilaInterruptor("Simular error", simularError, !cargando, vm::cambiarSimularError)
        FilaInterruptor(
            "Tiempo máximo (2 s por carga)",
            tiempoMaximoActivo,
            !cargando,
            vm::cambiarTiempoMaximo
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(onClick = vm::cargarUnaPorUna, enabled = !cargando) {
                Text("Una por una")
            }
            Button(onClick = vm::cargarALaVez, enabled = !cargando) {
                Text("A la vez")
            }
        }

        when (val estado = carga) {
            CargaUi.Inicial -> Unit
            CargaUi.Cargando -> CircularProgressIndicator()
            is CargaUi.Exito -> RecuadroExito(
                titulo = "Modo: ${estado.modo}",
                detalle = "Perfil: ${estado.perfil}\nPedidos: ${estado.pedidos}\n" +
                    "Tiempo: ${estado.milisegundos} ms"
            )
            is CargaUi.Error -> {
                RecuadroError(estado.mensaje)
                OutlinedButton(onClick = vm::cargarALaVez) {
                    Text("Reintentar")
                }
            }
        }
    }
}

@Composable
private fun FilaInterruptor(
    texto: String,
    activado: Boolean,
    habilitado: Boolean,
    alCambiar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(checked = activado, onCheckedChange = alCambiar, enabled = habilitado)
        Text(texto)
    }
}

@Composable
private fun RecuadroError(mensaje: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(mensaje, textAlign = TextAlign.Center, modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun RecuadroExito(titulo: String, detalle: String) {
    Surface(
        color = VerdeExito,
        contentColor = SobreVerdeExito,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(titulo, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            Text(detalle, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TarjetaTarea(titulo: String, contenido: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth()
            )
            contenido()
        }
    }
}

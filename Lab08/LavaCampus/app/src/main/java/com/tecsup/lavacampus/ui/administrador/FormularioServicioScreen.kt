package com.tecsup.lavacampus.ui.administrador

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.model.Unidad
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.FilaAjuste
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.viewmodel.ServiciosViewModel

/** Crear (servicioId = 0) o editar un servicio. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioServicioScreen(vm: ServiciosViewModel, servicioId: Int, onBack: () -> Unit) {
    LaunchedEffect(servicioId) { vm.cargar(servicioId) }
    val cargado by vm.cargado.collectAsStateWithLifecycle()
    val editado by vm.editado.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val terminado by vm.terminado.collectAsStateWithLifecycle()
    LaunchedEffect(terminado) { if (terminado) onBack() }

    val esNuevo = servicioId == 0
    Scaffold(
        topBar = { AppTopBar(if (esNuevo) "Nuevo servicio" else "Editar servicio", onBack = onBack) },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        if (!cargado) {
            Box(Modifier.fillMaxSize().padding(pad), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }
        // Estado del formulario, inicializado con el servicio editado (o vacío si es nuevo)
        val s = editado
        var nombre by remember(s) { mutableStateOf(s?.nombre ?: "") }
        var unidad by remember(s) { mutableStateOf(s?.let { Unidad.valueOf(it.unidad) } ?: Unidad.KG) }
        var precio by remember(s) { mutableStateOf(s?.precio?.let { "%.2f".format(it) } ?: "") }
        var horas by remember(s) { mutableIntStateOf(s?.horasEntrega ?: 24) }
        var descripcion by remember(s) { mutableStateOf(s?.descripcion ?: "") }
        var icono by remember(s) { mutableStateOf(s?.icono ?: "wash") }
        var activo by remember(s) { mutableStateOf(s?.activo ?: true) }
        var confirmarEliminar by remember { mutableStateOf(false) }

        Column(
            Modifier.padding(pad).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    LavaTextField(nombre, { nombre = it }, "Nombre del servicio", Icons.Filled.LocalLaundryService)

                    Text("Se cobra por", style = MaterialTheme.typography.labelLarge)
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        Unidad.entries.forEachIndexed { i, u ->
                            SegmentedButton(
                                selected = unidad == u,
                                onClick = { unidad = u },
                                shape = SegmentedButtonDefaults.itemShape(i, Unidad.entries.size)
                            ) { Text(u.etiqueta) }
                        }
                    }

                    LavaTextField(precio, { precio = it }, "Precio (S/)", Icons.Filled.Payments,
                        teclado = KeyboardType.Decimal)

                    Text("Tiempo de entrega", style = MaterialTheme.typography.labelLarge)
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        ServiciosViewModel.HORAS_VALIDAS.forEachIndexed { i, h ->
                            SegmentedButton(
                                selected = horas == h,
                                onClick = { horas = h },
                                shape = SegmentedButtonDefaults.itemShape(i, ServiciosViewModel.HORAS_VALIDAS.size)
                            ) { Text("$h h") }
                        }
                    }

                    LavaTextField(descripcion, { descripcion = it }, "Descripción", Icons.AutoMirrored.Filled.Notes,
                        unaLinea = false)

                    Text("Ícono", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        ServiciosViewModel.ICONOS.forEach { i ->
                            val elegido = icono == i
                            IconoServicio(
                                i,
                                tam = 56.dp,
                                modifier = Modifier
                                    .then(
                                        if (elegido) Modifier.border(
                                            BorderStroke(3.dp, MaterialTheme.colorScheme.primary), CircleShape
                                        ) else Modifier
                                    )
                                    .clickable { icono = i }
                            )
                        }
                    }

                    FilaAjuste(
                        icono = Icons.Filled.ToggleOn,
                        titulo = "Servicio activo",
                        descripcion = "Si está apagado no aparece al hacer un pedido",
                        activo = activo,
                        onCambio = { activo = it }
                    )
                }
            }

            if (mensaje != null) {
                Text(mensaje!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
            }

            LavaButton("Guardar", icono = Icons.Filled.Save, onClick = {
                vm.guardar(nombre, unidad, precio, horas, descripcion, icono, activo)
            })
            if (s != null) {
                OutlinedButton(
                    onClick = { confirmarEliminar = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Filled.Delete, contentDescription = null)
                    Text("  Eliminar servicio", style = MaterialTheme.typography.labelLarge)
                }
            }
        }

        if (confirmarEliminar && s != null) {
            AlertDialog(
                onDismissRequest = { confirmarEliminar = false },
                icon = { Icon(Icons.Filled.Delete, contentDescription = null) },
                title = { Text("¿Eliminar \"${s.nombre}\"?") },
                text = { Text("Si el servicio ya aparece en algún pedido no se podrá eliminar; en ese caso desactívalo.") },
                confirmButton = {
                    TextButton(onClick = { confirmarEliminar = false; vm.eliminar(s) }) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = { TextButton(onClick = { confirmarEliminar = false }) { Text("Cancelar") } }
            )
        }
    }
}

package com.panez.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.panez.centrotareas.model.PrimosUi
import com.panez.centrotareas.ui.components.BotonPrincipal
import com.panez.centrotareas.ui.components.BotonSecundario
import com.panez.centrotareas.ui.components.ChipEstado
import com.panez.centrotareas.ui.components.EncabezadoPanel
import com.panez.centrotareas.ui.components.IndicadorAvance
import com.panez.centrotareas.ui.components.MetricaControl
import com.panez.centrotareas.ui.components.PanelControl
import com.panez.centrotareas.ui.components.TonoEstado
import com.panez.centrotareas.ui.theme.Dimens
import com.panez.centrotareas.viewmodel.TareasViewModel

@Composable
fun SeccionPrimos(vm: TareasViewModel) {
    val primos by vm.primos.collectAsState()
    var texto by rememberSaveable { mutableStateOf("3000000") }
    var hastaContado by rememberSaveable { mutableStateOf(0) }
    val calculando = primos is PrimosUi.Calculando

    PanelControl {
        EncabezadoPanel("Contar números primos", "Cálculo pesado en Dispatchers.Default")
        OutlinedTextField(
            value = texto,
            onValueChange = { nuevo -> texto = nuevo.filter(Char::isDigit).take(9) },
            label = { Text("Hasta el número") },
            singleLine = true,
            enabled = !calculando,
            textStyle = MaterialTheme.typography.headlineSmall,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            BotonPrincipal(
                texto = "Contar",
                alPulsar = {
                    texto.toIntOrNull()?.takeIf { it >= 2 }?.let { hasta ->
                        hastaContado = hasta
                        vm.contar(hasta)
                    }
                },
                modifier = Modifier.weight(1f),
                habilitado = !calculando
            )
            BotonSecundario(
                "Cancelar",
                vm::cancelarPrimos,
                Modifier.weight(1f),
                habilitado = calculando
            )
        }
    }

    when (val estado = primos) {
        PrimosUi.Inicial -> Unit
        is PrimosUi.Calculando -> PanelControl {
            IndicadorAvance(
                fraccion = estado.porcentaje / 100f,
                valor = "${estado.porcentaje}%",
                etiqueta = "Calculando"
            )
        }
        is PrimosUi.Listo -> PanelControl {
            ChipEstado("Primos hasta $hastaContado", TonoEstado.Exito)
            Text(
                "${estado.cantidad}",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )
            MetricaControl("Tiempo", "${estado.milisegundos} ms", Modifier.fillMaxWidth())
        }
        PrimosUi.Cancelado -> PanelControl {
            ChipEstado("Cancelado", TonoEstado.Advertencia)
        }
    }
}

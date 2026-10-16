package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Control de cantidad con botones menos y más.
 * Con valor 0 el servicio está apagado: "+" lo enciende en [minimo] y "-" desde [minimo] lo apaga.
 * Nunca pasa de [maximo]. [paso] es 0.5 para KG y 1 para PRENDA/UNIDAD.
 */
@Composable
fun SelectorCantidad(
    valor: Double,
    paso: Double,
    minimo: Double,
    maximo: Double,
    onCambio: (Double) -> Unit,
    modifier: Modifier = Modifier,
    unidad: String = ""
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        FilledTonalIconButton(
            onClick = { onCambio(if (valor - paso < minimo) 0.0 else valor - paso) },
            enabled = valor > 0
        ) { Icon(Icons.Filled.Remove, contentDescription = "Menos") }

        val texto = if (paso < 1) "%.1f".format(valor) else valor.toInt().toString()
        Text(
            if (unidad.isBlank()) texto else "$texto $unidad",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.widthIn(min = 64.dp)
        )

        FilledTonalIconButton(
            onClick = { onCambio(if (valor == 0.0) minimo else minOf(valor + paso, maximo)) },
            enabled = valor < maximo
        ) { Icon(Icons.Filled.Add, contentDescription = "Más") }
    }
}

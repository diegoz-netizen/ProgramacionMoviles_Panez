package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tecsup.lavacampus.model.EstadoPedido

/**
 * Línea de seguimiento del pedido dibujada con Canvas:
 * Recibido -> En proceso -> Listo -> Entregado.
 * [vertical] = false para la tarjeta de Inicio, true para el Detalle.
 */
@Composable
fun StepperEstado(estado: EstadoPedido, modifier: Modifier = Modifier, vertical: Boolean = false) {
    val actual = EstadoPedido.etapas.indexOf(estado) // -1 si está CANCELADO
    val activo = MaterialTheme.colorScheme.primary
    val inactivo = MaterialTheme.colorScheme.outline
    if (vertical) StepperVertical(actual, activo, inactivo, modifier)
    else StepperHorizontal(actual, activo, inactivo, modifier)
}

@Composable
private fun StepperHorizontal(actual: Int, activo: Color, inactivo: Color, modifier: Modifier) {
    val etapas = EstadoPedido.etapas
    Column(modifier.fillMaxWidth()) {
        Canvas(Modifier.fillMaxWidth().height(28.dp)) {
            val paso = size.width / etapas.size
            val y = size.height / 2
            for (i in 0 until etapas.size - 1) {
                drawLine(
                    color = if (i < actual) activo else inactivo,
                    start = Offset(paso * (i + 0.5f), y),
                    end = Offset(paso * (i + 1.5f), y),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            etapas.indices.forEach { i -> dibujarPunto(Offset(paso * (i + 0.5f), y), i, actual, activo, inactivo) }
        }
        Row(Modifier.fillMaxWidth()) {
            etapas.forEachIndexed { i, e ->
                Text(
                    e.etiqueta,
                    style = MaterialTheme.typography.labelMedium,
                    color = if (i <= actual) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun StepperVertical(actual: Int, activo: Color, inactivo: Color, modifier: Modifier) {
    val etapas = EstadoPedido.etapas
    Column(modifier.fillMaxWidth()) {
        etapas.forEachIndexed { i, e ->
            Row {
                Canvas(Modifier.width(28.dp).height(56.dp)) {
                    val centro = Offset(size.width / 2, 14.dp.toPx())
                    if (i < etapas.size - 1) {
                        drawLine(
                            color = if (i < actual) activo else inactivo,
                            start = centro,
                            end = Offset(centro.x, size.height + 14.dp.toPx()),
                            strokeWidth = 4.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                    dibujarPunto(centro, i, actual, activo, inactivo)
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.padding(top = 4.dp)) {
                    Text(e.etiqueta, style = MaterialTheme.typography.titleMedium,
                        color = if (i <= actual) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        when {
                            i < actual -> "Completado"
                            i == actual -> "Etapa actual"
                            else -> "Pendiente"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/** Círculo de una etapa: lleno si ya pasó, con anillo si es la actual, hueco si está pendiente. */
private fun DrawScope.dibujarPunto(centro: Offset, i: Int, actual: Int, activo: Color, inactivo: Color) {
    val radio = 9.dp.toPx()
    when {
        i < actual -> drawCircle(activo, radio, centro)
        i == actual -> {
            drawCircle(activo.copy(alpha = 0.25f), radio * 1.45f, centro)
            drawCircle(activo, radio, centro)
            drawCircle(Color.White, radio * 0.4f, centro)
        }
        else -> {
            drawCircle(Color.White, radio, centro)
            drawCircle(inactivo, radio, centro, style = Stroke(width = 3.dp.toPx()))
        }
    }
}

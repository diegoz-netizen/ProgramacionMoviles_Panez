package com.panez.centrotareas.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.style.TextAlign
import com.panez.centrotareas.ui.theme.Dimens

enum class TonoEstado { Neutro, Activo, Exito, Advertencia, Error }

@Composable
fun PanelControl(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(Dimens.borde, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.md),
            content = contenido
        )
    }
}

@Composable
fun EncabezadoPanel(titulo: String, descripcion: String) {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.xs)) {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        Text(
            descripcion,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** El punto late mientras hay trabajo: si el hilo principal se bloqueara, dejaría de moverse. */
@Composable
fun PanelHiloPrincipal(toques: Int, trabajando: Boolean, alTocar: () -> Unit) {
    PanelControl {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.md),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PuntoEstado(trabajando)
            Column(modifier = Modifier.weight(1f)) {
                Text("Hilo principal", style = MaterialTheme.typography.titleMedium)
                Text(
                    if (trabajando) "Responde mientras trabaja" else "Libre",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Button(onClick = alTocar, shape = MaterialTheme.shapes.medium) {
                Text("Tócame: $toques", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun PuntoEstado(latiendo: Boolean) {
    val opacidad = if (latiendo) {
        val transicion = rememberInfiniteTransition(label = "pulso")
        val valor by transicion.animateFloat(
            initialValue = 1f,
            targetValue = 0.25f,
            animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
            label = "opacidad"
        )
        valor
    } else {
        1f
    }
    Box(
        modifier = Modifier
            .size(Dimens.puntoEstado)
            .alpha(opacidad)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
    )
}

/** Con [fraccion] nula el indicador gira sin un avance definido. */
@Composable
fun IndicadorAvance(fraccion: Float?, valor: String, etiqueta: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        val tamano = Modifier.size(Dimens.indicador)
        if (fraccion != null) {
            CircularProgressIndicator(
                progress = { fraccion },
                modifier = tamano,
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = Dimens.trazoIndicador,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round,
                gapSize = Dimens.sinSeparacion
            )
        } else {
            CircularProgressIndicator(
                modifier = tamano,
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = Dimens.trazoIndicador,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(valor, style = MaterialTheme.typography.displayMedium)
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ChipEstado(texto: String, tono: TonoEstado) {
    val colores = MaterialTheme.colorScheme
    val (fondo, contenido) = when (tono) {
        TonoEstado.Neutro -> colores.surfaceVariant to colores.onSurfaceVariant
        TonoEstado.Activo -> colores.secondaryContainer to colores.onSecondaryContainer
        TonoEstado.Exito -> colores.primaryContainer to colores.onPrimaryContainer
        TonoEstado.Advertencia -> colores.tertiaryContainer to colores.onTertiaryContainer
        TonoEstado.Error -> colores.errorContainer to colores.onErrorContainer
    }
    Surface(color = fondo, contentColor = contenido, shape = MaterialTheme.shapes.small) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = Dimens.md, vertical = Dimens.xs)
        )
    }
}

@Composable
fun MetricaControl(etiqueta: String, valor: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.md),
            verticalArrangement = Arrangement.spacedBy(Dimens.xs)
        ) {
            Text(
                etiqueta,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(valor, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

@Composable
fun BotonPrincipal(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    Button(
        onClick = alPulsar,
        enabled = habilitado,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(Dimens.altoBoton)
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun BotonSecundario(
    texto: String,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true
) {
    OutlinedButton(
        onClick = alPulsar,
        enabled = habilitado,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.height(Dimens.altoBoton)
    ) {
        Text(texto, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
fun FilaInterruptor(
    titulo: String,
    descripcion: String,
    activado: Boolean,
    habilitado: Boolean,
    alCambiar: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Dimens.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, style = MaterialTheme.typography.titleMedium)
            Text(
                descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(checked = activado, onCheckedChange = alCambiar, enabled = habilitado)
    }
}

@Composable
fun RecuadroAlerta(mensaje: String) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            mensaje,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(Dimens.lg)
        )
    }
}

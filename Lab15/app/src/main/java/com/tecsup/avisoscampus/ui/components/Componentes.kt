package com.tecsup.avisoscampus.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import com.tecsup.avisoscampus.ui.theme.Dimens
import com.tecsup.avisoscampus.ui.theme.FormaPildora
import com.tecsup.avisoscampus.ui.theme.avisos

/**
 * Tarjeta base de todas las secciones: ícono en una caja de color, título,
 * subtítulo y, a la derecha, un elemento opcional (etiqueta o switch).
 */
@Composable
fun TarjetaSeccion(
    @DrawableRes icono: Int,
    titulo: String,
    subtitulo: String,
    modifier: Modifier = Modifier,
    fondoIcono: Color = MaterialTheme.colorScheme.primaryContainer,
    colorIcono: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    extremo: @Composable RowScope.() -> Unit = {},
    contenido: @Composable ColumnScope.() -> Unit = {}
) {
    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.md)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(Dimens.iconoCaja)
                        .background(fondoIcono, MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(icono),
                        contentDescription = null,
                        tint = colorIcono,
                        modifier = Modifier.size(Dimens.icono)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = Dimens.md)
                ) {
                    Text(
                        titulo,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        subtitulo,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                extremo()
            }
            contenido()
        }
    }
}

/** Tipos de etiqueta: cada uno con su par de colores del tema. */
enum class TipoEtiqueta { NEUTRA, EXITO, ERROR, ACENTO, PRIMARIA }

/** Etiqueta tipo píldora para estados y orígenes. */
@Composable
fun EtiquetaEstado(texto: String, tipo: TipoEtiqueta, modifier: Modifier = Modifier) {
    val (fondo, frente) = when (tipo) {
        TipoEtiqueta.NEUTRA -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        TipoEtiqueta.EXITO -> MaterialTheme.avisos.exitoContenedor to MaterialTheme.avisos.sobreExitoContenedor
        TipoEtiqueta.ERROR -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
        TipoEtiqueta.ACENTO -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        TipoEtiqueta.PRIMARIA -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }
    Text(
        texto,
        style = MaterialTheme.typography.labelMedium,
        color = frente,
        modifier = modifier
            .background(fondo, FormaPildora)
            .padding(horizontal = Dimens.md, vertical = Dimens.xs)
    )
}

/**
 * Anillo de la cuenta regresiva. Se vacía de forma animada a medida que bajan los segundos;
 * sin recordatorio activo muestra el total.
 */
@Composable
fun AnilloCuenta(faltan: Int?, total: Int) {
    val fraccion by animateFloatAsState(
        targetValue = (faltan ?: total).toFloat() / total,
        label = "anillo"
    )
    Box(modifier = Modifier.size(Dimens.anillo), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { fraccion },
            modifier = Modifier.size(Dimens.anillo),
            color = if (faltan != null) MaterialTheme.avisos.ambar else MaterialTheme.colorScheme.outlineVariant,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = Dimens.anilloGrosor,
            strokeCap = StrokeCap.Round
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${faltan ?: total}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                "seg",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** Mensaje cuando una lista está vacía: ícono grande y texto centrado. */
@Composable
fun EstadoVacio(@DrawableRes icono: Int, texto: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimens.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.sm)
    ) {
        Icon(
            painterResource(icono),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(Dimens.iconoCaja)
        )
        Text(
            texto,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

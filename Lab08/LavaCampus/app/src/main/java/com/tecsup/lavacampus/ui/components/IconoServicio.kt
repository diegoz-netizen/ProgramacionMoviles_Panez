package com.tecsup.lavacampus.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Ícono del servicio dibujado con Canvas según el campo icono:
 * "wash" = lavadora, "iron" = plancha, "shirt" = camisa.
 */
@Composable
fun IconoServicio(
    icono: String,
    modifier: Modifier = Modifier,
    tam: Dp = 48.dp,
    tinte: Color = MaterialTheme.colorScheme.primary,
    fondo: Color = MaterialTheme.colorScheme.primaryContainer
) {
    Box(modifier.size(tam).background(fondo, CircleShape), contentAlignment = Alignment.Center) {
        Canvas(Modifier.size(tam).padding(tam * 0.22f)) {
            val trazo = Stroke(width = size.minDimension * 0.08f, cap = StrokeCap.Round, join = StrokeJoin.Round)
            when (icono) {
                "iron" -> plancha(tinte, trazo)
                "shirt" -> camisa(tinte, trazo)
                else -> lavadora(tinte, trazo)
            }
        }
    }
}

private fun DrawScope.lavadora(c: Color, trazo: Stroke) {
    val w = size.width
    val h = size.height
    drawRoundRect(c, Offset(w * 0.08f, 0f), Size(w * 0.84f, h), CornerRadius(w * 0.12f), style = trazo)
    drawLine(c, Offset(w * 0.08f, h * 0.24f), Offset(w * 0.92f, h * 0.24f), trazo.width)
    drawCircle(c, w * 0.05f, Offset(w * 0.26f, h * 0.12f))
    drawCircle(c, w * 0.05f, Offset(w * 0.42f, h * 0.12f))
    drawCircle(c, w * 0.26f, Offset(w * 0.5f, h * 0.62f), style = trazo)
    drawCircle(c.copy(alpha = 0.35f), w * 0.15f, Offset(w * 0.5f, h * 0.62f))
}

private fun DrawScope.plancha(c: Color, trazo: Stroke) {
    val w = size.width
    val h = size.height
    val base = Path().apply {
        moveTo(w * 0.04f, h * 0.82f)
        lineTo(w * 0.96f, h * 0.82f)
        lineTo(w * 0.96f, h * 0.5f)
        quadraticTo(w * 0.96f, h * 0.36f, w * 0.78f, h * 0.36f)
        lineTo(w * 0.36f, h * 0.36f)
        quadraticTo(w * 0.1f, h * 0.4f, w * 0.04f, h * 0.82f)
        close()
    }
    drawPath(base, c, style = trazo)
    val asa = Path().apply {
        moveTo(w * 0.4f, h * 0.36f)
        lineTo(w * 0.46f, h * 0.16f)
        lineTo(w * 0.86f, h * 0.16f)
        lineTo(w * 0.86f, h * 0.36f)
    }
    drawPath(asa, c, style = trazo)
    drawCircle(c, w * 0.05f, Offset(w * 0.3f, h * 0.68f))
}

private fun DrawScope.camisa(c: Color, trazo: Stroke) {
    val w = size.width
    val h = size.height
    val cuerpo = Path().apply {
        moveTo(w * 0.36f, h * 0.06f)
        lineTo(w * 0.04f, h * 0.24f)
        lineTo(w * 0.16f, h * 0.46f)
        lineTo(w * 0.26f, h * 0.4f)
        lineTo(w * 0.26f, h * 0.96f)
        lineTo(w * 0.74f, h * 0.96f)
        lineTo(w * 0.74f, h * 0.4f)
        lineTo(w * 0.84f, h * 0.46f)
        lineTo(w * 0.96f, h * 0.24f)
        lineTo(w * 0.64f, h * 0.06f)
        quadraticTo(w * 0.5f, h * 0.22f, w * 0.36f, h * 0.06f)
        close()
    }
    drawPath(cuerpo, c, style = trazo)
    drawLine(c, Offset(w * 0.5f, h * 0.2f), Offset(w * 0.5f, h * 0.96f), trazo.width * 0.6f)
}

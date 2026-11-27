package com.tecsup.avisoscampus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.ui.components.EstadoVacio
import com.tecsup.avisoscampus.ui.components.EtiquetaEstado
import com.tecsup.avisoscampus.ui.components.TarjetaSeccion
import com.tecsup.avisoscampus.ui.components.TipoEtiqueta
import com.tecsup.avisoscampus.ui.theme.Dimens

/**
 * Tarjeta Historial: los 20 avisos más recientes como línea de tiempo, lo nuevo arriba.
 * No conoce el ViewModel: recibe la lista y avisa con onLimpiar. Usa Column con forEach
 * porque la pantalla ya se desplaza y una LazyColumn dentro daría error.
 */
@Composable
fun HistorialAvisos(avisos: List<Aviso>, onLimpiar: () -> Unit) {
    TarjetaSeccion(
        icono = R.drawable.ic_historial,
        titulo = "Historial",
        subtitulo = if (avisos.isEmpty()) "Se guarda aunque cierres la app"
        else if (avisos.size == 1) "1 aviso guardado" else "${avisos.size} avisos guardados"
    ) {
        if (avisos.isEmpty()) {
            EstadoVacio(R.drawable.ic_campana, "Sin avisos todavía")
        } else {
            val visibles = avisos.take(20)
            Column {
                visibles.forEachIndexed { i, aviso ->
                    FilaLineaTiempo(aviso, esUltima = i == visibles.lastIndex)
                }
            }
            TextButton(onClick = onLimpiar, modifier = Modifier.align(Alignment.End)) {
                Text("Borrar historial")
            }
        }
    }
}

/** Una fila de la línea de tiempo: punto del color del origen, línea y contenido. */
@Composable
private fun FilaLineaTiempo(aviso: Aviso, esUltima: Boolean) {
    val colorPunto = colorDeOrigen(aviso.origen)
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .padding(top = Dimens.xs)
                    .size(Dimens.puntoLinea)
                    .background(colorPunto, CircleShape)
            )
            if (!esUltima) {
                Box(
                    modifier = Modifier
                        .width(Dimens.lineaTiempo)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outlineVariant)
                )
            }
        }
        Column(
            modifier = Modifier.padding(start = Dimens.md, bottom = Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.xs)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
            ) {
                EtiquetaEstado(aviso.origen.texto, tipoDeOrigen(aviso.origen))
                if (aviso.urgente) EtiquetaEstado("Urgente", TipoEtiqueta.ERROR)
                Text(
                    aviso.hora,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(aviso.titulo, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(aviso.texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun colorDeOrigen(origen: Origen): Color = when (origen) {
    Origen.BOTON -> MaterialTheme.colorScheme.primary
    Origen.RECORDATORIO -> MaterialTheme.colorScheme.tertiary
    Origen.MODO_AVION -> MaterialTheme.colorScheme.secondary
}

private fun tipoDeOrigen(origen: Origen): TipoEtiqueta = when (origen) {
    Origen.BOTON -> TipoEtiqueta.PRIMARIA
    Origen.RECORDATORIO -> TipoEtiqueta.ACENTO
    Origen.MODO_AVION -> TipoEtiqueta.NEUTRA
}

package com.tecsup.avisoscampus.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.ui.theme.Dimens
import com.tecsup.avisoscampus.ui.theme.FormaCabecera
import com.tecsup.avisoscampus.ui.theme.FormaPildora
import com.tecsup.avisoscampus.ui.theme.avisos

/**
 * Cabecera con degradado índigo: nombre de la app, campana con el número de avisos
 * guardados y un resumen del estado (permiso y modo avión).
 */
@Composable
fun CabeceraAvisos(totalAvisos: Int, estadoPermiso: String, escuchandoAvion: Boolean) {
    val degradado = Brush.verticalGradient(
        listOf(MaterialTheme.avisos.cabeceraInicio, MaterialTheme.avisos.cabeceraFin)
    )
    val sobre = MaterialTheme.colorScheme.onPrimary
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(degradado, FormaCabecera)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = Dimens.xl, vertical = Dimens.xl),
        verticalArrangement = Arrangement.spacedBy(Dimens.lg)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "CENTRO DE AVISOS",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.avisos.ambar
                )
                Text("AvisosCampus", style = MaterialTheme.typography.headlineMedium, color = sobre)
                Text(
                    "Tus avisos del campus en un solo lugar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = sobre.copy(alpha = 0.8f)
                )
            }
            Box {
                Box(
                    modifier = Modifier
                        .size(Dimens.campana)
                        .background(sobre.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painterResource(R.drawable.ic_campana),
                        contentDescription = "Avisos guardados",
                        tint = sobre,
                        modifier = Modifier.size(Dimens.xxl)
                    )
                }
                // Insignia con el total de avisos del historial
                Text(
                    "$totalAvisos",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = Dimens.xs, y = -Dimens.xs)
                        .background(MaterialTheme.avisos.ambar, FormaPildora)
                        .padding(horizontal = Dimens.sm, vertical = Dimens.xs / 2)
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.sm)) {
            ResumenCabecera("Permiso: $estadoPermiso")
            ResumenCabecera(if (escuchandoAvion) "Modo avión: escuchando" else "Modo avión: apagado")
        }
    }
}

@Composable
private fun ResumenCabecera(texto: String) {
    val sobre = MaterialTheme.colorScheme.onPrimary
    Text(
        texto,
        style = MaterialTheme.typography.labelMedium,
        color = sobre,
        modifier = Modifier
            .background(sobre.copy(alpha = 0.15f), FormaPildora)
            .padding(horizontal = Dimens.md, vertical = Dimens.xs)
    )
}

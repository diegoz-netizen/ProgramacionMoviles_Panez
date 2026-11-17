package com.panez.campusgps.ui

import com.panez.campusgps.ui.theme.Dimens
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.panez.campusgps.model.TipoError
import com.panez.campusgps.model.UbicacionUi

// Recuadro de error con su acción: Abrir ajustes, Reintentar o ninguna (sin Play Services)
@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onAbrirAjustes: () -> Unit,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.lg)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.errorContainer,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = error.mensaje,
                modifier = Modifier.padding(horizontal = Dimens.lg, vertical = Dimens.md),
                color = MaterialTheme.colorScheme.onErrorContainer,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        when (error.tipo) {
            TipoError.SIN_PERMISO -> BotonAccion("Abrir ajustes", onAbrirAjustes)
            TipoError.GPS_APAGADO, TipoError.OTRO -> BotonAccion("Reintentar", onReintentar)
            TipoError.SIN_PLAY_SERVICES -> Unit
        }
    }
}

@Composable
private fun BotonAccion(texto: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Text(texto)
    }
}

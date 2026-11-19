package com.panez.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
fun SeccionTarea(vm: TareasViewModel) {
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    val pasos = (progreso * 100).toInt()

    PanelControl {
        EncabezadoPanel("Tarea con progreso", "Avance recibido desde el Repository")
        ChipEstado(mensaje, tonoDelMensaje(mensaje, corriendo))
        IndicadorAvance(fraccion = progreso, valor = "$pasos%", etiqueta = "Avance")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            MetricaControl("Pasos", "$pasos/100", Modifier.weight(1f))
            MetricaControl(
                "Segundo plano",
                if (corriendo) "Default" else "Inactivo",
                Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            BotonPrincipal("Iniciar", vm::tareaLarga, Modifier.weight(1f), habilitado = !corriendo)
            BotonSecundario("Cancelar", vm::cancelar, Modifier.weight(1f), habilitado = corriendo)
        }
    }
}

private fun tonoDelMensaje(mensaje: String, corriendo: Boolean): TonoEstado = when {
    corriendo -> TonoEstado.Activo
    mensaje == "Terminó" -> TonoEstado.Exito
    mensaje == "Cancelado" -> TonoEstado.Advertencia
    else -> TonoEstado.Neutro
}

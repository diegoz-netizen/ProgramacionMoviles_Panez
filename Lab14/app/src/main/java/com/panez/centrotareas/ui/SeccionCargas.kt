package com.panez.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.panez.centrotareas.model.CargaUi
import com.panez.centrotareas.ui.components.BotonPrincipal
import com.panez.centrotareas.ui.components.BotonSecundario
import com.panez.centrotareas.ui.components.ChipEstado
import com.panez.centrotareas.ui.components.EncabezadoPanel
import com.panez.centrotareas.ui.components.FilaInterruptor
import com.panez.centrotareas.ui.components.IndicadorAvance
import com.panez.centrotareas.ui.components.MetricaControl
import com.panez.centrotareas.ui.components.PanelControl
import com.panez.centrotareas.ui.components.RecuadroAlerta
import com.panez.centrotareas.ui.components.TonoEstado
import com.panez.centrotareas.ui.theme.Dimens
import com.panez.centrotareas.viewmodel.TareasViewModel

@Composable
fun SeccionCargas(vm: TareasViewModel) {
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val tiempoMaximoActivo by vm.tiempoMaximoActivo.collectAsState()
    val cargando = carga is CargaUi.Cargando

    PanelControl {
        EncabezadoPanel("Dos cargas", "Compara el tiempo de cada modo")
        FilaInterruptor(
            titulo = "Simular error",
            descripcion = "Los pedidos fallan sin conexión",
            activado = simularError,
            habilitado = !cargando,
            alCambiar = vm::cambiarSimularError
        )
        FilaInterruptor(
            titulo = "Tiempo máximo",
            descripcion = "Límite de 2 s por carga",
            activado = tiempoMaximoActivo,
            habilitado = !cargando,
            alCambiar = vm::cambiarTiempoMaximo
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.sm)
        ) {
            BotonSecundario(
                "Una por una",
                vm::cargarUnaPorUna,
                Modifier.weight(1f),
                habilitado = !cargando
            )
            BotonPrincipal(
                "A la vez",
                vm::cargarALaVez,
                Modifier.weight(1f),
                habilitado = !cargando
            )
        }
    }

    when (val estado = carga) {
        CargaUi.Inicial -> Unit
        CargaUi.Cargando -> PanelControl {
            IndicadorAvance(fraccion = null, valor = "...", etiqueta = "Cargando datos")
        }
        is CargaUi.Exito -> PanelControl {
            ChipEstado("Modo: ${estado.modo}", TonoEstado.Exito)
            MetricaControl("Tiempo", "${estado.milisegundos} ms", Modifier.fillMaxWidth())
            MetricaControl("Perfil", estado.perfil, Modifier.fillMaxWidth())
            MetricaControl("Pedidos", "${estado.pedidos}", Modifier.fillMaxWidth())
        }
        is CargaUi.Error -> PanelControl {
            RecuadroAlerta(estado.mensaje)
            BotonSecundario("Reintentar", vm::cargarALaVez, Modifier.fillMaxWidth())
        }
    }
}

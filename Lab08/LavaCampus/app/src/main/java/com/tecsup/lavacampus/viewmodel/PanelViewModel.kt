package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.ui.components.graficos.DatoGrafico
import com.tecsup.lavacampus.ui.theme.PaletaGraficos
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Panel de la lavandería: contadores, ingresos y datos para los gráficos. */
class PanelViewModel(repo: LavaRepository) : ViewModel() {

    private val hoy = LocalDate.now()

    /** Lunes de esta semana (si hoy es domingo, el lunes anterior). */
    private val inicioSemana = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toString()

    /** Pedidos registrados hoy, sin contar los cancelados. */
    val pedidosHoy: StateFlow<Int> = repo.observarPedidosDeFecha(hoy.toString())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val enProceso: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoPedido.EN_PROCESO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Pedidos LISTO esperando que el estudiante los recoja. */
    val porRecoger: StateFlow<Int> = repo.observarCantidadPorEstado(EstadoPedido.LISTO)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Pedidos con la fecha de entrega vencida que aún no se entregan. */
    val atrasados: StateFlow<Int> = repo.observarIdsAtrasados(hoy.toString())
        .map { it.size }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    /** Suma del total de pedidos pagados desde el lunes. */
    val ingresosSemana: StateFlow<Double> = repo.observarIngresosDesde(inicioSemana)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    /** Dona: pedidos por estado (activos y entregados), cada uno con el color de su estado. */
    val porEstado: StateFlow<List<DatoGrafico>> = combine(
        repo.observarCantidadPorEstado(EstadoPedido.RECIBIDO),
        repo.observarCantidadPorEstado(EstadoPedido.EN_PROCESO),
        repo.observarCantidadPorEstado(EstadoPedido.LISTO),
        repo.observarCantidadPorEstado(EstadoPedido.ENTREGADO)
    ) { recibido, proceso, listo, entregado ->
        listOf(
            EstadoPedido.RECIBIDO to recibido,
            EstadoPedido.EN_PROCESO to proceso,
            EstadoPedido.LISTO to listo,
            EstadoPedido.ENTREGADO to entregado
        ).map { (estado, cantidad) -> DatoGrafico(estado.etiqueta, cantidad.toFloat(), estado.color) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Barras: ingresos de la semana por servicio. */
    val porServicio: StateFlow<List<DatoGrafico>> = repo.observarIngresosPorServicio(inicioSemana)
        .map { lista ->
            lista.mapIndexed { i, ingreso ->
                DatoGrafico(ingreso.nombre, ingreso.total.toFloat(), PaletaGraficos[i % PaletaGraficos.size])
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

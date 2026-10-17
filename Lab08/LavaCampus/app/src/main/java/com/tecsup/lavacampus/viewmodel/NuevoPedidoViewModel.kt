package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.LineaPedido
import com.tecsup.lavacampus.model.Unidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate

/** Una línea elegida, lista para mostrarse en el resumen. */
data class LineaResumen(val servicio: Servicio, val cantidad: Double, val subtotal: Double)

/** Reglas de cantidad según cómo se cobra el servicio. */
data class ReglaCantidad(val paso: Double, val minimo: Double, val maximo: Double)

fun Servicio.reglaCantidad(): ReglaCantidad =
    if (unidad == Unidad.KG.name) ReglaCantidad(paso = 0.5, minimo = 1.0, maximo = 10.0)
    else ReglaCantidad(paso = 1.0, minimo = 1.0, maximo = 30.0)

class NuevoPedidoViewModel(private val repo: LavaRepository) : ViewModel() {

    private var usuarioId: Int = 0

    fun cargar(id: Int) {
        usuarioId = id
    }

    /** Solo servicios ACTIVOS: uno desactivado por el administrador desaparece de aquí. */
    val servicios: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Cantidad elegida por servicio (servicioId -> cantidad). Sin entrada = apagado. */
    private val _cantidades = MutableStateFlow<Map<Int, Double>>(emptyMap())
    val cantidades: StateFlow<Map<Int, Double>> = _cantidades.asStateFlow()

    private val _nota = MutableStateFlow("")
    val nota: StateFlow<String> = _nota.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    /** Id del pedido creado; la pantalla lo observa para volver a Mis pedidos. */
    private val _pedidoCreado = MutableStateFlow<Long?>(null)
    val pedidoCreado: StateFlow<Long?> = _pedidoCreado.asStateFlow()

    /** Líneas elegidas (solo de servicios activos), recalculadas en cada cambio. */
    val lineas: StateFlow<List<LineaResumen>> = combine(servicios, _cantidades) { lista, cant ->
        lista.mapNotNull { s ->
            val c = cant[s.id] ?: 0.0
            if (c > 0) LineaResumen(s, c, c * s.precio) else null
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** total = suma de cantidad x precio. */
    val total: StateFlow<Double> = combine(servicios, _cantidades) { lista, cant ->
        lista.sumOf { s -> (cant[s.id] ?: 0.0) * s.precio }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    /** Hoy + el mayor tiempo de entrega de los servicios elegidos (24 h = 1 día); el domingo pasa al lunes. */
    val fechaEntrega: StateFlow<LocalDate?> = combine(servicios, _cantidades) { lista, cant ->
        val horas = lista.filter { (cant[it.id] ?: 0.0) > 0 }.maxOfOrNull { it.horasEntrega }
        horas?.let { calcularEntrega(LocalDate.now(), it) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /**
     * Sube o baja la cantidad de un servicio en [delta] (el paso de su unidad).
     * Al encenderse arranca en el mínimo; si baja del mínimo se apaga (0); nunca pasa del máximo.
     */
    fun cambiarCantidad(servicioId: Int, delta: Double) {
        val servicio = servicios.value.firstOrNull { it.id == servicioId } ?: return
        val regla = servicio.reglaCantidad()
        _cantidades.update { actual ->
            val c = actual[servicioId] ?: 0.0
            val nueva = when {
                delta > 0 && c == 0.0 -> regla.minimo
                delta > 0 -> minOf(c + delta, regla.maximo)
                c + delta < regla.minimo -> 0.0
                else -> c + delta
            }
            if (nueva == 0.0) actual - servicioId else actual + (servicioId to nueva)
        }
        _mensaje.value = null
    }

    fun cambiarNota(texto: String) {
        _nota.value = texto
    }

    fun confirmar() {
        val elegidas = lineas.value
        val entrega = fechaEntrega.value
        if (elegidas.isEmpty() || entrega == null || _guardando.value) return

        viewModelScope.launch {
            _guardando.value = true
            if (repo.contarActivos(usuarioId) >= MAX_ACTIVOS) {
                _mensaje.value = "Ya tienes $MAX_ACTIVOS pedidos activos"
                _guardando.value = false
                return@launch
            }
            val id = repo.crearPedido(
                usuarioId = usuarioId,
                lineas = elegidas.map { LineaPedido(it.servicio.id, it.cantidad) },
                fechaEntrega = entrega.toString(),
                nota = _nota.value.trim()
            )
            _guardando.value = false
            _pedidoCreado.value = id
        }
    }

    companion object {
        const val MAX_ACTIVOS = 3

        fun calcularEntrega(desde: LocalDate, horas: Int): LocalDate {
            var f = desde.plusDays((horas / 24).toLong())
            if (f.dayOfWeek == DayOfWeek.SUNDAY) f = f.plusDays(1)
            return f
        }
    }
}

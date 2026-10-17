package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Gestión de un pedido por el administrador: máquina de estados con condiciones.
 * RECIBIDO -> EN_PROCESO -> LISTO (exige casillero libre) -> ENTREGADO (exige pagado).
 */
@OptIn(ExperimentalCoroutinesApi::class)
class GestionPedidoViewModel(private val repo: LavaRepository) : ViewModel() {

    private val pedidoId = MutableStateFlow<Int?>(null)

    fun cargar(id: Int) {
        if (pedidoId.value != id) {
            pedidoId.value = id
            _casillero.value = null
            _mensaje.value = null
        }
    }

    val pedido: StateFlow<PedidoConDetalle?> = pedidoId
        .filterNotNull()
        .flatMapLatest { repo.observarPedido(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Casilleros usados por OTROS pedidos LISTO (el casillero actual del propio pedido no cuenta). */
    val casillerosOcupados: StateFlow<Set<String>> = combine(repo.observarCasillerosOcupados(), pedido) { ocupados, p ->
        ocupados.toSet() - setOfNotNull(p?.pedido?.casillero)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    private val _casillero = MutableStateFlow<String?>(null)
    /** Casillero elegido en pantalla (si no se eligió, se muestra el ya asignado). */
    val casilleroElegido: StateFlow<String?> = _casillero.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun elegirCasillero(c: String) {
        if (c in casillerosOcupados.value) return
        _casillero.value = c
        _mensaje.value = null
    }

    fun marcarPagado(pagado: Boolean) {
        val p = pedido.value?.pedido ?: return
        if (EstadoPedido.valueOf(p.estado).esFinal) return
        viewModelScope.launch {
            repo.marcarPagado(p.id, pagado)
            _mensaje.value = null
        }
    }

    /** Avanza solo al estado siguiente y nunca retrocede. Si una condición falla, muestra el motivo. */
    fun avanzar() {
        val p = pedido.value?.pedido ?: return
        val siguiente = EstadoPedido.valueOf(p.estado).siguiente() ?: return
        viewModelScope.launch {
            when (siguiente) {
                EstadoPedido.LISTO -> {
                    val c = _casillero.value ?: p.casillero
                    when {
                        c == null -> { _mensaje.value = "Elige un casillero para marcar el pedido como listo"; return@launch }
                        c in casillerosOcupados.value -> { _mensaje.value = "El casillero $c ya está ocupado"; return@launch }
                    }
                    repo.cambiarEstado(p.id, EstadoPedido.LISTO, c)
                }
                EstadoPedido.ENTREGADO -> {
                    if (!p.pagado) {
                        _mensaje.value = "No se puede entregar: primero marca el pedido como cobrado"
                        return@launch
                    }
                    repo.cambiarEstado(p.id, EstadoPedido.ENTREGADO, p.casillero)
                }
                else -> repo.cambiarEstado(p.id, siguiente, p.casillero)
            }
            _mensaje.value = null
        }
    }

    /** El administrador cancela solo desde RECIBIDO o EN_PROCESO (nunca LISTO ni ENTREGADO). */
    fun puedeCancelar(estado: EstadoPedido): Boolean =
        estado == EstadoPedido.RECIBIDO || estado == EstadoPedido.EN_PROCESO

    fun cancelar() {
        val p = pedido.value?.pedido ?: return
        val estado = EstadoPedido.valueOf(p.estado)
        viewModelScope.launch {
            if (!puedeCancelar(estado)) {
                _mensaje.value = "Un pedido ${estado.etiqueta.lowercase()} ya no se puede cancelar"
                return@launch
            }
            repo.cambiarEstado(p.id, EstadoPedido.CANCELADO, null)
            _mensaje.value = null
        }
    }
}

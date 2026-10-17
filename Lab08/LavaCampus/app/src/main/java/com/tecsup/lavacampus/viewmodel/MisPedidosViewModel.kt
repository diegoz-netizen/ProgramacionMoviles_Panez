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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Pedidos del estudiante separados en Activos e Historial. */
@OptIn(ExperimentalCoroutinesApi::class)
class MisPedidosViewModel(private val repo: LavaRepository) : ViewModel() {

    private val usuarioId = MutableStateFlow<Int?>(null)

    fun cargar(id: Int) {
        usuarioId.value = id
    }

    private val pedidos = usuarioId
        .filterNotNull()
        .flatMapLatest { repo.observarPedidosDeUsuario(it) }

    /** RECIBIDO, EN_PROCESO y LISTO. */
    val activos: StateFlow<List<PedidoConDetalle>> = pedidos
        .map { lista -> lista.filter { !EstadoPedido.valueOf(it.pedido.estado).esFinal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** ENTREGADO y CANCELADO. */
    val historial: StateFlow<List<PedidoConDetalle>> = pedidos
        .map { lista -> lista.filter { EstadoPedido.valueOf(it.pedido.estado).esFinal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /** Misma regla del detalle: solo se cancela mientras está RECIBIDO. */
    fun puedeCancelar(p: PedidoConDetalle): Boolean = p.pedido.estado == EstadoPedido.RECIBIDO.name

    fun cancelar(p: PedidoConDetalle) {
        viewModelScope.launch {
            if (!puedeCancelar(p)) {
                _mensaje.value = "Solo se cancela antes de empezar el lavado"
                return@launch
            }
            repo.cambiarEstado(p.pedido.id, EstadoPedido.CANCELADO, null)
            _mensaje.value = null
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}

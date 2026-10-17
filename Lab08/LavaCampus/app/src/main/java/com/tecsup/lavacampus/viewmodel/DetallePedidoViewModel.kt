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

/** Detalle de un pedido del estudiante. Si el administrador cambia el estado, se actualiza solo. */
@OptIn(ExperimentalCoroutinesApi::class)
class DetallePedidoViewModel(private val repo: LavaRepository) : ViewModel() {

    private val pedidoId = MutableStateFlow<Int?>(null)

    fun cargar(id: Int) {
        pedidoId.value = id
    }

    val pedido: StateFlow<PedidoConDetalle?> = pedidoId
        .filterNotNull()
        .flatMapLatest { repo.observarPedido(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Regla: el estudiante solo cancela mientras está RECIBIDO (aún no empezó el lavado). */
    val puedeCancelar: StateFlow<Boolean> = pedido
        .map { it?.pedido?.estado == EstadoPedido.RECIBIDO.name }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    fun cancelar() {
        val actual = pedido.value?.pedido ?: return
        viewModelScope.launch {
            // Se vuelve a validar: el administrador pudo avanzarlo mientras la pantalla estaba abierta
            if (actual.estado != EstadoPedido.RECIBIDO.name) {
                _mensaje.value = "Solo se cancela antes de empezar el lavado"
                return@launch
            }
            repo.cambiarEstado(actual.id, EstadoPedido.CANCELADO, null)
            _mensaje.value = null
        }
    }
}

package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.EstadoPedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Chips de filtro de la bandeja. null = Todos. */
enum class FiltroBandeja(val etiqueta: String, val estado: EstadoPedido?) {
    TODOS("Todos", null),
    RECIBIDO("Recibido", EstadoPedido.RECIBIDO),
    EN_PROCESO("En proceso", EstadoPedido.EN_PROCESO),
    LISTO("Listo", EstadoPedido.LISTO)
}

/** Bandeja del administrador: todos los pedidos con filtro por estado y buscador. */
class BandejaViewModel(private val repo: LavaRepository) : ViewModel() {

    private val _filtro = MutableStateFlow(FiltroBandeja.TODOS)
    val filtro: StateFlow<FiltroBandeja> = _filtro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /** Pedidos filtrados por el chip elegido y por el texto (nombre del estudiante o número). */
    val pedidos: StateFlow<List<PedidoConDetalle>> = combine(
        repo.observarPedidos(), _filtro, _busqueda
    ) { lista, filtro, texto ->
        val t = texto.trim().removePrefix("#")
        lista.filter { p ->
            (filtro.estado == null || p.pedido.estado == filtro.estado.name) &&
                (t.isEmpty() || p.usuario.nombre.contains(t, ignoreCase = true) || p.pedido.id.toString() == t)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun elegirFiltro(f: FiltroBandeja) {
        _filtro.value = f
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    /** "Iniciar": RECIBIDO -> EN_PROCESO (no tiene condiciones). */
    fun iniciar(p: PedidoConDetalle) {
        if (p.pedido.estado != EstadoPedido.RECIBIDO.name) return
        viewModelScope.launch {
            repo.cambiarEstado(p.pedido.id, EstadoPedido.EN_PROCESO, null)
            _mensaje.value = null
        }
    }

    /** "Entregar": LISTO -> ENTREGADO, solo si ya está pagado. */
    fun entregar(p: PedidoConDetalle) {
        if (p.pedido.estado != EstadoPedido.LISTO.name) return
        viewModelScope.launch {
            if (!p.pedido.pagado) {
                _mensaje.value = "El pedido #${p.pedido.id} no está cobrado. Márcalo como cobrado antes de entregar"
                return@launch
            }
            repo.cambiarEstado(p.pedido.id, EstadoPedido.ENTREGADO, p.pedido.casillero)
            _mensaje.value = null
        }
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}

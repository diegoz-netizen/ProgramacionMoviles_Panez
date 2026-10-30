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
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import java.time.LocalTime

/** Inicio del estudiante: su pedido activo con el seguimiento y sus últimos pedidos. */
@OptIn(ExperimentalCoroutinesApi::class)
class InicioViewModel(private val repo: LavaRepository) : ViewModel() {

    private val usuarioId = MutableStateFlow<Int?>(null)

    private val _saludo = MutableStateFlow(saludoSegunHora(LocalTime.now().hour))
    /** "Buenos días", "Buenas tardes" o "Buenas noches" según la hora del celular. */
    val saludo: StateFlow<String> = _saludo.asStateFlow()

    /** Recibe el estudiante actual. Si cambia (otro perfil), la lista se vuelve a observar. */
    fun cargar(id: Int) {
        usuarioId.value = id
        _saludo.value = saludoSegunHora(LocalTime.now().hour)
    }

    private val pedidos = usuarioId
        .filterNotNull()
        .flatMapLatest { repo.observarPedidosDeUsuario(it) } // ya vienen del más nuevo al más antiguo

    /** El más reciente que sigue en curso: RECIBIDO, EN_PROCESO o LISTO. */
    val pedidoActivo: StateFlow<PedidoConDetalle?> = pedidos
        .map { lista -> lista.firstOrNull { EstadoPedido.valueOf(it.pedido.estado) in ACTIVOS } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    /** Los 2 últimos pedidos, en cualquier estado. */
    val recientes: StateFlow<List<PedidoConDetalle>> = pedidos
        .map { it.take(2) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    companion object {
        /** De 5 a 11: días · de 12 a 18: tardes · el resto: noches. */
        fun saludoSegunHora(hora: Int): String = when (hora) {
            in 5..11 -> "Buenos días"
            in 12..18 -> "Buenas tardes"
            else -> "Buenas noches"
        }

        val ACTIVOS = setOf(EstadoPedido.RECIBIDO, EstadoPedido.EN_PROCESO, EstadoPedido.LISTO)
    }
}

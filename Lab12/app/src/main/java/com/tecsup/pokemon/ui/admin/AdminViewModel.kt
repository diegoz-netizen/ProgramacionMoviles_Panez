package com.tecsup.pokemon.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.notification.Notificador
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUi(
    val totalGuardado: Int = 0,
    val totalFavoritos: Int = 0,
    val sincronizando: Boolean = false,
    val mensaje: String? = null
)

class AdminViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {
    private val _ui = MutableStateFlow(AdminUi())
    val ui: StateFlow<AdminUi> = _ui.asStateFlow()

    init {
        actualizarTotales()
    }

    fun sincronizar() {
        _ui.update { it.copy(sincronizando = true, mensaje = null) }
        viewModelScope.launch {
            repo.refrescar()
                .onSuccess { cantidad ->
                    notificador.mostrar("Pokédex", "Se guardaron $cantidad elementos")
                    _ui.update { it.copy(mensaje = "Sincronizado: $cantidad elementos") }
                }
                .onFailure { _ui.update { u -> u.copy(mensaje = "No se pudo sincronizar") } }
            _ui.update { it.copy(sincronizando = false) }
            actualizarTotales()
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            repo.limpiarCache()
            _ui.update { it.copy(mensaje = "Caché limpiada") }
            actualizarTotales()
        }
    }

    fun mensajeMostrado() {
        _ui.update { it.copy(mensaje = null) }
    }

    private fun actualizarTotales() {
        viewModelScope.launch {
            val guardados = repo.cantidadGuardada()
            val favoritos = repo.cantidadFavoritos()
            _ui.update { it.copy(totalGuardado = guardados, totalFavoritos = favoritos) }
        }
    }
}

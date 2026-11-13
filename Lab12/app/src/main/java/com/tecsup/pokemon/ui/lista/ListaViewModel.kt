package com.tecsup.pokemon.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.UiState
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import retrofit2.HttpException

class ListaViewModel(
    private val repo: ItemRepository,
    private val notificador: Notificador
) : ViewModel() {

    private val _estado = MutableStateFlow<UiState<List<Item>>>(UiState.Cargando)
    val estado: StateFlow<UiState<List<Item>>> = _estado.asStateFlow()

    // Aviso cuando no hay internet pero sí datos guardados en Room
    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    // Texto de la barra de búsqueda. Filtra lo que sale de Room; no vuelve a llamar a la API
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // Tipo elegido en el filtro (null = todos) y tipos que hay en Room para mostrar en el filtro
    private val _filtroTipo = MutableStateFlow<String?>(null)
    val filtroTipo: StateFlow<String?> = _filtroTipo.asStateFlow()
    private val _tiposDisponibles = MutableStateFlow<List<String>>(emptyList())
    val tiposDisponibles: StateFlow<List<String>> = _tiposDisponibles.asStateFlow()

    // Evita mostrar "vacío" antes de que responda la API la primera vez
    private var refrescoTerminado = false

    init {
        viewModelScope.launch {
            combine(repo.items, _busqueda, _filtroTipo) { lista, texto, tipo -> Triple(lista, texto, tipo) }
                .collect { (lista, texto, tipo) ->
                    _tiposDisponibles.value = lista.map { it.tipo }.filter { it.isNotBlank() }.distinct().sorted()
                    if (lista.isNotEmpty() || refrescoTerminado) _estado.value = UiState.Exito(filtrar(lista, texto, tipo))
                }
        }
        refrescar()
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    /** Elige un tipo del filtro; si se toca el mismo otra vez (o null) se ven todos. */
    fun filtrarPorTipo(tipo: String?) {
        _filtroTipo.value = if (tipo == _filtroTipo.value) null else tipo
    }

    /** Coincidencia por nombre, sin importar mayúsculas: "char" encuentra a Charizard y Charmander. */
    private fun filtrar(lista: List<Item>, texto: String, tipo: String?): List<Item> {
        val buscado = texto.trim()
        return lista
            .filter { tipo == null || it.tipo == tipo }
            .filter { buscado.isEmpty() || it.titulo.contains(buscado, ignoreCase = true) }
    }

    fun refrescar() {
        if (_estado.value is UiState.Error) _estado.value = UiState.Cargando
        viewModelScope.launch {
            val resultado = repo.refrescar()
            refrescoTerminado = true
            resultado
                .onSuccess { n ->
                    if (_estado.value is UiState.Cargando) _estado.value = UiState.Exito(emptyList())
                    _aviso.value = null
                    notificador.mostrar("Datos actualizados", "Se guardaron $n elementos")
                }
                .onFailure { e ->
                    // Sin nada guardado: pantalla de error con Reintentar. Con datos: se muestran con un aviso.
                    if (repo.cantidadGuardada() == 0) _estado.value = UiState.Error(mensajeDe(e))
                    else _aviso.value = "Sin conexión: mostrando datos guardados"
                }
        }
    }

    fun alternarFavorito(id: String) {
        viewModelScope.launch { repo.alternarFavorito(id) }
    }

    private fun mensajeDe(e: Throwable) = when (e) {
        is IOException -> "No hay conexión a internet"
        is HttpException -> "El servidor respondió con error ${e.code()}"
        else -> "Ocurrió un error inesperado"
    }
}

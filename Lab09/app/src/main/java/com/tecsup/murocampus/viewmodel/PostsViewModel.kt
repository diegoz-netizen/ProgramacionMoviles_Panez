package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** El detalle: la publicación y sus comentarios. */
data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

/** Solo conoce la interface PostRepository: nada de Retrofit aquí. */
class PostsViewModel(private val repo: PostRepository) : ViewModel() {

    private val _lista = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val lista: StateFlow<UiState<List<Post>>> = _lista.asStateFlow()

    // null significa Todos
    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    // Texto del campo de búsqueda
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    /** La lista ya descargada, filtrada por título en memoria: buscar no hace otra llamada. */
    val listaVisible: StateFlow<UiState<List<Post>>> =
        combine(_lista, _busqueda) { estado, texto ->
            val buscado = texto.trim()
            if (estado is UiState.Exito && buscado.isNotEmpty()) {
                UiState.Exito(estado.datos.filter { it.title.contains(buscado, ignoreCase = true) })
            } else {
                estado
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiState.Cargando)

    init { cargar() }

    /** Guarda lo que se escribe en el buscador. */
    fun buscar(texto: String) {
        _busqueda.value = texto
    }

    /** Carga la lista respetando el filtro actual (también lo usa Reintentar). */
    fun cargar() {
        val userId = _usuarioFiltro.value
        viewModelScope.launch {
            _lista.value = UiState.Cargando
            val resultado = if (userId == null) repo.listar() else repo.porUsuario(userId)
            resultado
                .onSuccess { _lista.value = UiState.Exito(it) }
                .onFailure { _lista.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Guarda el filtro y vuelve a cargar. */
    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    /** Pide la publicación y sus comentarios a la vez; si una falla, todo el detalle es Error. */
    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            val resultado = coroutineScope {
                val post = async { repo.detalle(id) }
                val comentarios = async { repo.comentarios(id) }
                post.await().mapCatching { DetalleUi(it, comentarios.await().getOrThrow()) }
            }
            resultado
                .onSuccess { _detalle.value = UiState.Exito(it) }
                .onFailure { _detalle.value = UiState.Error(mensajeDe(it)) }
        }
    }

    // null: todavía no se envió nada
    private val _envio = MutableStateFlow<UiState<Post>?>(null)
    val envio: StateFlow<UiState<Post>?> = _envio.asStateFlow()

    /** Envía la publicación nueva con POST. */
    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _envio.value = UiState.Cargando
            repo.crear(NuevoPost(userId = 1, title = titulo, body = texto))
                .onSuccess { _envio.value = UiState.Exito(it) }
                .onFailure { _envio.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Al salir del formulario, se olvida el último envío. */
    fun limpiarEnvio() {
        _envio.value = null
    }

    // null: no se pidió borrar; Exito guarda el id borrado
    private val _borrado = MutableStateFlow<UiState<Int>?>(null)
    val borrado: StateFlow<UiState<Int>?> = _borrado.asStateFlow()

    /** Borra con DELETE y, si sale bien, quita la publicación de la lista sin volver a descargarla. */
    fun borrar(id: Int) {
        viewModelScope.launch {
            _borrado.value = UiState.Cargando
            repo.borrar(id)
                .onSuccess {
                    val actual = _lista.value
                    if (actual is UiState.Exito) {
                        _lista.value = UiState.Exito(actual.datos.filterNot { it.id == id })
                    }
                    _borrado.value = UiState.Exito(id)
                }
                .onFailure { _borrado.value = UiState.Error(mensajeDe(it)) }
        }
    }

    /** Al salir del detalle, se olvida el último borrado. */
    fun limpiarBorrado() {
        _borrado.value = null
    }
}

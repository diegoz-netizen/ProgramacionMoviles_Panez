package com.tecsup.pokemon

import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.domain.model.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

// Repositorio falso en memoria: permite probar los ViewModel sin red ni Room
class FakeItemRepository(inicial: List<Item> = emptyList()) : ItemRepository {
    val guardados = MutableStateFlow(inicial)
    var resultadoRefrescar: Result<Int> = Result.success(0)
    var resultadoDetalle: Result<Item> = Result.failure(IllegalStateException("sin configurar"))
    val favoritosAlternados = mutableListOf<String>()

    override val items = guardados
    override val favoritos = guardados.map { lista -> lista.filter { it.esFavorito } }

    override suspend fun refrescar() = resultadoRefrescar

    override suspend fun obtenerDetalle(id: String) = resultadoDetalle

    override suspend fun alternarFavorito(id: String) {
        favoritosAlternados += id
        guardados.value = guardados.value.map {
            if (it.id == id) it.copy(esFavorito = !it.esFavorito) else it
        }
    }

    override suspend fun cantidadGuardada() = guardados.value.size

    override suspend fun cantidadFavoritos() = guardados.value.count { it.esFavorito }

    override suspend fun limpiarCache() {
        guardados.value = emptyList()
    }
}

fun unItem(nombre: String, favorito: Boolean = false) =
    Item(id = nombre, titulo = nombre.replaceFirstChar { it.uppercase() }, subtitulo = "fire", imagenUrl = null, esFavorito = favorito)

package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.domain.model.Item
import kotlinx.coroutines.flow.Flow

/**
 * Única puerta a los Pokémon. Room es la fuente de verdad: items y favoritos salen de Room
 * y Retrofit solo los actualiza con refrescar(). El id es el nombre del Pokémon (String).
 */
interface ItemRepository {
    /** Sale de Room. */
    val items: Flow<List<Item>>

    val favoritos: Flow<List<Item>>

    /** Retrofit -> Room; devuelve cuántos guardó. */
    suspend fun refrescar(): Result<Int>

    suspend fun obtenerDetalle(id: String): Result<Item>

    suspend fun alternarFavorito(id: String)

    suspend fun cantidadGuardada(): Int

    suspend fun cantidadFavoritos(): Int

    suspend fun limpiarCache()
}

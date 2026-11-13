package com.tecsup.pokemon.data.repository

import com.tecsup.pokemon.data.local.FavoritoEntity
import com.tecsup.pokemon.data.local.ItemDao
import com.tecsup.pokemon.data.mapper.aEntity
import com.tecsup.pokemon.data.mapper.aModelo
import com.tecsup.pokemon.data.remote.ApiService
import com.tecsup.pokemon.domain.model.Item
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import retrofit2.HttpException

/**
 * Room es la fuente única de verdad: la lista y los favoritos salen siempre de la base de datos.
 * Retrofit solo la actualiza en refrescar().
 */
class ItemRepositoryImpl(
    private val api: ApiService,
    private val dao: ItemDao,
    /** UID de la cuenta con sesión (o null). Los favoritos se guardan por cuenta. */
    private val uidActual: () -> String?
) : ItemRepository {

    private fun uid() = uidActual().orEmpty()

    // get(): se arma al pedirlo, así cada sesión ve los favoritos de su propia cuenta
    override val items: Flow<List<Item>>
        get() = dao.todos(uid()).map { lista -> lista.map { it.aModelo() } }
    override val favoritos: Flow<List<Item>>
        get() = dao.favoritos(uid()).map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = conErrores {
        // Los favoritos viven en su propia tabla, así que el Upsert de items ya no los borra
        val nuevos = api.listar().pokemon.map { it.pokemon.aEntity() }
        dao.guardarTodos(nuevos)
        nuevos.size
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> = conErrores {
        api.detalle(id).aModelo(esFavorito = dao.esFavorito(uid(), id))
    }

    override suspend fun alternarFavorito(id: String) {
        if (dao.esFavorito(uid(), id)) dao.quitarFavorito(uid(), id)
        else dao.agregarFavorito(FavoritoEntity(uid(), id))
    }

    override suspend fun cantidadGuardada() = dao.contar()
    override suspend fun cantidadFavoritos() = dao.contarFavoritos(uid())
    override suspend fun limpiarCache() = dao.borrarTodo()

    /** IOException: sin internet. HttpException: 404, 500... SerializationException: JSON distinto al DTO. */
    private inline fun <T> conErrores(bloque: () -> T): Result<T> = try {
        Result.success(bloque())
    } catch (e: IOException) {
        Result.failure(e)
    } catch (e: HttpException) {
        Result.failure(e)
    } catch (e: SerializationException) {
        Result.failure(e)
    }
}

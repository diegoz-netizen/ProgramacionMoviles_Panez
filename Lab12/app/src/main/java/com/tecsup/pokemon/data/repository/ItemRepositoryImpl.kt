package com.tecsup.pokemon.data.repository

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
    private val dao: ItemDao
) : ItemRepository {

    override val items: Flow<List<Item>> = dao.todos().map { lista -> lista.map { it.aModelo() } }
    override val favoritos: Flow<List<Item>> = dao.favoritos().map { lista -> lista.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = conErrores {
        // Si se guarda sin cuidado, el Upsert pone esFavorito en false. Se conservan aquí:
        val favoritos = dao.idsFavoritos().toSet()
        val nuevos = api.listar().pokemon
            .map { it.pokemon.aEntity(esFavorito = it.pokemon.name in favoritos) }
        dao.guardarTodos(nuevos)
        nuevos.size
    }

    override suspend fun obtenerDetalle(id: String): Result<Item> = conErrores {
        api.detalle(id).aModelo(esFavorito = id in dao.idsFavoritos())
    }

    override suspend fun alternarFavorito(id: String) = dao.alternarFavorito(id)
    override suspend fun cantidadGuardada() = dao.contar()
    override suspend fun cantidadFavoritos() = dao.contarFavoritos()
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

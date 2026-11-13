package com.tecsup.pokemon.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    // esFavorito se calcula con la tabla favoritos de la cuenta (:uid), no con la columna de items
    @Query(
        """SELECT id, titulo, subtitulo, imagenUrl,
           EXISTS(SELECT 1 FROM favoritos f WHERE f.itemId = items.id AND f.uid = :uid) AS esFavorito
           FROM items ORDER BY titulo"""
    )
    fun todos(uid: String): Flow<List<ItemEntity>>

    @Query(
        """SELECT id, titulo, subtitulo, imagenUrl, 1 AS esFavorito
           FROM items JOIN favoritos f ON f.itemId = items.id
           WHERE f.uid = :uid ORDER BY titulo"""
    )
    fun favoritos(uid: String): Flow<List<ItemEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE uid = :uid AND itemId = :id)")
    suspend fun esFavorito(uid: String, id: String): Boolean

    @Query("SELECT COUNT(*) FROM items")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM favoritos WHERE uid = :uid")
    suspend fun contarFavoritos(uid: String): Int

    @Upsert
    suspend fun guardarTodos(items: List<ItemEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun agregarFavorito(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE uid = :uid AND itemId = :id")
    suspend fun quitarFavorito(uid: String, id: String)

    @Query("DELETE FROM items")
    suspend fun borrarTodo()
}

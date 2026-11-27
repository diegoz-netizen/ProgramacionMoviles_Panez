package com.tecsup.avisoscampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tecsup.avisoscampus.data.local.entity.AvisoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AvisoDao {

    /** Lo más reciente arriba. El Flow vuelve a emitir cada vez que cambia la tabla. */
    @Query("SELECT * FROM aviso ORDER BY id DESC")
    fun observarTodos(): Flow<List<AvisoEntity>>

    /** Devuelve el id que generó SQLite. */
    @Insert
    suspend fun insertar(aviso: AvisoEntity): Long

    @Query("DELETE FROM aviso")
    suspend fun borrarTodos()
}

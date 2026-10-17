package com.tecsup.lavacampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.ClienteResumen
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtener(id: Int): Usuario?

    @Insert suspend fun insertarTodos(lista: List<Usuario>)

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int

    /**
     * Estudiantes con su cantidad de pedidos (sin cancelados) y lo gastado en pedidos pagados.
     * LEFT JOIN para que también aparezcan los estudiantes que aún no tienen pedidos.
     */
    @Query(
        """
        SELECT u.id, u.nombre, u.codigo, u.correo,
               COUNT(CASE WHEN p.estado != 'CANCELADO' THEN p.id END) AS cantidadPedidos,
               COALESCE(SUM(CASE WHEN p.pagado = 1 THEN p.total END), 0.0) AS totalGastado
        FROM usuarios u
        LEFT JOIN pedidos p ON p.usuarioId = u.id
        WHERE u.rol = 'CLIENTE'
        GROUP BY u.id
        ORDER BY u.nombre
        """
    )
    fun observarClientes(): Flow<List<ClienteResumen>>
}

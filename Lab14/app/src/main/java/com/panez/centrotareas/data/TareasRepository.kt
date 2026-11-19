package com.panez.centrotareas.data

import kotlinx.coroutines.flow.Flow

interface TareasRepository {
    /** Emite el avance de la tarea simulada, de 0.01f a 1f. */
    fun progresoTarea(): Flow<Float>

    /** Cuenta los primos hasta [hasta] y avisa cada vez que cambia el porcentaje (0 a 100). */
    suspend fun contarPrimos(hasta: Int, alAvanzar: (Int) -> Unit): Int

    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}

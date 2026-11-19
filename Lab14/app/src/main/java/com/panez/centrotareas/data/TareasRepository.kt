package com.panez.centrotareas.data

import kotlinx.coroutines.flow.Flow

interface TareasRepository {
    /** Emite el avance de la tarea simulada, de 0.01f a 1f. */
    fun progresoTarea(): Flow<Float>

    suspend fun contarPrimos(hasta: Int): Int

    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>
}

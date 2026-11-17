package com.panez.campusgps.data

import com.panez.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>

    // El ViewModel la consulta antes de buscar (sin recibir un Context)
    fun playServicesDisponible(): Boolean
}

// La ubicación del celular está apagada o no se pudo determinar
class GpsApagadoException : Exception("No se pudo obtener la ubicación")

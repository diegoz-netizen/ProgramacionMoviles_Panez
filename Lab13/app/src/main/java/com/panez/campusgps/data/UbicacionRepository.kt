package com.panez.campusgps.data

import com.panez.campusgps.model.Posicion

interface UbicacionRepository {
    suspend fun obtenerPosicion(): Result<Posicion>
}

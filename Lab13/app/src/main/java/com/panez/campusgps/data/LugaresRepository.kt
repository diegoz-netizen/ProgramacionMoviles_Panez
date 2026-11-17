package com.panez.campusgps.data

import com.panez.campusgps.model.Lugar
import kotlinx.coroutines.flow.StateFlow

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>

    /** La misma lista, pero avisa cada vez que se mueve un lugar. */
    val lugares: StateFlow<List<Lugar>>

    /** Cambia la posición de un lugar (modo edición) y lo marca como ubicado. */
    fun moverLugar(id: Int, latitud: Double, longitud: Double)
}

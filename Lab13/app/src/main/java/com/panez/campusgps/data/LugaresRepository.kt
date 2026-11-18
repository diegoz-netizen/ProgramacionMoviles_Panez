package com.panez.campusgps.data

import com.panez.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}

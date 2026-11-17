package com.panez.campusgps.data

import com.panez.campusgps.model.Lugar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Datos ficticios de la Fase 1: 5 lugares cerca del punto base -12.0464, -77.0428 (Lima). Solo en memoria.
class LugaresRepositoryLocal : LugaresRepository {

    private val _lugares = MutableStateFlow(
        listOf(
            Lugar(1, "Biblioteca Central", "Estudio", -12.0460, -77.0435),
            Lugar(2, "Cafetería Campus", "Comida", -12.0470, -77.0420),
            Lugar(3, "Laboratorio de Cómputo", "Estudio", -12.0455, -77.0415),
            Lugar(4, "Auditorio Principal", "Eventos", -12.0480, -77.0440),
            Lugar(5, "Estacionamiento Norte", "Servicios", -12.0440, -77.0430)
        )
    )
    override val lugares: StateFlow<List<Lugar>> = _lugares.asStateFlow()

    override fun obtenerLugares(): List<Lugar> = _lugares.value

    override fun moverLugar(id: Int, latitud: Double, longitud: Double) = _lugares.update { lista ->
        lista.map { if (it.id == id) it.copy(latitud = latitud, longitud = longitud, ubicado = true) else it }
    }
}

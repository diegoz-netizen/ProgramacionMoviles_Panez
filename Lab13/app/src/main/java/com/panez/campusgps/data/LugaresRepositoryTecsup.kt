package com.panez.campusgps.data

import com.panez.campusgps.model.Lugar

/** Pabellones de Tecsup Lima (Santa Anita). Son fijos: los usuarios no pueden moverlos. */
class LugaresRepositoryTecsup : LugaresRepository {

    override fun obtenerLugares(): List<Lugar> = listOf(
        // Ubicados por Diego sobre la vista satelital del campus
        Lugar(1, "Pabellón 1", "Pabellón", -12.0451381, -76.9528217),
        Lugar(2, "Pabellón 2", "Pabellón", -12.0449755, -76.9528301),
        Lugar(3, "Pabellón 3", "Pabellón", -12.0450207, -76.9523480),
        Lugar(4, "Pabellón 4", "Pabellón", -12.0447565, -76.9531077),
        Lugar(5, "Pabellón 5", "Pabellón", -12.0448637, -76.9528073),
        Lugar(6, "Pabellón 6", "Pabellón", -12.0447161, -76.9523701),
        Lugar(7, "Pabellón 7", "Pabellón", -12.0444456, -76.9529495),
        Lugar(8, "Pabellón 8", "Pabellón", -12.0441830, -76.9524170),
        Lugar(9, "Pabellón 9", "Pabellón", -12.0442377, -76.9532693),
        Lugar(11, "Pabellón 11", "Pabellón", -12.0437069, -76.9521351),
        Lugar(12, "Pabellón 12", "Pabellón", -12.0432396, -76.9522655),
        Lugar(13, "Pabellón 13", "Pabellón", -12.0430130, -76.9523617),
        Lugar(14, "Pabellón 14", "Pabellón", -12.0439443, -76.9517596),
        Lugar(17, "Canchas deportivas", "Deportes", -12.0438459, -76.9537481),
        // Coordenadas publicadas en Google Maps
        Lugar(10, "Pabellón 10", "Pabellón", -12.043727, -76.952826),
        Lugar(15, "Pabellón 15", "Pabellón", -12.043487, -76.9531418),
        Lugar(16, "Pabellón 16", "Pabellón", -12.043398, -76.9527886),
        // Puerta principal, frente a la Av. Cascanueces: es el punto 1 de la red de pasillos
        Lugar(18, "Entrada e informes", "Servicios", -12.0452880, -76.9523798)
    ).sortedBy { it.id }
}

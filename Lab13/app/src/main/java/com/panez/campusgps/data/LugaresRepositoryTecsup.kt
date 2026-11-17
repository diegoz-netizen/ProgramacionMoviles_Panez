package com.panez.campusgps.data

import android.content.Context
import com.panez.campusgps.model.Lugar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Pabellones de Tecsup Lima (Santa Anita).
 * Pabellones 10, 15, 16 y la entrada: coordenadas de Google Maps. El resto los ubicó Diego
 * desde la app (modo edición). Si alguno se vuelve a mover, el cambio se guarda en
 * SharedPreferences y tiene prioridad sobre estos valores.
 */
class LugaresRepositoryTecsup(context: Context) : LugaresRepository {

    private val prefs = context.getSharedPreferences("pabellones", Context.MODE_PRIVATE)

    private val _lugares = MutableStateFlow(base().map(::conPosicionGuardada))
    override val lugares: StateFlow<List<Lugar>> = _lugares.asStateFlow()

    override fun obtenerLugares(): List<Lugar> = _lugares.value

    override fun moverLugar(id: Int, latitud: Double, longitud: Double) {
        prefs.edit().putString(clave(id), "$latitud,$longitud").apply()
        _lugares.value = _lugares.value.map {
            if (it.id == id) it.copy(latitud = latitud, longitud = longitud, ubicado = true) else it
        }
    }

    /** Si el usuario ya lo ubicó, se usa la posición guardada. */
    private fun conPosicionGuardada(lugar: Lugar): Lugar {
        val guardada = prefs.getString(clave(lugar.id), null)?.split(",") ?: return lugar
        val lat = guardada.getOrNull(0)?.toDoubleOrNull() ?: return lugar
        val lng = guardada.getOrNull(1)?.toDoubleOrNull() ?: return lugar
        return lugar.copy(latitud = lat, longitud = lng, ubicado = true)
    }

    private fun clave(id: Int) = "lugar_$id"

    private fun base(): List<Lugar> = listOf(
        // Ubicados por Diego en el celular (modo edición), sobre la vista satelital del campus
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
        Lugar(18, "Entrada e informes", "Servicios", -12.0448814, -76.9514571)
    ).sortedBy { it.id }
}

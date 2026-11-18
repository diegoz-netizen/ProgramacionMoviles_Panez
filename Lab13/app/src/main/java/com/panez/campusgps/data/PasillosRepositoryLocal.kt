package com.panez.campusgps.data

import android.content.Context
import com.panez.campusgps.model.Nodo
import com.panez.campusgps.model.RedPasillos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Guarda la red en SharedPreferences como texto:
 * nodos "id,lat,lng;id,lat,lng" y tramos "a-b;a-b".
 */
class PasillosRepositoryLocal(context: Context) : PasillosRepository {

    private val prefs = context.getSharedPreferences("pasillos_v2", Context.MODE_PRIVATE)

    private val _red = MutableStateFlow(leer())
    override val red: StateFlow<RedPasillos> = _red.asStateFlow()

    override fun agregarNodo(latitud: Double, longitud: Double, unirCon: Int?): Int {
        val actual = _red.value
        val id = (actual.nodos.maxOfOrNull { it.id } ?: 0) + 1
        val tramos = if (unirCon != null) actual.tramos + (unirCon to id) else actual.tramos
        guardar(RedPasillos(actual.nodos + Nodo(id, latitud, longitud), tramos))
        return id
    }

    override fun unir(a: Int, b: Int) {
        val actual = _red.value
        val existe = actual.tramos.any { (x, y) -> (x == a && y == b) || (x == b && y == a) }
        if (a != b && !existe) guardar(actual.copy(tramos = actual.tramos + (a to b)))
    }

    override fun deshacer() {
        val actual = _red.value
        val ultimo = actual.nodos.maxOfOrNull { it.id } ?: return
        guardar(
            RedPasillos(
                actual.nodos.filter { it.id != ultimo },
                actual.tramos.filter { (a, b) -> a != ultimo && b != ultimo }
            )
        )
    }

    override fun borrarTodo() = guardar(RedPasillos())

    private fun guardar(red: RedPasillos) {
        prefs.edit()
            .putString("nodos", red.nodos.joinToString(";") { "${it.id},${it.latitud},${it.longitud}" })
            .putString("tramos", red.tramos.joinToString(";") { "${it.first}-${it.second}" })
            .apply()
        _red.value = red
    }

    private fun leer(): RedPasillos {
        // Mientras nadie edite la red, se usa la de Tecsup que viene con la app
        if (!prefs.contains("nodos")) return RedBaseTecsup.red
        val nodos = prefs.getString("nodos", "").orEmpty().split(";").mapNotNull { texto ->
            val p = texto.split(",")
            val id = p.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
            val lat = p.getOrNull(1)?.toDoubleOrNull() ?: return@mapNotNull null
            val lng = p.getOrNull(2)?.toDoubleOrNull() ?: return@mapNotNull null
            Nodo(id, lat, lng)
        }
        val tramos = prefs.getString("tramos", "").orEmpty().split(";").mapNotNull { texto ->
            val p = texto.split("-")
            val a = p.getOrNull(0)?.toIntOrNull() ?: return@mapNotNull null
            val b = p.getOrNull(1)?.toIntOrNull() ?: return@mapNotNull null
            a to b
        }
        return RedPasillos(nodos, tramos)
    }
}

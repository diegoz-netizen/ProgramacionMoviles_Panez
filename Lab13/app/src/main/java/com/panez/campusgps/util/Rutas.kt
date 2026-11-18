package com.panez.campusgps.util

import com.panez.campusgps.model.Nodo
import com.panez.campusgps.model.Posicion
import com.panez.campusgps.model.Recorrido
import com.panez.campusgps.model.RedPasillos
import java.util.PriorityQueue
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val RADIO_TIERRA_M = 6_371_000.0

/** Más lejos que esto del pasillo más cercano, se considera que aún no llegas al campus. */
const val MAXIMO_FUERA_DEL_CAMPUS_M = 100f

/** Distancia en metros entre dos puntos (fórmula de Haversine). Kotlin puro: se puede probar sin Android. */
fun metrosEntre(a: Posicion, b: Posicion): Float {
    val dLat = Math.toRadians(b.latitud - a.latitud)
    val dLng = Math.toRadians(b.longitud - a.longitud)
    val h = sin(dLat / 2).pow(2) +
        cos(Math.toRadians(a.latitud)) * cos(Math.toRadians(b.latitud)) * sin(dLng / 2).pow(2)
    return (2 * RADIO_TIERRA_M * asin(sqrt(h))).toFloat()
}

private fun Nodo.posicion() = Posicion(latitud, longitud)

/** La ruta interna solo tiene sentido al llegar a Tecsup o estando dentro. Sin red no se puede saber. */
fun estaEnElCampus(red: RedPasillos, posicion: Posicion): Boolean =
    red.nodos.isEmpty() || red.nodos.any { metrosEntre(posicion, it.posicion()) <= MAXIMO_FUERA_DEL_CAMPUS_M }

/**
 * Camino a pie desde [origen] hasta [destino] siguiendo los pasillos.
 * 1. Entra a la red por el nodo más cercano al origen y sale por el más cercano al destino.
 * 2. Entre esos dos nodos busca el camino más corto con Dijkstra.
 * Si no hay red, o los nodos no están conectados, devuelve la línea recta.
 */
fun calcularRecorrido(red: RedPasillos, origen: Posicion, destino: Posicion): Recorrido {
    val recta = Recorrido(listOf(origen, destino), metrosEntre(origen, destino), porPasillos = false)
    if (red.nodos.isEmpty()) return recta

    val entrada = red.nodos.minBy { metrosEntre(origen, it.posicion()) }
    val salida = red.nodos.minBy { metrosEntre(destino, it.posicion()) }
    val nodos = caminoMasCorto(red, entrada.id, salida.id) ?: return recta

    val puntos = listOf(origen) + nodos.map { it.posicion() } + destino
    val metros = puntos.zipWithNext { a, b -> metrosEntre(a, b) }.sum()
    // Si dar la vuelta por los pasillos es absurdo (estás casi encima), se va directo
    if (metros > recta.metros * 4 && recta.metros < 30f) return recta
    return Recorrido(puntos, metros, porPasillos = true)
}

/** Dijkstra: lista de nodos del camino más corto entre [desde] y [hasta], o null si no se conectan. */
fun caminoMasCorto(red: RedPasillos, desde: Int, hasta: Int): List<Nodo>? {
    val porId = red.nodos.associateBy { it.id }
    if (desde !in porId || hasta !in porId) return null

    // Vecinos de cada nodo con el largo del tramo (los pasillos se caminan en los dos sentidos)
    val vecinos = mutableMapOf<Int, MutableList<Pair<Int, Float>>>()
    red.tramos.forEach { (a, b) ->
        val na = porId[a] ?: return@forEach
        val nb = porId[b] ?: return@forEach
        val largo = metrosEntre(na.posicion(), nb.posicion())
        vecinos.getOrPut(a) { mutableListOf() }.add(b to largo)
        vecinos.getOrPut(b) { mutableListOf() }.add(a to largo)
    }

    val distancia = mutableMapOf(desde to 0f)
    val anterior = mutableMapOf<Int, Int>()
    val cola = PriorityQueue<Pair<Int, Float>>(compareBy { it.second })
    cola.add(desde to 0f)
    while (cola.isNotEmpty()) {
        val (actual, d) = cola.poll()!!
        if (actual == hasta) break
        if (d > (distancia[actual] ?: Float.MAX_VALUE)) continue // ya había uno mejor
        vecinos[actual].orEmpty().forEach { (vecino, largo) ->
            val nueva = d + largo
            if (nueva < (distancia[vecino] ?: Float.MAX_VALUE)) {
                distancia[vecino] = nueva
                anterior[vecino] = actual
                cola.add(vecino to nueva)
            }
        }
    }
    if (hasta !in distancia) return null

    // Se reconstruye el camino de atrás hacia adelante
    val camino = mutableListOf(hasta)
    while (camino.last() != desde) camino.add(anterior.getValue(camino.last()))
    return camino.reversed().map { porId.getValue(it) }
}

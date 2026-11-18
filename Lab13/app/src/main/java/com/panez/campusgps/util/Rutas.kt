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

// Ids de los nodos temporales del cálculo (los de la red son positivos)
private const val ID_ENTRADA = -1
private const val ID_LLEGADA = -2

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

/** Punto del pasillo más cercano a [p], y los dos nodos del tramo donde cae. */
private data class Acceso(val punto: Posicion, val a: Int, val b: Int)

private fun accesoMasCercano(red: RedPasillos, p: Posicion): Acceso? {
    val porId = red.nodos.associateBy { it.id }
    val escalaLng = cos(Math.toRadians(p.latitud))
    return red.tramos.mapNotNull { (a, b) ->
        val na = porId[a] ?: return@mapNotNull null
        val nb = porId[b] ?: return@mapNotNull null
        // Proyección sobre el tramo, en un plano local (a esta escala la curvatura no importa)
        val dx = (nb.longitud - na.longitud) * escalaLng
        val dy = nb.latitud - na.latitud
        val largo = dx * dx + dy * dy
        val t = if (largo == 0.0) 0.0
        else (((p.longitud - na.longitud) * escalaLng * dx + (p.latitud - na.latitud) * dy) / largo).coerceIn(0.0, 1.0)
        Acceso(
            Posicion(na.latitud + (nb.latitud - na.latitud) * t, na.longitud + (nb.longitud - na.longitud) * t),
            a,
            b
        )
    }.minByOrNull { metrosEntre(p, it.punto) }
}

/**
 * Camino a pie desde [origen] hasta [destino] siguiendo los pasillos.
 * 1. Entra a la red por el punto del pasillo más cercano al origen.
 * 2. Termina en el punto del pasillo que queda frente al destino, sin cruzar el edificio.
 * 3. Entre esos dos puntos busca el camino más corto con Dijkstra.
 * Si no hay red, o los puntos no están conectados, devuelve la línea recta.
 */
fun calcularRecorrido(red: RedPasillos, origen: Posicion, destino: Posicion): Recorrido {
    val recta = Recorrido(listOf(origen, destino), metrosEntre(origen, destino), porPasillos = false)
    val entrada = accesoMasCercano(red, origen) ?: return recta
    val llegada = accesoMasCercano(red, destino) ?: return recta

    // Los dos accesos se agregan al grafo como nodos temporales
    val inicio = Nodo(ID_ENTRADA, entrada.punto.latitud, entrada.punto.longitud)
    val fin = Nodo(ID_LLEGADA, llegada.punto.latitud, llegada.punto.longitud)
    val mismoTramo = setOf(entrada.a, entrada.b) == setOf(llegada.a, llegada.b)
    val ampliada = RedPasillos(
        nodos = red.nodos + inicio + fin,
        tramos = red.tramos +
            listOf(ID_ENTRADA to entrada.a, ID_ENTRADA to entrada.b, ID_LLEGADA to llegada.a, ID_LLEGADA to llegada.b) +
            if (mismoTramo) listOf(ID_ENTRADA to ID_LLEGADA) else emptyList()
    )
    val nodos = caminoMasCorto(ampliada, ID_ENTRADA, ID_LLEGADA) ?: return recta

    val puntos = listOf(origen) + nodos.map { it.posicion() }
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

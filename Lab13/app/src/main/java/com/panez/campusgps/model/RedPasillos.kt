package com.panez.campusgps.model

/** Un punto de la red de pasillos: un cruce, una esquina o la puerta de un pabellón. */
data class Nodo(val id: Int, val latitud: Double, val longitud: Double)

/** Pasillos del campus como grafo: nodos unidos por tramos que se pueden caminar (en los dos sentidos). */
data class RedPasillos(
    val nodos: List<Nodo> = emptyList(),
    val tramos: List<Pair<Int, Int>> = emptyList()
)

/** Camino calculado hasta un lugar. porPasillos = false: no había red y se usó la línea recta. */
data class Recorrido(val puntos: List<Posicion>, val metros: Float, val porPasillos: Boolean)

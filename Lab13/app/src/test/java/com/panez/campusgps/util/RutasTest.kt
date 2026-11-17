package com.panez.campusgps.util

import com.panez.campusgps.model.Nodo
import com.panez.campusgps.model.Posicion
import com.panez.campusgps.model.RedPasillos
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RutasTest {

    // Un cuadrado de pasillos: 1-2-3 por arriba y 1-4-3 por abajo, el de abajo más largo
    private val n1 = Nodo(1, -12.0450, -76.9530)
    private val n2 = Nodo(2, -12.0450, -76.9520)
    private val n3 = Nodo(3, -12.0440, -76.9520)
    private val n4 = Nodo(4, -12.0460, -76.9540)
    private val red = RedPasillos(
        nodos = listOf(n1, n2, n3, n4),
        tramos = listOf(1 to 2, 2 to 3, 1 to 4, 4 to 3)
    )

    @Test
    fun dijkstra_eligeElCaminoMasCorto() {
        assertEquals(listOf(1, 2, 3), caminoMasCorto(red, 1, 3)!!.map { it.id })
    }

    @Test
    fun dijkstra_sinConexion_devuelveNull() {
        val separada = RedPasillos(listOf(n1, n2, n3), listOf(1 to 2))
        assertNull(caminoMasCorto(separada, 1, 3))
    }

    @Test
    fun recorrido_sinRed_usaLineaRecta() {
        val r = calcularRecorrido(RedPasillos(), Posicion(n1.latitud, n1.longitud), Posicion(n3.latitud, n3.longitud))
        assertFalse(r.porPasillos)
        assertEquals(2, r.puntos.size)
    }

    @Test
    fun recorrido_porPasillos_esMasLargoQueLaRecta() {
        val origen = Posicion(n1.latitud, n1.longitud)
        val destino = Posicion(n3.latitud, n3.longitud)
        val r = calcularRecorrido(red, origen, destino)
        assertTrue(r.porPasillos)
        assertTrue(r.metros > metrosEntre(origen, destino))
    }

    @Test
    fun metrosEntre_unaCuadraDeLatitud() {
        // 0.001 grados de latitud son unos 111 m
        val m = metrosEntre(Posicion(-12.0450, -76.9530), Posicion(-12.0440, -76.9530))
        assertEquals(111f, m, 1f)
    }
}

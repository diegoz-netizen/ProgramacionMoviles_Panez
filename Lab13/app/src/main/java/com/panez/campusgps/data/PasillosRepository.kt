package com.panez.campusgps.data

import com.panez.campusgps.model.RedPasillos
import kotlinx.coroutines.flow.StateFlow

/** Red de pasillos del campus, que se dibuja desde la app. */
interface PasillosRepository {
    val red: StateFlow<RedPasillos>

    /** Agrega un nodo y, si [unirCon] no es null, lo une con ese nodo. Devuelve el id nuevo. */
    fun agregarNodo(latitud: Double, longitud: Double, unirCon: Int?): Int
    fun unir(a: Int, b: Int)
    /** Quita el último nodo dibujado y sus tramos. */
    fun deshacer()
    fun borrarTodo()
}

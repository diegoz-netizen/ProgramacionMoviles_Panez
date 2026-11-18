package com.panez.campusgps.data

import com.panez.campusgps.model.RedPasillos

/** Red de pasillos del campus con la que se calculan las rutas. */
interface PasillosRepository {
    fun obtenerRed(): RedPasillos
}

/** Los pasillos de Tecsup Lima vienen con la app y no se pueden editar. */
class PasillosRepositoryTecsup : PasillosRepository {
    override fun obtenerRed(): RedPasillos = RedBaseTecsup.red
}

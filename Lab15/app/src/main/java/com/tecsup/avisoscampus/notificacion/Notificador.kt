package com.tecsup.avisoscampus.notificacion

import android.content.Context

/** Todo lo que toca el sistema de notificaciones: canal, permiso y mostrar. */
object Notificador {

    const val CANAL = "avisos"

    fun crearCanal(context: Context) {
        // TODO: si SDK_INT >= O, crear NotificationChannel y registrarlo
    }
}

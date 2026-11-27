package com.tecsup.avisoscampus.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/** Todo lo que toca el sistema de notificaciones: canal, permiso y mostrar. */
object Notificador {

    const val CANAL = "avisos"

    /** Desde Android 8 (O) toda notificación necesita un canal. Crearlo dos veces no hace nada. */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(CANAL, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(canal)
        }
    }
}

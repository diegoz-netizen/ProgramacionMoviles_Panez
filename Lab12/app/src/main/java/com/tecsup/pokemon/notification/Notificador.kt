package com.tecsup.pokemon.notification

/** Muestra un aviso al usuario. El ViewModel la usa sin conocer Context ni NotificationCompat. */
interface Notificador {
    fun mostrar(titulo: String, texto: String)
}

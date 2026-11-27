package com.tecsup.avisoscampus.notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.avisoscampus.MainActivity
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.model.Aviso

/** Todo lo que toca el sistema de notificaciones: canal, permiso y mostrar. */
object Notificador {

    const val CANAL = "avisos"
    const val CANAL_URGENTE = "urgentes"

    /**
     * Desde Android 8 (O) toda notificación necesita un canal. Crearlos dos veces no hace nada.
     * Avisos: importancia normal. Urgentes: importancia alta, aparece como emergente.
     */
    fun crearCanal(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val avisos = NotificationChannel(CANAL, "Avisos", NotificationManager.IMPORTANCE_DEFAULT)
            val urgentes = NotificationChannel(CANAL_URGENTE, "Urgentes", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Recordatorios que no deben pasar desapercibidos"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannels(listOf(avisos, urgentes))
        }
    }

    /** Antes de Android 13 (API 33) el permiso se concede al instalar. */
    fun tienePermiso(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /** Muestra el aviso. Devuelve false si no hay permiso, sin lanzar error. */
    @SuppressLint("MissingPermission") // el permiso se revisa en la primera línea
    fun mostrar(context: Context, aviso: Aviso): Boolean {
        if (!tienePermiso(context)) return false

        // Al tocar la notificación se abre la app (inmutable: nadie puede cambiar el Intent)
        val intent = Intent(context, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val canal = if (aviso.urgente) CANAL_URGENTE else CANAL
        val prioridad = if (aviso.urgente) NotificationCompat.PRIORITY_HIGH else NotificationCompat.PRIORITY_DEFAULT

        val notificacion = NotificationCompat.Builder(context, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setColor(ContextCompat.getColor(context, R.color.indigo_avisos)) // acento índigo
            .setContentTitle(aviso.titulo)
            .setContentText(aviso.texto)
            .apply {
                // Al expandir la notificación se ve el texto completo
                aviso.detalle?.let { setStyle(NotificationCompat.BigTextStyle().bigText(it)) }
            }
            .setPriority(prioridad) // para Android 7 o menos, que no tiene canales
            .setContentIntent(pendiente)
            .setAutoCancel(true) // se quita al tocarla
            .build()

        // El id del aviso hace que cada notificación sea distinta
        NotificationManagerCompat.from(context).notify(aviso.id, notificacion)
        return true
    }
}

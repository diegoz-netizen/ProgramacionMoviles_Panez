package com.tecsup.pokemon.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tecsup.pokemon.MainActivity
import com.tecsup.pokemon.R

/** Notificación local con NotificationCompat. Si el permiso está negado no muestra nada y la app sigue normal. */
class NotificadorAndroid(private val context: Context) : Notificador {

    init {
        crearCanal()
    }

    override fun mostrar(titulo: String, texto: String) {
        if (!tienePermiso()) return

        // Al tocar la notificación se abre la app
        val abrirApp = PendingIntent.getActivity(
            context,
            0,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificacion = NotificationCompat.Builder(context, CANAL)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle(titulo)
            .setContentText(texto)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(ID, notificacion)
    }

    /** Android 8+ exige un canal. Crearlo varias veces no hace daño. */
    private fun crearCanal() {
        val canal = NotificationChannel(CANAL, "Sincronización", NotificationManager.IMPORTANCE_DEFAULT)
            .apply { description = "Avisa cuántos Pokémon se guardaron al sincronizar" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }

    /** POST_NOTIFICATIONS solo se pide desde Android 13; antes no hace falta. */
    private fun tienePermiso() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

    private companion object {
        const val CANAL = "sincronizacion"
        const val ID = 1
    }
}

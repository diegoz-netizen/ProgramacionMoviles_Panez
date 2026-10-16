package com.tecsup.lavacampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.lavacampus.data.local.dao.ServicioDao
import com.tecsup.lavacampus.data.local.entity.Servicio

@Database(
    entities = [Servicio::class /* TODO (Paso 3): agrega Usuario, Pedido y DetallePedido */],
    version = 1,
    exportSchema = false
)
abstract class LavaDatabase : RoomDatabase() {
    abstract fun servicioDao(): ServicioDao
    // TODO (Paso 3): usuarioDao(), pedidoDao() y detalleDao()

    companion object {
        @Volatile private var INSTANCE: LavaDatabase? = null

        /** Una sola instancia de la base para toda la app (singleton). */
        fun obtener(context: Context): LavaDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                LavaDatabase::class.java,
                "lavacampus.db"
            ).build().also { INSTANCE = it }
        }
    }
}

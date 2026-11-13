package com.tecsup.pokemon.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [ItemEntity::class, FavoritoEntity::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        /**
         * v1 -> v2: favoritos por cuenta en su propia tabla. Los favoritos viejos (columna esFavorito)
         * no tenían dueño, así que no se pasan; los Pokémon guardados se conservan.
         */
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS favoritos (uid TEXT NOT NULL, itemId TEXT NOT NULL, " +
                        "PRIMARY KEY(uid, itemId))"
                )
            }
        }

        /** v2 -> v3: columna tipo en items para el filtro (los guardados quedan sin tipo hasta sincronizar). */
        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE items ADD COLUMN tipo TEXT NOT NULL DEFAULT ''")
            }
        }

        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "pokedex.db")
                .addMigrations(MIGRACION_1_2, MIGRACION_2_3)
                .build()
    }
}

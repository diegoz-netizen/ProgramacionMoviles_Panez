package com.tecsup.avisoscampus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.avisoscampus.data.local.dao.AvisoDao
import com.tecsup.avisoscampus.data.local.entity.AvisoEntity

@Database(entities = [AvisoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun avisoDao(): AvisoDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "avisos.db").build()
    }
}

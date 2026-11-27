package com.tecsup.avisoscampus.di

import android.content.Context
import com.tecsup.avisoscampus.data.AvisosRepository
import com.tecsup.avisoscampus.data.AvisosRepositoryRoom
import com.tecsup.avisoscampus.data.local.AppDatabase

/** Inyección manual: se crea una vez en AvisosApp y comparte la misma base de datos. */
class AppContainer(context: Context) {
    private val baseDeDatos = AppDatabase.crear(context)
    val avisosRepository: AvisosRepository = AvisosRepositoryRoom(baseDeDatos.avisoDao())
}

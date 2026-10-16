package com.tecsup.lavacampus

import android.app.Application
import com.tecsup.lavacampus.data.local.DatosIniciales
import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.data.repository.LavaRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Crea la base y el repository una sola vez para toda la app. */
class LavaApp : Application() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    lateinit var db: LavaDatabase
        private set
    lateinit var repository: LavaRepository
        private set
    // TODO (Paso 5): crea aquí PreferenciasStore

    override fun onCreate() {
        super.onCreate()
        db = LavaDatabase.obtener(this)
        repository = LavaRepositoryRoom(db)
        scope.launch { DatosIniciales.sembrar(db) }
    }
}

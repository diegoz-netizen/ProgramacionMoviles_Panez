package com.tecsup.pokemon

import android.app.Application

/**
 * Vive mientras la app esté abierta. Guarda el AppContainer aquí (y no en MainActivity)
 * para que al girar la pantalla no se cree otra API ni otra base de datos.
 */
class PokemonApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(applicationContext)
    }
}

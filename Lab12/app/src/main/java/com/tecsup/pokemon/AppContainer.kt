package com.tecsup.pokemon

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.data.auth.FirebaseAuthRepository
import com.tecsup.pokemon.data.local.AppDatabase
import com.tecsup.pokemon.data.remote.RetrofitClient
import com.tecsup.pokemon.data.repository.ItemRepository
import com.tecsup.pokemon.data.repository.ItemRepositoryImpl
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.notification.NotificadorAndroid

/** Crea una sola vez la API, la base de datos y los repositorios. Los ViewModel los reciben por la Factory. */
class AppContainer(private val context: Context) {
    private val database = AppDatabase.crear(context)

    val itemRepository: ItemRepository by lazy { ItemRepositoryImpl(RetrofitClient.api, database.itemDao()) {
            FirebaseAuth.getInstance().currentUser?.uid
        } }

    val authRepository: AuthRepository by lazy { FirebaseAuthRepository() }
    val notificador: Notificador by lazy { NotificadorAndroid(context) }
}

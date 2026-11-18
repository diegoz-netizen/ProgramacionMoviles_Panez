package com.panez.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.panez.campusgps.data.LugaresRepositoryTecsup
import com.panez.campusgps.data.PasillosRepositoryTecsup
import com.panez.campusgps.data.UbicacionRepositoryGoogle

// El Context se queda aquí: el ViewModel solo recibe las interfaces
class CampusViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            return MapaViewModel(
                ubicacionRepo = UbicacionRepositoryGoogle(context.applicationContext),
                lugaresRepo = LugaresRepositoryTecsup(),
                pasillosRepo = PasillosRepositoryTecsup()
            ) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}

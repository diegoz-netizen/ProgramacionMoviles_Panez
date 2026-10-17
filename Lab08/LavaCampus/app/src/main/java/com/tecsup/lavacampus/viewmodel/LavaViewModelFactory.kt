package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository

/**
 * Crea los ViewModels pasándoles el repository y las preferencias (inyección manual, sin Hilt).
 * Cada ViewModel nuevo se agrega como una rama del when.
 */
class LavaViewModelFactory(
    private val repo: LavaRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(ServiciosViewModel::class.java) -> ServiciosViewModel(repo)
        modelClass.isAssignableFrom(SesionViewModel::class.java) -> SesionViewModel(repo, prefs)
        modelClass.isAssignableFrom(InicioViewModel::class.java) -> InicioViewModel(repo)
        modelClass.isAssignableFrom(NuevoPedidoViewModel::class.java) -> NuevoPedidoViewModel(repo)
        modelClass.isAssignableFrom(DetallePedidoViewModel::class.java) -> DetallePedidoViewModel(repo)
        modelClass.isAssignableFrom(MisPedidosViewModel::class.java) -> MisPedidosViewModel(repo)
        modelClass.isAssignableFrom(PanelViewModel::class.java) -> PanelViewModel(repo)
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}

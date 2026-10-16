package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tecsup.lavacampus.data.repository.LavaRepository

/**
 * Crea los ViewModels pasándoles el repository (inyección manual, sin Hilt).
 * Cada ViewModel nuevo se agrega como una rama del when.
 */
class LavaViewModelFactory(private val repo: LavaRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(ServiciosViewModel::class.java) -> ServiciosViewModel(repo)
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}

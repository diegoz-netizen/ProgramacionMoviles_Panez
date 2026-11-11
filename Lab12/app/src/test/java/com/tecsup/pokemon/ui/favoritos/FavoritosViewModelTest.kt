package com.tecsup.pokemon.ui.favoritos

import com.tecsup.pokemon.FakeItemRepository
import com.tecsup.pokemon.MainDispatcherRule
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.unItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritosViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    @Test
    fun soloMuestraLosFavoritos() = runTest {
        val repo = FakeItemRepository(listOf(unItem("charmander", true), unItem("vulpix"), unItem("ponyta", true)))
        val vm = FavoritosViewModel(repo)

        val estado = vm.estado.first { it is UiState.Exito } as UiState.Exito
        assertEquals(listOf("charmander", "ponyta"), estado.datos.map { it.id })
    }

    @Test
    fun alQuitarUnFavoritoSaleDeLaLista() = runTest {
        val repo = FakeItemRepository(listOf(unItem("charmander", true), unItem("ponyta", true)))
        val vm = FavoritosViewModel(repo)
        vm.estado.first { it is UiState.Exito }

        vm.alternarFavorito("charmander")

        val estado = vm.estado.first { it is UiState.Exito && it.datos.size == 1 } as UiState.Exito
        assertEquals(listOf("ponyta"), estado.datos.map { it.id })
    }
}

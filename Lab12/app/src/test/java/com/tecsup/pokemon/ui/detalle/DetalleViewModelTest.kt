package com.tecsup.pokemon.ui.detalle

import com.tecsup.pokemon.FakeItemRepository
import com.tecsup.pokemon.MainDispatcherRule
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.unItem
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class DetalleViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    @Test
    fun cargaElDetalle() {
        val repo = FakeItemRepository().apply { resultadoDetalle = Result.success(unItem("charmander")) }
        val vm = DetalleViewModel(repo, "charmander")

        assertEquals("Charmander", (vm.estado.value as UiState.Exito).datos.titulo)
    }

    @Test
    fun sinInternetMuestraErrorDeConexion() {
        val repo = FakeItemRepository().apply { resultadoDetalle = Result.failure(IOException()) }
        val vm = DetalleViewModel(repo, "charmander")

        assertEquals("No hay conexión a internet", (vm.estado.value as UiState.Error).mensaje)
    }

    @Test
    fun errorHttpMuestraElCodigoDelServidor() {
        val respuesta = Response.error<Any>(404, "no existe".toResponseBody("text/plain".toMediaType()))
        val repo = FakeItemRepository().apply { resultadoDetalle = Result.failure(HttpException(respuesta)) }
        val vm = DetalleViewModel(repo, "nada")

        assertEquals("El servidor respondió con error 404", (vm.estado.value as UiState.Error).mensaje)
    }

    @Test
    fun alternarFavoritoCambiaElEstadoDelDetalle() {
        val repo = FakeItemRepository(listOf(unItem("vulpix"))).apply {
            resultadoDetalle = Result.success(unItem("vulpix"))
        }
        val vm = DetalleViewModel(repo, "vulpix")
        assertFalse((vm.estado.value as UiState.Exito).datos.esFavorito)

        vm.alternarFavorito()

        assertTrue((vm.estado.value as UiState.Exito).datos.esFavorito)
        assertEquals(listOf("vulpix"), repo.favoritosAlternados)
    }
}

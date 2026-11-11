package com.tecsup.pokemon.ui.lista

import com.tecsup.pokemon.FakeItemRepository
import com.tecsup.pokemon.MainDispatcherRule
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.ui.UiState
import com.tecsup.pokemon.unItem
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/** Guarda los avisos en vez de mostrarlos, para revisar qué notificó el ViewModel. */
private class NotificadorFalso : Notificador {
    val avisos = mutableListOf<Pair<String, String>>()
    override fun mostrar(titulo: String, texto: String) {
        avisos += titulo to texto
    }
}

class ListaViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    private val notificador = NotificadorFalso()

    @Test
    fun conDatosGuardadosMuestraLaLista() {
        val repo = FakeItemRepository(listOf(unItem("charmander"), unItem("vulpix")))
        val vm = ListaViewModel(repo, notificador)

        val estado = vm.estado.value as UiState.Exito
        assertEquals(listOf("charmander", "vulpix"), estado.datos.map { it.id })
        assertNull(vm.aviso.value)
    }

    @Test
    fun sinInternetYSinDatosMuestraError() {
        val repo = FakeItemRepository().apply { resultadoRefrescar = Result.failure(IOException()) }
        val vm = ListaViewModel(repo, notificador)

        val estado = vm.estado.value
        assertTrue(estado is UiState.Error)
        assertEquals("No hay conexión a internet", (estado as UiState.Error).mensaje)
    }

    @Test
    fun sinInternetPeroConDatosGuardadosMuestraAviso() {
        val repo = FakeItemRepository(listOf(unItem("growlithe"))).apply {
            resultadoRefrescar = Result.failure(IOException())
        }
        val vm = ListaViewModel(repo, notificador)

        assertTrue(vm.estado.value is UiState.Exito)
        assertEquals("Sin conexión: mostrando datos guardados", vm.aviso.value)
    }

    @Test
    fun reintentarDespuesDelErrorCargaLosDatos() {
        val repo = FakeItemRepository().apply { resultadoRefrescar = Result.failure(IOException()) }
        val vm = ListaViewModel(repo, notificador)
        assertTrue(vm.estado.value is UiState.Error)

        repo.resultadoRefrescar = Result.success(1)
        repo.guardados.value = listOf(unItem("ponyta"))
        vm.refrescar()

        assertEquals(listOf("ponyta"), (vm.estado.value as UiState.Exito).datos.map { it.id })
    }

    @Test
    fun alternarFavoritoActualizaElItem() {
        val repo = FakeItemRepository(listOf(unItem("charmander")))
        val vm = ListaViewModel(repo, notificador)

        vm.alternarFavorito("charmander")

        assertEquals(listOf("charmander"), repo.favoritosAlternados)
        assertTrue((vm.estado.value as UiState.Exito).datos.first().esFavorito)
    }

    @Test
    fun alSincronizarNotificaCuantosSeGuardaron() {
        val repo = FakeItemRepository().apply { resultadoRefrescar = Result.success(3) }
        ListaViewModel(repo, notificador)

        assertEquals(listOf("Datos actualizados" to "Se guardaron 3 elementos"), notificador.avisos)
    }

    @Test
    fun sinInternetNoNotifica() {
        val repo = FakeItemRepository().apply { resultadoRefrescar = Result.failure(IOException()) }
        ListaViewModel(repo, notificador)

        assertTrue(notificador.avisos.isEmpty())
    }

    @Test
    fun buscarFiltraPorNombreSinImportarMayusculas() {
        val repo = FakeItemRepository(listOf(unItem("charmander"), unItem("charizard"), unItem("vulpix")))
        val vm = ListaViewModel(repo, notificador)

        vm.buscar("CHAR")

        assertEquals(listOf("charmander", "charizard"), (vm.estado.value as UiState.Exito).datos.map { it.id })
    }

    @Test
    fun buscarSinCoincidenciasDejaLaListaVaciaYBorrarLaRecupera() {
        val repo = FakeItemRepository(listOf(unItem("charmander"), unItem("vulpix")))
        val vm = ListaViewModel(repo, notificador)

        vm.buscar("pikachu")
        assertTrue((vm.estado.value as UiState.Exito).datos.isEmpty())

        vm.buscar("")
        assertEquals(2, (vm.estado.value as UiState.Exito).datos.size)
    }
}

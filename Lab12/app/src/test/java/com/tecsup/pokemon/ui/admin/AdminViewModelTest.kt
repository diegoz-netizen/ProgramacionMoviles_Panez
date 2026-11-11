package com.tecsup.pokemon.ui.admin

import com.tecsup.pokemon.FakeItemRepository
import com.tecsup.pokemon.MainDispatcherRule
import com.tecsup.pokemon.notification.Notificador
import com.tecsup.pokemon.unItem
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

private class FakeNotificador : Notificador {
    val avisos = mutableListOf<Pair<String, String>>()
    override fun mostrar(titulo: String, texto: String) {
        avisos += titulo to texto
    }
}

class AdminViewModelTest {
    @get:Rule
    val main = MainDispatcherRule()

    @Test
    fun muestraLosTotalesAlAbrir() {
        val repo = FakeItemRepository(listOf(unItem("charmander", true), unItem("vulpix")))
        val vm = AdminViewModel(repo, FakeNotificador())

        assertEquals(2, vm.ui.value.totalGuardado)
        assertEquals(1, vm.ui.value.totalFavoritos)
    }

    @Test
    fun sincronizarAvisaCuantosGuardo() {
        val repo = FakeItemRepository().apply { resultadoRefrescar = Result.success(3) }
        val notificador = FakeNotificador()
        val vm = AdminViewModel(repo, notificador)

        vm.sincronizar()

        assertEquals(listOf("Pokédex" to "Se guardaron 3 elementos"), notificador.avisos)
        assertFalse(vm.ui.value.sincronizando)
    }

    @Test
    fun sincronizarSinInternetNoNotifica() {
        val repo = FakeItemRepository().apply { resultadoRefrescar = Result.failure(IOException()) }
        val notificador = FakeNotificador()
        val vm = AdminViewModel(repo, notificador)

        vm.sincronizar()

        assertTrue(notificador.avisos.isEmpty())
        assertEquals("No se pudo sincronizar", vm.ui.value.mensaje)
    }

    @Test
    fun limpiarCacheDejaLosTotalesEnCero() {
        val repo = FakeItemRepository(listOf(unItem("charmander", true)))
        val vm = AdminViewModel(repo, FakeNotificador())

        vm.limpiarCache()

        assertEquals(0, vm.ui.value.totalGuardado)
        assertEquals(0, vm.ui.value.totalFavoritos)
        assertEquals("Caché limpiada", vm.ui.value.mensaje)
    }
}

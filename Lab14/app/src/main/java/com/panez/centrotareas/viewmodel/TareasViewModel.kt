package com.panez.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panez.centrotareas.data.TareasRepository
import com.panez.centrotareas.model.CargaUi
import com.panez.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Contador de toques: prueba de que la pantalla no está congelada
    private val _toques = MutableStateFlow(0)
    val toques: StateFlow<Int> = _toques.asStateFlow()

    fun tocar() {
        _toques.value++
    }

    // ----- Tarea con progreso (el avance llega como Flow desde el Repository)
    private val _mensaje = MutableStateFlow("Listo")
    val mensaje: StateFlow<String> = _mensaje.asStateFlow()

    private val _progreso = MutableStateFlow(0f)
    val progreso: StateFlow<Float> = _progreso.asStateFlow()

    private val _corriendo = MutableStateFlow(false)
    val corriendo: StateFlow<Boolean> = _corriendo.asStateFlow()

    private var trabajo: Job? = null

    fun tareaLarga() {
        if (_corriendo.value) return
        _corriendo.value = true
        trabajo = viewModelScope.launch {
            _progreso.value = 0f
            _mensaje.value = "Trabajando..."
            try {
                Log.d("HILO", "Antes: ${Thread.currentThread().name}")
                repo.progresoTarea().collect { _progreso.value = it }
                Log.d("HILO", "Despues: ${Thread.currentThread().name}")
                _mensaje.value = "Terminó"
            } finally {
                _corriendo.value = false
            }
        }
    }

    fun cancelar() {
        if (!_corriendo.value) return
        trabajo?.cancel()
        _mensaje.value = "Cancelado"
    }

    // ----- Contar primos
    private val _primos = MutableStateFlow<PrimosUi>(PrimosUi.Inicial)
    val primos: StateFlow<PrimosUi> = _primos.asStateFlow()

    private var trabajoPrimos: Job? = null

    fun contar(hasta: Int) {
        if (hasta < 2 || _primos.value is PrimosUi.Calculando) return
        _primos.value = PrimosUi.Calculando
        trabajoPrimos = viewModelScope.launch {
            val inicio = System.currentTimeMillis()
            try {
                val cantidad = repo.contarPrimos(hasta)
                _primos.value = PrimosUi.Listo(cantidad, System.currentTimeMillis() - inicio)
            } catch (e: CancellationException) {
                _primos.value = PrimosUi.Cancelado
                throw e
            }
        }
    }

    fun cancelarPrimos() {
        trabajoPrimos?.cancel()
    }

    // ----- Dos cargas con tiempo máximo por carga
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()

    private val _simularError = MutableStateFlow(false)
    val simularError: StateFlow<Boolean> = _simularError.asStateFlow()

    private val _tiempoMaximoActivo = MutableStateFlow(true)
    val tiempoMaximoActivo: StateFlow<Boolean> = _tiempoMaximoActivo.asStateFlow()

    fun cambiarSimularError(valor: Boolean) {
        _simularError.value = valor
    }

    fun cambiarTiempoMaximo(valor: Boolean) {
        _tiempoMaximoActivo.value = valor
    }

    fun cargarUnaPorUna() = ejecutarCarga("Una por una") {
        val perfil = conTiempoMaximo { repo.cargarPerfil() }
        val pedidos = conTiempoMaximo { repo.cargarPedidos(_simularError.value) }
        perfil to pedidos
    }

    fun cargarALaVez() = ejecutarCarga("A la vez") {
        coroutineScope {
            val perfil = async { conTiempoMaximo { repo.cargarPerfil() } }
            val pedidos = async { conTiempoMaximo { repo.cargarPedidos(_simularError.value) } }
            perfil.await() to pedidos.await()
        }
    }

    private fun ejecutarCarga(modo: String, cargar: suspend () -> Pair<String, List<String>>) {
        if (_carga.value is CargaUi.Cargando) return
        _carga.value = CargaUi.Cargando
        viewModelScope.launch {
            val inicio = System.currentTimeMillis()
            try {
                val (perfil, pedidos) = cargar()
                _carga.value = CargaUi.Exito(
                    modo = modo,
                    perfil = perfil,
                    pedidos = pedidos.size,
                    milisegundos = System.currentTimeMillis() - inicio
                )
            } catch (e: TimeoutCancellationException) {
                // Va primero porque es una subclase de CancellationException.
                _carga.value = CargaUi.Error("Tardó demasiado")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _carga.value = CargaUi.Error("No se pudo cargar: ${e.message}")
            }
        }
    }

    private suspend fun <T> conTiempoMaximo(bloque: suspend () -> T): T =
        if (_tiempoMaximoActivo.value) withTimeout(TIEMPO_MAXIMO_MS) { bloque() } else bloque()

    private companion object {
        const val TIEMPO_MAXIMO_MS = 2000L
    }
}

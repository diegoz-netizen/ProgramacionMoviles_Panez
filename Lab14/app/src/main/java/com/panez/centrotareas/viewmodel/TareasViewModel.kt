package com.panez.centrotareas.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panez.centrotareas.data.TareasRepository
import com.panez.centrotareas.model.CargaUi
import com.panez.centrotareas.model.PrimosUi
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TareasViewModel(private val repo: TareasRepository) : ViewModel() {

    // ----- Contador de toques: prueba de que la pantalla no está congelada
    private val _toques = MutableStateFlow(0)
    val toques: StateFlow<Int> = _toques.asStateFlow()

    fun tocar() {
        _toques.value++
    }

    // ----- Tarea con progreso (pasos 1 a 4)
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
                withContext(Dispatchers.Default) {
                    Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
                    for (i in 1..100) {
                        delay(40)
                        _progreso.value = i / 100f
                    }
                }
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

    // ----- Contar primos (paso 5)
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

    // ----- Dos cargas (pasos 6 y 7)
    private val _carga = MutableStateFlow<CargaUi>(CargaUi.Inicial)
    val carga: StateFlow<CargaUi> = _carga.asStateFlow()
    // TODO Paso 6: fun cargarUnaPorUna() y fun cargarALaVez()
    // TODO Paso 7: simularError y manejo de errores
}

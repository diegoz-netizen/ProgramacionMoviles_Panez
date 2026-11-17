package com.panez.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panez.campusgps.data.GpsApagadoException
import com.panez.campusgps.data.LugaresRepository
import com.panez.campusgps.data.UbicacionRepository
import com.panez.campusgps.model.Lugar
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.model.Posicion
import com.panez.campusgps.model.TipoError
import com.panez.campusgps.model.UbicacionUi
import com.panez.campusgps.util.distanciaMetros
import com.panez.campusgps.util.formatearDistancia
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    // Modo edición: se resalta un pabellón a la vez y al tocar el mapa se mueve ahí
    private val _modoEdicion = MutableStateFlow(false)
    val modoEdicion: StateFlow<Boolean> = _modoEdicion.asStateFlow()

    // Última posición conocida, para recalcular distancias cuando se mueve un pabellón
    private var ultimaPosicion: Posicion? = null

    init {
        viewModelScope.launch {
            lugaresRepo.lugares.collect { lista ->
                val posicion = ultimaPosicion
                _lugares.value = if (posicion == null) lista.map { LugarConDistancia(it, null) }
                else ordenarPorDistancia(lista, posicion)
            }
        }
    }

    // El primero de la lista con distancia calculada; null mientras no hay ubicación
    val masCercano: StateFlow<Lugar?> = lugares
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun buscar() {
        // Sin Google Play Services no hay ubicación ni mapa: se avisa sin reintentar
        if (!ubicacionRepo.playServicesDisponible()) {
            _estado.value = UbicacionUi.Error(
                "Este dispositivo no tiene Google Play Services.",
                TipoError.SIN_PLAY_SERVICES
            )
            return
        }
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    _estado.value = UbicacionUi.Lista(it)
                    actualizarDistancias(it)
                }
                .onFailure { _estado.value = errorDe(it) }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(MENSAJE_SIN_PERMISO, TipoError.SIN_PERMISO)
    }

    // Calcula la distancia a cada lugar y publica la lista ordenada de menor a mayor
    private fun actualizarDistancias(posicion: Posicion) {
        ultimaPosicion = posicion
        val ordenados = ordenarPorDistancia(lugaresRepo.obtenerLugares(), posicion)
        _lugares.value = ordenados
        ordenados.forEach {
            Log.d("CampusGPS", "${it.lugar.nombre}: ${it.metros?.let(::formatearDistancia)}")
        }
    }

    private fun ordenarPorDistancia(lista: List<Lugar>, posicion: Posicion) =
        lista.map { LugarConDistancia(it, distanciaMetros(posicion, it)) }.sortedBy { it.metros }

    /** Entra o sale del modo edición. Al entrar se resalta el primer pabellón sin ubicar. */
    fun alternarEdicion() {
        val activar = !_modoEdicion.value
        _modoEdicion.value = activar
        if (activar) {
            val porId = lugaresRepo.obtenerLugares().sortedBy { it.id }
            _seleccionado.value = (porId.firstOrNull { !it.ubicado } ?: porId.firstOrNull())?.id
        }
    }

    /** En modo edición, el toque en el mapa mueve el pabellón resaltado y pasa al siguiente. */
    fun tocarMapa(latitud: Double, longitud: Double) {
        if (!_modoEdicion.value) return
        val id = _seleccionado.value ?: return
        lugaresRepo.moverLugar(id, latitud, longitud)
        siguiente()
    }

    fun siguiente() = moverSeleccion(+1)
    fun anterior() = moverSeleccion(-1)

    private fun moverSeleccion(paso: Int) {
        val ids = lugaresRepo.obtenerLugares().map { it.id }.sorted()
        if (ids.isEmpty()) return
        val actual = ids.indexOf(_seleccionado.value).coerceAtLeast(0)
        _seleccionado.value = ids[(actual + paso + ids.size) % ids.size]
    }

    // El lugar tocado en la lista (o null para ninguno)
    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }
    // Cada problema tiene su mensaje y su tipo (la pantalla elige el botón según el tipo)
    private fun errorDe(e: Throwable): UbicacionUi.Error = when (e) {
        is SecurityException -> UbicacionUi.Error(MENSAJE_SIN_PERMISO, TipoError.SIN_PERMISO)
        is GpsApagadoException -> UbicacionUi.Error(
            "No se pudo obtener la ubicación.\nActiva el GPS e intenta de nuevo",
            TipoError.GPS_APAGADO
        )
        else -> UbicacionUi.Error(
            e.message ?: "No se pudo obtener la ubicación",
            TipoError.OTRO
        )
    }

    private companion object {
        const val MENSAJE_SIN_PERMISO = "Sin permiso de ubicación no puedo mostrar dónde estás"
    }
}

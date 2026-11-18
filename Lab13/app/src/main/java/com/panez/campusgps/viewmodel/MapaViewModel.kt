package com.panez.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panez.campusgps.data.GpsApagadoException
import com.panez.campusgps.data.LugaresRepository
import com.panez.campusgps.data.PasillosRepository
import com.panez.campusgps.model.Recorrido
import com.panez.campusgps.util.calcularRecorrido
import com.panez.campusgps.util.estaEnElCampus
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository,
    private val pasillosRepo: PasillosRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado: StateFlow<UbicacionUi> = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(
        lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) }
    )
    val lugares: StateFlow<List<LugarConDistancia>> = _lugares.asStateFlow()

    // Destino: el lugar elegido en el mapa, en Lugares o en Rutas
    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado: StateFlow<Int?> = _seleccionado.asStateFlow()

    // Punto de partida de la ruta: null = mi ubicación; si no, el id de un lugar
    private val _origen = MutableStateFlow<Int?>(null)
    val origen: StateFlow<Int?> = _origen.asStateFlow()

    // Filtro de la pestaña Lugares: null = todos; si no, "Pabellón", "Deportes" o "Servicios"
    private val _filtro = MutableStateFlow<String?>(null)
    val filtro: StateFlow<String?> = _filtro.asStateFlow()

    // Texto de la barra de búsqueda del mapa
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    // true: la ruta planificada se dibuja en el mapa
    private val _ruta = MutableStateFlow(false)
    val ruta: StateFlow<Boolean> = _ruta.asStateFlow()

    private val red = pasillosRepo.obtenerRed()

    // Lugares de la pestaña Lugares, según la categoría elegida
    val visibles: StateFlow<List<LugarConDistancia>> = combine(lugares, filtro) { lista, cat ->
        lista.filter { cat == null || it.lugar.categoria == cat }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    // Sugerencias de la búsqueda, por nombre o número ("P7")
    val resultados: StateFlow<List<LugarConDistancia>> = combine(lugares, busqueda) { lista, texto ->
        val q = texto.trim()
        if (q.isEmpty()) emptyList()
        else lista.filter {
            it.lugar.nombre.contains(q, ignoreCase = true) || "P${it.lugar.id}".equals(q, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // La app guía dentro de Tecsup: lejos del campus no se calcula ruta desde tu ubicación
    val fueraDelCampus: StateFlow<Boolean> = combine(estado, origen) { est, inicio ->
        val posicion = (est as? UbicacionUi.Lista)?.posicion
        inicio == null && posicion != null && !estaEnElCampus(red, posicion)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Camino por los pasillos (Dijkstra) entre el origen y el destino elegidos
    val planificado: StateFlow<Recorrido?> = combine(origen, seleccionado, estado, lugares) { inicio, destinoId, est, lista ->
        val destino = lista.firstOrNull { it.lugar.id == destinoId }?.lugar
        val desde = if (inicio != null) {
            lista.firstOrNull { it.lugar.id == inicio }?.lugar?.let { Posicion(it.latitud, it.longitud) }
        } else {
            (est as? UbicacionUi.Lista)?.posicion?.takeIf { estaEnElCampus(red, it) }
        }
        if (destino == null || desde == null || inicio == destinoId) null
        else calcularRecorrido(red, desde, Posicion(destino.latitud, destino.longitud))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Lo que se dibuja en el mapa
    val recorrido: StateFlow<Recorrido?> = combine(planificado, ruta) { camino, activa ->
        if (activa) camino else null
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // El primero de la lista con distancia calculada; null mientras no hay ubicación
    val masCercano: StateFlow<Lugar?> = lugares
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun filtrar(categoria: String?) {
        _filtro.value = categoria
    }

    fun buscarTexto(texto: String) {
        _busqueda.value = texto
    }

    fun cambiarOrigen(id: Int?) {
        _origen.value = id
        _ruta.value = false
    }

    fun alternarRuta() {
        _ruta.value = !_ruta.value
    }

    fun iniciarRuta() {
        _ruta.value = true
    }

    /** "Llévame": va desde tu ubicación hasta ese lugar. */
    fun llevarA(id: Int) {
        _origen.value = null
        _seleccionado.value = id
        _ruta.value = true
    }

    /** Ruta frecuente: elige origen y destino de una vez. */
    fun planificar(origenId: Int, destinoId: Int) {
        _origen.value = origenId
        _seleccionado.value = destinoId
        _ruta.value = false
    }

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
        val ordenados = ordenarPorDistancia(lugaresRepo.obtenerLugares(), posicion)
        _lugares.value = ordenados
        ordenados.forEach {
            Log.d("CampusGPS", "${it.lugar.nombre}: ${it.metros?.let(::formatearDistancia)}")
        }
    }

    private fun ordenarPorDistancia(lista: List<Lugar>, posicion: Posicion) =
        lista.map { LugarConDistancia(it, distanciaMetros(posicion, it)) }.sortedBy { it.metros }

    // El lugar elegido (o null para ninguno)
    fun seleccionar(id: Int?) {
        if (id != _seleccionado.value) _ruta.value = false
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

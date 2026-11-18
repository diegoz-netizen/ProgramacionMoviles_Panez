package com.panez.campusgps.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.panez.campusgps.data.GpsApagadoException
import com.panez.campusgps.data.LugaresRepository
import com.panez.campusgps.data.PasillosRepository
import com.panez.campusgps.model.Recorrido
import com.panez.campusgps.model.RedPasillos
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

    // Filtro de la hoja inferior: null = todos; si no, "Pabellón", "Deportes" o "Servicios"
    private val _filtro = MutableStateFlow<String?>(null)
    val filtro: StateFlow<String?> = _filtro.asStateFlow()

    fun filtrar(categoria: String?) {
        _filtro.value = categoria
    }

    // Texto de la barra de búsqueda
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    fun buscarTexto(texto: String) {
        _busqueda.value = texto
    }

    // Ruta punteada desde el usuario hasta el lugar elegido
    private val _ruta = MutableStateFlow(false)
    val ruta: StateFlow<Boolean> = _ruta.asStateFlow()

    fun alternarRuta() {
        _ruta.value = !_ruta.value
    }

    /** "Llévame": elige el lugar y traza la ruta de una vez. */
    fun llevarA(id: Int) {
        _seleccionado.value = id
        _ruta.value = true
    }

    // Lo que se dibuja en el mapa y en la lista: categoría elegida y texto buscado
    val visibles: StateFlow<List<LugarConDistancia>> = combine(lugares, filtro, busqueda) { lista, cat, texto ->
        val q = texto.trim()
        lista.filter { item ->
            (cat == null || item.lugar.categoria == cat) &&
                (q.isEmpty() || item.lugar.nombre.contains(q, ignoreCase = true) ||
                    "P${item.lugar.id}".equals(q, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _lugares.value)

    // ---- Pasillos del campus (grafo para calcular la ruta a pie) ----
    val red: StateFlow<RedPasillos> = pasillosRepo.red

    // Modo para dibujar los pasillos tocando el mapa
    private val _modoPasillos = MutableStateFlow(false)
    val modoPasillos: StateFlow<Boolean> = _modoPasillos.asStateFlow()

    // Último punto dibujado: el siguiente toque se une con él
    private val _nodoActual = MutableStateFlow<Int?>(null)
    val nodoActual: StateFlow<Int?> = _nodoActual.asStateFlow()

    fun alternarPasillos() {
        _modoPasillos.value = !_modoPasillos.value
        _modoEdicion.value = false
        _nodoActual.value = null
        seleccionar(null)
    }

    /** Tocar un punto ya dibujado: lo une con el anterior (para cerrar cruces) y sigue desde ahí. */
    fun tocarNodo(id: Int) {
        _nodoActual.value?.let { if (it != id) pasillosRepo.unir(it, id) }
        _nodoActual.value = id
    }

    /** Empieza un pasillo nuevo sin unirlo con el último punto. */
    fun nuevoTramo() {
        _nodoActual.value = null
    }

    fun deshacerPasillo() {
        pasillosRepo.deshacer()
        _nodoActual.value = pasillosRepo.red.value.nodos.maxOfOrNull { it.id }
    }

    fun borrarPasillos() {
        pasillosRepo.borrarTodo()
        _nodoActual.value = null
    }

    // La app guía dentro de Tecsup: lejos del campus no se traza ruta
    val fueraDelCampus: StateFlow<Boolean> = combine(estado, red) { est, redActual ->
        val posicion = (est as? UbicacionUi.Lista)?.posicion
        posicion != null && !estaEnElCampus(redActual, posicion)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Camino a pie hasta el lugar elegido, por los pasillos (Dijkstra) o en línea recta si no hay red
    val recorrido: StateFlow<Recorrido?> = combine(ruta, seleccionado, estado, red, lugares) { r, id, est, redActual, lista ->
        val posicion = (est as? UbicacionUi.Lista)?.posicion
        val lugar = lista.firstOrNull { it.lugar.id == id }?.lugar
        if (!r || posicion == null || lugar == null || !estaEnElCampus(redActual, posicion)) null
        else calcularRecorrido(redActual, posicion, Posicion(lugar.latitud, lugar.longitud))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

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
        _modoPasillos.value = false
        val activar = !_modoEdicion.value
        _modoEdicion.value = activar
        if (activar) {
            val porId = lugaresRepo.obtenerLugares().sortedBy { it.id }
            _seleccionado.value = (porId.firstOrNull { !it.ubicado } ?: porId.firstOrNull())?.id
        }
    }

    /** En modo edición, el toque en el mapa mueve el pabellón resaltado y pasa al siguiente. */
    fun tocarMapa(latitud: Double, longitud: Double) {
        // Dibujando pasillos: cada toque agrega un punto unido al anterior
        if (_modoPasillos.value) {
            _nodoActual.value = pasillosRepo.agregarNodo(latitud, longitud, _nodoActual.value)
            return
        }
        // Fuera del modo edición, tocar el mapa cierra la ficha del lugar
        if (!_modoEdicion.value) {
            seleccionar(null)
            return
        }
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

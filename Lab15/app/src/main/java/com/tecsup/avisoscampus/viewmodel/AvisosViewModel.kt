package com.tecsup.avisoscampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.avisoscampus.data.AvisosRepository
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import com.tecsup.avisoscampus.model.PermisoUi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvisosViewModel(private val repo: AvisosRepository) : ViewModel() {

    val avisos: StateFlow<List<Aviso>> = repo.avisos

    // Evento de una sola vez: "muestra esta notificación".
    // El ViewModel NO tiene Context: lo recibe la pantalla.
    private val _eventos = MutableSharedFlow<Aviso>()
    val eventos: SharedFlow<Aviso> = _eventos.asSharedFlow()

    private val _permiso = MutableStateFlow<PermisoUi>(PermisoUi.SinPedir)
    val permiso: StateFlow<PermisoUi> = _permiso.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /** La pantalla avisa qué respondió la persona (o que ya estaba concedido). */
    fun permisoRespondido(concedido: Boolean) {
        _permiso.value = if (concedido) PermisoUi.Concedido else PermisoUi.Denegado
        if (concedido) _mensaje.value = null // ya se puede mostrar: el mensaje se quita solo
    }

    /** Anota el aviso en el historial y le pide a la pantalla que lo muestre. */
    fun avisarAhora() {
        viewModelScope.launch {
            val aviso = repo.registrar("Tarea lista", "Tu entrega fue registrada", Origen.BOTON)
            _eventos.emit(aviso)
        }
    }

    // Recordatorio: el Job permite cancelarlo; faltan es null si no hay uno activo
    private var trabajo: Job? = null
    private val _faltan = MutableStateFlow<Int?>(null)
    val faltan: StateFlow<Int?> = _faltan.asStateFlow()

    /** Cuenta regresiva en viewModelScope: sigue aunque la app se minimice o se rote. */
    fun recordarEn(segundos: Int = 10) {
        if (trabajo?.isActive == true) return
        trabajo = viewModelScope.launch {
            try {
                for (s in segundos downTo 1) {
                    _faltan.value = s
                    delay(1000)
                }
                val aviso = repo.registrar("Recordatorio", "Revisa tu laboratorio pendiente", Origen.RECORDATORIO)
                _eventos.emit(aviso)
            } finally {
                _faltan.value = null // termine bien o cancelado
            }
        }
    }

    /** Cancela la cuenta: no se emite ningún aviso. */
    fun cancelarRecordatorio() {
        trabajo?.cancel()
    }

    // Si la pantalla debe registrar el receptor del modo avión
    private val _escuchandoAvion = MutableStateFlow(false)
    val escuchandoAvion: StateFlow<Boolean> = _escuchandoAvion.asStateFlow()

    fun cambiarEscucha(valor: Boolean) {
        _escuchandoAvion.value = valor
    }

    /** El receptor avisó un cambio: se anota y se pide mostrarlo. */
    fun alCambiarModoAvion(activo: Boolean) {
        viewModelScope.launch {
            val estado = if (activo) "Activado" else "Desactivado"
            val aviso = repo.registrar("Modo avión", estado, Origen.MODO_AVION)
            _eventos.emit(aviso)
        }
    }

    /** La pantalla no pudo mostrar la notificación (no hay permiso). */
    fun noSePudoMostrar() {
        _mensaje.value = "Sin permiso: el aviso se guardó en el historial, pero no se mostró"
    }
}

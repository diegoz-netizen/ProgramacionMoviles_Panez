package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.prefs.PreferenciasStore
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Sesión de la app: quién está conectado, con qué perfil y los ajustes del dispositivo.
 * Lo comparten el Splash, el Login, el tema (el color depende del rol), Perfil y Ajustes.
 */
class SesionViewModel(
    private val repo: LavaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /** Pestaña con la que se abre el Login (al cambiar de perfil sin usuario recordado). */
    private val _rolSugerido = MutableStateFlow(Rol.CLIENTE)
    val rolSugerido: StateFlow<Rol> = _rolSugerido.asStateFlow()

    // ---------- Ajustes guardados en DataStore ----------
    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val notificaciones: StateFlow<Boolean> = prefs.notificaciones
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)
    val recibirPedidos: StateFlow<Boolean> = prefs.recibirPedidos
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    /** true cuando ya se revisó si había un perfil guardado (el Splash espera esto). */
    private val _sesionRevisada = MutableStateFlow(false)
    val sesionRevisada: StateFlow<Boolean> = _sesionRevisada.asStateFlow()

    init {
        // Si hay perfil guardado en DataStore, se recupera al usuario y se salta el login
        viewModelScope.launch {
            val perfil = prefs.perfilActivo.first()
            val id = prefs.usuarioId.first()
            if (perfil.isNotBlank() && id != 0) {
                _usuarioActual.value = repo.obtenerUsuario(id)
            }
            _sesionRevisada.value = true
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        viewModelScope.launch {
            if (correo.isBlank() || clave.isBlank()) {
                _error.value = "Completa el correo y la contraseña"
                return@launch
            }
            val usuario = repo.login(correo.trim(), clave)
            if (usuario == null) {
                _error.value = "Correo o contraseña incorrectos"
                return@launch
            }
            if (usuario.rol != rolSeleccionado.name) {
                _error.value = if (rolSeleccionado == Rol.ADMIN)
                    "Esta cuenta no es de administrador. Elige la pestaña Estudiante"
                else
                    "Esta cuenta es de administrador. Elige la pestaña Administrador"
                return@launch
            }
            prefs.guardarSesion(rolSeleccionado, usuario.id)
            _error.value = null
            _usuarioActual.value = usuario
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun cerrarSesion() {
        // Primero se limpia el usuario en memoria (de inmediato), luego DataStore.
        // Si fuera al revés, el Login vería todavía al usuario anterior y lo reenviaría a su pantalla.
        _usuarioActual.value = null
        _error.value = null
        viewModelScope.launch { prefs.cerrarSesion() }
    }

    /**
     * Cambia al otro perfil (Estudiante <-> Administrador) en el mismo dispositivo.
     * Si ese perfil ya tiene un usuario recordado, entra directo y guarda la sesión;
     * si no, cierra la sesión y deja el Login listo con la pestaña del otro perfil.
     * [alTerminar] recibe el nuevo usuario, o null si hay que ir al Login.
     */
    fun cambiarPerfil(alTerminar: (Usuario?) -> Unit) {
        val actual = _usuarioActual.value ?: return
        val otro = if (actual.rol == Rol.ADMIN.name) Rol.CLIENTE else Rol.ADMIN
        viewModelScope.launch {
            val id = prefs.ultimoUsuarioDe(otro).first()
            val usuario = if (id != 0) repo.obtenerUsuario(id) else null
            if (usuario != null && usuario.rol == otro.name) {
                prefs.guardarSesion(otro, usuario.id)
                _usuarioActual.value = usuario
                alTerminar(usuario)
            } else {
                _rolSugerido.value = otro
                cerrarSesion()
                alTerminar(null)
            }
        }
    }

    // ---------- Editar nombre y correo ----------
    private val _errorNombre = MutableStateFlow<String?>(null)
    val errorNombre: StateFlow<String?> = _errorNombre.asStateFlow()

    private val _errorCorreo = MutableStateFlow<String?>(null)
    val errorCorreo: StateFlow<String?> = _errorCorreo.asStateFlow()

    /**
     * Valida y guarda el nombre y el correo del usuario conectado.
     * Reglas: nombre de al menos 3 letras, correo con formato válido y que no lo use otro usuario
     * (el correo es con el que se inicia sesión, por eso no se puede repetir).
     * [alGuardar] se llama solo si todo salió bien (la pantalla cierra el formulario).
     */
    fun guardarDatos(nombre: String, correo: String, alGuardar: () -> Unit) {
        val actual = _usuarioActual.value ?: return
        val n = nombre.trim().replace(Regex("\\s+"), " ")
        val c = correo.trim().lowercase()
        _errorNombre.value = if (n.length < 3) "Escribe un nombre de al menos 3 letras" else null
        _errorCorreo.value = if (!FORMATO_CORREO.matches(c)) "Escribe un correo válido (ejemplo@correo.com)" else null
        if (_errorNombre.value != null || _errorCorreo.value != null) return

        viewModelScope.launch {
            if (repo.correoEnUso(c, actual.id)) {
                _errorCorreo.value = "Ese correo ya lo usa otra cuenta"
                return@launch
            }
            repo.actualizarDatos(actual.id, n, c)?.let { _usuarioActual.value = it }
            alGuardar()
        }
    }

    /** Limpia los errores al abrir o cerrar el formulario. */
    fun limpiarErroresDatos() {
        _errorNombre.value = null
        _errorCorreo.value = null
    }

    // ---------- Foto de perfil ----------
    private val _avisoFoto = MutableStateFlow<String?>(null)
    /** Mensaje si la imagen elegida no se pudo usar. */
    val avisoFoto: StateFlow<String?> = _avisoFoto.asStateFlow()

    /** Guarda la imagen elegida en la galería como foto del usuario conectado. */
    fun cambiarFoto(uriImagen: String) {
        val actual = _usuarioActual.value ?: return
        viewModelScope.launch {
            val nuevo = repo.cambiarFoto(actual.id, uriImagen)
            if (nuevo == null) {
                _avisoFoto.value = "No se pudo usar esa imagen. Prueba con otra."
            } else {
                _avisoFoto.value = null
                _usuarioActual.value = nuevo // todas las pantallas que muestran al usuario se actualizan
            }
        }
    }

    fun quitarFoto() {
        val actual = _usuarioActual.value ?: return
        viewModelScope.launch {
            repo.quitarFoto(actual.id)?.let { _usuarioActual.value = it }
            _avisoFoto.value = null
        }
    }

    fun cambiarModoOscuro(activo: Boolean) {
        viewModelScope.launch { prefs.guardarModoOscuro(activo) }
    }

    fun cambiarNotificaciones(activo: Boolean) {
        viewModelScope.launch { prefs.guardarNotificaciones(activo) }
    }

    fun cambiarRecibirPedidos(activo: Boolean) {
        viewModelScope.launch { prefs.guardarRecibirPedidos(activo) }
    }

    companion object {
        /** algo@dominio.ext, sin espacios. */
        val FORMATO_CORREO = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    }
}

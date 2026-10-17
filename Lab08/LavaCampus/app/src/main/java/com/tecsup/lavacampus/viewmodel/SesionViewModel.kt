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
 * Sesión de la app: quién está conectado y con qué perfil.
 * Lo comparten el Splash, el Login y el tema (el color depende del rol).
 */
class SesionViewModel(
    private val repo: LavaRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

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
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
        }
    }
}

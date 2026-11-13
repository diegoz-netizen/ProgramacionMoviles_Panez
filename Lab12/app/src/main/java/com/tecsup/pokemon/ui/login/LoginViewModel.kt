package com.tecsup.pokemon.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.pokemon.data.auth.AuthRepository
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado del ingreso. [revisandoSesion] es true mientras se busca una sesión guardada al abrir la app. */
data class LoginUiState(
    val revisandoSesion: Boolean = true,
    val cargando: Boolean = false,
    val error: String? = null,
    val sesion: Sesion? = null,
    /** true: se muestra la pantalla de registro en lugar del login. */
    val registrando: Boolean = false,
    /** Mensaje de confirmación en el login, por ejemplo después de registrarse. */
    val aviso: String? = null
)

/** Ingreso, sesión guardada y cierre de sesión. Vive mientras la app esté abierta. */
class LoginViewModel(private val auth: AuthRepository) : ViewModel() {

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    init {
        // Si Firebase recuerda una sesión, se entra directo sin pedir la clave
        viewModelScope.launch {
            _estado.value = LoginUiState(revisandoSesion = false, sesion = auth.sesionActual())
        }
    }

    fun iniciarSesion(correo: String, clave: String) {
        // Campos vacíos: se avisa sin llamar a Firebase
        if (correo.isBlank() || clave.isBlank()) {
            _estado.update { it.copy(error = "Ingresa tu correo y tu contraseña") }
            return
        }
        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            auth.iniciarSesion(correo.trim(), clave)
                .onSuccess { sesion -> _estado.update { it.copy(cargando = false, sesion = sesion) } }
                .onFailure { e -> _estado.update { it.copy(cargando = false, error = e.message) } }
        }
    }

    /** Crea una cuenta nueva (rol USUARIO) y vuelve al login para ingresar con ella. Valida sin llamar a Firebase. */
    fun registrar(correo: String, clave: String, confirmar: String) {
        val error = when {
            correo.isBlank() || clave.isBlank() || confirmar.isBlank() -> "Completa todos los campos"
            !correo.contains("@") -> "Escribe un correo válido"
            clave.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            clave != confirmar -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _estado.update { it.copy(error = error) }
            return
        }
        _estado.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            auth.registrar(correo.trim(), clave)
                .onSuccess {
                    _estado.update {
                        it.copy(cargando = false, registrando = false, aviso = "Cuenta creada. Ingresa con tu correo y contraseña.")
                    }
                }
                .onFailure { e -> _estado.update { it.copy(cargando = false, error = e.message) } }
        }
    }

    fun irARegistro() = _estado.update { it.copy(registrando = true, error = null) }
    fun volverALogin() = _estado.update { it.copy(registrando = false, error = null, aviso = null) }

    fun limpiarError() = _estado.update { it.copy(error = null, aviso = null) }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _estado.value = LoginUiState(revisandoSesion = false)
    }
}

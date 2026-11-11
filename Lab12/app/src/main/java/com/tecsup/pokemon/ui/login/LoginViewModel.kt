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
    val sesion: Sesion? = null
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

    fun limpiarError() = _estado.update { it.copy(error = null) }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _estado.value = LoginUiState(revisandoSesion = false)
    }
}

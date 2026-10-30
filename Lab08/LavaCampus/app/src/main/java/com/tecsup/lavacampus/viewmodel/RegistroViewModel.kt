package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** Error de cada campo del formulario de registro (null = sin error). */
data class ErroresRegistro(
    val nombre: String? = null,
    val codigo: String? = null,
    val correo: String? = null,
    val clave: String? = null,
    val confirmar: String? = null
) {
    val hayErrores get() = listOf(nombre, codigo, correo, clave, confirmar).any { it != null }
}

/**
 * Registro de un estudiante nuevo. Aquí viven las reglas del formulario;
 * la pantalla solo muestra los errores y el Repository solo guarda.
 */
class RegistroViewModel(private val repo: LavaRepository) : ViewModel() {

    private val _errores = MutableStateFlow(ErroresRegistro())
    val errores: StateFlow<ErroresRegistro> = _errores.asStateFlow()

    private val _guardando = MutableStateFlow(false)
    val guardando: StateFlow<Boolean> = _guardando.asStateFlow()

    /**
     * Reglas: nombre de al menos 3 letras; código U + 9 dígitos y no repetido;
     * correo válido y no repetido; contraseña de al menos 4 caracteres y confirmada.
     * Solo se registran estudiantes (rol CLIENTE). [alRegistrar] recibe el correo y la clave para iniciar sesión.
     */
    fun registrar(nombre: String, codigo: String, correo: String, clave: String, confirmar: String,
                  alRegistrar: (correo: String, clave: String) -> Unit) {
        if (_guardando.value) return
        val n = nombre.trim().replace(Regex("\\s+"), " ")
        val cod = codigo.trim().uppercase()
        val c = correo.trim().lowercase()
        var e = ErroresRegistro(
            nombre = if (n.length < 3) "Escribe tu nombre completo" else null,
            codigo = if (!FORMATO_CODIGO.matches(cod)) "El código es U seguido de 9 dígitos (U202310234)" else null,
            correo = if (!SesionViewModel.FORMATO_CORREO.matches(c)) "Escribe un correo válido (ejemplo@correo.com)" else null,
            clave = if (clave.length < 4) "La contraseña debe tener al menos 4 caracteres" else null,
            confirmar = if (confirmar != clave) "Las contraseñas no coinciden" else null
        )
        _errores.value = e
        if (e.hayErrores) return

        viewModelScope.launch {
            _guardando.value = true
            if (repo.correoEnUso(c, 0)) e = e.copy(correo = "Ese correo ya tiene una cuenta")
            if (repo.codigoEnUso(cod)) e = e.copy(codigo = "Ese código ya está registrado")
            _errores.value = e
            if (!e.hayErrores) {
                repo.registrarUsuario(
                    Usuario(nombre = n, codigo = cod, correo = c, clave = clave, rol = Rol.CLIENTE.name)
                )
                alRegistrar(c, clave)
            }
            _guardando.value = false
        }
    }

    fun limpiarError(campo: String) {
        _errores.value = when (campo) {
            "nombre" -> _errores.value.copy(nombre = null)
            "codigo" -> _errores.value.copy(codigo = null)
            "correo" -> _errores.value.copy(correo = null)
            "clave" -> _errores.value.copy(clave = null)
            else -> _errores.value.copy(confirmar = null)
        }
    }

    companion object {
        val FORMATO_CODIGO = Regex("^U\\d{9}$")
    }
}

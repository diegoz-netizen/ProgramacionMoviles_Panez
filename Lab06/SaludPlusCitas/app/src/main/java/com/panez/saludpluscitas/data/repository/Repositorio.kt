package com.panez.saludpluscitas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.panez.saludpluscitas.data.model.*

/** Datos en memoria (sin BD). Se pierden al cerrar la app. */
object Repositorio {
    val usuarios = mutableStateListOf(
        Usuario("Juan Pérez", "987654321", "juan@correo.com", "123456") // usuario de prueba
    )

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // ---- Usuarios ----
    fun registrarUsuario(u: Usuario): Boolean {
        if (usuarios.any { it.telefono == u.telefono }) return false
        usuarios.add(u)
        usuarioActual = u
        return true
    }

    fun iniciarSesion(identificador: String, contrasena: String): Boolean {
        val id = identificador.trim()
        val u = usuarios.find {
            (it.telefono == id || it.correo.equals(id, ignoreCase = true)) && it.contrasena == contrasena
        } ?: return false
        usuarioActual = u
        return true
    }

    fun cerrarSesion() { usuarioActual = null }
}

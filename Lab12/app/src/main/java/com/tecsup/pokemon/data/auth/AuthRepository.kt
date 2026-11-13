package com.tecsup.pokemon.data.auth

import com.tecsup.pokemon.domain.model.Sesion

/** Ingreso y sesión. El ViewModel depende de esta interface, no de Firebase. */
interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>

    /** Crea la cuenta en Firebase Authentication. Las cuentas nuevas son de rol USUARIO. */
    suspend fun registrar(correo: String, clave: String): Result<Sesion>

    /** null si no hay sesión. */
    suspend fun sesionActual(): Sesion?

    fun cerrarSesion()
}

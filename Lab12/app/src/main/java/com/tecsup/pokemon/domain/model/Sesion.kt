package com.tecsup.pokemon.domain.model

/** Tipo de usuario. Se lee de Firestore (usuarios/{uid}, campo rol), no se decide en la app. */
enum class Rol { ADMIN, USUARIO }

/** Persona que inició sesión. */
data class Sesion(val uid: String, val correo: String, val rol: Rol)

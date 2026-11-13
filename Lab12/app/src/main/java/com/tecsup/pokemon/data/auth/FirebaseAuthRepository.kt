package com.tecsup.pokemon.data.auth

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import com.tecsup.pokemon.domain.model.Rol
import com.tecsup.pokemon.domain.model.Sesion
import kotlinx.coroutines.tasks.await

/**
 * Ingreso con Firebase Authentication. El rol no se decide en la app:
 * se lee de Firestore, en usuarios/{uid} campo "rol" ("admin" o "usuario").
 */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = try {
        val uid = auth.signInWithEmailAndPassword(correo, clave).await().user!!.uid
        Result.success(leerSesion(uid, correo))
    } catch (e: Exception) {
        Result.failure(Exception(mensajeDe(e), e))
    }

    /**
     * Crea la cuenta. No se escribe el rol en Firestore (las reglas lo prohíben, write: if false):
     * sin documento en usuarios/{uid}, leerSesion() devuelve USUARIO. Un admin solo se crea desde la consola.
     */
    override suspend fun registrar(correo: String, clave: String): Result<Sesion> = try {
        val uid = auth.createUserWithEmailAndPassword(correo, clave).await().user!!.uid
        // Firebase deja la sesión abierta al crear la cuenta: se cierra para que la persona ingrese desde el login
        auth.signOut()
        Result.success(Sesion(uid, correo, Rol.USUARIO))
    } catch (e: Exception) {
        Result.failure(Exception(mensajeDe(e), e))
    }

    /** Firebase recuerda la sesión: al reabrir la app, currentUser sigue lleno hasta cerrarSesion(). */
    override suspend fun sesionActual(): Sesion? {
        val u = auth.currentUser ?: return null
        return try {
            leerSesion(u.uid, u.email ?: "")
        } catch (e: Exception) {
            null // sin internet ni caché de Firestore: se vuelve a pedir el ingreso
        }
    }

    override fun cerrarSesion() = auth.signOut()

    private suspend fun leerSesion(uid: String, correo: String): Sesion {
        val doc = db.collection("usuarios").document(uid).get().await()
        val rol = if (doc.getString("rol") == "admin") Rol.ADMIN else Rol.USUARIO
        return Sesion(uid, correo, rol)
    }

    /** Mensaje claro para la pantalla; el ViewModel no necesita conocer las excepciones de Firebase. */
    private fun mensajeDe(e: Exception) = when (e) {
        is FirebaseAuthInvalidCredentialsException, is FirebaseAuthInvalidUserException ->
            "Correo o contraseña incorrectos"
        is FirebaseAuthUserCollisionException -> "Ese correo ya tiene una cuenta"
        is FirebaseAuthWeakPasswordException -> "La contraseña debe tener al menos 6 caracteres"
        is FirebaseNetworkException -> "No hay conexión a internet"
        else -> "No se pudo iniciar sesión"
    }
}

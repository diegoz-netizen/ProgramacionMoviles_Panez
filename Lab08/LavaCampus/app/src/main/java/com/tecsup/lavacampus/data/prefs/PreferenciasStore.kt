package com.tecsup.lavacampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.tecsup.lavacampus.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

/** Una sola instancia de DataStore para toda la app, declarada a nivel de archivo. */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore("ajustes")

/**
 * Preferencias pequeñas del dispositivo (clave-valor).
 * Los pedidos, servicios y usuarios NO van aquí: van en Room.
 */
class PreferenciasStore(private val context: Context) {

    private object Claves {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")   // CLIENTE | ADMIN | ""
        val USUARIO_ID = intPreferencesKey("usuarioId")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val NOTIFICACIONES = booleanPreferencesKey("notificaciones")
        val RECIBIR_PEDIDOS = booleanPreferencesKey("recibirPedidos")
        // Último usuario de cada perfil, para "Cambiar de perfil" sin volver a iniciar sesión
        val ULTIMO_CLIENTE_ID = intPreferencesKey("ultimoClienteId")
        val ULTIMO_ADMIN_ID = intPreferencesKey("ultimoAdminId")
    }

    /** Si el archivo no se puede leer se usan los valores por defecto en lugar de cerrar la app. */
    private val datos: Flow<Preferences> = context.dataStore.data
        .catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }

    // ---------- Lectura: un Flow por preferencia ----------
    val perfilActivo: Flow<String> = datos.map { it[Claves.PERFIL_ACTIVO] ?: "" }
    val usuarioId: Flow<Int> = datos.map { it[Claves.USUARIO_ID] ?: 0 }
    val modoOscuro: Flow<Boolean> = datos.map { it[Claves.MODO_OSCURO] ?: false }
    val notificaciones: Flow<Boolean> = datos.map { it[Claves.NOTIFICACIONES] ?: true }
    val recibirPedidos: Flow<Boolean> = datos.map { it[Claves.RECIBIR_PEDIDOS] ?: true }

    /** Id del último usuario que entró con ese perfil (0 si nunca entró). */
    fun ultimoUsuarioDe(rol: Rol): Flow<Int> = datos.map {
        it[if (rol == Rol.ADMIN) Claves.ULTIMO_ADMIN_ID else Claves.ULTIMO_CLIENTE_ID] ?: 0
    }

    // ---------- Escritura: funciones suspend ----------
    suspend fun guardarSesion(rol: Rol, usuarioId: Int) {
        context.dataStore.edit {
            it[Claves.PERFIL_ACTIVO] = rol.name
            it[Claves.USUARIO_ID] = usuarioId
            it[if (rol == Rol.ADMIN) Claves.ULTIMO_ADMIN_ID else Claves.ULTIMO_CLIENTE_ID] = usuarioId
        }
    }

    suspend fun guardarModoOscuro(activo: Boolean) {
        context.dataStore.edit { it[Claves.MODO_OSCURO] = activo }
    }

    suspend fun guardarNotificaciones(activo: Boolean) {
        context.dataStore.edit { it[Claves.NOTIFICACIONES] = activo }
    }

    suspend fun guardarRecibirPedidos(activo: Boolean) {
        context.dataStore.edit { it[Claves.RECIBIR_PEDIDOS] = activo }
    }

    /** Borra solo la sesión; el modo oscuro y los ajustes del dispositivo se conservan. */
    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it.remove(Claves.PERFIL_ACTIVO)
            it.remove(Claves.USUARIO_ID)
        }
    }
}

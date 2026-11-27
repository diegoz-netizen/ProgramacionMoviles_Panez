package com.tecsup.avisoscampus.data

import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Lo que el ViewModel puede pedir sobre el historial. */
interface AvisosRepository {
    val avisos: Flow<List<Aviso>>
    suspend fun registrar(
        titulo: String,
        texto: String,
        origen: Origen,
        urgente: Boolean = false,
        detalle: String? = null
    ): Aviso
    suspend fun limpiar()
}

/** Guarda el historial en memoria mientras la app está abierta (la versión de la Fase 1). */
class AvisosRepositoryMemoria : AvisosRepository {

    private val _avisos = MutableStateFlow<List<Aviso>>(emptyList())
    override val avisos: StateFlow<List<Aviso>> = _avisos.asStateFlow()

    private var siguiente = 1

    override suspend fun registrar(
        titulo: String,
        texto: String,
        origen: Origen,
        urgente: Boolean,
        detalle: String?
    ): Aviso {
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val aviso = Aviso(siguiente++, titulo, texto, origen, hora, urgente, detalle)
        _avisos.value = listOf(aviso) + _avisos.value // lo más reciente arriba
        return aviso
    }

    override suspend fun limpiar() {
        _avisos.value = emptyList()
    }
}

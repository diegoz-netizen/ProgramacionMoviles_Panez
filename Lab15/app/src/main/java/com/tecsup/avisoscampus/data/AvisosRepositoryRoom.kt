package com.tecsup.avisoscampus.data

import com.tecsup.avisoscampus.data.local.dao.AvisoDao
import com.tecsup.avisoscampus.data.local.entity.AvisoEntity
import com.tecsup.avisoscampus.model.Aviso
import com.tecsup.avisoscampus.model.Origen
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Guarda el historial en SQLite: no se pierde al cerrar la app. */
class AvisosRepositoryRoom(private val dao: AvisoDao) : AvisosRepository {

    override val avisos: Flow<List<Aviso>> =
        dao.observarTodos().map { filas -> filas.map { it.aModelo() } }

    override suspend fun registrar(
        titulo: String,
        texto: String,
        origen: Origen,
        urgente: Boolean,
        detalle: String?
    ): Aviso {
        val hora = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val fila = AvisoEntity(
            titulo = titulo, texto = texto, detalle = detalle,
            origen = origen.name, hora = hora, urgente = urgente
        )
        val id = dao.insertar(fila).toInt()
        return fila.copy(id = id).aModelo()
    }

    override suspend fun limpiar() = dao.borrarTodos()

    /** La pantalla y el ViewModel no conocen la entidad de Room. */
    private fun AvisoEntity.aModelo() =
        Aviso(id, titulo, texto, Origen.valueOf(origen), hora, urgente, detalle)
}

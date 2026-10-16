package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.LavaDatabase
import com.tecsup.lavacampus.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

/** Implementación del contrato con Room. Solo conoce los DAOs, nunca pantallas ni ViewModels. */
class LavaRepositoryRoom(private val db: LavaDatabase) : LavaRepository {

    private val servicioDao = db.servicioDao()

    // ---------- Servicios (listo) ----------
    override fun observarServicios(): Flow<List<Servicio>> = servicioDao.observarTodos()

    override fun observarServiciosActivos(): Flow<List<Servicio>> = servicioDao.observarActivos()

    override suspend fun obtenerServicio(id: Int): Servicio? = servicioDao.obtener(id)

    override suspend fun guardarServicio(s: Servicio) {
        if (s.id == 0) servicioDao.insertar(s) else servicioDao.actualizar(s)
    }

    override suspend fun eliminarServicio(s: Servicio) = servicioDao.eliminar(s)

    override suspend fun cambiarActivo(id: Int, activo: Boolean) = servicioDao.cambiarActivo(id, activo)

    override suspend fun servicioTieneDetalles(id: Int): Boolean =
        TODO("Paso 4: devolver detalleDao.contarPorServicio(id) > 0")

    // TODO (Paso 4): implementa usuarios, pedidos y panel delegando en los DAOs.
    //  crearPedido va dentro de db.withTransaction { ... }.
}

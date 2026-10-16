package com.tecsup.lavacampus.data.repository

import com.tecsup.lavacampus.data.local.entity.Servicio
import kotlinx.coroutines.flow.Flow

/**
 * El contrato de datos de la app. Los ViewModels solo conocen esta interface;
 * hoy la implementa Room y más adelante (Semana 9) un servicio web, sin tocar ViewModels ni pantallas.
 *
 * Regla de oro: lectura que actualiza la pantalla -> Flow (sin suspend).
 *               Escritura o acción única -> suspend.
 */
interface LavaRepository {

    // Servicios (ejemplo resuelto)
    fun observarServicios(): Flow<List<Servicio>>
    fun observarServiciosActivos(): Flow<List<Servicio>>
    suspend fun obtenerServicio(id: Int): Servicio?
    suspend fun guardarServicio(s: Servicio)
    suspend fun eliminarServicio(s: Servicio)
    suspend fun cambiarActivo(id: Int, activo: Boolean)
    suspend fun servicioTieneDetalles(id: Int): Boolean

    // TODO (Paso 4): cuando existan Usuario y PedidoConDetalle (Pasos 1 y 2), agrega:
    // Usuarios
    //   suspend fun login(correo: String, clave: String): Usuario?
    //   fun observarClientes(): Flow<List<ClienteResumen>>
    // Pedidos
    //   suspend fun crearPedido(usuarioId: Int, lineas: List<LineaPedido>, fechaEntrega: String, nota: String): Long
    //   suspend fun contarActivos(usuarioId: Int): Int
    //   fun observarPedidosDeUsuario(usuarioId: Int): Flow<List<PedidoConDetalle>>
    //   fun observarPedidos(): Flow<List<PedidoConDetalle>>
    //   fun observarPedido(id: Int): Flow<PedidoConDetalle?>
    //   suspend fun cambiarEstado(id: Int, estado: EstadoPedido, casillero: String?)
    //   suspend fun marcarPagado(id: Int, pagado: Boolean)
    //   fun observarCasillerosOcupados(): Flow<List<String>>
    // Panel
    //   fun observarPedidosDeFecha(fecha: String): Flow<Int>
    //   fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>
    //   fun observarIngresosDesde(fechaInicio: String): Flow<Double>
    //   fun observarIngresosPorServicio(fechaInicio: String): Flow<List<IngresoServicio>>
}

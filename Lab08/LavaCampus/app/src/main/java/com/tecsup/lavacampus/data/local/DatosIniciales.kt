package com.tecsup.lavacampus.data.local

import androidx.room.withTransaction
import com.tecsup.lavacampus.data.local.entity.DetallePedido
import com.tecsup.lavacampus.data.local.entity.Pedido
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.entity.Usuario
import com.tecsup.lavacampus.model.EstadoPedido
import com.tecsup.lavacampus.model.Rol
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/** Datos de prueba ficticios (Ley 29733). Se cargan solo la primera vez. */
object DatosIniciales {

    val servicios = listOf(
        Servicio(nombre = "Lavado y secado", descripcion = "Lavado, secado y doblado de ropa",
            unidad = "KG", precio = 8.00, horasEntrega = 24, icono = "wash"),
        Servicio(nombre = "Planchado", descripcion = "Planchado de camisas, pantalones y polos",
            unidad = "PRENDA", precio = 2.50, horasEntrega = 48, icono = "iron"),
        Servicio(nombre = "Lavado en seco", descripcion = "Para sacos, abrigos y prendas delicadas",
            unidad = "PRENDA", precio = 12.00, horasEntrega = 48, icono = "shirt"),
        Servicio(nombre = "Edredón / frazada", descripcion = "Lavado de ropa de cama gruesa",
            unidad = "UNIDAD", precio = 20.00, horasEntrega = 48, activo = false, icono = "wash")
    )

    val usuarios = listOf(
        Usuario(nombre = "Diego Panez", codigo = "U202310234", correo = "ana@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
        Usuario(nombre = "Luis Quispe", codigo = "U202311578", correo = "luis@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
        Usuario(nombre = "Marta Rojas", codigo = "U202312906", correo = "marta@correo.test", clave = "1234", rol = Rol.CLIENTE.name),
        Usuario(nombre = "Administrador", codigo = "", correo = "admin@lavacampus.test", clave = "admin", rol = Rol.ADMIN.name)
    )

    suspend fun sembrar(db: LavaDatabase) {
        if (db.usuarioDao().contar() > 0) return

        // Todo en una transacción: o se carga todo o no se carga nada.
        db.withTransaction {
            val s = servicios.map { it.copy(id = db.servicioDao().insertar(it).toInt()) }
            val (lavado, planchado, seco) = Triple(s[0], s[1], s[2])

            db.usuarioDao().insertarTodos(usuarios)
            val (diego, luis, marta) = usuarios.take(3).map { u ->
                requireNotNull(db.usuarioDao().login(u.correo, u.clave)).id
            }

            val hoy = LocalDate.now()
            // Fechas desde el lunes de esta semana, para que los ingresos de la semana siempre tengan datos
            val lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
            fun dia(diasAtras: Long): LocalDate = maxOf(hoy.minusDays(diasAtras), lunes)

            suspend fun pedido(
                usuarioId: Int,
                registro: LocalDate,
                estado: EstadoPedido,
                lineas: List<Pair<Servicio, Double>>,
                pagado: Boolean = false,
                casillero: String? = null,
                nota: String = ""
            ) {
                val detalles = lineas.map { (serv, cant) ->
                    DetallePedido(pedidoId = 0, servicioId = serv.id, cantidad = cant,
                        precioUnitario = serv.precio, subtotal = cant * serv.precio)
                }
                val dias = (lineas.maxOf { it.first.horasEntrega } / 24).toLong()
                var entrega = registro.plusDays(dias)
                if (entrega.dayOfWeek == DayOfWeek.SUNDAY) entrega = entrega.plusDays(1)

                val pedidoId = db.pedidoDao().insertar(
                    Pedido(
                        usuarioId = usuarioId,
                        fechaRegistro = registro.toString(),
                        fechaEntrega = entrega.toString(),
                        estado = estado.name,
                        total = detalles.sumOf { it.subtotal }, // coincide con la suma de sus detalles
                        casillero = casillero,
                        pagado = pagado,
                        nota = nota
                    )
                ).toInt()
                db.detalleDao().insertarTodos(detalles.map { it.copy(pedidoId = pedidoId) })
            }

            // ENTREGADOS y pagados (ingresos de la semana; el de Marta de hace 10 días es de la semana pasada)
            pedido(diego, lunes, EstadoPedido.ENTREGADO, listOf(lavado to 3.0, planchado to 4.0), pagado = true)
            pedido(luis, lunes, EstadoPedido.ENTREGADO, listOf(lavado to 5.0), pagado = true)
            pedido(marta, dia(3), EstadoPedido.ENTREGADO, listOf(seco to 3.0), pagado = true)
            pedido(marta, hoy.minusDays(10), EstadoPedido.ENTREGADO, listOf(lavado to 3.5), pagado = true)

            // LISTOS con casillero
            pedido(diego, dia(1), EstadoPedido.LISTO, listOf(lavado to 2.5), casillero = "C-05")
            pedido(luis, dia(1), EstadoPedido.LISTO, listOf(planchado to 6.0, seco to 1.0), pagado = true, casillero = "C-07")

            // EN PROCESO
            pedido(diego, hoy, EstadoPedido.EN_PROCESO, listOf(seco to 2.0), nota = "Saco azul, cuidado con los botones")
            pedido(marta, dia(1), EstadoPedido.EN_PROCESO, listOf(lavado to 2.0, planchado to 3.0))

            // RECIBIDOS de hoy
            pedido(luis, hoy, EstadoPedido.RECIBIDO, listOf(lavado to 4.0))
            pedido(marta, hoy, EstadoPedido.RECIBIDO, listOf(planchado to 8.0))

            // Un CANCELADO para el historial
            pedido(luis, dia(2), EstadoPedido.CANCELADO, listOf(lavado to 1.0))
        }
    }
}

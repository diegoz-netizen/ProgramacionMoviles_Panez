package com.tecsup.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Una línea del pedido. precioUnitario y subtotal son una "foto" del momento:
 * si mañana sube el precio del servicio, los pedidos antiguos conservan lo que costaron.
 */
@Entity(
    tableName = "detalles_pedido",
    foreignKeys = [
        ForeignKey(
            entity = Pedido::class,
            parentColumns = ["id"],
            childColumns = ["pedidoId"],
            onDelete = ForeignKey.CASCADE // al borrar un pedido se borran sus detalles
        ),
        ForeignKey(
            entity = Servicio::class,
            parentColumns = ["id"],
            childColumns = ["servicioId"],
            onDelete = ForeignKey.RESTRICT // un servicio usado en un pedido no se puede borrar
        )
    ],
    indices = [Index("pedidoId"), Index("servicioId")]
)
data class DetallePedido(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pedidoId: Int,
    val servicioId: Int,
    val cantidad: Double,
    val precioUnitario: Double,
    val subtotal: Double
)

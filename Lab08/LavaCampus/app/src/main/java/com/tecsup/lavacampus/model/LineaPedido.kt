package com.tecsup.lavacampus.model

/** Lo que el estudiante eligió en Nuevo pedido: un servicio y su cantidad. */
data class LineaPedido(
    val servicioId: Int,
    val cantidad: Double
)

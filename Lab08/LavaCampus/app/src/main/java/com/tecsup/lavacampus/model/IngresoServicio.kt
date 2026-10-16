package com.tecsup.lavacampus.model

/** Resultado de la consulta de ingresos agrupados por servicio (JOIN + SUM + GROUP BY). */
data class IngresoServicio(
    val nombre: String,
    val total: Double
)

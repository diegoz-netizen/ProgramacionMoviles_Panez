package com.panez.lab02carritokotlin

class ProductoFisico(
    id: String,
    nombre: String,
    precioBase: Double,
    cantidad: Int,
    val pesoKg: Double,
    val costoEnvioPorKg: Double = 5.0
) : Producto(id, nombre, precioBase, cantidad) {

    constructor() : this(
        id = "PF-000",
        nombre = "Producto Físico Genérico",
        precioBase = 0.0,
        cantidad = 1,
        pesoKg = 0.5,
        costoEnvioPorKg = 5.0
    )


    override fun calcularPrecioUnitario(): Double {
        val costoEnvioTotal = pesoKg * costoEnvioPorKg
        return precioBase + costoEnvioTotal
    }

    override fun mostrarDetalle(): String {
        return String.format("%s (Físico - %.1fkg)", super.mostrarDetalle(), pesoKg)
    }
}
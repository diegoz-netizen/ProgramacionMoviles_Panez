package com.panez.lab02carritokotlin

class ProductoDigital(
    id: String,
    nombre: String,
    precioBase: Double,
    cantidad: Int,
    val tamanoMB: Double,
    val tipoLicencia: String
) : Producto(id, nombre, precioBase, cantidad) {

    constructor() : this(
        id = "PD-000",
        nombre = "Licencia Digital Estándar",
        precioBase = 0.0,
        cantidad = 1,
        tamanoMB = 100.0,
        tipoLicencia = "ANUAL"
    )

    override fun calcularPrecioUnitario(): Double {
        return if (tipoLicencia.equals("VITALICIA", ignoreCase = true)) {
            precioBase * 1.10
        } else {
            precioBase
        }
    }

    override fun mostrarDetalle(): String {
        return String.format("%s (Digital - %.0fMB [%s])", super.mostrarDetalle(), tamanoMB, tipoLicencia)
    }
}
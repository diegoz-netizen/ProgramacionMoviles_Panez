package com.panez.lab02carritokotlin

abstract class Producto(
    val id: String,
    val nombre: String,
    precioBase: Double,
    cantidad: Int
) {
    var precioBase: Double = precioBase
        protected set(value) {
            require(value >= 0.0) { "El precio no puede ser negativo" }
            field = value
        }

    var cantidad: Int = cantidad
        set(value) {
            require(value > 0) { "La cantidad debe ser mayor a 0" }
            field = value
        }

    init {
        require(precioBase >= 0.0) { "El precio base no puede ser negativo" }
        require(cantidad > 0) { "La cantidad inicial debe ser mayor a 0" }
    }
    abstract fun calcularPrecioUnitario(): Double
    fun calcularImporte(): Double {
        return calcularPrecioUnitario() * cantidad
    }

    open fun mostrarDetalle(): String {
        return String.format("%-20s x%d  S/%8.2f", nombre, cantidad, calcularImporte())
    }
}
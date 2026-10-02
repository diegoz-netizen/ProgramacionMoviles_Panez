package com.tecsup.lab06tecsupstore

data class Producto(
    val nombre: String,
    val precio: Double,
    val cantidad: Int
) {
    val subtotal: Double
        get() = precio * cantidad
}
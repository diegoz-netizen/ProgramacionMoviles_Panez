package com.panez.lab02carritokotlin

class CarritoDeCompras(val clienteNombre: String) : Facturable {

    constructor() : this("ANÓNIMO")

    private val _productos = mutableListOf<Producto>()

    val productos: List<Producto>
        get() = _productos.toList()

    fun agregarProducto(producto: Producto) {
        _productos.add(producto)
        println("Producto agregado: ${producto.nombre}")
    }

    fun eliminarProductoPorId(id: String): Boolean {
        val removido = _productos.removeIf { it.id == id }
        if (removido) {
            println("Producto con ID '$id' eliminado del carrito.")
        } else {
            println("No se encontró el producto con ID '$id'.")
        }
        return removido
    }

    fun calcularSubtotal(): Double {
        return _productos.sumOf { it.calcularImporte() }
    }

    fun calcularIGVTotal(): Double {
        val subtotal = calcularSubtotal()
        return calcularIGV(subtotal)
    }

    fun calcularTotal(): Double {
        return calcularSubtotal() + calcularIGVTotal()
    }

    fun obtenerCantidadTotalItems(): Int {
        return _productos.sumOf { it.cantidad }
    }

    fun limpiarCarrito() {
        _productos.clear()
    }
}
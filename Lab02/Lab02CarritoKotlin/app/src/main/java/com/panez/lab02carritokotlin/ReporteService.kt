package com.panez.lab02carritokotlin

class ReporteService {

    fun imprimirCabecera(clienteNombre: String) {
        println("==================================")
        println("   CARRITO DE COMPRAS - TIENDA TECSUP  ")
        println("==================================")
        println("Cliente: $clienteNombre\n")
    }

    fun imprimirDetalle(carrito: CarritoDeCompras) {
        println("--------- DETALLE DEL CARRITO ---------")
        if (carrito.productos.isEmpty()) {
            println("El carrito se encuentra vacío.")
        } else {
            carrito.productos.forEachIndexed { index, producto ->
                val numeracion = index + 1
                println("$numeracion. ${producto.mostrarDetalle()}")
            }
        }
        println("---------------------------------------")
    }

    fun imprimirResumenTotales(carrito: CarritoDeCompras) {
        val subtotal = carrito.calcularSubtotal()
        val igv = carrito.calcularIGVTotal()
        val total = carrito.calcularTotal()
        val cantidadItems = carrito.obtenerCantidadTotalItems()

        println(String.format("%-22s: %d", "Cantidad de productos", cantidadItems))
        println(String.format("%-22s: S/%8.2f", "Subtotal", subtotal))
        println(String.format("%-22s: S/%8.2f", "IGV (18%)", igv))
        println(String.format("%-22s: S/%8.2f", "TOTAL A PAGAR", total))
    }

    fun generarReporteCompleto(carrito: CarritoDeCompras) {
        imprimirCabecera(carrito.clienteNombre)
        imprimirDetalle(carrito)
        println()
        imprimirResumenTotales(carrito)
    }
}
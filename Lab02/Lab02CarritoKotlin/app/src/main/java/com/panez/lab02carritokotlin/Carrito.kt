package com.panez.lab02carritokotlin
data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)
fun calcularSubtotal(productos:List<Producto>): Double {
    var subtotal = 0.0
    for(p in productos){
        subtotal += p.precio *   p.cantidad
    }
    return subtotal
}
fun calcularIGV(subtotal: Double): Double {
    // TODO: devuelve el 18% del subtotal
    var igv = 0.18
    return subtotal * igv
}
fun calcularTotal(subtotal: Double, igv: Double): Double {
    // TODO: devuelve la suma de ambos
    val precioTotal = subtotal + igv

    return precioTotal
}
fun mostrarDetalle(productos: List<Producto>) {
    println("--------- DETALLE DEL CARRITO ---------")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f",
            i, p.nombre, p.cantidad, importe))
        i++
    }
    println("---------------------------------------")
}

fun calcularDescuento(total: Double): Double {
    return when {
        total > 5000 -> total * 0.10
        total > 3000 -> total * 0.05
        else -> 0.0
    }
}

fun main() {
    println("==================================")
    println("  CARRITO DE COMPRAS - TIENDA TECSUP  ")
    println("==================================")

    val nombreCliente = "Juan Leon"
    val carrito = mutableListOf<Producto>()
    println("Cliente: $nombreCliente")
    println()

    carrito.add(Producto("Laptop HP", 2500.0,1))
    carrito.add(Producto("Mouse Logitech", 45.5, 2))
    carrito.add(Producto("Audifonos Sony", 120.0, 1))
    carrito.add(Producto("USB Kingston 64GB", 25.0, 3))

    for (producto in carrito){
        println("Producto agregado: ${producto.nombre}")
    }

    println()

    mostrarDetalle(carrito)

    val subtotal = calcularSubtotal(carrito)
    val igv = calcularIGV(subtotal)
    val total = calcularTotal(subtotal, igv)

    println(String.format("%-22s: %d", "Cantidad de productos", carrito.size))
    println(String.format("%-22s: S/%8.2f", "Subtotal", subtotal))
    println(String.format("%-22s: S/%8.2f", "IGV (18%)", igv))
    println(String.format("%-22s: S/%8.2f", "TOTAL A PAGAR", total))

    println("---------------------------------------")

    val masCaro = carrito.maxByOrNull { it.precio }
    if (masCaro != null) {
        println("Producto mas caro: ${masCaro.nombre} " +
                String.format("(S/ %.2f)", masCaro.precio))
    }

    val descuento = calcularDescuento(total)
    val totalDescuento = total - descuento

    val mensajeDescuento = when {
        total > 5000 -> "10% prociento por compra mayor a  S/ 5000"
        total > 3000 -> "5% porciento por compra mayor a S/ 3000"
        else -> "Sin descuento"
    }

    println("Descuento aplicado: $mensajeDescuento")
    println(String.format("%-22s: S/%8.2f", "TOTAL A CON DESCUENTO", totalDescuento))
    println()
    println("Gracias por su compra, $nombreCliente!")
}


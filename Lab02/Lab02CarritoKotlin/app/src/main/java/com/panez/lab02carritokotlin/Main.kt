package com.panez.lab02carritokotlin

fun main() {
    val carritoAnonimo = CarritoDeCompras()
    println("--- PRUEBA DE CARRITO CON CONSTRUCTOR SECUNDARIO ---")
    println("Cliente creado: ${carritoAnonimo.clienteNombre}")
    println("---------------------------------------------------\n")

    val clienteNombre = "Juan Leon"
    val carrito = CarritoDeCompras(clienteNombre)
    val reporteService = ReporteService()

    val laptop = ProductoFisico(
        id = "P01",
        nombre = "Laptop HP",
        precioBase = 2500.0,
        cantidad = 1,
        pesoKg = 2.2,
        costoEnvioPorKg = 5.0
    )

    val mouse = ProductoFisico(
        id = "P02",
        nombre = "Mouse Logitech",
        precioBase = 45.5,
        cantidad = 2,
        pesoKg = 0.3
    )

    val audifonos = ProductoDigital(
        id = "D01",
        nombre = "Audifonos Sony Driver",
        precioBase = 120.0,
        cantidad = 1,
        tamanoMB = 15.0,
        tipoLicencia = "ESTANDAR"
    )

    val usb = ProductoFisico(
        id = "P03",
        nombre = "USB Kingston 64GB",
        precioBase = 25.0,
        cantidad = 3,
        pesoKg = 0.1
    )

    val ebook = ProductoDigital(
        id = "D02",
        nombre = "E-Book Kotlin POO",
        precioBase = 50.0,
        cantidad = 1,
        tamanoMB = 25.0,
        tipoLicencia = "VITALICIA"
    )

    println("=== AGREGANDO PRODUCTOS AL CARRITO ===")
    carrito.agregarProducto(laptop)
    carrito.agregarProducto(mouse)
    carrito.agregarProducto(audifonos)
    carrito.agregarProducto(usb)
    carrito.agregarProducto(ebook)
    println()

    println("=== PRUEBA DE ELIMINACIÓN DE PRODUCTO ===")
    carrito.eliminarProductoPorId("D01") // Elimina Audifonos Sony Driver
    println()

    reporteService.generarReporteCompleto(carrito)
}
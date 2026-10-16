package com.tecsup.lavacampus.model

/** Forma de cobro de un servicio. Se guarda en la tabla servicios como Unidad.name. */
enum class Unidad(val etiqueta: String, val abreviatura: String) {
    KG("Kilo", "kg"),
    PRENDA("Prenda", "prenda"),
    UNIDAD("Unidad", "unidad")
}

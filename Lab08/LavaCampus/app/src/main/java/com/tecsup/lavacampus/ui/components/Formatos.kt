package com.tecsup.lavacampus.ui.components

import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.local.relation.PedidoConDetalle
import com.tecsup.lavacampus.model.Unidad
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Textos que se repiten en varias pantallas, para que todas los muestren igual. */

private val ES = Locale.forLanguageTag("es-PE")
private val FORMATO_LARGO = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ES)
private val FORMATO_CORTO = DateTimeFormatter.ofPattern("d MMM yyyy", ES)

/** "2026-10-19" -> "Lunes 19 de octubre" */
fun fechaLarga(fecha: String): String = runCatching {
    LocalDate.parse(fecha).format(FORMATO_LARGO).replaceFirstChar { it.uppercase() }
}.getOrDefault(fecha)

/** "2026-10-19" -> "19 oct 2026" */
fun fechaCorta(fecha: String): String = runCatching {
    LocalDate.parse(fecha).format(FORMATO_CORTO)
}.getOrDefault(fecha)

/** 12.5 -> "S/ 12.50" */
fun soles(monto: Double): String = "S/ %.2f".format(monto)

/** 3.0 kg -> "3 kg", 2.5 kg -> "2.5 kg", 4 prendas -> "4 prendas" */
fun cantidadConUnidad(cantidad: Double, unidad: String): String {
    val numero = if (cantidad % 1.0 == 0.0) cantidad.toInt().toString() else "%.1f".format(cantidad)
    val u = runCatching { Unidad.valueOf(unidad) }.getOrNull()
    val texto = when {
        u == Unidad.KG -> "kg"
        u == null -> unidad.lowercase()
        cantidad == 1.0 -> u.abreviatura
        else -> u.abreviatura + "s"
    }
    return "$numero $texto"
}

/** "3 kg Lavado y secado · 4 prendas Planchado" */
fun PedidoConDetalle.resumen(): String =
    detalles.joinToString(" · ") { "${cantidadConUnidad(it.detalle.cantidad, it.servicio.unidad)} ${it.servicio.nombre}" }

/** Unidad de cobro en texto: KG -> "por kg", PRENDA -> "por prenda", UNIDAD -> "por unidad". */
fun Servicio.textoUnidad(): String = when (unidad) {
    Unidad.KG.name -> "por kg"
    Unidad.PRENDA.name -> "por prenda"
    Unidad.UNIDAD.name -> "por unidad"
    else -> "por ${unidad.lowercase()}"
}

/** "S/ 8.00 por kg" */
fun Servicio.textoPrecio(): String = "${soles(precio)} ${textoUnidad()}"

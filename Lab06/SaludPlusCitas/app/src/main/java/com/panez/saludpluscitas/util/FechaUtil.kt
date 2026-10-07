package com.panez.saludpluscitas.util

import java.time.DayOfWeek
import java.time.LocalDate

private val DIAS = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
private val MESES = listOf(
    "enero", "febrero", "marzo", "abril", "mayo", "junio",
    "julio", "agosto", "setiembre", "octubre", "noviembre", "diciembre"
)

fun LocalDate.esHabil() = dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY
fun LocalDate.nombreDia() = DIAS[dayOfWeek.value - 1]
fun LocalDate.etiquetaCorta() = nombreDia().take(3)
fun LocalDate.nombreMes() = MESES[monthValue - 1]

/** "Octubre 2026" */
fun LocalDate.mesAnio() = nombreMes().replaceFirstChar { it.uppercase() } + " " + year

/** "Martes 16 de setiembre 2026" */
fun LocalDate.fechaLarga() = "${nombreDia()} $dayOfMonth de ${nombreMes()} $year"

/** Convierte "2026-09-16" a texto largo; si no se puede parsear devuelve el original. */
fun String.fechaLegible(): String = runCatching { LocalDate.parse(this).fechaLarga() }.getOrDefault(this)

/**
 * Semana = bloque de 5 días hábiles consecutivos.
 * La semana 0 empieza hoy (o el lunes siguiente si hoy es fin de semana); nunca incluye días pasados.
 */
fun semanaHabil(hoy: LocalDate, semana: Int): List<LocalDate> =
    generateSequence(hoy) { it.plusDays(1) }
        .filter { it.esHabil() }
        .drop(5 * semana)
        .take(5)
        .toList()
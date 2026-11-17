package com.panez.campusgps.util

import java.util.Locale
import kotlin.math.ceil
import kotlin.math.roundToInt

// Menos de 1000 m: metros enteros ("350 m"). Desde 1000 m: km con un decimal ("1.5 km")
fun formatearDistancia(metros: Float): String =
    if (metros < 1000f) {
        "${metros.roundToInt()} m"
    } else {
        String.format(Locale.US, "%.1f km", metros / 1000f)
    }

// Minutos caminando a paso normal (unos 80 m por minuto); como mínimo 1
fun minutosAPie(metros: Float): Int = ceil(metros / 80f).toInt().coerceAtLeast(1)

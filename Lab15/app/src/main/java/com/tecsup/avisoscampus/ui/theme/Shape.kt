package com.tecsup.avisoscampus.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/** Formas amables: tarjetas muy redondeadas y etiquetas tipo píldora. */
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/** Píldora para etiquetas y la insignia de la campana. */
val FormaPildora = RoundedCornerShape(percent = 50)

/** Cabecera: solo las esquinas de abajo redondeadas. */
val FormaCabecera = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)

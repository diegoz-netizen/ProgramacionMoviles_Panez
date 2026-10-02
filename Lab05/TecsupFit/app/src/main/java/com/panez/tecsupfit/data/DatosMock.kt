package com.panez.tecsupfit.data

import androidx.compose.runtime.mutableStateListOf
import com.panez.tecsupfit.model.Clase
import com.panez.tecsupfit.model.Reserva
import com.panez.tecsupfit.model.Rutina

val listaClases = mutableStateListOf(
    Clase(
        1,
        "Yoga funcional",
        "7:00 am",
        "Sala 2",
        "45 min",
        "Relajación y estiramiento para empezar el día.",
        10,
        12
    ), Clase(
        2,
        "Cross Training",
        "6:00 pm",
        "Sala 1",
        "45 min",
        "Entrenamiento funcional de alta intensidad. Cupos limitados.",
        8,
        12
    ), Clase(
        3,
        "Spinning",
        "7:30 pm",
        "Sala 3",
        "45 min",
        "Cardio intenso sobre bicicleta estática.",
        12,
        15
    )
)

val reservasAgendadas = mutableStateListOf<Reserva>(
    Reserva(
        clase = listaClases[0], horario = "Ayer, 7:00 am", estado = "Completada"
    )
)

val listaRutinas = mutableStateListOf(
    Rutina(1, "Rutina de fuerza", "45 min", 8, false),
    Rutina(2, "Cardio HIIT", "30 min", 6, true),
    Rutina(3, "Full body", "60 min", 10, false)
)
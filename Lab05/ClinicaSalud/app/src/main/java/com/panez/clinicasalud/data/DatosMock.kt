package com.panez.clinicasalud.data

import androidx.compose.runtime.mutableStateListOf
import com.panez.clinicasalud.model.Cita
import com.panez.clinicasalud.model.Medico

val listaMedicos = listOf(
    Medico(1, "Dra. Ana Torres", "Cardiología", 4.9, 128,
        "Especialista en arritmias e hipertensión. Formación en la Clínica Mayo."),
    Medico(2, "Dr. Luis Vega", "Pediatría", 4.7, 95,
        "Pediatra con 10 años de experiencia en atención infantil."),
    Medico(3, "Dra. Rosa Díaz", "Dermatología", 4.8, 112,
        "Dermatóloga especializada en tratamientos estéticos y clínicos.")
)

val citasAgendadas = mutableStateListOf<Cita>(
    Cita(
        medico = listaMedicos[1],
        fecha = "Miércoles 15",
        hora = "3:00 pm",
        estado = "Completada"
    )
)
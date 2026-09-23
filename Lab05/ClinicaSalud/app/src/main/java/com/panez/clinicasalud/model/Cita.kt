package com.panez.clinicasalud.model

data class Cita(
    val medico: Medico,
    val fecha: String,
    val hora: String,
    val estado: String
)
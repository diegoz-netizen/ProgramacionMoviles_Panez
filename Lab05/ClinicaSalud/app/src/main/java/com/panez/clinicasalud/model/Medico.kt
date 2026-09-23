package com.panez.clinicasalud.model

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val calificacion: Double,
    val resenas: Int,
    val descripcion: String
)
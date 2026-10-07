package com.panez.saludpluscitas.data.model

data class Cita(
    val id: Int,
    val medicoId: Int,
    val especialidadId: Int,
    val fecha: String,
    val hora: String,
    val motivo: String,
    val usuarioTelefono: String
)
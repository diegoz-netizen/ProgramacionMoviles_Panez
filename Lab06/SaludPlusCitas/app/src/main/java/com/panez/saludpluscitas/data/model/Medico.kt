package com.panez.saludpluscitas.data.model

data class Medico(
    val id: Int,
    val nombre: String,
    val especialidadId: Int,
    val sedeId: Int,
    val descripcion: String,
    val rating: Double,
    val resenas: Int,
    val cmp: String,
    val direccion: String,
    val diasAtencion: List<Int> // 1=Lunes ... 7=Domingo
)
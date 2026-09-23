package com.panez.tecsupfit.model

data class Clase(
    val id: Int,
    val nombre: String,
    val horario: String,
    val sala: String,
    val duracion: String,
    val descripcion: String,
    val cuposDisponibles: Int,
    val cuposTotales: Int
)
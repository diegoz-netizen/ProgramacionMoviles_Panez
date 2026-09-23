package com.panez.tecsupfit.model

data class Rutina(
    val id: Int,
    val nombre: String,
    val duracion: String,
    val ejercicios: Int,
    val completada: Boolean
)
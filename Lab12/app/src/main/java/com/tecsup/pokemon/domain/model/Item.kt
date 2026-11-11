package com.tecsup.pokemon.domain.model

/**
 * Modelo que usa la UI. La lista solo llena los primeros campos (salen de Room);
 * el detalle además trae número, medidas, tipos y estadísticas desde el segundo endpoint.
 */
data class Item(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false,
    val numero: Int? = null,
    val alturaM: Double? = null,
    val pesoKg: Double? = null,
    val tipos: List<String> = emptyList(),
    val estadisticas: List<Estadistica> = emptyList()
)

/** Estadística base del Pokémon, por ejemplo "hp" = 90. */
data class Estadistica(val nombre: String, val valor: Int)

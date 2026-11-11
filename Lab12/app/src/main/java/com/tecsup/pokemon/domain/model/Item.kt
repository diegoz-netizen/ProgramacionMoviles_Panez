package com.tecsup.pokemon.domain.model

data class Item(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String?,
    val esFavorito: Boolean = false
)

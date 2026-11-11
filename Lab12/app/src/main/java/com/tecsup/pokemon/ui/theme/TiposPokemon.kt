package com.tecsup.pokemon.ui.theme

import androidx.compose.ui.graphics.Color

/** Nombre en español y color de cada tipo de PokeAPI (los colores clásicos de la Pokédex). */
data class TipoPokemon(val nombre: String, val color: Color)

private val tipos = mapOf(
    "fire" to TipoPokemon("Fuego", Color(0xFFEE8130)),
    "water" to TipoPokemon("Agua", Color(0xFF6390F0)),
    "grass" to TipoPokemon("Planta", Color(0xFF7AC74C)),
    "electric" to TipoPokemon("Eléctrico", Color(0xFFF7D02C)),
    "flying" to TipoPokemon("Volador", Color(0xFFA98FF3)),
    "ground" to TipoPokemon("Tierra", Color(0xFFE2BF65)),
    "rock" to TipoPokemon("Roca", Color(0xFFB6A136)),
    "fighting" to TipoPokemon("Lucha", Color(0xFFC22E28)),
    "psychic" to TipoPokemon("Psíquico", Color(0xFFF95587)),
    "ghost" to TipoPokemon("Fantasma", Color(0xFF735797)),
    "dragon" to TipoPokemon("Dragón", Color(0xFF6F35FC)),
    "dark" to TipoPokemon("Siniestro", Color(0xFF705746)),
    "steel" to TipoPokemon("Acero", Color(0xFFB7B7CE)),
    "fairy" to TipoPokemon("Hada", Color(0xFFD685AD)),
    "ice" to TipoPokemon("Hielo", Color(0xFF96D9D6)),
    "bug" to TipoPokemon("Bicho", Color(0xFFA6B91A)),
    "poison" to TipoPokemon("Veneno", Color(0xFFA33EA1)),
    "normal" to TipoPokemon("Normal", Color(0xFFA8A77A))
)

fun tipoPokemon(nombre: String) =
    tipos[nombre] ?: TipoPokemon(nombre.replaceFirstChar(Char::uppercase), Color(0xFF9E9E9E))

/** Nombre corto en español de cada estadística base. */
fun nombreEstadistica(nombre: String) = when (nombre) {
    "hp" -> "PS"
    "attack" -> "Ataque"
    "defense" -> "Defensa"
    "special-attack" -> "At. esp."
    "special-defense" -> "Def. esp."
    "speed" -> "Velocidad"
    else -> nombre
}

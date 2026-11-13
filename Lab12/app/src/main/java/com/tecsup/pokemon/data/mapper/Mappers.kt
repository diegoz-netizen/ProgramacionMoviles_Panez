package com.tecsup.pokemon.data.mapper

import com.tecsup.pokemon.data.local.ItemEntity
import com.tecsup.pokemon.data.remote.dto.ItemDto
import com.tecsup.pokemon.data.remote.dto.PokemonDetalleDto
import com.tecsup.pokemon.domain.model.Estadistica
import com.tecsup.pokemon.domain.model.Item

private const val SPRITES = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/"

/** "charizard-mega-x" -> "Charizard Mega X" */
private fun nombreBonito(nombre: String) =
    nombre.split("-").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

/** El número sale de la url: https://pokeapi.co/api/v2/pokemon/4/ -> 4 */
private fun ItemDto.numero(): String = url.trimEnd('/').substringAfterLast('/')

/** Dto (API) -> Entity (Room). El id es el nombre (lo usa el endpoint de detalle). */
fun ItemDto.aEntity(tipo: String, esFavorito: Boolean = false) = ItemEntity(
    id = name,
    titulo = nombreBonito(name),
    subtitulo = "N.º ${numero()} · Tipo $tipo",
    imagenUrl = "$SPRITES${numero()}.png",
    esFavorito = esFavorito,
    tipo = tipo
)

/** Entity (Room) -> modelo de la UI. */
fun ItemEntity.aModelo() = Item(
    id = id,
    titulo = titulo,
    subtitulo = subtitulo,
    imagenUrl = imagenUrl,
    esFavorito = esFavorito,
    tipo = tipo
)

/** Detalle -> modelo de la UI, con altura y peso en metros y kilos. */
fun PokemonDetalleDto.aModelo(esFavorito: Boolean) = Item(
    id = name,
    titulo = nombreBonito(name),
    subtitulo = "N.º $id · Tipos: ${types.joinToString { it.type.name }} · " +
        "Altura ${height / 10.0} m · Peso ${weight / 10.0} kg",
    imagenUrl = sprites.frontDefault,
    esFavorito = esFavorito,
    numero = id,
    alturaM = height / 10.0,
    pesoKg = weight / 10.0,
    tipos = types.map { it.type.name },
    estadisticas = stats.map { Estadistica(it.stat.name, it.valor) }
)

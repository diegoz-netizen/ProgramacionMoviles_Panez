package com.tecsup.pokemon.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Lo que responde PokeAPI. Solo se declaran los campos que usa la app;
// el resto se ignora (ignoreUnknownKeys en RetrofitClient).

/** GET type/fire: { "pokemon": [ { "pokemon": { "name", "url" } } ] } */
@Serializable
data class TipoDto(val pokemon: List<PokemonDelTipoDto>)

@Serializable
data class PokemonDelTipoDto(val pokemon: ItemDto)

/** Un Pokémon de la lista: nombre y url de su detalle (https://pokeapi.co/api/v2/pokemon/4/). */
@Serializable
data class ItemDto(val name: String, val url: String)

/** GET pokemon/{nombre} */
@Serializable
data class PokemonDetalleDto(
    val id: Int,
    val name: String,
    val height: Int, // en decímetros
    val weight: Int, // en hectogramos
    val types: List<TipoSlotDto>,
    val sprites: SpritesDto,
    val stats: List<EstadisticaDto> = emptyList()
)

/** { "base_stat": 90, "stat": { "name": "hp" } } */
@Serializable
data class EstadisticaDto(
    @SerialName("base_stat") val valor: Int,
    val stat: NombreDto
)

@Serializable
data class TipoSlotDto(val type: NombreDto)

@Serializable
data class NombreDto(val name: String)

@Serializable
data class SpritesDto(
    @SerialName("front_default") val frontDefault: String? = null
)

package com.tecsup.pokemon.data.remote

import com.tecsup.pokemon.data.remote.dto.PokemonDetalleDto
import com.tecsup.pokemon.data.remote.dto.TipoDto
import retrofit2.http.GET
import retrofit2.http.Path

/** Endpoints de PokeAPI que usa la app. */
interface ApiService {

    /** Lista: Pokémon de un tipo (la variante del grupo es fire). */
    @GET("type/{tipo}")
    suspend fun listar(@Path("tipo") tipo: String = TIPO): TipoDto

    /** Detalle de un Pokémon por su nombre. */
    @GET("pokemon/{nombre}")
    suspend fun detalle(@Path("nombre") nombre: String): PokemonDetalleDto

    companion object {
        const val TIPO = "fire"
    }
}

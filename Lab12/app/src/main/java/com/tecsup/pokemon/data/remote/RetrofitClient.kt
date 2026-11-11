package com.tecsup.pokemon.data.remote

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/** Crea el ApiService. La baseUrl termina en "/", si no Retrofit falla. */
object RetrofitClient {
    private const val BASE_URL = "https://pokeapi.co/api/v2/"

    // PokeAPI devuelve muchos campos que no usamos: se ignoran
    private val json = Json { ignoreUnknownKeys = true }

    // Muestra en Logcat la URL y el código de cada petición
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(ApiService::class.java)
    }
}

package com.tecsup.murocampus.data.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/** Cliente único de Retrofit: se crea una sola vez (by lazy). */
object RetrofitClient {

    private const val URL_BASE = "https://jsonplaceholder.typicode.com/"

    // ignoreUnknownKeys: si el servidor agrega un campo nuevo, la app no falla
    private val json = Json { ignoreUnknownKeys = true }

    // Agrega el encabezado Accept a todas las peticiones: le dice al servidor que esperamos JSON
    private val encabezadoJson = Interceptor { cadena ->
        val peticion = cadena.request().newBuilder()
            .header("Accept", "application/json")
            .build()
        cadena.proceed(peticion)
    }

    // Registra en Logcat cada petición y respuesta (solo para desarrollo).
    // Va después del encabezado para que el log muestre la petición ya con Accept.
    private val cliente = OkHttpClient.Builder()
        .addInterceptor(encabezadoJson)
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .client(cliente)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    val api: PostApi by lazy { retrofit.create(PostApi::class.java) }

    val usuarioApi: UsuarioApi by lazy { retrofit.create(UsuarioApi::class.java) }
}

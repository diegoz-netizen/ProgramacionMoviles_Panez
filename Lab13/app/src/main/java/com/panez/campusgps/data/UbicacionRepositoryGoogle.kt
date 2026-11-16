package com.panez.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.panez.campusgps.model.Posicion
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(context: Context) : UbicacionRepository {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)

    // El permiso lo pide la pantalla antes de llamar; si falta, Fused lanza SecurityException
    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> = try {
        val ubicacion = cliente.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()
        if (ubicacion != null) {
            Result.success(Posicion(ubicacion.latitude, ubicacion.longitude))
        } else {
            Result.failure(IllegalStateException("No se pudo obtener la ubicación"))
        }
    } catch (e: SecurityException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

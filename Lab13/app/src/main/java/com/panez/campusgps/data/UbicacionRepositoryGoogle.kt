package com.panez.campusgps.data

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager
import androidx.core.location.LocationManagerCompat
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.panez.campusgps.model.Posicion
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(private val context: Context) : UbicacionRepository {

    private val cliente = LocationServices.getFusedLocationProviderClient(context)
    private val locationManager = context.getSystemService(LocationManager::class.java)

    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) ==
            ConnectionResult.SUCCESS

    // El permiso lo pide la pantalla antes de llamar; si falta, Fused lanza SecurityException
    @SuppressLint("MissingPermission")
    override suspend fun obtenerPosicion(): Result<Posicion> = try {
        // Con la ubicación del celular apagada ni siquiera se intenta
        if (!LocationManagerCompat.isLocationEnabled(locationManager)) throw GpsApagadoException()
        val ubicacion = cliente.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).await()
        if (ubicacion != null) {
            Result.success(Posicion(ubicacion.latitude, ubicacion.longitude))
        } else {
            // getCurrentLocation devuelve null si no puede determinar la ubicación
            Result.failure(GpsApagadoException())
        }
    } catch (e: SecurityException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }
}

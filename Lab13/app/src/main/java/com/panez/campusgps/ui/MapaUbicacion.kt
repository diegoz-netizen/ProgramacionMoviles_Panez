package com.panez.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.panez.campusgps.model.Lugar
import com.panez.campusgps.model.LugarConDistancia
import com.panez.campusgps.model.UbicacionUi

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercano: Lugar?,
    modifier: Modifier = Modifier
) {
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(-12.0464, -77.0428), 12f)
    }

    // Cuando llega la ubicación, la cámara viaja hasta ella
    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            val aqui = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            camara.animate(CameraUpdateFactory.newLatLngZoom(aqui, 16f))
        }
    }

    // Al tocar un lugar de la lista, la cámara viaja hasta él
    LaunchedEffect(seleccionado) {
        val lugar = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar ?: return@LaunchedEffect
        camara.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lugar.latitud, lugar.longitud), 17f))
    }

    GoogleMap(modifier = modifier, cameraPositionState = camara) {
        // Marcador propio: rojo (el de por defecto)
        if (estado is UbicacionUi.Lista) {
            Marker(
                state = rememberUpdatedMarkerState(
                    position = LatLng(estado.posicion.latitud, estado.posicion.longitud)
                ),
                title = "Estás aquí",
                snippet = "Latitud ${estado.posicion.latitud}, longitud ${estado.posicion.longitud}"
            )
        }
        // Un marcador por cada lugar: azul, y el más cercano en verde (solo dibuja lo que recibe)
        lugares.forEach { item ->
            key(item.lugar.id) {
                val marcador = rememberUpdatedMarkerState(
                    position = LatLng(item.lugar.latitud, item.lugar.longitud)
                )
                // El lugar seleccionado muestra su nombre en el mapa
                LaunchedEffect(seleccionado) {
                    if (seleccionado == item.lugar.id) marcador.showInfoWindow()
                }
                Marker(
                    state = marcador,
                    title = item.lugar.nombre,
                    snippet = item.lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (item.lugar.id == masCercano?.id) BitmapDescriptorFactory.HUE_GREEN
                        else BitmapDescriptorFactory.HUE_AZURE
                    )
                )
            }
        }
    }
}

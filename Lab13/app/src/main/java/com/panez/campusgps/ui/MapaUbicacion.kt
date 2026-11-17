package com.panez.campusgps.ui

import androidx.compose.ui.graphics.Color
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapProperties
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
    modifier: Modifier = Modifier,
    modoEdicion: Boolean = false,
    onTocarMapa: (latitud: Double, longitud: Double) -> Unit = { _, _ -> },
    onTocarMarcador: (id: Int) -> Unit = {}
) {
    // La cámara empieza sobre Tecsup Lima (Santa Anita)
    val camara = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(TECSUP_LAT, TECSUP_LNG), 17f)
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

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        // Satélite con nombres de calles: se ven los techos de los pabellones
        properties = MapProperties(mapType = MapType.HYBRID),
        onMapClick = { onTocarMapa(it.latitude, it.longitude) }
    ) {
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
                val resaltado = item.lugar.id == seleccionado
                Marker(
                    state = marcador,
                    title = item.lugar.nombre,
                    snippet = if (item.lugar.ubicado) item.lugar.categoria else "${item.lugar.categoria} · por ubicar",
                    // Naranja: el resaltado. Verde: el más cercano. Violeta: sin ubicar. Azul: el resto
                    icon = BitmapDescriptorFactory.defaultMarker(
                        when {
                            resaltado -> BitmapDescriptorFactory.HUE_ORANGE
                            item.lugar.id == masCercano?.id -> BitmapDescriptorFactory.HUE_GREEN
                            !item.lugar.ubicado -> BitmapDescriptorFactory.HUE_VIOLET
                            else -> BitmapDescriptorFactory.HUE_AZURE
                        }
                    ),
                    alpha = if (modoEdicion && !resaltado) 0.6f else 1f,
                    // En modo edición, tocar un marcador lo resalta para volver a ubicarlo
                    onClick = {
                        if (modoEdicion) {
                            onTocarMarcador(item.lugar.id)
                            true
                        } else false
                    },
                    zIndex = if (resaltado) 1f else 0f
                )
                // El pabellón resaltado lleva un círculo alrededor
                if (resaltado) {
                    Circle(
                        center = LatLng(item.lugar.latitud, item.lugar.longitud),
                        radius = 18.0,
                        strokeColor = Color(0xFFFF9800),
                        strokeWidth = 4f,
                        fillColor = Color(0x44FF9800)
                    )
                }
            }
        }
    }
}

// Centro aproximado del campus de Tecsup Lima (Santa Anita)
private const val TECSUP_LAT = -12.0440
private const val TECSUP_LNG = -76.9528

package com.panez.campusgps.ui

import com.panez.campusgps.ui.theme.VerdeCercano
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.material3.MaterialTheme
import com.google.maps.android.compose.Polygon
import com.panez.campusgps.ui.theme.AmbarSeleccion
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.CameraUpdateFactory
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
    val colores = MaterialTheme.colorScheme
    val densidad = LocalDensity.current.density

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
        // Plano del campus: cada lugar es un bloque de color con su etiqueta encima (solo dibuja lo que recibe)
        lugares.forEach { item ->
            key(item.lugar.id) {
                val lugar = item.lugar
                val resaltado = lugar.id == seleccionado
                val color = when {
                    resaltado -> AmbarSeleccion
                    lugar.id == masCercano?.id -> VerdeCercano
                    !lugar.ubicado -> colores.outline
                    else -> colores.primary
                }
                // Bloque aproximado del edificio, centrado en su coordenada
                Polygon(
                    points = bloque(lugar.latitud, lugar.longitud, lugar.categoria),
                    fillColor = color.copy(alpha = if (resaltado) 0.55f else 0.38f),
                    strokeColor = color,
                    strokeWidth = if (resaltado) 6f else 3f,
                    zIndex = if (resaltado) 1f else 0f
                )
                val marcador = rememberUpdatedMarkerState(position = LatLng(lugar.latitud, lugar.longitud))
                // El lugar seleccionado muestra su nombre en el mapa
                LaunchedEffect(seleccionado) {
                    if (seleccionado == lugar.id) marcador.showInfoWindow()
                }
                val etiqueta = remember(lugar.id, color, densidad) {
                    etiquetaPabellon(
                        textoEtiqueta(lugar.id, lugar.nombre, lugar.categoria),
                        color.toArgb(),
                        android.graphics.Color.WHITE,
                        densidad
                    )
                }
                Marker(
                    state = marcador,
                    title = lugar.nombre,
                    snippet = if (lugar.ubicado) lugar.categoria else "${lugar.categoria} · por ubicar",
                    icon = etiqueta,
                    anchor = Offset(0.5f, 0.5f),
                    alpha = if (modoEdicion && !resaltado) 0.6f else 1f,
                    // En modo edición, tocar una etiqueta la resalta para volver a ubicarla
                    onClick = {
                        if (modoEdicion) {
                            onTocarMarcador(lugar.id)
                            true
                        } else false
                    },
                    zIndex = if (resaltado) 2f else 1f
                )
            }
        }
    }
}

// Centro aproximado del campus de Tecsup Lima (Santa Anita)
private const val TECSUP_LAT = -12.0440
private const val TECSUP_LNG = -76.9528

/**
 * Rectángulo aproximado de cada lugar alrededor de su coordenada (no es el contorno real del edificio).
 * 0.0001 grados son unos 11 m. Las canchas ocupan más espacio que un pabellón.
 */
private fun bloque(lat: Double, lng: Double, categoria: String): List<LatLng> {
    val (dLat, dLng) = if (categoria == "Deportes") 0.00022 to 0.00022 else 0.00008 to 0.00015
    return listOf(
        LatLng(lat - dLat, lng - dLng),
        LatLng(lat - dLat, lng + dLng),
        LatLng(lat + dLat, lng + dLng),
        LatLng(lat + dLat, lng - dLng)
    )
}

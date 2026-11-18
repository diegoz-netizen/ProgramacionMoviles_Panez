package com.panez.campusgps.ui

import com.panez.campusgps.model.Recorrido
import com.google.android.gms.maps.model.JointType
import androidx.compose.foundation.layout.PaddingValues
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.Circle
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
    onTocarMapa: () -> Unit = {},
    onTocarMarcador: (id: Int) -> Unit = {},
    recorrido: Recorrido? = null,
    contentPadding: PaddingValues = PaddingValues()
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
            camara.animate(CameraUpdateFactory.newLatLngZoom(aqui, 17.5f))
        }
    }

    val posicion = (estado as? UbicacionUi.Lista)?.posicion
    val elegido = lugares.firstOrNull { it.lugar.id == seleccionado }?.lugar

    // Al elegir un lugar la cámara viaja hasta él; con la ruta, encuadra todo el recorrido
    LaunchedEffect(seleccionado, recorrido != null) {
        val lugar = elegido ?: return@LaunchedEffect
        val destino = LatLng(lugar.latitud, lugar.longitud)
        if (recorrido != null) {
            val limites = LatLngBounds.builder().apply {
                recorrido.puntos.forEach { include(LatLng(it.latitud, it.longitud)) }
            }.build()
            camara.animate(CameraUpdateFactory.newLatLngBounds(limites, MARGEN_RUTA))
            // Si están muy cerca, no acercar de más (se perdería de vista el campus)
            if (camara.position.zoom > ZOOM_MAXIMO_RUTA) {
                camara.animate(CameraUpdateFactory.zoomTo(ZOOM_MAXIMO_RUTA))
            }
        } else {
            camara.animate(CameraUpdateFactory.newLatLngZoom(destino, 18f))
        }
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camara,
        // Satélite con nombres de calles: se ven los techos de los pabellones
        properties = MapProperties(mapType = MapType.HYBRID),
        // Sin los botones de Google: la app trae su propio botón de ubicación
        uiSettings = MapUiSettings(zoomControlsEnabled = false, myLocationButtonEnabled = false, mapToolbarEnabled = false),
        // Deja libre lo que tapan la búsqueda (arriba) y la tarjeta (abajo)
        contentPadding = contentPadding,
        onMapClick = { onTocarMapa() }
    ) {
        // Tu posición: punto azul con un halo, como en las apps de mapas
        if (posicion != null) {
            val aqui = LatLng(posicion.latitud, posicion.longitud)
            Circle(
                center = aqui,
                radius = 10.0,
                fillColor = colores.primary.copy(alpha = 0.18f),
                strokeColor = colores.primary.copy(alpha = 0.4f),
                strokeWidth = 2f
            )
            val punto = remember(densidad) { puntoUbicacion(colores.primary.toArgb(), densidad) }
            Marker(
                state = rememberUpdatedMarkerState(position = aqui),
                title = "Estás aquí",
                icon = punto,
                anchor = Offset(0.5f, 0.5f),
                zIndex = 3f
            )
        }
        // Ruta a pie por los pasillos del campus
        if (recorrido != null) {
            Polyline(
                points = recorrido.puntos.map { LatLng(it.latitud, it.longitud) },
                color = AmbarSeleccion,
                width = 12f,
                pattern = listOf(Dot(), Gap(16f)),
                jointType = JointType.ROUND,
                zIndex = 2f
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
                    snippet = lugar.categoria,
                    icon = etiqueta,
                    anchor = Offset(0.5f, 0.5f),
                    // Tocar una etiqueta abre su tarjeta
                    onClick = {
                        onTocarMarcador(lugar.id)
                        true
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
private const val MARGEN_RUTA = 160
private const val ZOOM_MAXIMO_RUTA = 18.5f

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

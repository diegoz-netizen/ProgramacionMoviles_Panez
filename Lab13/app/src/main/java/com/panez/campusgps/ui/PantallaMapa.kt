package com.panez.campusgps.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.panez.campusgps.model.UbicacionUi
import com.panez.campusgps.util.formatearDistancia
import com.panez.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val estado by vm.estado.collectAsState()

    val contexto = LocalContext.current
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { resultado ->
        // Basta con uno (FINE o COARSE) para buscar; si niega los dos, la app sigue abierta
        if (resultado.values.any { it }) vm.buscar() else vm.permisoDenegado()
    }

    fun tienePermiso(permiso: String) =
        ContextCompat.checkSelfPermission(contexto, permiso) == PackageManager.PERMISSION_GRANTED

    // Pantalla de la app en Ajustes, para conceder el permiso a mano
    fun abrirAjustes() {
        contexto.startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", contexto.packageName, null)
            )
        )
    }

    fun alPulsar() {
        if (tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION) ||
            tienePermiso(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            vm.buscar()
        } else {
            lanzador.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CampusGPS", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        val lugares by vm.lugares.collectAsState()
        val seleccionado by vm.seleccionado.collectAsState()
        val masCercano by vm.masCercano.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val error = estado as? UbicacionUi.Error
            if (error == null) {
                // El mapa ocupa el espacio libre de arriba
                MapaUbicacion(
                    estado = estado,
                    lugares = lugares,
                    seleccionado = seleccionado,
                    masCercano = masCercano,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = { alPulsar() }, enabled = estado !is UbicacionUi.Buscando) {
                    Text("Obtener mi ubicación")
                }
                when (val e = estado) {
                    UbicacionUi.Inicial -> Text(
                        "Pulsa el botón para empezar",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    UbicacionUi.Buscando -> Text("Buscando tu ubicación…")
                    is UbicacionUi.Lista -> Unit
                    is UbicacionUi.Error -> MensajeError(
                        error = e,
                        onAbrirAjustes = { abrirAjustes() },
                        onReintentar = { alPulsar() },
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (error == null) {
                    OutlinedButton(
                        onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                        enabled = masCercano != null,
                        // Azul como en el diseño (Material 3 lo pinta gris por defecto)
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (masCercano != null) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                    ) {
                        Text("Ir al más cercano")
                    }
                    val cercano = lugares.firstOrNull { it.lugar.id == masCercano?.id }
                    if (cercano?.metros != null) {
                        Text(
                            buildAnnotatedString {
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("Más cercano: ") }
                                append("${cercano.lugar.nombre} (${formatearDistancia(cercano.metros)})")
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    // La lista va debajo del mapa, con altura fija
                    ListaLugares(
                        lugares = lugares,
                        seleccionado = seleccionado,
                        onClick = { vm.seleccionar(it) },
                        masCercanoId = masCercano?.id,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                    )
                }
            }
        }
    }
}

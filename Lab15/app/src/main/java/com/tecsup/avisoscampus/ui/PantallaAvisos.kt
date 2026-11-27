package com.tecsup.avisoscampus.ui

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.tecsup.avisoscampus.R
import com.tecsup.avisoscampus.model.PermisoUi
import com.tecsup.avisoscampus.notificacion.Notificador
import com.tecsup.avisoscampus.receiver.ModoAvionReceiver
import com.tecsup.avisoscampus.ui.components.AnilloCuenta
import com.tecsup.avisoscampus.ui.components.CabeceraAvisos
import com.tecsup.avisoscampus.ui.components.EtiquetaEstado
import com.tecsup.avisoscampus.ui.components.TarjetaSeccion
import com.tecsup.avisoscampus.ui.components.TipoEtiqueta
import com.tecsup.avisoscampus.ui.theme.Dimens
import com.tecsup.avisoscampus.viewmodel.AvisosViewModel

private const val SEGUNDOS_RECORDATORIO = 10

@Composable
fun PantallaAvisos(vm: AvisosViewModel) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val faltan by vm.faltan.collectAsState()
    val escuchando by vm.escuchandoAvion.collectAsState()
    val avisos by vm.avisos.collectAsState()
    val mensaje by vm.mensaje.collectAsState()

    // El receptor vive solo mientras el switch está activo; al apagarlo o salir se quita
    DisposableEffect(escuchando) {
        if (escuchando) {
            val receptor = ModoAvionReceiver { activo -> vm.alCambiarModoAvion(activo) }
            ContextCompat.registerReceiver(
                contexto,
                receptor,
                IntentFilter(Intent.ACTION_AIRPLANE_MODE_CHANGED),
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
            onDispose { contexto.unregisterReceiver(receptor) }
        } else {
            onDispose { }
        }
    }

    // Abre el cuadro de permiso que dibuja Android y recibe la respuesta
    val lanzador = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    // Al iniciar: si el permiso ya estaba concedido, se lo decimos al ViewModel
    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) vm.permisoRespondido(true)
    }

    LaunchedEffect(Unit) {
        vm.eventos.collect { aviso ->
            val mostrado = Notificador.mostrar(contexto, aviso)
            if (!mostrado) vm.noSePudoMostrar()
        }
    }

    val (textoPermiso, tipoPermiso) = when (permiso) {
        PermisoUi.SinPedir -> "Sin pedir" to TipoEtiqueta.NEUTRA
        PermisoUi.Concedido -> "Permitido" to TipoEtiqueta.EXITO
        PermisoUi.Denegado -> "Denegado" to TipoEtiqueta.ERROR
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraAvisos(
            totalAvisos = avisos.size,
            estadoPermiso = textoPermiso,
            escuchandoAvion = escuchando
        )

        Column(
            modifier = Modifier.padding(Dimens.lg),
            verticalArrangement = Arrangement.spacedBy(Dimens.lg)
        ) {
            // 1. Permiso
            TarjetaSeccion(
                icono = R.drawable.ic_candado,
                titulo = "Permiso de notificaciones",
                subtitulo = "Android 13 o superior lo pide una vez",
                extremo = { EtiquetaEstado(textoPermiso, tipoPermiso) }
            ) {
                if (permiso != PermisoUi.Concedido) {
                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                vm.permisoRespondido(true) // antes de Android 13 no se pide
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) { Text("Permitir notificaciones") }
                }
                AnimatedVisibility(visible = mensaje != null) {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimens.sm)) {
                        Text(
                            mensaje.orEmpty(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.errorContainer, MaterialTheme.shapes.medium)
                                .padding(Dimens.md)
                        )
                        OutlinedButton(
                            onClick = {
                                // Abre la pantalla de notificaciones de esta app en Ajustes
                                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                                    .putExtra(Settings.EXTRA_APP_PACKAGE, contexto.packageName)
                                contexto.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                painterResource(R.drawable.ic_ajustes),
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.IconSize)
                            )
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Abrir ajustes")
                        }
                    }
                }
            }

            // 2. Avisos
            TarjetaSeccion(
                icono = R.drawable.ic_sobre,
                titulo = "Avisos",
                subtitulo = "Canal Avisos · importancia normal"
            ) {
                Button(onClick = { vm.avisarAhora() }, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        painterResource(R.drawable.ic_campana),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Mostrar aviso")
                }
            }

            // 3. Recordatorio
            val corriendo = faltan != null
            TarjetaSeccion(
                icono = R.drawable.ic_reloj,
                titulo = "Recordatorio",
                subtitulo = "Llega por el canal Urgentes",
                fondoIcono = MaterialTheme.colorScheme.tertiaryContainer,
                colorIcono = MaterialTheme.colorScheme.onTertiaryContainer,
                extremo = { EtiquetaEstado("Urgente", TipoEtiqueta.ACENTO) }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimens.lg)
                ) {
                    AnilloCuenta(faltan = faltan, total = SEGUNDOS_RECORDATORIO)
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Dimens.sm)
                    ) {
                        Text(
                            faltan?.let { "Faltan $it s" } ?: "Listo para programar",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Button(
                            onClick = { vm.recordarEn(SEGUNDOS_RECORDATORIO) },
                            enabled = !corriendo,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Recordar en 10 s") }
                        OutlinedButton(
                            onClick = { vm.cancelarRecordatorio() },
                            enabled = corriendo,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Cancelar") }
                    }
                }
            }

            // 4. Receptor de anuncios
            TarjetaSeccion(
                icono = R.drawable.ic_avion,
                titulo = "Receptor de anuncios",
                subtitulo = if (escuchando) "Escuchando cambios del sistema" else "Receptor apagado",
                fondoIcono = MaterialTheme.colorScheme.secondaryContainer,
                colorIcono = MaterialTheme.colorScheme.onSecondaryContainer
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Escuchar el modo avión",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(checked = escuchando, onCheckedChange = { vm.cambiarEscucha(it) })
                }
            }

            // 5. Historial
            HistorialAvisos(avisos, onLimpiar = { vm.limpiarHistorial() })

            Spacer(Modifier.height(Dimens.sm).navigationBarsPadding())
        }
    }
}

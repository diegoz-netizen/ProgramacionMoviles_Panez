package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.IconoServicio
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.viewmodel.SesionViewModel

/**
 * Login único para ambos perfiles. El selector cambia el color de toda la pantalla
 * (turquesa para el estudiante, violeta para el administrador).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(vm: SesionViewModel, onIngresar: (Rol) -> Unit) {
    val usuario by vm.usuarioActual.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    val oscuro by vm.modoOscuro.collectAsStateWithLifecycle()

    var rol by rememberSaveable { mutableStateOf(Rol.CLIENTE) }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }

    // Cuando el ViewModel confirma el login, la pantalla navega según el rol del usuario
    LaunchedEffect(usuario) {
        usuario?.let { onIngresar(Rol.valueOf(it.rol)) }
    }

    LavaTheme(rol = rol, oscuro = oscuro) {
        Column(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(24.dp))
            IconoServicio("wash", tam = 88.dp)
            Spacer(Modifier.height(16.dp))
            Text("LavaCampus", style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary)
            Text(
                if (rol == Rol.CLIENTE) "Tu ropa limpia, sin salir del campus"
                else "Gestiona los pedidos de la lavandería",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("Iniciar sesión", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(14.dp))

                    // Selector de rol
                    val opciones = listOf(
                        Triple(Rol.CLIENTE, "Estudiante", Icons.Filled.School),
                        Triple(Rol.ADMIN, "Administrador", Icons.Filled.AdminPanelSettings)
                    )
                    SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                        opciones.forEachIndexed { i, (valor, etiqueta, icono) ->
                            SegmentedButton(
                                selected = rol == valor,
                                onClick = { rol = valor; vm.limpiarError() },
                                shape = SegmentedButtonDefaults.itemShape(i, opciones.size),
                                icon = { Icon(icono, contentDescription = null) }
                            ) { Text(etiqueta) }
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    LavaTextField(
                        valor = correo,
                        onCambio = { correo = it; vm.limpiarError() },
                        etiqueta = "Correo",
                        icono = Icons.Filled.Email,
                        teclado = KeyboardType.Email
                    )
                    LavaTextField(
                        valor = clave,
                        onCambio = { clave = it; vm.limpiarError() },
                        etiqueta = "Contraseña",
                        icono = Icons.Filled.Lock,
                        esClave = true,
                        error = error
                    )
                    Spacer(Modifier.height(12.dp))
                    LavaButton("Ingresar", onClick = { vm.login(correo, clave, rol) })
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

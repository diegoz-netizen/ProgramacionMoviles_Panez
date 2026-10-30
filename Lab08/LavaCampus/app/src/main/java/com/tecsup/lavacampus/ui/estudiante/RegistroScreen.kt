package com.tecsup.lavacampus.ui.estudiante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.components.AppTopBar
import com.tecsup.lavacampus.ui.components.LavaButton
import com.tecsup.lavacampus.ui.components.LavaTextField
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.viewmodel.RegistroViewModel
import com.tecsup.lavacampus.viewmodel.SesionViewModel

/**
 * Crear cuenta de estudiante. Las validaciones las hace RegistroViewModel;
 * al registrarse, SesionViewModel inicia la sesión y NavLava lleva al Inicio.
 */
@Composable
fun RegistroScreen(vm: RegistroViewModel, sesion: SesionViewModel, onBack: () -> Unit) {
    val errores by vm.errores.collectAsStateWithLifecycle()
    val guardando by vm.guardando.collectAsStateWithLifecycle()
    val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()

    var nombre by rememberSaveable { mutableStateOf("") }
    var codigo by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var clave by rememberSaveable { mutableStateOf("") }
    var confirmar by rememberSaveable { mutableStateOf("") }

    LavaTheme(rol = Rol.CLIENTE, oscuro = oscuro) {
        Scaffold(
            topBar = { AppTopBar("Crear cuenta", onBack = onBack) },
            containerColor = MaterialTheme.colorScheme.background
        ) { pad ->
            Column(
                Modifier.padding(pad).imePadding().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Regístrate como estudiante para pedir el lavado de tu ropa.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))
                LavaTextField(nombre, { nombre = it; vm.limpiarError("nombre") }, "Nombre completo",
                    Icons.Filled.Person, error = errores.nombre)
                LavaTextField(codigo, { codigo = it; vm.limpiarError("codigo") }, "Código de estudiante",
                    Icons.Filled.Badge, error = errores.codigo)
                LavaTextField(correo, { correo = it; vm.limpiarError("correo") }, "Correo",
                    Icons.Filled.Email, teclado = KeyboardType.Email, error = errores.correo)
                LavaTextField(clave, { clave = it; vm.limpiarError("clave") }, "Contraseña",
                    Icons.Filled.Lock, esClave = true, error = errores.clave)
                LavaTextField(confirmar, { confirmar = it; vm.limpiarError("confirmar") }, "Repite la contraseña",
                    Icons.Filled.Lock, esClave = true, error = errores.confirmar)
                Spacer(Modifier.height(12.dp))
                LavaButton(
                    "Crear cuenta",
                    onClick = {
                        vm.registrar(nombre, codigo, correo, clave, confirmar) { c, k ->
                            sesion.login(c, k, Rol.CLIENTE)
                        }
                    },
                    enabled = !guardando,
                    icono = Icons.Filled.PersonAdd
                )
            }
        }
    }
}

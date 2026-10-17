package com.tecsup.lavacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.navigation.NavLava
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory
import com.tecsup.lavacampus.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as LavaApp
        val factory = LavaViewModelFactory(app.repository, app.preferencias)
        setContent {
            // Una sola sesión para toda la app: define el color del tema (rol) y el modo oscuro
            val sesion: SesionViewModel = viewModel(factory = factory)
            val usuario by sesion.usuarioActual.collectAsStateWithLifecycle()
            val oscuro by sesion.modoOscuro.collectAsStateWithLifecycle()
            val rol = usuario?.let { Rol.valueOf(it.rol) } ?: Rol.CLIENTE

            LavaTheme(rol = rol, oscuro = oscuro) {
                Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
                    NavLava(factory, sesion)
                }
            }
        }
    }
}

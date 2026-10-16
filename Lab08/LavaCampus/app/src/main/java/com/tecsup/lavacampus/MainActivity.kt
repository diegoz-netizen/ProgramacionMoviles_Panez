package com.tecsup.lavacampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.tecsup.lavacampus.model.Rol
import com.tecsup.lavacampus.ui.navigation.NavLava
import com.tecsup.lavacampus.ui.theme.LavaTheme
import com.tecsup.lavacampus.viewmodel.LavaViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as LavaApp
        val factory = LavaViewModelFactory(app.repository)
        setContent {
            // TODO (Pasos 6 y 13): el rol y el modo oscuro saldrán de PreferenciasStore
            LavaTheme(rol = Rol.ADMIN) {
                NavLava(factory)
            }
        }
    }
}

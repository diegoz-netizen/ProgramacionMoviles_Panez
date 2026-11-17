package com.panez.campusgps

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.panez.campusgps.ui.PantallaSplash
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panez.campusgps.ui.PantallaMapa
import com.panez.campusgps.ui.theme.CampusGPSTheme
import com.panez.campusgps.viewmodel.CampusViewModelFactory
import com.panez.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusGPSTheme {
                // Primero el splash con el logo; rememberSaveable: al girar no vuelve a mostrarse
                var mostrarSplash by rememberSaveable { mutableStateOf(true) }
                if (mostrarSplash) {
                    PantallaSplash(onTerminar = { mostrarSplash = false })
                } else {
                    val vm: MapaViewModel = viewModel(factory = CampusViewModelFactory(applicationContext))
                    PantallaMapa(vm)
                }
            }
        }
    }
}

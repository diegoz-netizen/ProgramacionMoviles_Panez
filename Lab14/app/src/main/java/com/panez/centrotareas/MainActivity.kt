package com.panez.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.panez.centrotareas.ui.PantallaTareas
import com.panez.centrotareas.ui.theme.CentroTareasTheme
import com.panez.centrotareas.viewmodel.TareasViewModel
import com.panez.centrotareas.viewmodel.TareasViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme {
                val vm: TareasViewModel = viewModel(factory = TareasViewModelFactory())
                PantallaTareas(vm)
            }
        }
    }
}

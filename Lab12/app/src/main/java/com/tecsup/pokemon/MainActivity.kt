package com.tecsup.pokemon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.tecsup.pokemon.ui.navigation.AppNav
import com.tecsup.pokemon.ui.theme.PokedexTheme

class MainActivity : ComponentActivity() {

    // Si la persona niega el permiso, la app funciona igual pero sin avisos
    private val pedirPermiso = registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pedirPermisoDeNotificaciones()
        val container = (application as PokemonApp).container
        setContent {
            PokedexTheme {
                // Fondo del tema: sin esto el login quedaba blanco con textos claros en modo oscuro
                Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNav(container)
                }
            }
        }
    }

    /** Desde Android 13 las notificaciones necesitan el permiso POST_NOTIFICATIONS en tiempo de ejecución. */
    private fun pedirPermisoDeNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

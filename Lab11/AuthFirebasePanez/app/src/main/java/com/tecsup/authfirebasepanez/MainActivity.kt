package com.tecsup.authfirebasepanez

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.tecsup.authfirebasepanez.ui.theme.AuthFirebasePanezTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase
        FirebaseApp.initializeApp(this)

        setContent {
            AuthFirebasePanezTheme {
                Surface(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
                    AuthApp()
                }
            }
        }
    }
}

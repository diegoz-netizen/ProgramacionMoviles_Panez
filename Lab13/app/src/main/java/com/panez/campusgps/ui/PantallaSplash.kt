package com.panez.campusgps.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import com.panez.campusgps.R
import com.panez.campusgps.ui.theme.AzulMarca
import com.panez.campusgps.ui.theme.BlancoMarca
import com.panez.campusgps.ui.theme.Dimens
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Pantalla de inicio: el logo de Tecsup aparece y crece; luego pasa a la siguiente pantalla. */
@Composable
fun PantallaSplash(onTerminar: () -> Unit) {
    val escala = remember { Animatable(0.6f) }
    val opacidad = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { opacidad.animateTo(1f, tween(DURACION, easing = FastOutSlowInEasing)) }
        escala.animateTo(1f, tween(DURACION, easing = FastOutSlowInEasing))
        delay(ESPERA)
        onTerminar()
    }

    Surface(color = AzulMarca, modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize().scale(escala.value).alpha(opacidad.value)
        ) {
            // Colores de marca fijos (no cambian con el tema oscuro). El logo tiene fondo blanco: va dentro de una tarjeta blanca redondeada
            Surface(shape = MaterialTheme.shapes.extraLarge, color = BlancoMarca) {
                Image(
                    painter = painterResource(R.drawable.logo_tecsup),
                    contentDescription = "Logo de Tecsup",
                    modifier = Modifier.padding(Dimens.xl).width(Dimens.logoSplash)
                )
            }
            Text(
                "CampusGPS",
                style = MaterialTheme.typography.headlineLarge,
                color = BlancoMarca,
                modifier = Modifier.padding(top = Dimens.xl)
            )
            Text(
                "Encuentra tu pabellón en Tecsup Lima",
                style = MaterialTheme.typography.bodyLarge,
                color = BlancoMarca
            )
            CircularProgressIndicator(
                color = BlancoMarca,
                modifier = Modifier.padding(top = Dimens.xxl)
            )
        }
    }
}

private const val DURACION = 900
private const val ESPERA = 700L

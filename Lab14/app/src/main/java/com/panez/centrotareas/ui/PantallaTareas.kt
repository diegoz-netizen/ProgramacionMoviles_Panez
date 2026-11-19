package com.panez.centrotareas.ui

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.panez.centrotareas.R
import com.panez.centrotareas.model.CargaUi
import com.panez.centrotareas.model.PrimosUi
import com.panez.centrotareas.ui.components.PanelHiloPrincipal
import com.panez.centrotareas.ui.theme.Dimens
import com.panez.centrotareas.viewmodel.TareasViewModel

private enum class Seccion(val etiqueta: String, @param:DrawableRes val icono: Int) {
    Tarea("Tarea", R.drawable.ic_seccion_tarea),
    Primos("Primos", R.drawable.ic_seccion_primos),
    Cargas("Cargas", R.drawable.ic_seccion_cargas)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(vm: TareasViewModel) {
    val toques by vm.toques.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    val primos by vm.primos.collectAsState()
    val carga by vm.carga.collectAsState()

    var seccion by rememberSaveable { mutableStateOf(Seccion.Tarea) }
    val estadoSecciones = rememberSaveableStateHolder()

    val calculando = primos is PrimosUi.Calculando
    val cargando = carga is CargaUi.Cargando
    val activas = mapOf(
        Seccion.Tarea to corriendo,
        Seccion.Primos to calculando,
        Seccion.Cargas to cargando
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Centro de control", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        bottomBar = {
            BarraSecciones(
                actual = seccion,
                activas = activas,
                alElegir = { seccion = it }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimens.lg, vertical = Dimens.sm),
            verticalArrangement = Arrangement.spacedBy(Dimens.md)
        ) {
            PanelHiloPrincipal(
                toques = toques,
                trabajando = corriendo || calculando || cargando,
                alTocar = vm::tocar
            )
            estadoSecciones.SaveableStateProvider(seccion.name) {
                when (seccion) {
                    Seccion.Tarea -> SeccionTarea(vm)
                    Seccion.Primos -> SeccionPrimos(vm)
                    Seccion.Cargas -> SeccionCargas(vm)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BarraSecciones(
    actual: Seccion,
    activas: Map<Seccion, Boolean>,
    alElegir: (Seccion) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceContainerLow) {
        Seccion.entries.forEach { opcion ->
            NavigationBarItem(
                selected = opcion == actual,
                onClick = { alElegir(opcion) },
                icon = {
                    BadgedBox(
                        badge = {
                            if (activas[opcion] == true) {
                                Badge(containerColor = MaterialTheme.colorScheme.tertiary)
                            }
                        }
                    ) {
                        Icon(painterResource(opcion.icono), contentDescription = null)
                    }
                },
                label = { Text(opcion.etiqueta, style = MaterialTheme.typography.labelMedium) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

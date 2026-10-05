package com.panez.saludpluscitas.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.panez.saludpluscitas.data.model.Medico
import com.panez.saludpluscitas.navigation.Rutas
import com.panez.saludpluscitas.ui.theme.*

@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) { Text(texto, fontWeight = FontWeight.Bold) }
}

@Composable
fun CampoTexto(
    valor: String,
    onCambio: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    modifier: Modifier = Modifier,
    esClave: Boolean = false,
    teclado: KeyboardType = KeyboardType.Text,
    error: String? = null
) {
    OutlinedTextField(
        value = valor,
        onValueChange = onCambio,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        singleLine = true,
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else teclado),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onBack: (() -> Unit)? = null, acciones: @Composable RowScope.() -> Unit = {}) {
    CenterAlignedTopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver") }
            }
        },
        actions = acciones,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}


@Composable
fun TarjetaAccion(titulo: String, icono: ImageVector, fondo: Color, tinte: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(110.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Column(Modifier.fillMaxSize().padding(12.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(36.dp))
            Spacer(Modifier.height(8.dp))
            Text(titulo, fontWeight = FontWeight.SemiBold, color = tinte, textAlign = TextAlign.Center)
        }
    }
}

fun iconoEspecialidad(id: Int): ImageVector = when (id) {
    1 -> Icons.Filled.MedicalServices
    2 -> Icons.Filled.ChildCare
    3 -> Icons.Filled.PregnantWoman
    4 -> Icons.Filled.Favorite
    5 -> Icons.Filled.Face
    6 -> Icons.Filled.Accessibility
    7 -> Icons.Filled.Visibility
    else -> Icons.Filled.Psychology
}

@Composable
fun BarraInferior(seleccionado: Int, onTab: (String) -> Unit) {
    val items = listOf(
        Triple("Inicio", Icons.Filled.Home, Rutas.HOME),
        Triple("Citas", Icons.Filled.CalendarMonth, Rutas.MIS_CITAS),
        Triple("Resultados", Icons.Filled.Description, Rutas.RESULTADOS),
        Triple("Perfil", Icons.Filled.Person, Rutas.PERFIL)
    )
    NavigationBar(containerColor = Color.White) {
        items.forEachIndexed { i, (etiqueta, icono, ruta) ->
            NavigationBarItem(
                selected = i == seleccionado,
                onClick = { if (i != seleccionado) onTab(ruta) },
                icon = { Icon(icono, contentDescription = etiqueta) },
                label = { Text(etiqueta) }
            )
        }
    }
}


@Composable
fun ItemEspecialidad(id: Int, nombre: String, descripcion: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(AzulClaro, CircleShape), contentAlignment = Alignment.Center) {
                Icon(iconoEspecialidad(id), null, tint = AzulPrimario)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(nombre, fontWeight = FontWeight.SemiBold)
                Text(descripcion, color = TextoSecundario, fontSize = 13.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextoSecundario)
        }
    }
}

@Composable
fun Avatar(nombre: String, tam: Int = 52) {
    val iniciales = nombre.split(" ").filter { it.isNotBlank() && !it.endsWith(".") }
        .take(2).joinToString("") { it.first().uppercase() }
    Box(Modifier.size(tam.dp).background(AzulClaro, CircleShape), contentAlignment = Alignment.Center) {
        Text(iniciales, color = AzulPrimario, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TarjetaMedico(medico: Medico, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(medico.nombre)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(medico.nombre, fontWeight = FontWeight.SemiBold)
                Text(medico.descripcion, color = TextoSecundario, fontSize = 13.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = NaranjaAcento, modifier = Modifier.size(16.dp))
                    Text(" ${medico.rating} (${medico.resenas})", fontSize = 13.sp)
                }
            }
            Surface(color = VerdeClaro, shape = RoundedCornerShape(8.dp)) {
                Text("Disponible hoy", color = VerdeOk, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Composable
fun EstadoVacio(icono: ImageVector, mensaje: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icono, null, tint = TextoSecundario, modifier = Modifier.size(56.dp))
        Spacer(Modifier.height(12.dp))
        Text(mensaje, color = TextoSecundario, textAlign = TextAlign.Center)
    }
}


@Composable
fun ChipHorario(hora: String, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (seleccionado) AzulPrimario else AzulClaro,
        modifier = Modifier.height(42.dp).clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(hora, color = if (seleccionado) Color.White else AzulPrimario, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun FilaDato(icono: ImageVector, titulo: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, null, tint = AzulPrimario, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, color = TextoSecundario, fontSize = 12.sp)
            Text(valor, fontWeight = FontWeight.Medium)
        }
    }
}
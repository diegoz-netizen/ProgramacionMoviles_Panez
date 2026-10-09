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
import com.panez.saludpluscitas.data.repository.Repositorio
import com.panez.saludpluscitas.ui.theme.*
import com.panez.saludpluscitas.navigation.Rutas

@Composable
fun BotonPrincipal(texto: String, onClick: () -> Unit, modifier: Modifier = Modifier, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(58.dp),
        shape = RoundedCornerShape(14.dp)
    ) { Text(texto, fontWeight = FontWeight.Bold, fontSize = 18.sp) }
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
        label = { Text(etiqueta, fontSize = 16.sp) },
        leadingIcon = { Icon(icono, contentDescription = null) },
        singleLine = true,
        isError = error != null,
        supportingText = { if (error != null) Text(error, fontSize = 14.sp) },
        visualTransformation = if (esClave) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = if (esClave) KeyboardType.Password else teclado),
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        textStyle = MaterialTheme.typography.bodyLarge
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior(titulo: String, onBack: (() -> Unit)? = null, onMenu: (() -> Unit)? = null) {
    CenterAlignedTopAppBar(
        title = { Text(titulo, fontWeight = FontWeight.Bold, fontSize = 21.sp) },
        navigationIcon = {
            when {
                onBack != null -> IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                }
                onMenu != null -> IconButton(onClick = onMenu) {
                    Icon(Icons.Filled.Menu, contentDescription = "Menú")
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
    )
}

/** Menú lateral: Sede, Doctor, Agenda, Cerrar sesión. */
@Composable
fun MenuLateral(
    abierto: Boolean,
    onCerrar: () -> Unit,
    onSede: () -> Unit,
    onDoctor: () -> Unit,
    onAgenda: () -> Unit,
    onCerrarSesion: () -> Unit
) {
    if (!abierto) return
    ModalDrawerSheet(
        drawerContainerColor = Color.White,
        modifier = Modifier.width(300.dp)
    ) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Spacer(Modifier.height(24.dp))
            Text("SaludPlus", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = VerdePrincipal)
            Text("Clínica", fontSize = 15.sp, color = TextoSecundario)
            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            ItemMenu("Sede", Icons.Filled.LocationOn) { onCerrar(); onSede() }
            ItemMenu("Doctor", Icons.Filled.MedicalServices) { onCerrar(); onDoctor() }
            ItemMenu("Agenda", Icons.Filled.CalendarMonth) { onCerrar(); onAgenda() }

            Spacer(Modifier.weight(1f))
            HorizontalDivider()
            ItemMenu("Cerrar sesión", Icons.Filled.Logout, color = Color(0xFFB00020)) {
                onCerrar(); onCerrarSesion()
            }
        }
    }
}

@Composable
private fun ItemMenu(titulo: String, icono: ImageVector, color: Color = VerdePrincipal, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icono, null, tint = color, modifier = Modifier.size(28.dp))
        Spacer(Modifier.width(14.dp))
        Text(titulo, fontSize = 19.sp, fontWeight = FontWeight.Medium, color = color)
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
fun ItemEspecialidad(id: Int, nombre: String, descripcion: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(50.dp).background(VerdeSuave, CircleShape), contentAlignment = Alignment.Center) {
                Icon(iconoEspecialidad(id), null, tint = VerdePrincipal, modifier = Modifier.size(28.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(nombre, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(descripcion, color = TextoSecundario, fontSize = 15.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TextoSecundario)
        }
    }
}

@Composable
fun Avatar(nombre: String, tam: Int = 52) {
    val iniciales = nombre.split(" ").filter { it.isNotBlank() && !it.endsWith(".") }
        .take(2).joinToString("") { it.first().uppercase() }
    Box(Modifier.size(tam.dp).background(VerdeSuave, CircleShape), contentAlignment = Alignment.Center) {
        Text(iniciales, color = VerdePrincipal, fontWeight = FontWeight.Bold, fontSize = (tam / 2.6).sp)
    }
}

@Composable
fun TarjetaMedico(medico: Medico, onClick: () -> Unit) {
    val yaTiene = Repositorio.yaTieneCitaCon(medico.id)
    Card(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !yaTiene) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Avatar(medico.nombre, 56)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(medico.nombre, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(medico.descripcion, color = TextoSecundario, fontSize = 15.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Star, null, tint = NaranjaAcento, modifier = Modifier.size(18.dp))
                    Text(" ${medico.rating} (${medico.resenas})", fontSize = 15.sp)
                }
            }
            if (yaTiene) {
                Surface(color = Color(0xFFFFE0E0), shape = RoundedCornerShape(8.dp)) {
                    Text("Ya agendado", color = Color(0xFFB00020), fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            } else {
                Surface(color = VerdeClaro, shape = RoundedCornerShape(8.dp)) {
                    Text("Disponible", color = VerdeOk, fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun EstadoVacio(icono: ImageVector, mensaje: String, modifier: Modifier = Modifier) {
    Column(modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icono, null, tint = TextoSecundario, modifier = Modifier.size(64.dp))
        Spacer(Modifier.height(12.dp))
        Text(mensaje, color = TextoSecundario, textAlign = TextAlign.Center, fontSize = 17.sp)
    }
}

@Composable
fun ChipHorario(hora: String, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (seleccionado) VerdePrincipal else VerdeSuave,
        modifier = Modifier.height(50.dp).clickable(onClick = onClick)
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(hora, color = if (seleccionado) Color.White else VerdePrincipal,
                fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        }
    }
}

@Composable
fun FilaDato(icono: ImageVector, titulo: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icono, null, tint = VerdePrincipal, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(titulo, color = TextoSecundario, fontSize = 13.sp)
            Text(valor, fontWeight = FontWeight.Medium, fontSize = 17.sp)
        }
    }
}

@Composable
fun TarjetaAccion(
    titulo: String,
    icono: ImageVector,
    fondo: Color,
    tinte: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(120.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = fondo)
    ) {
        Column(
            Modifier.fillMaxSize().padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icono, contentDescription = null, tint = tinte, modifier = Modifier.size(40.dp))
            Spacer(Modifier.height(10.dp))
            Text(titulo, fontWeight = FontWeight.SemiBold, color = tinte,
                textAlign = TextAlign.Center, fontSize = 17.sp)
        }
    }
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
                icon = { Icon(icono, contentDescription = etiqueta, modifier = Modifier.size(26.dp)) },
                label = { Text(etiqueta, fontSize = 13.sp) }
            )
        }
    }
}
package com.tecsup.avisoscampus.model

/** De dónde salió un aviso. El texto es la etiqueta que se ve en el historial. */
enum class Origen(val texto: String) {
    BOTON("Botón"),
    RECORDATORIO("Recordatorio"),
    MODO_AVION("Modo avión")
}

/** Un aviso del historial. */
data class Aviso(
    val id: Int,
    val titulo: String,
    val texto: String,
    val origen: Origen,
    val hora: String,
    val urgente: Boolean = false // va por el canal Urgentes
)

/** Estado del permiso de notificaciones. */
sealed interface PermisoUi {
    data object SinPedir : PermisoUi
    data object Concedido : PermisoUi
    data object Denegado : PermisoUi
}

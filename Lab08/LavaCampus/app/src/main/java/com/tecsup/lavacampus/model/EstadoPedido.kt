package com.tecsup.lavacampus.model

import androidx.compose.ui.graphics.Color

/**
 * Estados del pedido en el orden de la máquina de estados:
 * RECIBIDO -> EN_PROCESO -> LISTO -> ENTREGADO. CANCELADO es final.
 * Se guarda en la tabla pedidos como EstadoPedido.name.
 */
enum class EstadoPedido(val etiqueta: String, val color: Color) {
    RECIBIDO("Recibido", Color(0xFFD97706)),
    EN_PROCESO("En proceso", Color(0xFF2563EB)),
    LISTO("Listo", Color(0xFF16A34A)),
    ENTREGADO("Entregado", Color(0xFF475569)),
    CANCELADO("Cancelado", Color(0xFFDC2626));

    val esFinal: Boolean get() = this == ENTREGADO || this == CANCELADO

    /** Estado siguiente en el flujo, o null si es final. Nunca retrocede. */
    fun siguiente(): EstadoPedido? = when (this) {
        RECIBIDO -> EN_PROCESO
        EN_PROCESO -> LISTO
        LISTO -> ENTREGADO
        ENTREGADO, CANCELADO -> null
    }

    companion object {
        /** Etapas que dibuja StepperEstado (sin CANCELADO). */
        val etapas = listOf(RECIBIDO, EN_PROCESO, LISTO, ENTREGADO)
    }
}

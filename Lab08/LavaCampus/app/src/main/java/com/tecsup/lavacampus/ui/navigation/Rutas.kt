package com.tecsup.lavacampus.ui.navigation

object Rutas {
    // Comunes
    const val SPLASH = "splash"
    const val LOGIN = "login"

    // Estudiante
    const val INICIO = "inicio"
    const val NUEVO_PEDIDO = "nuevo_pedido"
    const val MIS_PEDIDOS = "mis_pedidos"
    const val DETALLE_PEDIDO = "detalle_pedido/{pedidoId}"
    const val TARIFAS = "tarifas"
    const val PERFIL = "perfil"

    // Administrador
    const val PANEL = "panel"
    const val BANDEJA = "bandeja"
    const val GESTION_PEDIDO = "gestion_pedido/{pedidoId}"
    const val SERVICIOS = "servicios"
    const val FORMULARIO_SERVICIO = "formulario_servicio/{servicioId}"
    const val CLIENTES = "clientes"
    const val AJUSTES = "ajustes"

    fun detallePedido(id: Int) = "detalle_pedido/$id"
    fun gestionPedido(id: Int) = "gestion_pedido/$id"
    /** id = 0 para crear un servicio nuevo. */
    fun formularioServicio(id: Int) = "formulario_servicio/$id"
}

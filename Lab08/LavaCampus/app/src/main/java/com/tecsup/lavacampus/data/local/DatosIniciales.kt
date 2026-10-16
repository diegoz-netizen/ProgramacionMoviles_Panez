package com.tecsup.lavacampus.data.local

import com.tecsup.lavacampus.data.local.entity.Servicio

/** Datos de prueba ficticios (Ley 29733). Se cargan solo la primera vez. */
object DatosIniciales {

    val servicios = listOf(
        Servicio(nombre = "Lavado y secado", descripcion = "Lavado, secado y doblado de ropa",
            unidad = "KG", precio = 8.00, horasEntrega = 24, icono = "wash"),
        Servicio(nombre = "Planchado", descripcion = "Planchado de camisas, pantalones y polos",
            unidad = "PRENDA", precio = 2.50, horasEntrega = 48, icono = "iron"),
        Servicio(nombre = "Lavado en seco", descripcion = "Para sacos, abrigos y prendas delicadas",
            unidad = "PRENDA", precio = 12.00, horasEntrega = 48, icono = "shirt"),
        Servicio(nombre = "Edredón / frazada", descripcion = "Lavado de ropa de cama gruesa",
            unidad = "UNIDAD", precio = 20.00, horasEntrega = 48, activo = false, icono = "wash")
    )

    suspend fun sembrar(db: LavaDatabase) {
        // TODO (Paso 3): la condición pasa a ser usuarioDao().contar() == 0 y se insertan
        //  también los 3 estudiantes, el administrador y unos 10 pedidos con sus detalles.
        if (db.servicioDao().contar() == 0) {
            db.servicioDao().insertarTodos(servicios)
        }
    }
}

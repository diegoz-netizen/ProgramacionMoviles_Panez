package com.tecsup.avisoscampus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** Una fila de la tabla aviso (ver docs/BaseDeDatos.md). */
@Entity(tableName = "aviso")
data class AvisoEntity(
    // autoGenerate usa AUTOINCREMENT: el id nunca se reutiliza y sirve de id de la notificación
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val titulo: String,
    val texto: String,
    val detalle: String?,
    val origen: String, // Origen.name: BOTON, RECORDATORIO o MODO_AVION
    val hora: String,
    @ColumnInfo(defaultValue = "0") val urgente: Boolean
)

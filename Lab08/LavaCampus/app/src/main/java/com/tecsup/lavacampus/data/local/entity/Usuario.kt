package com.tecsup.lavacampus.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["correo"], unique = true)]
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val codigo: String, // código de estudiante (ej. U202310234); vacío para el administrador
    val correo: String,
    val clave: String,
    val rol: String // Rol.name: CLIENTE | ADMIN
)

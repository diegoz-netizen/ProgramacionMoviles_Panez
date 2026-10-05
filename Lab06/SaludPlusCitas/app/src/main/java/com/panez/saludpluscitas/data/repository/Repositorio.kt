package com.panez.saludpluscitas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.panez.saludpluscitas.data.model.*

/** Datos en memoria (sin BD). Se pierden al cerrar la app. */
object Repositorio {
    val usuarios = mutableStateListOf(
        Usuario("Juan Pérez", "987654321", "juan@correo.com", "123456") // usuario de prueba
    )

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // ---- Usuarios ----
    fun registrarUsuario(u: Usuario): Boolean {
        if (usuarios.any { it.telefono == u.telefono }) return false
        usuarios.add(u)
        usuarioActual = u
        return true
    }

    fun iniciarSesion(identificador: String, contrasena: String): Boolean {
        val id = identificador.trim()
        val u = usuarios.find {
            (it.telefono == id || it.correo.equals(id, ignoreCase = true)) && it.contrasena == contrasena
        } ?: return false
        usuarioActual = u
        return true
    }

    fun cerrarSesion() { usuarioActual = null }


    val especialidades = listOf(
        Especialidad(1, "Medicina General", "Atención integral"),
        Especialidad(2, "Pediatría", "Niños y adolescentes"),
        Especialidad(3, "Ginecología", "Salud de la mujer"),
        Especialidad(4, "Cardiología", "Corazón y sistema circulatorio"),
        Especialidad(5, "Dermatología", "Piel, cabello y uñas"),
        Especialidad(6, "Traumatología", "Huesos, músculos y articulaciones"),
        Especialidad(7, "Oftalmología", "Salud visual"),
        Especialidad(8, "Neurología", "Sistema nervioso")
    )

    val medicos = listOf(
        Medico(1, "Dr. Carlos Mendoza", 1, "Medicina General", 4.7, 85, "CMP 11201", "Av. Los Olivos 123, Lima"),
        Medico(2, "Dra. Rosa Quispe", 1, "Medicina General", 4.5, 60, "CMP 11544", "Av. Los Olivos 123, Lima"),
        Medico(3, "Dra. Claudia Rojas", 2, "Niños y adolescentes", 4.8, 96, "CMP 20987", "Av. Los Olivos 123, Lima"),
        Medico(4, "Dr. Pedro Salas", 2, "Niños y adolescentes", 4.4, 41, "CMP 21870", "Av. Los Olivos 123, Lima"),
        Medico(5, "Dra. Ana Torres", 3, "Ginecología", 4.9, 110, "CMP 12345", "Av. Los Olivos 123, Lima"),
        Medico(6, "Dr. Luis Ramírez", 3, "Ginecología", 4.7, 78, "CMP 13002", "Av. Los Olivos 123, Lima"),
        Medico(7, "Dra. Mariana Soto", 3, "Ginecología", 4.6, 55, "CMP 14876", "Av. Los Olivos 123, Lima"),
        Medico(8, "Dr. Jorge Paredes", 4, "Cardiología", 4.8, 70, "CMP 09911", "Av. Los Olivos 123, Lima"),
        Medico(9, "Dra. Elena Vargas", 5, "Dermatología", 4.6, 64, "CMP 15620", "Av. Los Olivos 123, Lima"),
        Medico(10, "Dr. Raúl Castillo", 6, "Traumatología", 4.5, 52, "CMP 16033", "Av. Los Olivos 123, Lima"),
        Medico(11, "Dra. Lucía Flores", 7, "Oftalmología", 4.7, 48, "CMP 17744", "Av. Los Olivos 123, Lima"),
        Medico(12, "Dr. Martín Ochoa", 8, "Neurología", 4.8, 39, "CMP 18255", "Av. Los Olivos 123, Lima")
    )

    fun buscarEspecialidades(texto: String) =
        especialidades.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    fun especialidadesDestacadas(n: Int = 3) = especialidades.take(n)
    fun obtenerEspecialidad(id: Int) = especialidades.find { it.id == id }

    fun obtenerMedico(id: Int) = medicos.find { it.id == id }

    fun medicosPorEspecialidad(especialidadId: Int) =
        medicos.filter { it.especialidadId == especialidadId }.sortedByDescending { it.rating }

    fun buscarMedicos(especialidadId: Int, texto: String) =
        medicosPorEspecialidad(especialidadId).filter { it.nombre.contains(texto.trim(), ignoreCase = true) }


    val horariosBase = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "14:00", "14:30", "15:00", "15:30"
    )

    val citas = mutableStateListOf<Cita>()
    private var siguienteCitaId = 1

    fun horariosDisponibles(medicoId: Int, fecha: String): List<String> {
        val ocupados = citas.filter { it.medicoId == medicoId && it.fecha == fecha }.map { it.hora }
        return horariosBase.filter { it !in ocupados }
    }
}

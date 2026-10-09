package com.panez.saludpluscitas.data.repository

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.panez.saludpluscitas.data.model.*
import java.time.LocalDate

object Repositorio {

    // ---------- USUARIOS ----------
    val usuarios = mutableStateListOf(
        Usuario("Diego Panez", "987654321", "diego@correo.com", "123456")
    )

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    fun registrarUsuario(u: Usuario): Boolean {
        if (usuarios.any { it.telefono == u.telefono }) return false
        usuarios.add(u)
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

    fun cerrarSesion() {
        usuarioActual = null
        sedeSeleccionada = null
    }

    // ---------- SEDES ----------
    val sedes = listOf(
        Sede(1, "Sede Santa Anita", "Av. Los Álamos 456, Santa Anita, Lima"),
        Sede(2, "Sede La Molina", "Av. Raúl Ferrero 1200, La Molina, Lima")
    )

    var sedeSeleccionada by mutableStateOf<Sede?>(null)
        private set

    fun seleccionarSede(s: Sede) { sedeSeleccionada = s }
    fun obtenerSede(id: Int) = sedes.find { it.id == id }

    // ---------- ESPECIALIDADES ----------
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

    fun buscarEspecialidades(texto: String) =
        especialidades.filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    fun obtenerEspecialidad(id: Int) = especialidades.find { it.id == id }

    // ---------- MEDICOS ----------
    // diasAtencion: 1=Lun, 2=Mar, 3=Mié, 4=Jue, 5=Vie, 6=Sáb, 7=Dom
    val medicos = listOf(
        // Sede Santa Anita (sedeId = 1)
        Medico(1, "Dr. Carlos Mendoza", 1, 1, "Medicina General", 4.7, 85, "CMP 11201", "Av. Los Álamos 456, Santa Anita", listOf(1, 3, 5)),
        Medico(2, "Dra. Rosa Quispe", 1, 1, "Medicina General", 4.5, 60, "CMP 11544", "Av. Los Álamos 456, Santa Anita", listOf(2, 4)),
        Medico(3, "Dra. Claudia Rojas", 2, 1, "Niños y adolescentes", 4.8, 96, "CMP 20987", "Av. Los Álamos 456, Santa Anita", listOf(1, 2, 4)),
        Medico(4, "Dr. Pedro Salas", 2, 1, "Niños y adolescentes", 4.4, 41, "CMP 21870", "Av. Los Álamos 456, Santa Anita", listOf(3, 5)),
        Medico(5, "Dra. Ana Torres", 3, 1, "Ginecología", 4.9, 110, "CMP 12345", "Av. Los Álamos 456, Santa Anita", listOf(1, 3, 5)),
        Medico(6, "Dr. Luis Ramírez", 4, 1, "Cardiología", 4.7, 78, "CMP 13002", "Av. Los Álamos 456, Santa Anita", listOf(2, 4)),

        // Sede La Molina (sedeId = 2)
        Medico(7, "Dra. Mariana Soto", 3, 2, "Ginecología", 4.6, 55, "CMP 14876", "Av. Raúl Ferrero 1200, La Molina", listOf(1, 3)),
        Medico(8, "Dr. Jorge Paredes", 4, 2, "Cardiología", 4.8, 70, "CMP 09911", "Av. Raúl Ferrero 1200, La Molina", listOf(2, 4, 5)),
        Medico(9, "Dra. Elena Vargas", 5, 2, "Dermatología", 4.6, 64, "CMP 15620", "Av. Raúl Ferrero 1200, La Molina", listOf(1, 4)),
        Medico(10, "Dr. Raúl Castillo", 6, 2, "Traumatología", 4.5, 52, "CMP 16033", "Av. Raúl Ferrero 1200, La Molina", listOf(3, 5)),
        Medico(11, "Dra. Lucía Flores", 7, 2, "Oftalmología", 4.7, 48, "CMP 17744", "Av. Raúl Ferrero 1200, La Molina", listOf(2, 3, 5)),
        Medico(12, "Dr. Martín Ochoa", 8, 2, "Neurología", 4.8, 39, "CMP 18255", "Av. Raúl Ferrero 1200, La Molina", listOf(1, 4))
    )

    fun obtenerMedico(id: Int) = medicos.find { it.id == id }

    /** Solo médicos de la sede seleccionada. */
    fun medicosDeSede(): List<Medico> {
        val s = sedeSeleccionada ?: return emptyList()
        return medicos.filter { it.sedeId == s.id }
    }

    fun especialidadesDeSede(): List<Especialidad> {
        val ids = medicosDeSede().map { it.especialidadId }.toSet()
        return especialidades.filter { it.id in ids }
    }

    fun medicosPorEspecialidad(especialidadId: Int) =
        medicosDeSede().filter { it.especialidadId == especialidadId }
            .sortedByDescending { it.rating }

    fun buscarMedicos(especialidadId: Int, texto: String) =
        medicosPorEspecialidad(especialidadId)
            .filter { it.nombre.contains(texto.trim(), ignoreCase = true) }

    // ---------- HORARIOS ----------
    val horariosBase = listOf(
        "08:00", "08:30", "09:00", "09:30", "10:00", "10:30",
        "11:00", "11:30", "14:00", "14:30", "15:00", "15:30"
    )

    val citas = mutableStateListOf<Cita>()
    private var siguienteCitaId = 1

    /** true si el usuario ya tiene una cita con ese médico (no cancelada). */
    fun yaTieneCitaCon(medicoId: Int): Boolean {
        val tel = usuarioActual?.telefono ?: return false
        return citas.any { it.medicoId == medicoId && it.usuarioTelefono == tel }
    }

    /** true si ya hay una cita con ese médico a esa fecha y hora. */
    fun horarioOcupado(medicoId: Int, fecha: String, hora: String): Boolean =
        citas.any { it.medicoId == medicoId && it.fecha == fecha && it.hora == hora }

    fun horariosDisponibles(medicoId: Int, fecha: String): List<String> {
        val ocupados = citas.filter { it.medicoId == medicoId && it.fecha == fecha }.map { it.hora }
        return horariosBase.filter { it !in ocupados }
    }

    fun obtenerCita(id: Int) = citas.find { it.id == id }

    fun agendarCita(medicoId: Int, fecha: String, hora: String, motivo: String): Cita? {
        if (yaTieneCitaCon(medicoId)) return null
        if (horarioOcupado(medicoId, fecha, hora)) return null
        val medico = obtenerMedico(medicoId) ?: return null
        val cita = Cita(
            siguienteCitaId++, medicoId, medico.especialidadId,
            fecha, hora, motivo, usuarioActual?.telefono ?: ""
        )
        citas.add(cita)
        return cita
    }

    fun citasDelUsuario(): List<Cita> {
        val tel = usuarioActual?.telefono ?: return emptyList()
        return citas.filter { it.usuarioTelefono == tel }
            .sortedWith(compareBy({ it.fecha }, { it.hora }))
    }

    fun cancelarCita(id: Int): Boolean = citas.removeIf { it.id == id }

    // ---------- RESULTADOS ----------
    val resultados = listOf(
        Resultado(1, "Hemograma completo", "2026-08-12", "Valores dentro del rango normal."),
        Resultado(2, "Perfil lipídico", "2026-08-12", "Colesterol LDL ligeramente elevado."),
        Resultado(3, "Radiografía de tórax", "2026-07-03", "Sin hallazgos relevantes."),
        Resultado(4, "Glucosa en ayunas", "2026-06-20", "Normal: 88 mg/dL.")
    )

    /** Devuelve los próximos 5 días hábiles de atención del médico. */
    fun diasDeAtencion(medicoId: Int): List<LocalDate> {
        val m = obtenerMedico(medicoId) ?: return emptyList()
        val hoy = LocalDate.now()
        val out = mutableListOf<LocalDate>()
        var d = hoy
        var guard = 0
        // hasta 30 días de atención o 120 días calendario, lo que ocurra primero
        while (out.size < 30 && guard < 120) {
            if (d.dayOfWeek.value in m.diasAtencion) out.add(d)
            d = d.plusDays(1)
            guard++
        }
        return out
    }
}
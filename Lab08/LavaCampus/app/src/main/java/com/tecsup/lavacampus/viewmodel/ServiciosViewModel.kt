package com.tecsup.lavacampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.lavacampus.data.local.entity.Servicio
import com.tecsup.lavacampus.data.repository.LavaRepository
import com.tecsup.lavacampus.model.Unidad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ServiciosViewModel(private val repo: LavaRepository) : ViewModel() {

    val servicios: StateFlow<List<Servicio>> = repo.observarServicios()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Solo los servicios ACTIVOS (pantalla Tarifas del estudiante). */
    val activos: StateFlow<List<Servicio>> = repo.observarServiciosActivos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun alternar(s: Servicio) {
        viewModelScope.launch { repo.cambiarActivo(s.id, !s.activo) }
    }

    // ---------- Formulario (crear / editar) ----------
    private val _editado = MutableStateFlow<Servicio?>(null)
    /** Servicio que se está editando; null mientras carga o si es nuevo. */
    val editado: StateFlow<Servicio?> = _editado.asStateFlow()

    private val _cargado = MutableStateFlow(false)
    val cargado: StateFlow<Boolean> = _cargado.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    /** true cuando se guardó o eliminó: la pantalla vuelve a la lista. */
    private val _terminado = MutableStateFlow(false)
    val terminado: StateFlow<Boolean> = _terminado.asStateFlow()

    /** id = 0 para un servicio nuevo. */
    fun cargar(id: Int) {
        viewModelScope.launch {
            _editado.value = if (id == 0) null else repo.obtenerServicio(id)
            _cargado.value = true
        }
    }

    fun guardar(
        nombre: String,
        unidad: Unidad,
        precioTexto: String,
        horasEntrega: Int,
        descripcion: String,
        icono: String,
        activo: Boolean
    ) {
        val precio = precioTexto.replace(',', '.').toDoubleOrNull()
        _mensaje.value = when {
            nombre.isBlank() -> "El nombre es obligatorio"
            precio == null || precio <= 0 -> "El precio debe ser mayor que 0"
            horasEntrega !in HORAS_VALIDAS -> "El tiempo de entrega debe ser 24, 48 o 72 h"
            else -> null
        }
        if (_mensaje.value != null) return

        val servicio = Servicio(
            id = _editado.value?.id ?: 0,
            nombre = nombre.trim(),
            descripcion = descripcion.trim(),
            unidad = unidad.name,
            precio = precio!!,
            horasEntrega = horasEntrega,
            activo = activo,
            icono = icono
        )
        viewModelScope.launch {
            repo.guardarServicio(servicio)
            _terminado.value = true
        }
    }

    /** Un servicio que ya está en algún pedido no se elimina: solo se desactiva. */
    fun eliminar(s: Servicio) {
        viewModelScope.launch {
            if (repo.servicioTieneDetalles(s.id)) {
                _mensaje.value = "\"${s.nombre}\" ya aparece en pedidos: no se puede eliminar, solo desactivar"
                return@launch
            }
            repo.eliminarServicio(s)
            _terminado.value = true
        }
    }

    companion object {
        val HORAS_VALIDAS = listOf(24, 48, 72)
        val ICONOS = listOf("wash", "iron", "shirt")
    }
}

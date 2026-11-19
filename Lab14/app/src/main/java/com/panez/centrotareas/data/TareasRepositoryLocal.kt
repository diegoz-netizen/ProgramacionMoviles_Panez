package com.panez.centrotareas.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class TareasRepositoryLocal : TareasRepository {

    override fun progresoTarea(): Flow<Float> = flow {
        Log.d("HILO", "Dentro: ${Thread.currentThread().name}")
        for (i in 1..100) {
            delay(40)
            emit(i / 100f)
        }
    }.flowOn(Dispatchers.Default)

    override suspend fun cargarPerfil(): String {
        delay(2000)
        return "Ana Torres (ficticia)"
    }

    override suspend fun cargarPedidos(simularError: Boolean): List<String> {
        delay(3000)
        if (simularError) throw IOException("Sin conexión")
        return listOf("Pedido 101", "Pedido 102", "Pedido 103")
    }

    override suspend fun contarPrimos(hasta: Int, alAvanzar: (Int) -> Unit): Int =
        withContext(Dispatchers.Default) {
            var cantidad = 0
            var ultimoPorcentaje = -1
            val total = (hasta - 1).toLong()
            for (n in 2..hasta) {
                ensureActive()
                if (esPrimo(n)) cantidad++
                // Solo se avisa al cambiar el porcentaje: máximo 100 avisos por cálculo.
                val porcentaje = ((n - 1) * 100L / total).toInt()
                if (porcentaje != ultimoPorcentaje) {
                    ultimoPorcentaje = porcentaje
                    alAvanzar(porcentaje)
                }
            }
            cantidad
        }

    private fun esPrimo(n: Int): Boolean {
        if (n < 2) return false
        var d = 2
        while (d.toLong() * d <= n) {
            if (n % d == 0) return false
            d++
        }
        return true
    }
}

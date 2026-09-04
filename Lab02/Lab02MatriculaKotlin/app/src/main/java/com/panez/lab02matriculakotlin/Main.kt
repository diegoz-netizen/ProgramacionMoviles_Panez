package com.panez.lab02matriculakotlin.ui

fun main() {
    println("=== REGISTRO DE MATRÍCULA ===")

    print("Nombre del estudiante: ")
    var nombreEstudiante = readln()
    while (nombreEstudiante.isBlank()) {
        print("El nombre no puede estar vacío. Ingrese el nombre nuevamente: ")
        nombreEstudiante = readln()
    }

    print("Cantidad de cursos: ")
    var cantidadCursos = readln().toIntOrNull() ?: 0
    while (cantidadCursos <= 0) {
        print("La cantidad de cursos debe ser mayor a 0. Ingrese nuevamente: ")
        cantidadCursos = readln().toIntOrNull() ?: 0
    }

    print("Valor de cada crédito (S/): ")
    var valorCredito = readln().toDoubleOrNull() ?: 0.0
    while (valorCredito <= 0.0) {
        print("El valor del crédito debe ser positivo. Ingrese nuevamente (S/): ")
        valorCredito = readln().toDoubleOrNull() ?: 0.0
    }

    val nombresCursos = mutableListOf<String>()
    val creditosPorCurso = mutableListOf<Int>()

    for (i in 1..cantidadCursos) {
        println("\n--- Curso $i ---")

        print("Nombre del curso: ")
        var nombre = readln()
        while (nombre.isBlank()) {
            print("El nombre del curso no puede estar vacío. Ingrese nuevamente: ")
            nombre = readln()
        }
        nombresCursos.add(nombre)

        print("Cantidad de créditos: ")
        var creditos = readln().toIntOrNull() ?: 0
        while (creditos <= 0) {
            print("Los créditos deben ser mayores a 0. Ingrese nuevamente: ")
            creditos = readln().toIntOrNull() ?: 0
        }
        creditosPorCurso.add(creditos)
    }

    println("\nSeleccione el Turno (1: Mañana [+10%], 2: Tarde [+15%], 3: Noche [+20%]): ")
    var opcionTurno = readln().toIntOrNull() ?: 0
    while (opcionTurno not in 1..3) {
        print("Opción inválida. Ingrese 1 (Mañana), 2 (Tarde) o 3 (Noche): ")
        opcionTurno = readln().toIntOrNull() ?: 0
    }

    val (nombreTurno, porcentajeRecargo) = when (opcionTurno) {
        1 -> "Mañana" to 0.10
        2 -> "Tarde" to 0.15
        else -> "Noche" to 0.20
    }

    println("\nSeleccione Categoría (1: Ordinario, 2: Becario [Exonerado]): ")
    var opcionCategoria = readln().toIntOrNull() ?: 0
    while (opcionCategoria not in 1..2) {
        print("Opción inválida. Ingrese 1 (Ordinario) o 2 (Becario): ")
        opcionCategoria = readln().toIntOrNull() ?: 0
    }

    val esBecario = (opcionCategoria == 2)
    val nombreCategoria = if (esBecario) "Becario" else "Ordinario"

    val totalCreditos = creditosPorCurso.sum()
    val totalPagarBase = totalCreditos * valorCredito
    val recargoTurno = totalPagarBase * porcentajeRecargo
    val subtotalCalculado = totalPagarBase + recargoTurno
    val totalPagarFinal = if (esBecario) 0.0 else subtotalCalculado

    val montoNeto = totalPagarFinal / 1.18
    val igv = totalPagarFinal - montoNeto

    val cargaAcademica = when {
        totalCreditos <= 12 -> "Matrícula regular"
        totalCreditos in 13..18 -> "Carga completa"
        else -> "Autorización"
    }

    val formaPago = when {
        esBecario -> "Exonerado (Beca 100%)"
        totalPagarFinal > 2500.0 -> "3 cuotas"
        else -> "2 cuotas"
    }

    println("\n=======================================================")
    println("RESULTADO FINAL DE MATRÍCULA")
    println("=======================================================")
    println("Estudiante : $nombreEstudiante")
    println("Categoría  : $nombreCategoria")
    println("Turno      : $nombreTurno (+${(porcentajeRecargo * 100).toInt()}%)\n")

    println(String.format("%-25s %-12s %-10s", "Curso", "Créditos", "Costo"))
    println("-------------------------------------------------------")
    for (i in creditosPorCurso.indices) {
        val costoCurso = creditosPorCurso[i] * valorCredito
        println(String.format("%-25s %-12d S/%8.2f", nombresCursos[i], creditosPorCurso[i], costoCurso))
    }

    println("-------------------------------------------------------")
    println("Cursos matriculados : $cantidadCursos")
    println("Total de créditos   : $totalCreditos")
    println(String.format("Subtotal cursos     : S/%.2f", totalPagarBase))
    println(String.format("Recargo por turno   : S/%.2f", recargoTurno))
    println(String.format("Base imponible(Neto): S/%.2f", montoNeto))
    println(String.format("IGV (18%%)           : S/%.2f", igv))
    println(String.format("Total a pagar       : S/%.2f", totalPagarFinal))
    println("Carga académica     : $cargaAcademica")
    println("Forma de pago       : $formaPago")
    println("=======================================================")
}
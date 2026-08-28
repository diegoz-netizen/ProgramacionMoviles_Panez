package com.panez.lab02matriculakotlin

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

    val totalCreditos = creditosPorCurso.sum()
    val totalPagar = totalCreditos * valorCredito

    val cargaAcademica = when {
        totalCreditos <= 12 -> "Matrícula regular"
        totalCreditos in 13..18 -> "Carga completa"
        else -> "Autorización"
    }

    val formaPago = if (totalPagar > 2500.0) "3 cuotas" else "2 cuotas"
}
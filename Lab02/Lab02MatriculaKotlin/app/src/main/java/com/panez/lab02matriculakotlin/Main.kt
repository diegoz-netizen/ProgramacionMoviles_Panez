package com.panez.lab02matriculakotlin

fun main() {
    println("=== REGISTRO DE MATRÍCULA ===")

    print("Nombre del estudiante: ")
    val nombreEstudiante = readln()

    print("Cantidad de cursos: ")
    val cantidadCursos = readln().toInt()

    print("Valor de cada crédito (S/): ")
    val valorCredito = readln().toDouble()

    val nombresCursos = mutableListOf<String>()
    val creditosPorCurso = mutableListOf<Int>()

    for (i in 1..cantidadCursos) {
        println("\n--- Curso $i ---")
        print("Nombre del curso: ")
        val nombre = readln()
        nombresCursos.add(nombre)

        print("Cantidad de créditos: ")
        val creditos = readln().toInt()
        creditosPorCurso.add(creditos)
    }
}
package pe.edu.tecsup.lab03

fun main() {
    println("=== SISTEMA DE MATRÍCULA CON CONTROL DE AFORO ===")

    print("Ingrese el aforo máximo de vacantes para este proceso: ")
    var vacantesDisponibles = readln().toIntOrNull() ?: 0
    while (vacantesDisponibles <= 0) {
        print("El aforo debe ser mayor a 0. Ingrese nuevamente: ")
        vacantesDisponibles = readln().toIntOrNull() ?: 0
    }

    var contadorEstudiantes = 0

    while (vacantesDisponibles > 0) {
        contadorEstudiantes++
        println("\n=======================================================")
        println(" REGISTRO ESTUDIANTE #$contadorEstudiantes (Vacantes restantes: $vacantesDisponibles)")
        println("=======================================================")

        print("Nombre del estudiante: ")
        var nombreEstudiante = readln()
        while (!nombreEstudiante.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))) {
            print("Nombre inválido (solo letras). Ingrese nuevamente: ")
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

            while (!nombre.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+")) || nombresCursos.any { it.equals(nombre, ignoreCase = true) }) {
                if (!nombre.matches(Regex("[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+"))) {
                    print("El nombre del curso solo debe contener letras. Ingrese nuevamente: ")
                } else {
                    print("El curso '$nombre' ya fue ingresado. Ingrese un curso diferente: ")
                }
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
        while (opcionTurno !in 1..3) {
            print("Opción inválida. Ingrese 1 (Mañana), 2 (Tarde) o 3 (Noche): ")
            opcionTurno = readln().toIntOrNull() ?: 0
        }

        val (nombreTurno, porcentajeRecargo) = when (opcionTurno) {
            1 -> "Mañana" to 0.10
            2 -> "Tarde" to 0.15
            else -> "Noche" to 0.20
        }

        println("\nSeleccione Categoría (1: Ordinario, 2: Becario [Exonerado de Matrícula]): ")
        var opcionCategoria = readln().toIntOrNull() ?: 0
        while (opcionCategoria !in 1..2) {
            print("Opción inválida. Ingrese 1 (Ordinario) o 2 (Becario): ")
            opcionCategoria = readln().toIntOrNull() ?: 0
        }

        val esBecario = (opcionCategoria == 2)
        val nombreCategoria = if (esBecario) "Becario" else "Ordinario"

        var costoMatriculaCobrado = 0.0
        if (!esBecario) {
            print("\nIngrese el costo de la matrícula (S/): ")
            costoMatriculaCobrado = readln().toDoubleOrNull() ?: 0.0
            while (costoMatriculaCobrado <= 0.0) {
                print("El costo de la matrícula debe ser un monto positivo. Ingrese nuevamente (S/): ")
                costoMatriculaCobrado = readln().toDoubleOrNull() ?: 0.0
            }
        }

        val textoMatricula = if (esBecario) "Exonerado (S/ 0.00)" else String.format("S/%.2f", costoMatriculaCobrado)

        val totalCreditos = creditosPorCurso.sum()
        val totalPagarBase = totalCreditos * valorCredito
        val recargoTurno = totalPagarBase * porcentajeRecargo

        val totalPagarFinal = totalPagarBase + recargoTurno + costoMatriculaCobrado

        val montoNeto = totalPagarFinal / 1.18
        val igv = totalPagarFinal - montoNeto

        val cargaAcademica = when {
            totalCreditos <= 12 -> "Matrícula regular"
            totalCreditos in 13..18 -> "Carga completa"
            else -> "Autorización"
        }

        val numeroCuotas = if (totalPagarFinal > 2500.0) 3 else 2
        val montoCuota = totalPagarFinal / numeroCuotas

        val formaPago = "$numeroCuotas cuotas de S/ ${String.format("%.2f", montoCuota)}"

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
        println("Costo de matrícula  : $textoMatricula")
        println(String.format("Subtotal cursos     : S/%.2f", totalPagarBase))
        println(String.format("Recargo por turno   : S/%.2f", recargoTurno))
        println(String.format("Base imponible(Neto): S/%.2f", montoNeto))
        println(String.format("IGV (18%%)           : S/%.2f", igv))
        println(String.format("Total a pagar       : S/%.2f", totalPagarFinal))
        println("Carga académica     : $cargaAcademica")
        println("Forma de pago       : $formaPago")
        println(String.format("Monto por cuota     : S/%.2f c/u", montoCuota))
        println("=======================================================")

        vacantesDisponibles--

        if (vacantesDisponibles > 0) {
            println("\nQuedan $vacantesDisponibles vacante(s). Presione ENTER para matricular al siguiente estudiante...")
            readln()
        }
    }

    println("\n=== AFORO COMPLETO: Se han agotado todas las vacantes configuradas ===")
}
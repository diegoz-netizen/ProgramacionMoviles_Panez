package com.panez.lab02carritokotlin

interface Facturable {
    val tasaIGV: Double get() = 0.18

    fun calcularIGV(montoBase: Double): Double {
        return montoBase * tasaIGV
    }
}
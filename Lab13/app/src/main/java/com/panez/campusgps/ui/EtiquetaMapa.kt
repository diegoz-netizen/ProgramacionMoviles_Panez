package com.panez.campusgps.ui

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

/**
 * Etiqueta del plano: una pastilla de color con el número del pabellón ("P14").
 * Google Maps solo acepta imágenes como marcador, por eso se dibuja en un Bitmap.
 */
fun etiquetaPabellon(texto: String, colorFondo: Int, colorTexto: Int, densidad: Float): BitmapDescriptor {
    val pincel = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = 13f * densidad
        typeface = Typeface.DEFAULT_BOLD
        color = colorTexto
        textAlign = Paint.Align.CENTER
    }
    val ancho = pincel.measureText(texto) + 16f * densidad
    val alto = 24f * densidad
    val bitmap = Bitmap.createBitmap(ancho.toInt(), alto.toInt(), Bitmap.Config.ARGB_8888)
    val lienzo = Canvas(bitmap)
    val fondo = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = colorFondo }
    val borde = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = android.graphics.Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 1.5f * densidad
    }
    val caja = RectF(1f, 1f, ancho - 1f, alto - 1f)
    lienzo.drawRoundRect(caja, alto / 2, alto / 2, fondo)
    lienzo.drawRoundRect(caja, alto / 2, alto / 2, borde)
    val baseTexto = alto / 2 - (pincel.descent() + pincel.ascent()) / 2
    lienzo.drawText(texto, ancho / 2, baseTexto, pincel)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/** Texto corto de cada lugar en el plano. */
fun textoEtiqueta(id: Int, nombre: String, categoria: String): String = when (categoria) {
    "Pabellón" -> "P$id"
    "Deportes" -> "Canchas"
    else -> nombre.substringBefore(" ")
}

/** Punto azul de "estás aquí": círculo de color con borde blanco. */
fun puntoUbicacion(colorPunto: Int, densidad: Float): BitmapDescriptor {
    val lado = (22f * densidad).toInt()
    val bitmap = Bitmap.createBitmap(lado, lado, Bitmap.Config.ARGB_8888)
    val lienzo = Canvas(bitmap)
    val centro = lado / 2f
    val pincel = Paint(Paint.ANTI_ALIAS_FLAG)
    pincel.color = android.graphics.Color.WHITE
    lienzo.drawCircle(centro, centro, centro - 1f, pincel)
    pincel.color = colorPunto
    lienzo.drawCircle(centro, centro, centro - 4f * densidad, pincel)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/** Para qué sirve cada tipo de lugar (texto de la ficha). */
fun descripcionDe(categoria: String): String = when (categoria) {
    "Pabellón" -> "Aulas y laboratorios"
    "Deportes" -> "Canchas deportivas y zona de recreación"
    else -> "Informes, admisión y acceso principal"
}

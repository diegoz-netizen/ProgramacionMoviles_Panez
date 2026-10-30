package com.tecsup.lavacampus.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Guarda las fotos de perfil en el almacenamiento interno de la app (carpeta files/fotos).
 * La imagen elegida en la galería se copia recortada en cuadrado y reducida a 320 px,
 * así no depende de que la foto original siga existiendo y pesa poco.
 * En la base de datos solo se guarda la ruta del archivo.
 */
class FotosPerfil(context: Context) {

    private val resolver = context.contentResolver
    private val carpeta = File(context.filesDir, "fotos")

    /** Copia la imagen de [uri] y devuelve la ruta del archivo nuevo, o null si no se pudo leer. */
    suspend fun guardar(usuarioId: Int, uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            // 1. Tamaño original, sin cargar la imagen completa en memoria
            val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, limites) }
            val mayor = maxOf(limites.outWidth, limites.outHeight)
            if (mayor <= 0) return@runCatching null

            // 2. Se carga reducida (potencia de 2) para no gastar memoria con fotos grandes
            var muestra = 1
            while (mayor / (muestra * 2) >= LADO) muestra *= 2
            val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
            val original = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opciones) }
                ?: return@runCatching null

            // 3. Gira según la orientación de la cámara, recorta al centro y escala
            val girada = girarSegunExif(original, uri)
            val lado = minOf(girada.width, girada.height)
            val cuadrada = Bitmap.createBitmap(girada, (girada.width - lado) / 2, (girada.height - lado) / 2, lado, lado)
            val final = Bitmap.createScaledBitmap(cuadrada, LADO, LADO, true)

            // 4. Nombre distinto en cada cambio para que la pantalla vuelva a cargar la imagen
            carpeta.mkdirs()
            val archivo = File(carpeta, "usuario_${usuarioId}_${System.currentTimeMillis()}.jpg")
            archivo.outputStream().use { final.compress(Bitmap.CompressFormat.JPEG, 90, it) }
            archivo.absolutePath
        }.getOrNull()
    }

    /** Borra el archivo de una foto anterior. Un avatar incluido ("avatar:...") no es un archivo y no se toca. */
    suspend fun borrar(ruta: String?) = withContext(Dispatchers.IO) {
        if (ruta != null && !ruta.startsWith(PREFIJO_AVATAR)) File(ruta).delete()
    }

    private fun girarSegunExif(bitmap: Bitmap, uri: Uri): Bitmap {
        val orientacion = resolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL
        val grados = when (orientacion) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        val matriz = Matrix().apply { postRotate(grados) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matriz, true)
    }

    companion object {
        const val LADO = 320
        const val PREFIJO_AVATAR = "avatar:"
    }
}

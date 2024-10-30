package utils

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun bitmapToFile(bitmap: Bitmap, cacheDir: File, fileName: String): File {
    val file = File(cacheDir, "$fileName.png")  // Crear archivo temporal en cache
    try {
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)  // Guardar como PNG
            out.flush()
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return file
}

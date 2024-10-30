package com.example.alphakid_v8

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import utils.bitmapToFile
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var resultMessage: TextView  // Definimos el TextView
    private var tesseract: ITesseract? = null
    private val CAMERA_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Crear dinámicamente un LinearLayout
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Inicializar y agregar el TextView a la vista
        resultMessage = TextView(this).apply {
            textSize = 24f
            setTextColor(resources.getColor(android.R.color.black, null))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(16, 16, 16, 16)
            }
        }
        layout.addView(resultMessage)

        // Inicializar y agregar el botón de cámara
        val captureButton = Button(this).apply {
            text = "Abrir Cámara"
            setOnClickListener { openCamera() }
        }
        layout.addView(captureButton)

        setContentView(layout)  // Establecemos el layout como vista principal

        // Inicializar Tesseract
        initTesseract()
    }

    private fun initTesseract() {
        tesseract = Tesseract().apply {
            val dataPath = "${filesDir.absolutePath}/tessdata"
            copyTessDataIfNeeded(dataPath)
            setDatapath(dataPath)
            setLanguage("spa")  // Configurado para español
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                recognizeTextFromImage(it)
            }
        }
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        val processedBitmap = preprocessImage(bitmap)
        val imageFile: File = bitmapToFile(processedBitmap, cacheDir, "temp_image")

        try {
            val recognizedText = tesseract?.doOCR(imageFile)
            resultMessage.text = recognizedText ?: "No se pudo reconocer el texto."  // Asignar el texto al TextView

            if (recognizedText?.contains("avión", ignoreCase = true) == true) {
                Toast.makeText(this, "¡Palabra correcta!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Palabra incorrecta", Toast.LENGTH_SHORT).show()
            }
        } catch (e: TesseractException) {
            Log.e("Tesseract", "Error recognizing text", e)
        }
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.GaussianBlur(mat, mat, Size(5.0, 5.0), 0.0)
        Imgproc.adaptiveThreshold(
            mat, mat, 255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY, 11, 2.0
        )

        val processedBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(mat, processedBitmap)
        return processedBitmap
    }

    private fun copyTessDataIfNeeded(dataPath: String) {
        val tessDataFile = File(dataPath, "spa.traineddata")
        if (!tessDataFile.exists()) {
            tessDataFile.parentFile?.mkdirs()
            assets.open("tessdata/spa.traineddata").use { inputStream ->
                tessDataFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }
}
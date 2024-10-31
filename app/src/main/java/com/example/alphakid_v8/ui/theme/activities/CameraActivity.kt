package com.example.alphakid_v8.ui.theme.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.opengl.GLES20
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.sourceforge.tess4j.ITesseract
import net.sourceforge.tess4j.Tesseract
import net.sourceforge.tess4j.TesseractException
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import utils.bitmapToFile
import java.io.File

class CameraActivity : AppCompatActivity() {

    private lateinit var resultMessage: TextView
    private var tesseract: ITesseract? = null
    private val CAMERA_REQUEST_CODE = 100

    //Se sobreescribe el metodo onCreate() para inicializar la camara y el reconocimiento de texto.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Load OpenCV library
        if (!OpenCVLoader.initDebug()) {
            Log.e("CameraActivity", "OpenCV initialization failed")
        } else {
            Log.d("CameraActivity", "OpenCV initialization succeeded")
        }

        // Check OpenGL version
        checkOpenGLVersion()

        // Create a LinearLayout dynamically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        // Initialize and add the TextView to the view
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

        // Initialize and add the camera button
        val captureButton = Button(this).apply {
            text = "Abrir Cámara"
            setOnClickListener { openCamera() }
        }
        layout.addView(captureButton)

        setContentView(layout)

        // Initialize Tesseract
        initTesseract()
    }
//Esto lo que hace es verificar la versión de OpenGL que se está utilizando en el dispositivo.
    private fun checkOpenGLVersion() {
        val version = GLES20.glGetString(GLES20.GL_VERSION)
        Log.d("OpenGL Version", "OpenGL ES version: $version")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val supported = packageManager.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
            Log.d("OpenGL Support", "OpenGL ES Extension Pack supported: $supported")
        }
    }
//Se inicializa el objeto Tesseract para el reconocimiento de texto.
    private fun initTesseract() {
        tesseract = Tesseract().apply {
            val dataPath = "${filesDir.absolutePath}/tessdata"
            copyTessDataIfNeeded(dataPath)
            setDatapath(dataPath)
            setLanguage("spa")
        }
    }
//Se abre la cámara para tomar una foto.
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "No se pudo abrir la cámara", Toast.LENGTH_SHORT).show()
        }
    }
//Se obtiene la imagen tomada por la cámara y se procesa para reconocer el texto.
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let {
                recognizeTextFromImage(it)
            }
        }
    }
//Se procesa la imagen para mejorar la calidad y facilitar el reconocimiento de texto.
    private fun recognizeTextFromImage(bitmap: Bitmap) {
        CoroutineScope(Dispatchers.Main).launch {
            val processedBitmap = withContext(Dispatchers.Default) {
                preprocessImage(bitmap)
            }
            val imageFile: File = bitmapToFile(processedBitmap, cacheDir, "temp_image")

            try {
                val recognizedText = withContext(Dispatchers.Default) {
                    tesseract?.doOCR(imageFile)
                }
                resultMessage.text = recognizedText ?: "No se pudo reconocer el texto."

                if (recognizedText?.contains("avión", ignoreCase = true) == true) {
                    Toast.makeText(this@CameraActivity, "¡Palabra correcta!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@CameraActivity, "Palabra incorrecta", Toast.LENGTH_SHORT).show()
                }
            } catch (e: TesseractException) {
                Log.e("Tesseract", "Error recognizing text", e)
            }
        }
    }
//Se preprocesa la imagen para mejorar la calidad y facilitar el reconocimiento de texto.
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

    // Copy the trained data file to the app's data directory
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
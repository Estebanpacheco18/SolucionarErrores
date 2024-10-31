package com.example.alphakid_v8.ui.theme.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.opengl.GLES20
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.alphakid_v8.R
import com.example.alphakid_v8.data.models.repositories.RewardSystem
import com.example.alphakid_v8.ui.theme.AlphaKid_v8Theme
import com.example.alphakid_v8.ui.theme.PrimaryColor
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraActivity : ComponentActivity() {
    private lateinit var rewardSystem: RewardSystem
    private val CAMERA_REQUEST_CODE = 100
    private lateinit var resultMessage: MutableState<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            Log.e("CameraActivity", "OpenCV initialization failed")
        } else {
            Log.d("CameraActivity", "OpenCV initialization succeeded")
        }

        checkOpenGLVersion()

        resultMessage = mutableStateOf("")
        rewardSystem = RewardSystem(this)

        setContent {
            AlphaKid_v8Theme {
                CameraScreen(
                    onOpenCamera = { openCamera() },
                    resultMessage = resultMessage
                )
            }
        }
    }

    private fun checkOpenGLVersion() {
        val version = GLES20.glGetString(GLES20.GL_VERSION)
        Log.d("OpenGL Version", "OpenGL ES version: $version")

        val supported = packageManager.hasSystemFeature(PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
        Log.d("OpenGL Support", "OpenGL ES Extension Pack supported: $supported")
    }

    @SuppressLint("QueryPermissionsNeeded")
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
            resultMessage.value = "Procesando imagen..."
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                recognizeTextFromImage(imageBitmap)
            } else {
                resultMessage.value = "No se pudo obtener la imagen."
            }
        }
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        lifecycleScope.launch {
            try {
                val processedBitmap = withContext(Dispatchers.Default) { preprocessImage(bitmap) }
                val recognizedText = withContext(Dispatchers.Default) { recognizeText(processedBitmap) }
                resultMessage.value = recognizedText ?: "No se pudo reconocer el texto."

                val customToastView = LinearLayout(this@CameraActivity).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(16, 16, 16, 16)
                    setBackgroundColor(ContextCompat.getColor(this@CameraActivity, android.R.color.holo_blue_dark))
                }

                val imageView = ImageView(this@CameraActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(50, 50).apply {
                        setMargins(0, 0, 16, 0)
                    }
                    setImageResource(R.drawable.logo)
                }

                val textView = TextView(this@CameraActivity).apply {
                    layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                    setTextColor(android.graphics.Color.WHITE)
                    text = if (recognizedText != null) {
                        rewardSystem.addPoints(10)
                        "¡Palabra correcta! +10 puntos"
                    } else {
                        "Palabra incorrecta o no enfocada. Inténtalo de nuevo."
                    }
                }

                customToastView.addView(imageView)
                customToastView.addView(textView)

                Toast(this@CameraActivity).apply {
                    duration = Toast.LENGTH_SHORT
                    view = customToastView
                }.show()

                if (recognizedText != null) {
                    showPointsAndRewards()
                }
            } catch (e: Exception) {
                resultMessage.value = "Error al procesar la imagen: ${e.message}"
                Log.e("CameraActivity", "Error al procesar la imagen", e)
            }
        }
    }

    private fun showPointsAndRewards() {
        // Implementa la lógica para mostrar los puntos y recompensas aquí
        Toast.makeText(this, "Puntos y recompensas actualizados", Toast.LENGTH_SHORT).show()
    }

    private fun preprocessImage(bitmap: Bitmap): Bitmap {
        return try {
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
            processedBitmap
        } catch (e: Exception) {
            Log.e("CameraActivity", "Error en el procesamiento de la imagen", e)
            bitmap
        }
    }

    private suspend fun recognizeText(bitmap: Bitmap): String? = suspendCancellableCoroutine { cont ->
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val recognizedText = visionText.text
                if (recognizedText.contains("A V I O N", ignoreCase = true)) {
                    cont.resume(recognizedText)
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener { e ->
                Log.e("CameraActivity", "Text recognition failed", e)
                cont.resumeWithException(e)
            }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CameraScreen(onOpenCamera: () -> Unit, resultMessage: MutableState<String>) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = "Camera Activity", color = Color.White)
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryColor)
                )
            }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = onOpenCamera) {
                        Text("Abrir Cámara")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = resultMessage.value, color = Color.Black)
                }
            }
        }
    }
}
package com.example.alphakid_v8.ui.theme.activities

import android.annotation.SuppressLint
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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.alphakid_v8.R
import com.example.alphakid_v8.data.models.repositories.RewardSystem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class CameraActivity : AppCompatActivity() {

    private lateinit var resultMessage: TextView
    private val rewardSystem = RewardSystem()
    private val CAMERA_REQUEST_CODE = 100

    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!OpenCVLoader.initDebug()) {
            Log.e("CameraActivity", "OpenCV initialization failed")
        } else {
            Log.d("CameraActivity", "OpenCV initialization succeeded")
        }

        checkOpenGLVersion()
        setupUI()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setupUI() {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

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

        val captureButton = Button(this).apply {
            text = "Abrir Cámara"
            setOnClickListener { openCamera() }
        }
        layout.addView(captureButton)

        setContentView(layout)
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
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            imageBitmap?.let { recognizeTextFromImage(it) }
        }
    }

    private fun recognizeTextFromImage(bitmap: Bitmap) {
        lifecycleScope.launch {
            val processedBitmap = withContext(Dispatchers.Default) { preprocessImage(bitmap) }
            val recognizedText = withContext(Dispatchers.Default) { recognizeText(processedBitmap) }
            resultMessage.text = recognizedText ?: "No se pudo reconocer el texto."

            val customToastView = LinearLayout(this@CameraActivity).apply {
                orientation = LinearLayout.HORIZONTAL
                setPadding(16, 16, 16, 16)
                setBackgroundColor(ContextCompat.getColor(this@CameraActivity, android.R.color.holo_blue_dark))
            }

            val imageView = ImageView(this@CameraActivity).apply {
                layoutParams = LinearLayout.LayoutParams(50, 50).apply { // Set smaller size
                    setMargins(0, 0, 16, 0)
                }
                setImageResource(R.drawable.logo) // Replace with your logo resource
            }

            val textView = TextView(this@CameraActivity).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                setTextColor(android.graphics.Color.WHITE)
                text = if (recognizedText?.contains("avión", ignoreCase = true) == true) {
                    rewardSystem.addPoints(10)
                    "¡Palabra correcta! +10 puntos"
                } else {
                    "Palabra incorrecta"
                }
            }

            customToastView.addView(imageView)
            customToastView.addView(textView)

            Toast(this@CameraActivity).apply {
                duration = Toast.LENGTH_SHORT
                view = customToastView
            }.show()

            showPointsAndRewards()
        }
    }

    private fun showPointsAndRewards() {
        val totalPoints = rewardSystem.getTotalPoints()
        val availableRewards = rewardSystem.getAvailableRewards()

        // Display points and rewards
        resultMessage.text = "Puntos: $totalPoints\nRecompensas disponibles: ${availableRewards.joinToString { it.name }}"
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

    private fun recognizeText(bitmap: Bitmap): String? {
        // Implement a basic text recognition algorithm here
        // For simplicity, this example returns a dummy text
        return "avión"
    }
}
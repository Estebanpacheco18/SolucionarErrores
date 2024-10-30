package com.example.alphakid_v8

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class ChallengeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContent {
                ChallengeScreen(onOpenCamera = { openCamera() })
            }
        } catch (e: Exception) {
            Log.e("ChallengeActivity", "Error setting content", e)
        }
    }

    private fun openCamera() {
        try {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("ChallengeActivity", "Error opening CameraActivity", e)
        }
    }
}

@Composable
fun ChallengeScreen(onOpenCamera: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Forma la palabra: AVIÓN")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onOpenCamera) {
            Text(text = "Abrir Cámara")
        }
    }
}
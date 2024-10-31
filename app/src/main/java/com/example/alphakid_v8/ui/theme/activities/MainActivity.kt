package com.example.alphakid_v8.ui.theme.activities

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContent {
                CountdownScreen { navigateToChallenge() }
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error during navigation", e)
        }
    }

    private fun navigateToChallenge() {
        try {
            val intent = Intent(this, ChallengeActivity::class.java)
            startActivity(intent)
            finish()  // Opcional: evita que el usuario vuelva atrás con el botón de atrás
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to navigate to ChallengeActivity", e)
        }
    }
}

@Composable
fun CountdownScreen(onFinish: () -> Unit) {
    var countdown by remember { mutableStateOf(3) }

    LaunchedEffect(Unit) {
        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                countdown = (millisUntilFinished / 1000).toInt()
            }

            override fun onFinish() {
                onFinish()
            }
        }.start()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (countdown > 0) countdown.toString() else "¡YA!",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
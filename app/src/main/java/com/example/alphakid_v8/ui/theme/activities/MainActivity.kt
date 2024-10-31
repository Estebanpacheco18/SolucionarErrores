package com.example.alphakid_v8.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.alphakid_v8.ui.theme.AlphaKid_v8Theme
import com.example.alphakid_v8.ui.theme.activities.ChallengeActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlphaKid_v8Theme {
                MainScreen()
            }
        }
    }

    fun navigateToChallenge() {
        try {
            val intent = Intent(this, ChallengeActivity::class.java)
            startActivity(intent)
            finish()  // Optional: prevents the user from going back with the back button
        } catch (e: Exception) {
            Log.e("MainActivity", "Failed to navigate to ChallengeActivity", e)
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)
    val userName = remember { sharedPreferences.getString("user_name", "User") }

    CountdownScreen(onFinish = { (context as MainActivity).navigateToChallenge() })

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Text(text = "Welcome, $userName!")
    }
}

@Composable
fun CountdownScreen(onFinish: () -> Unit) {
    var countdown by remember { mutableIntStateOf(3) }

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
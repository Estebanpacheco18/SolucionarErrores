package com.example.alphakid_v8.ui.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.alphakid_v8.R
import com.example.alphakid_v8.ui.theme.AlphaKid_v8Theme
import com.example.alphakid_v8.ui.theme.BackgroundColor
import com.example.alphakid_v8.ui.theme.PrimaryColor
import com.example.alphakid_v8.ui.theme.activities.NameRegistrationActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlphaKid_v8Theme {
                SplashScreen()
            }
        }

        // Delay for 3 seconds and then navigate to NameRegistrationActivity
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, NameRegistrationActivity::class.java))
            finish()
        }, 3000) // 3000 milliseconds = 3 seconds
    }
}

@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(163.dp, 153.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(color = PrimaryColor)
        }
    }
}
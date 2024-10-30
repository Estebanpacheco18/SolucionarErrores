package com.example.alphakid_v8

import android.content.Intent
import android.os.Bundle
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

class SuccessActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SuccessScreen(onNextChallenge = { navigateToNextChallenge() })
        }
    }

    private fun navigateToNextChallenge() {
        startActivity(Intent(this, ChallengeActivity::class.java))
        finish()
    }
}

@Composable
fun SuccessScreen(onNextChallenge: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "¡Palabra correcta!")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onNextChallenge) {
            Text(text = "Siguiente reto")
        }
    }
}
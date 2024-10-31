package com.example.alphakid_v8.ui.theme.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.alphakid_v8.ui.theme.AlphaKid_v8Theme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AlphaKid_v8Theme {
                LoginScreen(onLogin = { name ->
                    saveGuestName(name)
                    navigateToMainActivity()
                })
            }
        }
    }

    private fun saveGuestName(name: String) {
        val sharedPreferences = getSharedPreferences("guest_profile", MODE_PRIVATE)
        sharedPreferences.edit().putString("guest_name", name).apply()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

@Composable
fun LoginScreen(onLogin: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Enter your name") }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { onLogin(name) }) {
                Text("Join as Guest")
            }
        }
    }
}
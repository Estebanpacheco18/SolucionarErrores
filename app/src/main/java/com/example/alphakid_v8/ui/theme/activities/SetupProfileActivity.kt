package com.example.alphakid_v8.ui.theme.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.alphakid_v8.R

class SetupProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetupProfileScreen(onProfileSetup = { userName, backgroundResId ->
                saveUserProfile(userName, backgroundResId)
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            })
        }
    }

    private fun saveUserProfile(userName: String, backgroundResId: Int) {
        val sharedPreferences = getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("user_name", userName)
            putInt("background_res_id", backgroundResId)
            apply()
        }
    }
}

@Composable
fun SetupProfileScreen(onProfileSetup: (String, Int) -> Unit) {
    var userName by remember { mutableStateOf("") }
    val backgroundResId = listOf(R.drawable.fxemoji__babychick, R.drawable.fxemoji__chicken, R.drawable.fxemoji__negativesquaredab).random()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Setup your profile")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = userName,
            onValueChange = { userName = it },
            label = { Text("Enter your name") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Image(
            painter = painterResource(id = backgroundResId),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { onProfileSetup(userName, backgroundResId) }) {
            Text(text = "Save Profile")
        }
    }
}
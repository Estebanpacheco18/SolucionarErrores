package com.example.alphakid_v8.ui.theme.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.alphakid_v8.R
import com.example.alphakid_v8.data.models.repositories.RewardSystem
import com.example.alphakid_v8.ui.theme.AlphaKid_v8Theme
import com.example.alphakid_v8.ui.theme.PrimaryColor

class ProfileActivity : ComponentActivity() {
    private lateinit var rewardSystem: RewardSystem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rewardSystem = RewardSystem(this)
        val sharedPreferences = getSharedPreferences("user_profile", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("user_name", "Unknown")
        val backgroundResId = sharedPreferences.getInt("background_res_id", R.drawable.fxemoji__babychick)

        setContent {
            AlphaKid_v8Theme {
                ProfileScreen(
                    userName = userName ?: "Unknown",
                    totalPoints = rewardSystem.getTotalPoints(),
                    backgroundResId = backgroundResId,
                    onStartChallenge = { startChallengeActivity() }
                )
            }
        }
    }

    private fun startChallengeActivity() {
        startActivity(Intent(this, ChallengeActivity::class.java))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userName: String, totalPoints: Int, backgroundResId: Int, onStartChallenge: () -> Unit) {
    var timeLeft by remember { mutableStateOf(3) }
    var isCountingDown by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = backgroundResId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(text = userName, color = Color.White)
                            Text(text = "Points: $totalPoints", color = Color.White)
                        }
                    }
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
                if (isCountingDown) {
                    Text(text = "Starting in $timeLeft...", color = Color.Black)
                } else {
                    Button(onClick = {
                        isCountingDown = true
                        object : CountDownTimer(3000, 1000) {
                            override fun onTick(millisUntilFinished: Long) {
                                timeLeft = (millisUntilFinished / 1000).toInt()
                            }

                            override fun onFinish() {
                                isCountingDown = false
                                onStartChallenge()
                            }
                        }.start()
                    }) {
                        Text("Start Challenge")
                    }
                }
            }
        }
    }
}
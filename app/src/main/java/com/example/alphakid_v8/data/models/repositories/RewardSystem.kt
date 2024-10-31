package com.example.alphakid_v8.data.models.repositories

import android.content.Context
import android.content.SharedPreferences

class RewardSystem(private val context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    fun getTotalPoints(): Int {
        return sharedPreferences.getInt("total_points", 0)
    }

    fun addPoints(points: Int) {
        val totalPoints = getTotalPoints() + points
        sharedPreferences.edit().putInt("total_points", totalPoints).apply()
    }

    fun getAvailableRewards(): List<Reward> {
        // Implement your logic to get available rewards
        return listOf()
    }
}
package com.example.alphakid_v8.data.models.repositories

class RewardSystem {
    private var totalPoints: Int = 0
    private val rewards: MutableList<Reward> = mutableListOf()

    fun addPoints(points: Int) {
        totalPoints += points
    }

    fun getTotalPoints(): Int {
        return totalPoints
    }

    fun addReward(reward: Reward) {
        rewards.add(reward)
    }

    fun getAvailableRewards(): List<Reward> {
        return rewards.filter { it.pointsRequired <= totalPoints }
    }
}
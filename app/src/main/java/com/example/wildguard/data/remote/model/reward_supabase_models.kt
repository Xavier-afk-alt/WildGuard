package com.example.wildguard.data.remote.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RewardProgressRow(
    @SerialName("user_id")
    val userId: String,

    @SerialName("current_points")
    val currentPoints: Int = 0,

    @SerialName("plant_growth_points")
    val plantGrowthPoints: Int = 0,

    @SerialName("total_earned_points")
    val totalEarnedPoints: Int = 0,

    @SerialName("last_daily_login_date")
    val lastDailyLoginDate: String? = null,

    @SerialName("login_count")
    val loginCount: Int = 0,

    @SerialName("report_count")
    val reportCount: Int = 0,

    @SerialName("guidebook_count")
    val guidebookCount: Int = 0,

    @SerialName("selected_animal")
    val selectedAnimal: String = "TIGER"
)

@Serializable
data class UserInventoryRow(
    @SerialName("user_id")
    val userId: String,

    @SerialName("reward_id")
    val rewardId: Int,

    val quantity: Int
)

@Serializable
data class UserAchievementRow(
    @SerialName("user_id")
    val userId: String,

    @SerialName("achievement_id")
    val achievementId: Int,

    val progress: Int = 0,

    val unlocked: Boolean = false,

    @SerialName("reward_granted")
    val rewardGranted: Boolean = false
)

@Serializable
data class DiscoveredAnimalRow(
    @SerialName("user_id")
    val userId: String,

    @SerialName("animal_type")
    val animalType: String
)

package com.example.wildguard.data.repository

import com.example.wildguard.data.remote.model.UserAchievementRow

interface AchievementRepository {

    suspend fun getAchievements(
        userId: String
    ): Result<List<UserAchievementRow>>

    suspend fun saveAchievement(
        achievement: UserAchievementRow
    ): Result<Unit>
}

package com.example.wildguard.data.repository

import com.example.wildguard.data.remote.model.RewardProgressRow

interface RewardProgressRepository {

    suspend fun getProgress(
        userId: String
    ): Result<RewardProgressRow?>

    suspend fun saveProgress(
        progress: RewardProgressRow
    ): Result<Unit>
}

package com.example.wildguard.data.repository

import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.remote.model.RewardProgressRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabaseRewardProgressRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) : RewardProgressRepository {

    override suspend fun getProgress(
        userId: String
    ): Result<RewardProgressRow?> =
        runCatching {
            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            client
                .from("reward_progress")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                    limit(count = 1)
                }
                .decodeList<RewardProgressRow>()
                .firstOrNull()
        }

    override suspend fun saveProgress(
        progress: RewardProgressRow
    ): Result<Unit> =
        runCatching {
            require(progress.userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            client
                .from("reward_progress")
                .upsert(progress)

            Unit
        }
}

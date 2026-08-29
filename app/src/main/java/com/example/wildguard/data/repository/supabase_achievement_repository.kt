package com.example.wildguard.data.repository

import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.remote.model.UserAchievementRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabaseAchievementRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) : AchievementRepository {

    override suspend fun getAchievements(
        userId: String
    ): Result<List<UserAchievementRow>> =
        runCatching {

            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }


            client
                .from("user_achievements")
                .select {
                    filter {

                        eq(
                            "user_id",
                            userId
                        )
                    }
                }
                .decodeList<UserAchievementRow>()
        }


    override suspend fun saveAchievement(
        achievement: UserAchievementRow
    ): Result<Unit> =
        runCatching {

            require(
                achievement.userId.isNotBlank()
            ) {

                "A Supabase authenticated user ID is required."
            }


            client
                .from("user_achievements")
                .upsert(
                    achievement
                ) {

                    onConflict =
                        "user_id,achievement_id"
                }


            Unit
        }
}
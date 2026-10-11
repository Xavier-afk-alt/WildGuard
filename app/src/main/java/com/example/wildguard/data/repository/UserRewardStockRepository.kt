package com.example.wildguard.data.repository

import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class UserRewardStockRow(
    @SerialName("user_id") val userId: String,
    @SerialName("reward_id") val rewardId: Int,
    @SerialName("remaining_stock") val remainingStock: Int,
    @SerialName("reset_date") val resetDate: String? = null
)

data class SavedRewardStock(val remainingStock: Int, val resetDate: String?)

class UserRewardStockRepository {
    suspend fun getStock(userId: String): Result<Map<Int, SavedRewardStock>> = runCatching {
        SupabaseProvider.client.from("user_reward_stock").select {
            filter { eq("user_id", userId) }
        }.decodeList<UserRewardStockRow>().associate {
            it.rewardId to SavedRewardStock(it.remainingStock, it.resetDate)
        }
    }

    suspend fun saveStock(
        userId: String,
        rewardId: Int,
        remainingStock: Int,
        resetDate: String? = java.time.LocalDate.now().toString()
    ): Result<Unit> = runCatching {
        SupabaseProvider.client.from("user_reward_stock").upsert(
            UserRewardStockRow(userId, rewardId, remainingStock.coerceAtLeast(0), resetDate)
        ) { onConflict = "user_id,reward_id" }
        Unit
    }
}

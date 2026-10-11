package com.example.wildguard.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.wildguard.R
import com.example.wildguard.data.*
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RewardCatalogRow(
    val id: Int,
    val type: String,
    val title: String,
    val description: String,
    val price: Int,
    val stock: Int,
    @SerialName("purchase_mode") val purchaseMode: String = "SINGLE",
    @SerialName("image_res") val imageRes: Int = 0
) {
    fun toRewardItem(): RewardItem {
        val resolvedImage = if (imageRes != 0) imageRes else when (id) {
            1 -> R.drawable.pat_hand
            2 -> R.drawable.watering_tool
            3 -> R.drawable.grab_voucher
            4 -> R.drawable.foodpanda_voucher
            else -> R.drawable.low_stock
        }
        return RewardItem(
            id = id,
            type = type,
            title = title,
            description = description,
            price = price,
            image = resolvedImage,
            stock = stock,
            purchaseMode = runCatching { PurchaseMode.valueOf(purchaseMode) }.getOrDefault(PurchaseMode.SINGLE),
            buttonStatus = if (purchaseMode.equals("PENDING", true)) "PENDING" else "PURCHASE"
        )
    }
}

@Serializable
data class AchievementCatalogRow(
    val id: Int,
    val title: String,
    val description: String,
    @SerialName("unlock_condition") val unlockCondition: String,
    val target: Int,
    @SerialName("reward_points") val rewardPoints: Int,
    val type: String,
    val icon: String = "🏆"
) {
    fun toAchievement() = Achievement(id, title, description, unlockCondition, target, rewardPoints, runCatching { AchievementType.valueOf(type) }.getOrDefault(AchievementType.NULL), icon)
}

object RewardCatalogRepository {
    val rewards = mutableStateListOf<RewardCatalogRow>()
    val achievements = mutableStateListOf<AchievementCatalogRow>()

    suspend fun refresh(): Result<Unit> = runCatching {
        if (!SupabaseProvider.isConfigured) return@runCatching
        val remoteRewards = SupabaseProvider.client.from("reward_catalog").select().decodeList<RewardCatalogRow>()
        val remoteAchievements = SupabaseProvider.client.from("achievement_catalog").select().decodeList<AchievementCatalogRow>()
        rewards.clear(); rewards.addAll(remoteRewards.sortedBy { it.id })
        achievements.clear(); achievements.addAll(remoteAchievements.sortedBy { it.id })
    }

    suspend fun saveReward(row: RewardCatalogRow): Result<Unit> = runCatching {
        SupabaseProvider.client.from("reward_catalog").upsert(row) { onConflict = "id" }
        refresh().getOrThrow()
    }

    suspend fun deleteReward(id: Int): Result<Unit> = runCatching {
        SupabaseProvider.client.from("reward_catalog").delete { filter { eq("id", id) } }
        rewards.removeAll { it.id == id }
    }

    suspend fun updateStock(id: Int, stock: Int): Result<Unit> = runCatching {
        SupabaseProvider.client.from("reward_catalog").update({ set("stock", stock.coerceAtLeast(0)) }) { filter { eq("id", id) } }
        rewards.indexOfFirst { it.id == id }.takeIf { it >= 0 }?.let { rewards[it] = rewards[it].copy(stock = stock.coerceAtLeast(0)) }
    }

    suspend fun saveAchievement(row: AchievementCatalogRow): Result<Unit> = runCatching {
        SupabaseProvider.client.from("achievement_catalog").upsert(row) { onConflict = "id" }
        refresh().getOrThrow()
    }

    suspend fun deleteAchievement(id: Int): Result<Unit> = runCatching {
        SupabaseProvider.client.from("achievement_catalog").delete { filter { eq("id", id) } }
        achievements.removeAll { it.id == id }
    }
}

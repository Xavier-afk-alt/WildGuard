package com.example.wildguard.data.repository

interface InventoryRepository {

    suspend fun getInventory(
        userId: String
    ): Result<Map<Int, Int>>

    suspend fun setQuantity(
        userId: String,
        rewardId: Int,
        quantity: Int
    ): Result<Unit>
}

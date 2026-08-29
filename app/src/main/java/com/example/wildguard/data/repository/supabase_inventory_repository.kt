package com.example.wildguard.data.repository

import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.remote.model.UserInventoryRow
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupabaseInventoryRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) : InventoryRepository {

    override suspend fun getInventory(
        userId: String
    ): Result<Map<Int, Int>> =
        runCatching {

            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            client
                .from("user_inventory")
                .select {
                    filter {
                        eq(
                            "user_id",
                            userId
                        )
                    }
                }
                .decodeList<UserInventoryRow>()
                .associate { row ->

                    row.rewardId to
                            row.quantity
                }
        }


    override suspend fun setQuantity(
        userId: String,
        rewardId: Int,
        quantity: Int
    ): Result<Unit> =
        runCatching {

            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            require(rewardId > 0) {
                "rewardId must be greater than zero."
            }

            require(quantity >= 0) {
                "Inventory quantity cannot be negative."
            }


            val row =
                UserInventoryRow(

                    userId =
                        userId,

                    rewardId =
                        rewardId,

                    quantity =
                        quantity
                )


            client
                .from("user_inventory")
                .upsert(row) {

                    onConflict =
                        "user_id,reward_id"
                }


            Unit
        }
}
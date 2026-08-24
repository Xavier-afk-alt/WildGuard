package com.example.wildguard.data.repository

import com.example.wildguard.data.PointEarningEvent

/**
 * Draft abstraction for the future Supabase point transaction system.
 *
 * The UI/ViewModel should depend on this interface instead of directly
 * depending on Supabase. That way the current local implementation can be
 * replaced later without redesigning RewardPage or the point logic.
 */
interface PointRepository {

    suspend fun addTransaction(
        userId: String,
        event: PointEarningEvent
    ): Result<Unit>

    suspend fun getTransactions(
        userId: String
    ): Result<List<PointEarningEvent>>

    /**
     * Future implementation should use sourceId/event ID or another unique
     * constraint so the same reward cannot be granted twice.
     */
    suspend fun transactionExists(
        userId: String,
        eventId: String
    ): Result<Boolean>
}

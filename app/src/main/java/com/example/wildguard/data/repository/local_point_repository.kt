package com.example.wildguard.data.repository

import com.example.wildguard.data.PointEarningEvent

/**
 * Temporary in-memory repository used until Supabase is connected.
 * This is deliberately small and can be removed later.
 */
class LocalPointRepository : PointRepository {

    private val transactions = mutableMapOf<String, MutableList<PointEarningEvent>>()

    override suspend fun addTransaction(
        userId: String,
        event: PointEarningEvent
    ): Result<Unit> {
        val userTransactions = transactions.getOrPut(userId) { mutableListOf() }

        if (userTransactions.any { it.id == event.id }) {
            return Result.success(Unit)
        }

        userTransactions.add(event)
        return Result.success(Unit)
    }

    override suspend fun getTransactions(
        userId: String
    ): Result<List<PointEarningEvent>> {
        return Result.success(
            transactions[userId]?.toList().orEmpty()
        )
    }

    override suspend fun transactionExists(
        userId: String,
        eventId: String
    ): Result<Boolean> {
        return Result.success(
            transactions[userId]?.any { it.id == eventId } == true
        )
    }
}

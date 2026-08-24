package com.example.wildguard.data.repository

import com.example.wildguard.data.PointEarningEvent

/**
 * SUPABASE DRAFT ONLY
 *
 * No Supabase dependency is required yet.
 *
 * When Supabase is introduced, implement PointRepository here and map the
 * PointEarningEvent fields to the point_transactions table.
 *
 * Suggested table:
 *
 * point_transactions
 * -------------------
 * id            uuid / text      primary key
 * user_id       uuid             not null
 * amount        integer          not null
 * source        text             not null
 * source_id     text             unique per user/reward event
 * title         text
 * description   text
 * created_at    timestamptz      default now()
 *
 * Recommended rule:
 *   unique(user_id, source_id)
 *
 * This prevents duplicate rewards when a button is tapped twice or a request
 * is retried by the network.
 */
class SupabasePointRepositoryDraft : PointRepository {

    override suspend fun addTransaction(
        userId: String,
        event: PointEarningEvent
    ): Result<Unit> {
        // TODO: Replace with Supabase insert/upsert.
        return Result.failure(
            NotImplementedError("Supabase is not connected yet.")
        )
    }

    override suspend fun getTransactions(
        userId: String
    ): Result<List<PointEarningEvent>> {
        // TODO: Replace with Supabase select().
        return Result.failure(
            NotImplementedError("Supabase is not connected yet.")
        )
    }

    override suspend fun transactionExists(
        userId: String,
        eventId: String
    ): Result<Boolean> {
        // TODO: Replace with Supabase existence query.
        return Result.failure(
            NotImplementedError("Supabase is not connected yet.")
        )
    }
}

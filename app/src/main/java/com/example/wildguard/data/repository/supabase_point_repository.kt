package com.example.wildguard.data.repository

import com.example.wildguard.data.PointEarningEvent
import com.example.wildguard.data.PointSource
import com.example.wildguard.data.remote.SupabaseProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * Real Supabase implementation of PointRepository.
 *
 * IMPORTANT:
 * This class is ready to use, but RewardViewModel is intentionally NOT switched
 * to it yet. The app currently has no Supabase Auth flow in WildGuard(6), so
 * there is no reliable auth.users.id to associate with reward transactions.
 *
 * Once Auth is connected, pass the authenticated user's UUID into the methods
 * inherited from PointRepository.
 */
class SupabasePointRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) : PointRepository {

    override suspend fun addTransaction(
        userId: String,
        event: PointEarningEvent
    ): Result<Unit> =
        runCatching {
            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            // Keep application-level duplicate protection as well as the
            // database UNIQUE(user_id, source_id) constraint.
            val exists = transactionExists(
                userId = userId,
                eventId = event.id
            ).getOrThrow()

            if (exists) {
                return@runCatching Unit
            }

            val row = buildJsonObject {
                put("user_id", userId)
                put("amount", event.points)
                put("source", event.source.name)
                put("source_id", event.id)
                put("title", event.title)
                put("description", event.description)
            }

            client
                .from("point_transactions")
                .insert(row)

            Unit
        }

    override suspend fun getTransactions(
        userId: String
    ): Result<List<PointEarningEvent>> =
        runCatching {
            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            client
                .from("point_transactions")
                .select {
                    filter {
                        eq("user_id", userId)
                    }
                }
                .decodeList<JsonObject>()
                .mapNotNull { row ->
                    row.toPointEarningEventOrNull()
                }
        }

    override suspend fun transactionExists(
        userId: String,
        eventId: String
    ): Result<Boolean> =
        runCatching {
            require(userId.isNotBlank()) {
                "A Supabase authenticated user ID is required."
            }

            require(eventId.isNotBlank()) {
                "A point event ID is required."
            }

            client
                .from("point_transactions")
                .select {
                    filter {
                        eq("user_id", userId)
                        eq("source_id", eventId)
                    }

                    limit(count = 1)
                }
                .decodeList<JsonObject>()
                .isNotEmpty()
        }

    private fun JsonObject.toPointEarningEventOrNull(): PointEarningEvent? {
        val sourceId =
            this["source_id"]
                ?.jsonPrimitive
                ?.content
                ?: return null

        val amount =
            this["amount"]
                ?.jsonPrimitive
                ?.intOrNull
                ?: return null

        val sourceName =
            this["source"]
                ?.jsonPrimitive
                ?.content
                ?: PointSource.OTHER.name

        val source =
            runCatching {
                PointSource.valueOf(sourceName)
            }.getOrDefault(
                PointSource.OTHER
            )

        val title =
            this["title"]
                ?.jsonPrimitive
                ?.content
                ?: "Points"

        val description =
            this["description"]
                ?.jsonPrimitive
                ?.content
                .orEmpty()

        return PointEarningEvent(
            id = sourceId,
            source = source,
            points = amount,
            title = title,
            description = description
        )
    }
}

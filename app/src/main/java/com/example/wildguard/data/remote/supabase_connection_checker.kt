package com.example.wildguard.data.remote

import io.github.jan.supabase.postgrest.from

/**
 * Temporary debug-only connectivity check.
 *
 * It performs a tiny read against the existing profiles table. An empty result
 * is still a successful connection. RLS may simply return no rows for anon.
 */
object SupabaseConnectionChecker {

    suspend fun testConnection(): Result<Unit> =
        runCatching {
            SupabaseProvider
                .client
                .from("profiles")
                .select {
                    limit(count = 1)
                }

            Unit
        }
}

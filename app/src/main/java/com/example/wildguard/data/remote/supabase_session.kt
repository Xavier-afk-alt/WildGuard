package com.example.wildguard.data.remote

import io.github.jan.supabase.auth.auth

/**
 * Small session helper prepared for the Auth step.
 *
 * Reward persistence must not start until a real authenticated user exists,
 * because all reward tables are keyed by auth.users.id.
 */
object SupabaseSession {

    fun currentUserIdOrNull(): String? =
        if (!SupabaseProvider.isConfigured) {
            null
        } else {
            SupabaseProvider.client.auth.currentUserOrNull()?.id
        }
}

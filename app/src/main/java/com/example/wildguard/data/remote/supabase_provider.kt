package com.example.wildguard.data.remote

import com.example.wildguard.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Single Supabase client shared by repositories.
 *
 * The Android app uses the public/publishable key only. Database access must
 * still be protected with Row Level Security (RLS) in Supabase.
 */
object SupabaseProvider {

    val isConfigured: Boolean
        get() =
            BuildConfig.SUPABASE_URL.startsWith("https://") &&
                    BuildConfig.SUPABASE_PUBLISHABLE_KEY.isNotBlank()

    val client: SupabaseClient by lazy {
        check(isConfigured) {
            "Supabase is not configured. Add SUPABASE_URL and " +
                    "SUPABASE_PUBLISHABLE_KEY to local.properties."
        }

        createSupabaseClient(
            supabaseUrl = BuildConfig.SUPABASE_URL,
            supabaseKey = BuildConfig.SUPABASE_PUBLISHABLE_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}

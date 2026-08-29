package com.example.wildguard

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.wildguard.data.remote.SupabaseConnectionChecker
import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.remote.SupabaseSession
import com.example.wildguard.navigation.MainPage
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()


        // ========================================================
        // TEMPORARY SUPABASE CONNECTION TEST
        //
        // This does NOT change any Reward functionality.
        // It only checks Android -> Supabase connection.
        // ========================================================

        if (BuildConfig.DEBUG) {

            if (SupabaseProvider.isConfigured) {

                lifecycleScope.launch {

                    SupabaseConnectionChecker
                        .testConnection()
                        .onSuccess {

                            Log.i(
                                "WildGuardSupabase",
                                "Supabase connection successful."
                            )


                            val userId =
                                SupabaseSession
                                    .currentUserIdOrNull()


                            if (userId != null) {

                                Log.i(
                                    "WildGuardSupabase",
                                    "Authenticated user ID: $userId"
                                )

                            } else {

                                Log.i(
                                    "WildGuardSupabase",
                                    "No authenticated Supabase user yet. " +
                                            "Connection is ready; Auth is the next step."
                                )
                            }
                        }
                        .onFailure { error ->

                            Log.e(
                                "WildGuardSupabase",
                                "Supabase connection failed: ${error.message}",
                                error
                            )
                        }
                }

            } else {

                Log.w(
                    "WildGuardSupabase",
                    "Supabase is not configured. " +
                            "Check SUPABASE_URL and " +
                            "SUPABASE_PUBLISHABLE_KEY in local.properties."
                )
            }
        }


        setContent {
            MainPage()
        }
    }
}
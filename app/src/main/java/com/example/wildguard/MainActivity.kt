package com.example.wildguard

import android.os.Bundle
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.wildguard.notification.WildGuardNotifications
import com.example.wildguard.notification.ProximityBackgroundScheduler
import android.content.Intent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.wildguard.data.remote.SupabaseConnectionChecker
import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.remote.SupabaseSession
import com.example.wildguard.data.repository.AdminContentRepository
import com.example.wildguard.navigation.MainPage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.handleDeeplinks
import org.osmdroid.config.Configuration
import com.example.wildguard.localization.AppLanguage
import com.example.wildguard.auth.AuthDeepLinkState
import com.google.android.datatransport.BuildConfig


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AdminContentRepository.initialize(this)
        AppLanguage.initialize(this)
        ProximityBackgroundScheduler.schedule(this)
        AuthDeepLinkState.handle(intent.data)

        Configuration.getInstance().apply {
            load(this@MainActivity, getSharedPreferences("osmdroid", MODE_PRIVATE))
            userAgentValue = "WildGuard/1.0 (${packageName})"
            // Keep viewed map tiles on disk. As the user pans or zooms, new
            // areas extend the cache instead of replacing the explored area.
            tileFileSystemCacheMaxBytes = 200L * 1024L * 1024L
            tileFileSystemCacheTrimBytes = 180L * 1024L * 1024L
        }

        if (SupabaseProvider.isConfigured) {
            SupabaseProvider.client.handleDeeplinks(intent)

            // Keep the shared database content current for both Admin and User UI.
            lifecycleScope.launch {
                while (isActive) {
                    AdminContentRepository.refreshNews()
                    AdminContentRepository.refreshAnnouncements()
                    notifyNewAnnouncementIfNeeded()
                    delay(3000)
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 501)
        } else {
            sendDailyLoginNotificationIfNeeded()
        }

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

    private fun sendDailyLoginNotificationIfNeeded() {
        val prefs = getSharedPreferences("wildguard_notifications", MODE_PRIVATE)
        val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        if (prefs.getString("last_daily_login", null) != today) {
            WildGuardNotifications.dailyLogin(this)
            prefs.edit().putString("last_daily_login", today).apply()
        }
    }

    private fun notifyNewAnnouncementIfNeeded() {
        val latest = AdminContentRepository.announcements.firstOrNull() ?: return
        val prefs = getSharedPreferences("wildguard_notifications", MODE_PRIVATE)
        val previousId = prefs.getString("last_popup_announcement_id", null)
        if (previousId == null) {
            prefs.edit().putString("last_popup_announcement_id", latest.id).apply()
        } else if (previousId != latest.id) {
            WildGuardNotifications.announcement(this, latest.title, latest.content)
            prefs.edit().putString("last_popup_announcement_id", latest.id).apply()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 501 && grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
            sendDailyLoginNotificationIfNeeded()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        AuthDeepLinkState.handle(intent.data)

        if (SupabaseProvider.isConfigured) {
            SupabaseProvider.client.handleDeeplinks(intent)
        }
    }
}

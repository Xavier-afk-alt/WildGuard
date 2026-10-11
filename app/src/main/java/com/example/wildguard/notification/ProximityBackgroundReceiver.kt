package com.example.wildguard.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.core.app.ActivityCompat
import com.example.wildguard.data.repository.SharedIncidentRepository
import com.example.wildguard.localization.AppLanguage
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object ProximityBackgroundScheduler {
    private const val REQUEST_CODE = 7401
    private const val INTERVAL_MS = 15 * 60 * 1000L

    fun schedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ProximityBackgroundReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 2 * 60 * 1000L,
            INTERVAL_MS,
            pendingIntent
        )
    }
}

class ProximityBackgroundReceiver : BroadcastReceiver() {
    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        AppLanguage.initialize(appContext)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val runtimePrefs = appContext.getSharedPreferences("wildguard_proximity_runtime", Context.MODE_PRIVATE)
                val role = runtimePrefs.getString("active_role", "user") ?: "user"

                fun evaluate(latitude: Double, longitude: Double) {
                    val alert = if (role == "admin") {
                        val reportId = runtimePrefs.getString("admin_report_id", null)
                        SharedIncidentRepository.reports.firstOrNull { it.reportId == reportId }?.let { report ->
                            ProximityAlertManager.checkAdmin(appContext, latitude, longitude, report)
                        }
                    } else {
                        ProximityAlertManager.checkUser(
                            appContext, latitude, longitude, SharedIncidentRepository.reports
                        )
                    }
                    alert?.let { WildGuardNotifications.proximity(appContext, it) }
                }

                fun evaluateSavedLocation() {
                    val prefix = if (role == "admin") "admin" else "user"
                    val latitude = runtimePrefs.getString("${prefix}_last_lat", null)?.toDoubleOrNull()
                    val longitude = runtimePrefs.getString("${prefix}_last_lon", null)?.toDoubleOrNull()
                    if (latitude != null && longitude != null) evaluate(latitude, longitude)
                }

                SharedIncidentRepository.refresh()
                val fine = ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val coarse = ActivityCompat.checkSelfPermission(appContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (!fine && !coarse) {
                    evaluateSavedLocation()
                    pendingResult.finish()
                    return@launch
                }
                val client = LocationServices.getFusedLocationProviderClient(appContext)
                client.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            evaluate(location.latitude, location.longitude)
                        } else evaluateSavedLocation()
                        pendingResult.finish()
                    }
                    .addOnFailureListener {
                        evaluateSavedLocation()
                        pendingResult.finish()
                    }
            } catch (_: Exception) {
                pendingResult.finish()
            }
        }
    }
}

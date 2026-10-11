package com.example.wildguard.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.wildguard.R
import com.example.wildguard.localization.AppLanguage

object WildGuardNotifications {
    private const val CHANNEL_ID = "wildguard_alerts"
    private const val PROXIMITY_CHANNEL_ID = "wildguard_proximity_alerts_v2"

    fun show(context: Context, title: String, message: String, id: Int = message.hashCode()) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "WildGuard Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Wildlife, reward and activity alerts" }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            try {
                NotificationManagerCompat.from(context).notify(id, notification)
            } catch (_: SecurityException) {
                // Android 13+ notification permission has not been granted yet.
            }
        }
    }

    fun dailyLogin(context: Context) = show(
        context,
        "WildGuard",
        AppLanguage.text("Your daily login reward is ready.", "您的每日登录奖励已准备好。", "Ganjaran log masuk harian anda sudah tersedia."),
        1001
    )
    fun wildlifeNearby(context: Context, location: String = "near your current location") =
        show(
            context,
            AppLanguage.text("New alert: wildlife nearby", "新消息：附近有野生动物", "Amaran baharu: hidupan liar berdekatan"),
            AppLanguage.text(
                "Wildlife was reported $location. Please stay aware of your surroundings.",
                "在 $location 有野生动物报告，请注意周围环境。",
                "Hidupan liar dilaporkan di $location. Sila peka terhadap persekitaran anda."
            ),
            1002
        )
    fun animalInteraction(context: Context, animalName: String) =
        show(
            context,
            "WildGuard Reward",
            AppLanguage.text("$animalName wants to interact.", "$animalName 想要互动。", "$animalName mahu berinteraksi."),
            1003 + ((animalName.hashCode() and 0x7fffffff) % 97)
        )

    fun foxInteraction(context: Context) = animalInteraction(context, "狐狸")

    fun plantNeedsWater(context: Context) =
        show(
            context,
            "WildGuard Reward",
            AppLanguage.text("Your plant needs water.", "您的植物需要水分。", "Tumbuhan anda memerlukan air."),
            1004
        )

    fun announcement(context: Context, title: String, message: String) = show(
        context,
        AppLanguage.text("New announcement: $title", "新公告：$title", "Pengumuman baharu: $title"),
        message,
        2000 + ((title.hashCode() and 0x7fffffff) % 997)
    )

    fun proximity(context: Context, alert: ProximityAlert) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PROXIMITY_CHANNEL_ID,
                "Wildlife Proximity Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Nearby wildlife and incident-distance alerts"
                enableVibration(true)
            }
            context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(context, PROXIMITY_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(alert.title)
            .setContentText(alert.message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(alert.message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            try {
                NotificationManagerCompat.from(context).notify(alert.key.hashCode(), notification)
            } catch (_: SecurityException) {
                // Notification permission is not granted.
            }
        }
    }
}

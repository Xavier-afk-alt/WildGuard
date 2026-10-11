package com.example.wildguard.notification

import android.content.Context
import android.location.Location
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.data.report.ReportStatus
import com.example.wildguard.data.report.Severity
import com.example.wildguard.localization.AppLanguage

data class ProximityAlert(
    val key: String,
    val title: String,
    val message: String,
    val distanceMetres: Int
)

object ProximityAlertManager {
    private const val PREFS = "wildguard_proximity_alerts"
    private const val COOLDOWN_MS = 15 * 60 * 1000L

    fun checkUser(
        context: Context,
        latitude: Double,
        longitude: Double,
        reports: List<IncidentReport>
    ): ProximityAlert? {
        val active = reports.filter {
            it.status != ReportStatus.RESOLVED && it.status != ReportStatus.REJECTED &&
                (it.latitude != 0.0 || it.longitude != 0.0)
        }
        val candidates = active.map { report ->
            val distance = distance(latitude, longitude, report.latitude, report.longitude)
            val danger = report.isAggressive || report.severity == Severity.CRITICAL
            Triple(report, distance, danger)
        }.filter { (_, distance, danger) -> distance <= if (danger) 800f else 300f }

        val selected = candidates.minWithOrNull(
            compareByDescending<Triple<IncidentReport, Float, Boolean>> { it.third }
                .thenBy { it.second }
        )
        if (selected == null) {
            clearActive(context, "user")
            return null
        }

        val (report, distance, danger) = selected
        val key = "${report.reportId}:${if (danger) "danger" else "nearby"}"
        val alert = if (danger) {
            ProximityAlert(
                key,
                AppLanguage.text("Emergency wildlife alert", "紧急野生动物警报", "Amaran kecemasan hidupan liar"),
                AppLanguage.text(
                    "Aggressive or critical wildlife was reported about ${formatDistance(distance)} away. Keep a safe distance.",
                    "约 ${formatDistance(distance)} 外有具攻击性或严重的野生动物报告。请保持安全距离。",
                    "Hidupan liar agresif atau kritikal dilaporkan kira-kira ${formatDistance(distance)} dari anda. Kekalkan jarak selamat."
                ),
                distance.toInt()
            )
        } else {
            ProximityAlert(
                key,
                AppLanguage.text("Nearby wildlife report", "附近野生动物报告", "Laporan hidupan liar berdekatan"),
                AppLanguage.text(
                    "A wildlife incident is about ${formatDistance(distance)} away. Please avoid the area.",
                    "约 ${formatDistance(distance)} 外有野生动物事件。请避开该区域。",
                    "Insiden hidupan liar berada kira-kira ${formatDistance(distance)} dari anda. Sila elakkan kawasan tersebut."
                ),
                distance.toInt()
            )
        }
        return consumeIfNew(context, "user", alert)
    }

    fun checkAdmin(
        context: Context,
        latitude: Double,
        longitude: Double,
        report: IncidentReport
    ): ProximityAlert? {
        if (report.status == ReportStatus.RESOLVED || report.status == ReportStatus.REJECTED) {
            clearActive(context, "admin")
            return null
        }
        val distance = distance(latitude, longitude, report.latitude, report.longitude)
        val tier = when {
            distance <= 150f -> "arrived"
            distance <= 1_000f -> "approaching"
            else -> null
        }
        if (tier == null) {
            clearActive(context, "admin")
            return null
        }
        val alert = if (tier == "arrived") {
            ProximityAlert(
                "${report.reportId}:arrived",
                AppLanguage.text("Incident location reached", "已到达事件地点", "Lokasi insiden telah dicapai"),
                AppLanguage.text(
                    "You are about ${formatDistance(distance)} from the incident. Verify the situation before marking it resolved.",
                    "您距离事件地点约 ${formatDistance(distance)}。标记为已解决前请先核实情况。",
                    "Anda kira-kira ${formatDistance(distance)} dari insiden. Sahkan keadaan sebelum menandainya selesai."
                ), distance.toInt()
            )
        } else {
            ProximityAlert(
                "${report.reportId}:approaching",
                AppLanguage.text("Approaching incident", "正在接近事件地点", "Menghampiri insiden"),
                AppLanguage.text(
                    "You are approximately ${formatDistance(distance)} from the incident location.",
                    "您距离事件地点约 ${formatDistance(distance)}。",
                    "Anda berada kira-kira ${formatDistance(distance)} dari lokasi insiden."
                ), distance.toInt()
            )
        }
        return consumeIfNew(context, "admin", alert)
    }

    private fun consumeIfNew(context: Context, role: String, alert: ProximityAlert): ProximityAlert? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val activeKey = prefs.getString("${role}_active", null)
        val lastTime = prefs.getLong("${role}_${alert.key}_time", 0L)
        val now = System.currentTimeMillis()
        if (activeKey == alert.key || now - lastTime < COOLDOWN_MS) return null
        prefs.edit()
            .putString("${role}_active", alert.key)
            .putLong("${role}_${alert.key}_time", now)
            .apply()
        return alert
    }

    private fun clearActive(context: Context, role: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove("${role}_active").apply()
    }

    private fun distance(fromLat: Double, fromLon: Double, toLat: Double, toLon: Double): Float {
        val result = FloatArray(1)
        Location.distanceBetween(fromLat, fromLon, toLat, toLon, result)
        return result[0]
    }

    private fun formatDistance(metres: Float): String =
        if (metres >= 1_000f) "${"%.1f".format(metres / 1_000f)} km" else "${metres.toInt()} m"
}

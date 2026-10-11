package com.example.wildguard.data.repository

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.data.report.IncidentType
import com.example.wildguard.data.report.Severity
import com.example.wildguard.data.report.ReportStatus
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
private data class IncidentReportRow(
    @SerialName("report_id") val reportId: String,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("incident_type") val incidentType: String,
    @SerialName("animal_species") val animalSpecies: String,
    @SerialName("animal_count") val animalCount: Int,
    @SerialName("is_aggressive") val isAggressive: Boolean,
    val latitude: Double,
    val longitude: Double,
    @SerialName("location_name") val locationName: String,
    val description: String,
    val weather: String = "Cloudy",
    val severity: String,
    val status: String = "OPEN",
    @SerialName("image_urls") val imageUrls: List<String> = emptyList(),
    @SerialName("reported_at") val reportedAt: Long
)

object SharedIncidentRepository {
    val reports = mutableStateListOf<IncidentReport>()

    // ------------------------
    // report-upload photo and save database
    // ------------------------
    suspend fun add(report: IncidentReport, context: Context): Result<Unit> = runCatching {
        check(SupabaseProvider.isConfigured) { "Supabase is not configured." }

        val storedImageUrls = report.imageUris.mapIndexed { index, source ->
            if (source.startsWith("http://") || source.startsWith("https://")) source
            else uploadReportImage(context, report, Uri.parse(source), index)
        }
        val storedReport = report.copy(imageUris = storedImageUrls)
        SupabaseProvider.client.from("incident_reports").upsert(storedReport.toRow()) {
            onConflict = "report_id"
        }
        reports.removeAll { it.reportId == storedReport.reportId }
        reports.add(0, storedReport)
        Unit
    }

    private suspend fun uploadReportImage(
        context: Context,
        report: IncidentReport,
        uri: Uri,
        index: Int
    ): String {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: error("Unable to read attached report photo.")
        val extension = context.contentResolver.getType(uri)?.substringAfter('/')
            ?.substringBefore('+')?.takeIf { it.matches(Regex("[A-Za-z0-9]+")) } ?: "jpg"
        val owner = report.userId.ifBlank { "anonymous" }
        val path = "$owner/${report.reportId}/${System.currentTimeMillis()}_$index.$extension"
        val bucket = SupabaseProvider.client.storage.from("report-images")
        bucket.upload(path, bytes) { upsert = false }
        return bucket.publicUrl(path)
    }

    suspend fun refresh(): Result<Unit> = runCatching {
        if (!SupabaseProvider.isConfigured) return@runCatching
        val remote = SupabaseProvider.client.from("incident_reports").select()
            .decodeList<IncidentReportRow>()
            .map { it.toReport() }
            .sortedByDescending { it.timestamp }
        reports.clear()
        reports.addAll(remote)
        Unit
    }

    suspend fun markResolved(reportId: String): Result<Unit> = runCatching {
        val index = reports.indexOfFirst { it.reportId == reportId }
        if (index < 0) return@runCatching
        val resolved = reports[index].copy(status = ReportStatus.RESOLVED)
        if (SupabaseProvider.isConfigured) {
            SupabaseProvider.client.from("incident_reports").update({
                set("status", ReportStatus.RESOLVED.name)
            }) {
                filter { eq("report_id", reportId) }
            }
        }
        reports[index] = resolved
        Unit
    }

    suspend fun delete(reportId: String): Result<Unit> = runCatching {
        check(SupabaseProvider.isConfigured) { "Supabase is not configured." }
        SupabaseProvider.client.from("incident_reports").delete {
            filter { eq("report_id", reportId) }
        }
        reports.removeAll { it.reportId == reportId }
        Unit
    }
}

private fun IncidentReport.toRow() = IncidentReportRow(
    reportId = reportId,
    userId = userId.ifBlank { null },
    incidentType = incidentType.name,
    animalSpecies = animalSpecies,
    animalCount = animalCount,
    isAggressive = isAggressive,
    latitude = latitude,
    longitude = longitude,
    locationName = locationName,
    description = description,
    weather = weather,
    severity = severity.name,
    status = status.name,
    imageUrls = imageUris,
    reportedAt = timestamp
)

private fun IncidentReportRow.toReport() = IncidentReport(
    reportId = reportId,
    userId = userId.orEmpty(),
    incidentType = runCatching { IncidentType.valueOf(incidentType) }.getOrDefault(IncidentType.OTHER),
    animalSpecies = animalSpecies,
    animalCount = animalCount,
    isAggressive = isAggressive,
    latitude = latitude,
    longitude = longitude,
    locationName = locationName,
    description = description,
    weather = weather,
    severity = runCatching { Severity.valueOf(severity) }.getOrDefault(Severity.MEDIUM),
    status = runCatching { ReportStatus.valueOf(status) }.getOrDefault(ReportStatus.OPEN),
    imageUris = imageUrls,
    timestamp = reportedAt
)

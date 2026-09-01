package com.example.wildguard.data.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.wildguard.data.remote.SupabaseProvider
import com.example.wildguard.data.report.IncidentReport
import com.example.wildguard.data.report.IncidentType
import com.example.wildguard.data.report.Severity
import io.github.jan.supabase.postgrest.from
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
    val severity: String,
    @SerialName("reported_at") val reportedAt: Long
)

object SharedIncidentRepository {
    val reports = mutableStateListOf<IncidentReport>()

    suspend fun add(report: IncidentReport): Result<Unit> = runCatching {
        if (reports.none { it.reportId == report.reportId }) reports.add(0, report)
        if (!SupabaseProvider.isConfigured) return@runCatching

        SupabaseProvider.client.from("incident_reports").upsert(report.toRow()) {
            onConflict = "report_id"
        }
        Unit
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
    severity = severity.name,
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
    severity = runCatching { Severity.valueOf(severity) }.getOrDefault(Severity.MEDIUM),
    timestamp = reportedAt
)

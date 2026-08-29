package com.example.wildguard.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.wildguard.data.report.IncidentReport
import java.util.UUID

class ReportViewModel : ViewModel() {

    private val _submittedReports = mutableStateListOf<IncidentReport>()
    val submittedReports: List<IncidentReport>
        get() = _submittedReports

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var lastSubmittedReport by mutableStateOf<IncidentReport?>(null)
        private set

    fun clearError() {
        errorMessage = null
    }

    fun submitReport(report: IncidentReport): Boolean {
        val validationError = validate(report)

        if (validationError != null) {
            errorMessage = validationError
            return false
        }

        val completedReport = report.copy(
            reportId = report.reportId.ifBlank {
                "WG-${UUID.randomUUID().toString().take(8).uppercase()}"
            }
        )

        _submittedReports.add(completedReport)
        lastSubmittedReport = completedReport
        errorMessage = null
        return true
    }

    private fun validate(report: IncidentReport): String? {
        if (report.animalCount <= 0) {
            return "Animal count must be at least 1."
        }

        if (report.locationName.isBlank() || report.locationName == "Choose Location") {
            return "Please choose the incident location."
        }

        if (report.description.isBlank()) {
            return "Please describe what happened."
        }

        return null
    }
}
